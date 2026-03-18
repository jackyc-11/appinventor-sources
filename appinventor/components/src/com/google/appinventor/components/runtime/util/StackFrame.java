package com.google.appinventor.components.runtime.util;

import android.util.Log;
import gnu.mapping.Symbol;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
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
  private static Set<String> breakpoints = new HashSet<>();
  private static Set<String> exitBreakpoints = new HashSet<>();
  private static volatile boolean debugMode = false;
  private static volatile boolean paused = false;
  private static volatile boolean stopRequested = false;
  private static final Lock pauseLock = new ReentrantLock();
  private static final Condition pauseCondition = pauseLock.newCondition();
  private static String pausedBlockId = null;

  private Deque<String> blockIds;
  private Map<Symbol, Object> values;

  public StackFrame(String blockId) {
    this.blockIds = new LinkedList<>();
    this.values = new HashMap<>();
    this.blockIds.add(blockId);
  }

  public String getBlockId() {
    return blockIds.getFirst();
  }

  public List<String> getBlockIds() {
    return (LinkedList<String>) blockIds;
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

  public JSONObject toJson() throws JSONException {
    JSONObject result = new JSONObject();

    org.json.JSONArray blockIdsArray = new org.json.JSONArray();
    for (String blockId : blockIds) {
      blockIdsArray.put(blockId);
    }
    result.put("blockIds", blockIdsArray);

    if (!values.isEmpty()) {
      JSONObject varsObject = new JSONObject();
      for (Map.Entry<Symbol, Object> entry : values.entrySet()) {
        String key = entry.getKey().toString();
        Object value = entry.getValue();
        String displayValue;
        if (value == null) {
          displayValue = "null";
        } else {
          try {
            displayValue = JsonUtil.getJsonRepresentation(value);
          } catch (JSONException e) {
            displayValue = value.toString();
          }
        }
        varsObject.put(key, displayValue);
      }
      result.put("vars", varsObject);
    }
    return result;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    StackFrame copy = (StackFrame) super.clone();
    copy.blockIds = (LinkedList<String>) ((LinkedList<String>) blockIds).clone();
    copy.values = (HashMap<Symbol, Object>) ((HashMap<Symbol, Object>) values).clone();
    return copy;
  }

  public static Deque<StackFrame> get() {
    return frames.get();
  }

  public static StackFrame enter(String blockId) {
    Log.d(LOG_TAG, "Entering block " + blockId);
    checkBreakpoint(blockId);
    Deque<StackFrame> myFrames = frames.get();
    if (myFrames.isEmpty()) {
      myFrames.push(new StackFrame(blockId));
    } else {
      myFrames.getFirst().push(blockId);
    }
    return myFrames.getFirst();
  }

  public static StackFrame exit(String blockId) {
    Log.d(LOG_TAG, "Exiting block " + blockId);
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
    return myFrames.isEmpty() ? null : myFrames.getFirst();
  }

  public static StackFrame pushFrame(String blockId) {
    Log.d(LOG_TAG, "Pushing new frame for block id " + blockId);
    checkBreakpoint(blockId);
    StackFrame newFrame = new StackFrame(blockId);
    frames.get().push(newFrame);
    return newFrame;
  }

  public static StackFrame popFrame() {
    Log.d(LOG_TAG, "Popping stack frame");
    Deque<StackFrame> myFrames = frames.get();
    return myFrames.isEmpty() ? null : myFrames.pop();
  }

  public static void clear() {
    Log.d(LOG_TAG, "Clearing all stack frames");
    frames.get().clear();
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

  private static void checkBreakpoint(String blockId) {
    if (!debugMode || !breakpoints.contains(blockId)) {
      return;
    }

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

  private static void notifyBreakpointHit(String blockId) {
    RetValManager.sendBreakpointHit(blockId);
  }
}