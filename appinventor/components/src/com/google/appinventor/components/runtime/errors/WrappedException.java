package com.google.appinventor.components.runtime.errors;

import android.util.Log;
import com.google.appinventor.components.runtime.util.StackFrame;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WrappedException extends Exception {
  private static final String LOG_TAG = "WrappedException";

  private List<StackFrame> stackTrace;
  private String errorType;

  public WrappedException(Exception e) {
    super(e);
    this.stackTrace = captureStack();
    logStackTrace();
  }

  public WrappedException(String message, String errorType) {
    super(message);
    this.errorType = errorType;
    this.stackTrace = captureStack();
    logStackTrace();
  }

  public String getErrorType() {
    return errorType;
  }

  public List<StackFrame> getBlockStackTrace() {
    return Collections.unmodifiableList(stackTrace);
  }

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