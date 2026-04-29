package com.google.appinventor.components.runtime.util;

import android.util.Log;
import com.google.appinventor.components.runtime.Component;
import gnu.mapping.Symbol;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StackFrame implements Cloneable {

  private static final String LOG_TAG = "StackFrame";

  private static ThreadLocal<Deque<StackFrame>> frames = new ThreadLocal<Deque<StackFrame>>() {
    @Override
    protected Deque<StackFrame> initialValue() {
      return new LinkedList<>();
    }
  };

  // Breakpoint management
  private static Set<String> breakpoints = Collections.synchronizedSet(new HashSet<>());
  private static Set<String> exitBreakpoints = Collections.synchronizedSet(new HashSet<>());
  private static volatile boolean debugMode = false;
  private static volatile boolean paused = false;
  private static volatile boolean stopRequested = false;
  private static final Lock pauseLock = new ReentrantLock();
  private static final Condition pauseCondition = pauseLock.newCondition();
  private static String pausedBlockId = null;

  private enum StepMode { NONE, STEP_INTO, STEP_OVER }
  private static volatile StepMode stepMode = StepMode.NONE;
  private static volatile boolean errorPaused = false;
  private static volatile int capturedFrameDepth = 0;
  private static volatile int capturedBlockDepth = 0;
  private static volatile int stepTargetFrameDepth = 0;
  private static volatile int stepTargetBlockDepth = 0;

  private Deque<String> blockIds;
  private Map<Symbol, Object> values;
  private Map<String, Object> returnValues;

  public StackFrame(String blockId) {
    this.blockIds = new LinkedList<>();
    this.values = new HashMap<>();
    this.returnValues = new HashMap<>();
    this.blockIds.add(blockId);
  }

  public String getBlockId() {
    return blockIds.getFirst();
  }

  public List<String> getBlockIds() {
    return new LinkedList<>(blockIds);
  }

  public void push(String newBlockId) {
    blockIds.push(newBlockId);
  }

  public String pop() {
    return blockIds.pop();
  }

  public Map<Symbol, Object> getVariables() {
    return values;
  }

  public void set(Symbol symbol, Object value) {
    values.put(symbol, value);
  }

  public static void put(String name, Object value) {
    Deque<StackFrame> myFrames = frames.get();
    if (!myFrames.isEmpty()) {
      Symbol symbol = Symbol.valueOf(name);
      myFrames.getFirst().set(symbol, value);
    }
  }

  public static void remove(String name) {
    Deque<StackFrame> myFrames = frames.get();
    if (!myFrames.isEmpty()) {
      myFrames.getFirst().values.remove(Symbol.valueOf(name));
    }
  }

  public static void recordReturn(String procName, Object value) {
    Iterator<StackFrame> it = frames.get().iterator();
    if (!it.hasNext()) return;
    it.next();
    if (!it.hasNext()) return;
    it.next().returnValues.put(procName, value);
  }

  private static String toDisplayValue(Object value) {
    if (value == null) return "null";
    if (value instanceof Component) {
      return ((Component) value).Name();
    }
    try {
      return JsonUtil.getJsonRepresentation(value);
    } catch (JSONException e) {
      return value.toString();
    }
  }

  private JSONObject serializeMapToJson(Map<?, Object> map) throws JSONException {
    JSONObject obj = new JSONObject();
    for (Map.Entry<?, Object> entry : map.entrySet()) {
      obj.put(entry.getKey().toString(), toDisplayValue(entry.getValue()));
    }
    return obj;
  }

  public JSONObject toJson() throws JSONException {
    JSONObject result = new JSONObject();

    org.json.JSONArray blockIdsArray = new org.json.JSONArray();
    for (String blockId : blockIds) {
      blockIdsArray.put(blockId);
    }
    result.put("blockIds", blockIdsArray);

    if (!values.isEmpty()) {
      result.put("vars", serializeMapToJson(values));
    }
    if (!returnValues.isEmpty()) {
      result.put("returnValues", serializeMapToJson(returnValues));
    }
    return result;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    StackFrame copy = (StackFrame) super.clone();
    copy.blockIds = new LinkedList<>(blockIds);
    copy.values = new HashMap<>(values);
    copy.returnValues = new HashMap<>(returnValues);
    return copy;
  }

  public static Deque<StackFrame> get() {
    return frames.get();
  }

  public static StackFrame enter(String blockId) {
    checkPauseConditions(blockId);
    Deque<StackFrame> myFrames = frames.get();
    if (myFrames.isEmpty()) {
      myFrames.push(new StackFrame(blockId));
    } else {
      myFrames.getFirst().push(blockId);
    }
    return myFrames.getFirst();
  }

  public static StackFrame exit(String blockId) {
    Deque<StackFrame> myFrames = frames.get();
    if (myFrames.isEmpty()) {
      Log.w(LOG_TAG, "Attempted to exit block " + blockId + " but no frames exist");
      return null;
    }
    if (debugMode && exitBreakpoints.remove(blockId)) {
      pauseAt(blockId);
    }
    String topBlockId = myFrames.getFirst().pop();
    if (topBlockId != null && !topBlockId.equals(blockId)) {
      Log.w(LOG_TAG, "Unexpected block id " + topBlockId + "; wanted to see: " + blockId);
    }
    if (myFrames.size() == 1 && myFrames.getFirst().blockIds.isEmpty()) {
      stepMode = StepMode.NONE;
      myFrames.getFirst().returnValues.clear();
    }
    return myFrames.isEmpty() ? null : myFrames.getFirst();
  }

  public static StackFrame pushFrame(String blockId) {
    if (debugMode && breakpoints.contains(blockId)) {
      pauseAt(blockId);
    }
    Deque<StackFrame> myFrames = frames.get();
    StackFrame newFrame = new StackFrame(blockId);
    if (!myFrames.isEmpty() && !myFrames.getFirst().returnValues.isEmpty()) {
      newFrame.returnValues.putAll(myFrames.getFirst().returnValues);
    }
    myFrames.push(newFrame);
    return newFrame;
  }

  public static StackFrame popFrame() {
    Deque<StackFrame> myFrames = frames.get();
    StackFrame popped = myFrames.isEmpty() ? null : myFrames.pop();
    if (myFrames.isEmpty() && debugMode && stepMode != StepMode.NONE) {
      stepMode = StepMode.NONE;
      if (popped != null) {
        RetValManager.sendBreakpointHit(popped.getBlockId());
      }
    }
    return popped;
  }

  public static void clear() {
    stepMode = StepMode.NONE;
    frames.get().clear();
  }

  public static void setErrorPaused(boolean value) {
    errorPaused = value;
  }

  public static void clearErrorPaused() {
    pauseLock.lock();
    try {
      errorPaused = false;
      pauseCondition.signalAll();
    } finally {
      pauseLock.unlock();
    }
  }

  public static void setBreakpoints(Set<String> newBreakpoints) {
    breakpoints = new HashSet<>(newBreakpoints);
  }

  public static void addBreakpoint(String blockId) {
    breakpoints.add(blockId);
  }

  public static void removeBreakpoint(String blockId) {
    breakpoints.remove(blockId);
  }

  public static void addExitBreakpoint(String blockId) {
    exitBreakpoints.add(blockId);
  }

  public static void removeExitBreakpoint(String blockId) {
    exitBreakpoints.remove(blockId);
  }

  public static void clearBreakpoints() {
    breakpoints.clear();
    exitBreakpoints.clear();
  }

  public static void setDebugMode(boolean enabled) {
    debugMode = enabled;
  }

  public static boolean isDebugMode() {
    return debugMode;
  }

  public static void setStepIntoMode() {
    stepMode = StepMode.STEP_INTO;
  }

  public static void setStepOverMode() {
    stepTargetFrameDepth = capturedFrameDepth;
    stepTargetBlockDepth = capturedBlockDepth;
    stepMode = StepMode.STEP_OVER;
  }

  public static void continuePause() {
    pauseLock.lock();
    try {
      paused = false;
      pausedBlockId = null;
      pauseCondition.signalAll();
    } finally {
      pauseLock.unlock();
    }
  }

  public static void stopExecution() {
    pauseLock.lock();
    try {
      stepMode = StepMode.NONE;
      errorPaused = false;
      if (paused) {
        stopRequested = true;
        paused = false;
        pausedBlockId = null;
        pauseCondition.signalAll();
      }
    } finally {
      pauseLock.unlock();
    }
  }

  public static boolean isPaused() {
    return paused;
  }

  public static String getPausedBlockId() {
    return pausedBlockId;
  }

  private static void pauseAt(String blockId) {
    Deque<StackFrame> myFrames = frames.get();
    capturedFrameDepth = myFrames.size();
    capturedBlockDepth = myFrames.isEmpty() ? 0 : myFrames.getFirst().blockIds.size();

    pauseLock.lock();
    try {
      paused = true;
      pausedBlockId = blockId;
      notifyBreakpointHit(blockId);
      while (paused) {
        try {
          pauseCondition.await();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
      if (stopRequested) {
        stopRequested = false;
        throw new DebugStopException();
      }
    } finally {
      pauseLock.unlock();
    }
  }

  private static void checkPauseConditions(String blockId) {
    if (!debugMode) return;
    if (errorPaused) {
      throw new DebugStopException();
    }

    boolean shouldPause = false;

    if (stepMode == StepMode.STEP_INTO) {
      stepMode = StepMode.NONE;
      shouldPause = true;
    } else if (stepMode == StepMode.STEP_OVER) {
      Deque<StackFrame> myFrames = frames.get();
      int frameDepth = myFrames.size();
      int blockDepth = myFrames.isEmpty() ? 0 : myFrames.getFirst().blockIds.size();
      boolean atOrShallower = frameDepth < stepTargetFrameDepth
          || (frameDepth == stepTargetFrameDepth && blockDepth <= stepTargetBlockDepth);
      if (atOrShallower) {
        stepMode = StepMode.NONE;
        shouldPause = true;
      }
    }

    if (breakpoints.contains(blockId)) {
      shouldPause = true;
    }

    if (shouldPause) {
      pauseAt(blockId);
    }
  }

  private static void notifyBreakpointHit(String blockId) {
    RetValManager.sendBreakpointHit(blockId);
  }
}