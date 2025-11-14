// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2017 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

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

/**
 * Represents a stack frame for tracking block execution in the REPL.
 * Each frame contains a stack of block IDs and optionally variable bindings.
 * Stack frames are stored in thread-local storage to support multi-threaded execution.
 */
public class StackFrame implements Cloneable {

  private static final String LOG_TAG = "StackFrame";

  // Thread-local storage for stack frames
  private static ThreadLocal<Deque<StackFrame>> frames = new ThreadLocal<Deque<StackFrame>>() {
    @Override
    protected Deque<StackFrame> initialValue() {
      return new LinkedList<>();
    }
  };

  // Stack of block IDs within this frame
  private Deque<String> blockIds;

  // Variable bindings within this frame
  private Map<Symbol, Object> values;

  /**
   * Creates a new stack frame with the given block ID.
   */
  public StackFrame(String blockId) {
    this.blockIds = new LinkedList<>();
    this.values = new HashMap<>();
    this.blockIds.add(blockId);
  }

  /**
   * Gets the current (top) block ID in this frame.
   */
  public String getBlockId() {
    return blockIds.isEmpty() ? null : blockIds.getFirst();
  }

  /**
   * Gets all block IDs in this frame.
   */
  public List<String> getBlockIds() {
    return (LinkedList<String>) blockIds;
  }

  /**
   * Pushes a new block ID onto the stack within this frame.
   */
  public void push(String newBlockId) {
    blockIds.push(newBlockId);
  }

  /**
   * Pops the top block ID from the stack within this frame.
   */
  public String pop() {
    return blockIds.isEmpty() ? null : blockIds.pop();
  }

  /**
   * Gets the variable bindings in this frame.
   */
  public Map<Symbol, Object> getVariables() {
    return values;
  }

  /**
   * Sets a variable binding in this frame.
   */
  public void set(Symbol symbol, Object value) {
    values.put(symbol, value);
  }

  /**
   * Converts this frame to a JSON object for transmission to the browser.
   */
  public JSONObject toJson() throws JSONException {
    JSONObject result = new JSONObject();

    // Convert blockIds deque to JSON array
    org.json.JSONArray blockIdsArray = new org.json.JSONArray();
    for (String blockId : blockIds) {
      blockIdsArray.put(blockId);
    }
    result.put("blockIds", blockIdsArray);

    // Only include variables if there are any
    if (!values.isEmpty()) {
      JSONObject varsObject = new JSONObject();
      for (Map.Entry<Symbol, Object> entry : values.entrySet()) {
        String key = entry.getKey().toString();
        Object value = entry.getValue();
        // Convert value to string representation for simplicity
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

  // ===== Static Methods for Managing Thread-Local Stack =====

  /**
   * Gets the stack of frames for the current thread.
   */
  public static Deque<StackFrame> get() {
    return frames.get();
  }

  /**
   * Enters a new block within the current frame.
   * Pushes the block ID onto the current frame's block stack.
   */
  public static StackFrame enter(String blockId) {
    Log.d(LOG_TAG, "Entering block " + blockId);
    Deque<StackFrame> myFrames = frames.get();
    if (myFrames.isEmpty()) {
      // If no frame exists, create one
      myFrames.push(new StackFrame(blockId));
    } else {
      myFrames.getLast().push(blockId);
    }
    return myFrames.getLast();
  }

  /**
   * Exits a block within the current frame.
   * Pops the block ID from the current frame's block stack.
   */
  public static StackFrame exit(String blockId) {
    Log.d(LOG_TAG, "Exiting block " + blockId);
    Deque<StackFrame> myFrames = frames.get();
    if (myFrames.isEmpty()) {
      Log.w(LOG_TAG, "Attempted to exit block " + blockId + " but no frames exist");
      return null;
    }
    String topBlockId = myFrames.getLast().pop();
    if (topBlockId != null && !topBlockId.equals(blockId)) {
      Log.w(LOG_TAG, "Unexpected block id " + topBlockId + "; wanted to see: " + blockId);
    }
    return myFrames.isEmpty() ? null : myFrames.getLast();
  }

  /**
   * Pushes a new frame onto the stack (for procedure calls, etc.).
   */
  public static StackFrame pushFrame(String blockId) {
    Log.d(LOG_TAG, "Pushing new frame for block id " + blockId);
    StackFrame newFrame = new StackFrame(blockId);
    frames.get().push(newFrame);
    return newFrame;
  }

  /**
   * Pops the current frame from the stack.
   */
  public static StackFrame popFrame() {
    Log.d(LOG_TAG, "Popping stack frame");
    Deque<StackFrame> myFrames = frames.get();
    return myFrames.isEmpty() ? null : myFrames.pop();
  }

  /**
   * Clears all frames for the current thread.
   * Should be called when an event handler completes or an error occurs.
   */
  public static void clear() {
    Log.d(LOG_TAG, "Clearing all stack frames");
    frames.get().clear();
  }
}
