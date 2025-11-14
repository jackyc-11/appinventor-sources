// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2017 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime.errors;

import android.util.Log;
import com.google.appinventor.components.runtime.util.StackFrame;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception wrapper that captures the block stack trace when an error occurs.
 * This allows us to show users which blocks were executing when the error happened.
 */
public class WrappedException extends Exception {
  private static final String LOG_TAG = "WrappedException";

  // The block-level stack trace captured when this exception was created
  private List<StackFrame> stackTrace;

  // The original error type (if available)
  private String errorType;

  /**
   * Wraps an exception with the current block stack trace.
   */
  public WrappedException(Exception e) {
    super(e);
    this.stackTrace = captureStack();
    logStackTrace();
  }

  /**
   * Creates a wrapped exception with a custom message and error type.
   */
  public WrappedException(String message, String errorType) {
    super(message);
    this.errorType = errorType;
    this.stackTrace = captureStack();
    logStackTrace();
  }

  /**
   * Gets the error type (if available).
   */
  public String getErrorType() {
    return errorType;
  }

  /**
   * Gets the captured block stack trace.
   */
  public List<StackFrame> getBlockStackTrace() {
    return Collections.unmodifiableList(stackTrace);
  }

  /**
   * Captures the current block stack by cloning all frames.
   */
  private static List<StackFrame> captureStack() {
    List<StackFrame> frames = new ArrayList<>();
    for (StackFrame frame : StackFrame.get()) {
      try {
        frames.add((StackFrame) frame.clone());
      } catch (CloneNotSupportedException e) {
        Log.e(LOG_TAG, "Failed to clone stack frame", e);
      }
    }
    return frames;
  }

  /**
   * Logs the stack trace for debugging purposes.
   */
  private void logStackTrace() {
    try {
      Log.d(LOG_TAG, "Captured stack trace with " + stackTrace.size() + " frames");
      for (StackFrame f : stackTrace) {
        Log.d(LOG_TAG, "  Frame: " + f.toJson().toString());
      }
    } catch (JSONException ex) {
      Log.e(LOG_TAG, "Error serializing stack frame to JSON", ex);
    }
  }
}
