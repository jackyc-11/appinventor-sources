package com.google.appinventor.components.runtime.util;

import android.util.Log;
import gnu.mapping.Symbol;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StackFrame implements Cloneable {

  private static final String LOG_TAG = "StackFrame";

  private static ThreadLocal<Deque<StackFrame>> frames = new ThreadLocal<Deque<StackFrame>>() {
    @Override
    protected Deque<StackFrame> initialValue() {
      return new LinkedList<>();
    }
  };

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
        varsObject.put(key, value != null ? value.toString() : "null");
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
    String topBlockId = myFrames.getFirst().pop();
    if (topBlockId != null && !topBlockId.equals(blockId)) {
      Log.w(LOG_TAG, "Unexpected block id " + topBlockId + "; wanted to see: " + blockId);
    }
    return myFrames.isEmpty() ? null : myFrames.getFirst();
  }

  public static StackFrame pushFrame(String blockId) {
    Log.d(LOG_TAG, "Pushing new frame for block id " + blockId);
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
}