// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2025 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.youngandroid;

import com.google.appinventor.client.Images;
import com.google.appinventor.client.Ode;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.dom.client.Style;

public class DebugPanel extends VerticalPanel {
  private FlowPanel variablesPanel;
  private FlowPanel callStackPanel;
  private FlowPanel breakpointsPanel;

  static {
    exportMethodsToJavascript();
  }

  public DebugPanel() {
    setSpacing(0);
    setWidth("100%");

    VerticalPanel container = new VerticalPanel();
    container.setWidth("100%");
    container.setStylePrimaryName("ode-CollapsablePanel");

    // Variables
    variablesPanel = new FlowPanel();
    variablesPanel.getElement().setId("aiVariablesPanel");
    variablesPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    variablesPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    variablesPanel.getElement().getStyle().setProperty("maxHeight", "300px");
    DisclosurePanel variablesDisclosure = new DisclosurePanel("Variables");
    variablesDisclosure.setContent(variablesPanel);
    variablesDisclosure.setWidth("100%");
    variablesDisclosure.setOpen(true);
    variablesDisclosure.getElement().setId("aiVariablesSection");
    variablesDisclosure.getElement().getStyle().setProperty("display", "none");
    container.add(variablesDisclosure);

    // Call Stack
    callStackPanel = new FlowPanel();
    callStackPanel.getElement().setId("aiCallStackPanel");
    callStackPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    callStackPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    callStackPanel.getElement().getStyle().setProperty("maxHeight", "300px");
    DisclosurePanel callStackDisclosure = new DisclosurePanel("Call Stack");
    callStackDisclosure.setContent(callStackPanel);
    callStackDisclosure.setWidth("100%");
    callStackDisclosure.setOpen(true);
    callStackDisclosure.getElement().setId("aiCallStackSection");
    callStackDisclosure.getElement().getStyle().setProperty("display", "none");
    container.add(callStackDisclosure);

    // Breakpoints
    VerticalPanel breakpointsContent = new VerticalPanel();
    breakpointsContent.setWidth("100%");
    breakpointsContent.setSpacing(0);

    // Debug control buttons toolbar
    HorizontalPanel debugToolbar = new HorizontalPanel();
    debugToolbar.setSpacing(5);
    debugToolbar.getElement().setId("aiDebugToolbar");
    debugToolbar.getElement().getStyle().setProperty("padding", "5px");
    debugToolbar.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    debugToolbar.getElement().getStyle().setProperty("borderBottom", "1px solid #ccc");
    debugToolbar.getElement().getStyle().setProperty("display", "none"); // Hidden by default

    Images images = Ode.getImageBundle();

    // Continue button
    Image continueButton = new Image(images.debugContinue());
    continueButton.setTitle("Continue");
    continueButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    continueButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        sendDebugContinue();
      }
    });
    debugToolbar.add(continueButton);

    // Step Over button
    Image stepOverButton = new Image(images.debugStepOver());
    stepOverButton.setTitle("Step Over");
    stepOverButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    stepOverButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        sendDebugStepOver();
      }
    });
    debugToolbar.add(stepOverButton);

    // Step Down (Into) button
    Image stepDownButton = new Image(images.debugStepDown());
    stepDownButton.setTitle("Step Down");
    stepDownButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    stepDownButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        sendDebugStepDown();
      }
    });
    debugToolbar.add(stepDownButton);

    // Step Up (Out) button
    Image stepUpButton = new Image(images.debugStepUp());
    stepUpButton.setTitle("Step Up");
    stepUpButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    stepUpButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        sendDebugStepUp();
      }
    });
    debugToolbar.add(stepUpButton);

    // Stop button
    Image stopButton = new Image(images.debugStop());
    stopButton.setTitle("Stop");
    stopButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    stopButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        sendDebugStop();
      }
    });
    debugToolbar.add(stopButton);

    breakpointsContent.add(debugToolbar);

    breakpointsPanel = new FlowPanel();
    breakpointsPanel.getElement().setId("aiBreakpointsPanel");
    breakpointsPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    breakpointsPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    breakpointsPanel.getElement().getStyle().setProperty("maxHeight", "300px");

    com.google.gwt.user.client.ui.Label noBreakpointsMsg = new com.google.gwt.user.client.ui.Label("(No breakpoints set)");
    noBreakpointsMsg.getElement().setClassName("no-breakpoints-msg");
    noBreakpointsMsg.getElement().getStyle().setColor("#999");
    noBreakpointsMsg.getElement().getStyle().setProperty("padding", "5px");
    noBreakpointsMsg.getElement().getStyle().setProperty("fontStyle", "italic");
    breakpointsPanel.add(noBreakpointsMsg);

    breakpointsContent.add(breakpointsPanel);

    DisclosurePanel breakpointsDisclosure = new DisclosurePanel("Breakpoints");
    breakpointsDisclosure.setContent(breakpointsContent);
    breakpointsDisclosure.setWidth("100%");
    breakpointsDisclosure.setOpen(true);
    container.add(breakpointsDisclosure);

    add(container);
  }

  private static native void sendDebugContinue() /*-{
    if (top.Blockly && top.Blockly.ReplMgr && top.Blockly.ReplMgr.sendDebugContinue) {
      top.Blockly.ReplMgr.sendDebugContinue();
    } else {
      console.error('Blockly.ReplMgr.sendDebugContinue not available');
    }
  }-*/;

  private static native void sendDebugStepOver() /*-{
    if (top.Blockly && top.Blockly.ReplMgr && top.Blockly.ReplMgr.sendDebugStepOver) {
      top.Blockly.ReplMgr.sendDebugStepOver();
    } else {
      console.error('Blockly.ReplMgr.sendDebugStepOver not available');
    }
  }-*/;

  private static native void sendDebugStepDown() /*-{
    if (top.Blockly && top.Blockly.ReplMgr && top.Blockly.ReplMgr.sendDebugStepDown) {
      top.Blockly.ReplMgr.sendDebugStepDown();
    } else {
      console.error('Blockly.ReplMgr.sendDebugStepDown not available');
    }
  }-*/;

  private static native void sendDebugStepUp() /*-{
    if (top.Blockly && top.Blockly.ReplMgr && top.Blockly.ReplMgr.sendDebugStepUp) {
      top.Blockly.ReplMgr.sendDebugStepUp();
    } else {
      console.error('Blockly.ReplMgr.sendDebugStepUp not available');
    }
  }-*/;

  private static native void sendDebugStop() /*-{
    if (top.Blockly && top.Blockly.ReplMgr && top.Blockly.ReplMgr.sendDebugStop) {
      top.Blockly.ReplMgr.sendDebugStop();
    } else {
      console.error('Blockly.ReplMgr.sendDebugStop not available');
    }
  }-*/;

  private static native void exportMethodsToJavascript() /*-{
    top.DebugPanel_showDebugToolbar = function() {
      var toolbar = top.document.getElementById('aiDebugToolbar');
      if (toolbar) {
        toolbar.style.display = 'block';
      }
    };

    top.DebugPanel_hideDebugToolbar = function() {
      var toolbar = top.document.getElementById('aiDebugToolbar');
      if (toolbar) {
        toolbar.style.display = 'none';
      }
    };

    top.DebugPanel_showDebugPanels = function() {
      var v = top.document.getElementById('aiVariablesSection');
      if (v) v.style.display = '';
      var cs = top.document.getElementById('aiCallStackSection');
      if (cs) cs.style.display = '';
    };

    top.DebugPanel_hideDebugPanels = function() {
      var v = top.document.getElementById('aiVariablesSection');
      if (v) v.style.display = 'none';
      var cs = top.document.getElementById('aiCallStackSection');
      if (cs) cs.style.display = 'none';
    };

    top.DebugPanel_setVariables = function(variables, globalVariables) {
      var container = top.document.getElementById('aiVariablesPanel');
      if (!container) return;
      var doc = container.ownerDocument || top.document;
      container.innerHTML = '';
      var hasVars = variables && Object.keys(variables).length > 0;
      var hasGlobals = globalVariables && Object.keys(globalVariables).length > 0;

      var localsSection = doc.createElement('div');
      localsSection.style.padding = '5px';
      localsSection.style.fontWeight = 'bold';
      localsSection.innerText = 'Locals';
      localsSection.style.borderBottom = '1px solid #ddd';
      container.appendChild(localsSection);
      var localsContent = doc.createElement('div');
      localsContent.style.paddingLeft = '15px';
      if (!hasVars) {
        localsContent.style.color = '#999';
        localsContent.style.padding = '5px';
        localsContent.innerText = '(No local variables)';
      } else {
        for (var varName in variables) {
          if (variables.hasOwnProperty(varName)) {
            var varEntry = doc.createElement('div');
            varEntry.style.padding = '3px 5px';
            varEntry.style.fontFamily = 'monospace';
            varEntry.style.fontSize = '0.95em';
            varEntry.style.borderBottom = '1px solid #eee';
            var nameSpan = doc.createElement('span');
            nameSpan.innerText = varName;
            var valueSpan = doc.createElement('span');
            valueSpan.innerText = ' = ' + variables[varName];
            varEntry.appendChild(nameSpan);
            varEntry.appendChild(valueSpan);
            localsContent.appendChild(varEntry);
          }
        }
      }
      container.appendChild(localsContent);

      var globalsSection = doc.createElement('div');
      globalsSection.style.padding = '5px';
      globalsSection.style.fontWeight = 'bold';
      globalsSection.innerText = 'Globals';
      globalsSection.style.borderBottom = '1px solid #ddd';
      globalsSection.style.marginTop = '10px';
      container.appendChild(globalsSection);
      var globalsContent = doc.createElement('div');
      globalsContent.style.paddingLeft = '15px';
      if (!hasGlobals) {
        globalsContent.style.color = '#999';
        globalsContent.style.padding = '5px';
        globalsContent.innerText = '(No global variables tracked)';
      } else {
        for (var globalName in globalVariables) {
          if (globalVariables.hasOwnProperty(globalName)) {
            var globalEntry = doc.createElement('div');
            globalEntry.style.padding = '3px 5px';
            globalEntry.style.fontFamily = 'monospace';
            globalEntry.style.fontSize = '0.95em';
            globalEntry.style.borderBottom = '1px solid #eee';
            var nameSpan = doc.createElement('span');
            nameSpan.innerText = globalName;
            var valueSpan = doc.createElement('span');
            valueSpan.innerText = ' = ' + globalVariables[globalName];
            globalEntry.appendChild(nameSpan);
            globalEntry.appendChild(valueSpan);
            globalsContent.appendChild(globalEntry);
          }
        }
      }
      container.appendChild(globalsContent);
    };

    top.DebugPanel_setCallStack = function(stackTrace, errorMessage, globalVariables, isBreakpoint) {
      var container = top.document.getElementById('aiCallStackPanel');
      if (!container) {
        return;
      }

      var doc = container.ownerDocument || top.document;

      container.innerHTML = '';
      if (!stackTrace || stackTrace.length === 0) {
        var emptyMsg = doc.createElement('div');
        emptyMsg.style.padding = '10px';
        emptyMsg.style.color = '#999';
        emptyMsg.innerText = 'No active call stack';
        container.appendChild(emptyMsg);
        return;
      }

      if ((errorMessage && !isBreakpoint) || isBreakpoint) {
        var messageDiv = doc.createElement('div');
        messageDiv.style.padding = '5px';
        messageDiv.style.color = '#d32f2f';
        messageDiv.style.fontSize = '0.95em';
        messageDiv.style.borderBottom = '1px solid #ffcccc';
        messageDiv.style.backgroundColor = '#fff5f5';
        messageDiv.style.marginBottom = '5px';
        messageDiv.innerText = isBreakpoint ? 'Paused at Breakpoint' : 'Error: ' + errorMessage;
        container.appendChild(messageDiv);
      }

      var allVars = {};

      for (var i = stackTrace.length - 1; i >= 0; i--) {
        var frame = stackTrace[i];
        if (frame.blockIds && frame.blockIds.length > 0) {

          for (var j = 0; j < frame.blockIds.length; j++) {
            var blockId = frame.blockIds[j];
            var entry = doc.createElement('div');
            entry.style.padding = '5px 10px';
            entry.style.borderBottom = '1px solid #ddd';
            entry.style.cursor = 'pointer';
            entry.style.fontFamily = 'monospace';

            entry.innerText = '  at ' + getBreakpointBlockLabel(blockId);

            entry.setAttribute('data-block-id', blockId);
            entry.onclick = function() {
              var bid = this.getAttribute('data-block-id');
              try {
                var ws = top.Blockly.common.getMainWorkspace();
                if (ws) {
                  var warningHandler = ws.getWarningHandler();
                  if (warningHandler) {
                    if (ws.currentDebugHighlightTimeout) {
                      top.clearTimeout(ws.currentDebugHighlightTimeout);
                      ws.currentDebugHighlightTimeout = null;
                    }
                    if (ws.currentDebugBlockId && ws.currentDebugCollapseStack) {
                      warningHandler.unHighlightBlock_(ws.currentDebugBlockId, ws.currentDebugCollapseStack);
                    }
                    ws.currentDebugCollapseStack = warningHandler.highlightBlock_(bid);
                    ws.currentDebugBlockId = bid;
                    ws.currentDebugHighlightTimeout = top.setTimeout(function() {
                      try {
                        if (ws.currentDebugBlockId && ws.currentDebugCollapseStack) {
                          warningHandler.unHighlightBlock_(ws.currentDebugBlockId, ws.currentDebugCollapseStack);
                          ws.currentDebugBlockId = null;
                          ws.currentDebugCollapseStack = null;
                        }
                      } catch (e) {}
                      ws.currentDebugHighlightTimeout = null;
                    }, 3000);
                  }
                }
              } catch (e) {
                console.error('Error highlighting block:', e);
              }
            };
            entry.onmouseover = function() {
              this.style.backgroundColor = '#e0e0e0';
            };
            entry.onmouseout = function() {
              this.style.backgroundColor = '';
            };
            container.appendChild(entry);
          }

          if (frame.vars && Object.keys(frame.vars).length > 0) {
            for (var varName in frame.vars) {
              if (frame.vars.hasOwnProperty(varName)) {
                allVars[varName] = frame.vars[varName];
              }
            }
          }
        }
      }

      top.DebugPanel_setVariables(allVars, globalVariables || {});
    };

    top.DebugPanel_clearCallStack = function() {
      var container = top.document.getElementById('aiCallStackPanel');
      if (!container) return;
      var doc = container.ownerDocument || top.document;
      container.innerHTML = '';
      var emptyMsg = doc.createElement('div');
      emptyMsg.style.padding = '10px';
      emptyMsg.innerText = 'No active call stack';
      container.appendChild(emptyMsg);

      try {
        var ws = top.Blockly.common.getMainWorkspace();
        if (ws) {
          if (ws.currentDebugHighlightTimeout) {
            top.clearTimeout(ws.currentDebugHighlightTimeout);
            ws.currentDebugHighlightTimeout = null;
          }
          if (ws.currentDebugBlockId && ws.currentDebugCollapseStack) {
            var warningHandler = ws.getWarningHandler();
            if (warningHandler) {
              warningHandler.unHighlightBlock_(ws.currentDebugBlockId, ws.currentDebugCollapseStack);
              ws.currentDebugBlockId = null;
              ws.currentDebugCollapseStack = null;
            }
          }
        }
      } catch (e) {
        console.error('Error clearing highlight:', e);
      }

      top.DebugPanel_setVariables({}, {});
    };

    // ── Breakpoints panel ────────────────────────────────────────────────

    var getBreakpointBlockLabel = function(blockId) {
      var label = blockId;
      try {
        var ws = top.Blockly.common.getMainWorkspace();
        if (ws) {
          var block = ws.getBlockById(blockId);
          if (block) {
            var blockType = block.type || 'unknown';
            if (block.type === 'component_event') {
              var compSel = block.getFieldValue && block.getFieldValue('COMPONENT_SELECTOR');
              label = (compSel || 'unknown') + '.' + (block.eventName || 'unknown') + ' event';
            } else if (block.type === 'procedures_defnoreturn' || block.type === 'procedures_defreturn') {
              label = 'procedure "' + (block.getFieldValue && block.getFieldValue('NAME') || 'unknown') + '"';
            } else if (block.type === 'procedures_callnoreturn' || block.type === 'procedures_callreturn') {
              label = 'call "' + (block.getFieldValue && block.getFieldValue('PROCNAME') || 'unknown') + '"';
            } else if (block.type && block.type.indexOf('component_set_get') === 0) {
              label = 'set ' + (block.instanceName || block.typeName || 'unknown') + '.' + (block.propertyName || 'unknown');
            } else if (block.type && block.type.indexOf('component_method') === 0) {
              label = (block.instanceName || block.typeName || 'unknown') + '.' + (block.methodName || 'unknown');
            } else if (block.type && block.type.indexOf('component_') === 0) {
              label = blockType.replace('component_', '').replace(/_/g, ' ') + ' ' + (block.instanceName || block.typeName || 'unknown');
            } else {
              label = blockType.replace(/_/g, ' ');
            }
          }
        }
      } catch (e) {}
      return label;
    };

    top.DebugPanel_addBreakpoint = function(blockId) {
      var container = top.document.getElementById('aiBreakpointsPanel');
      if (!container) return;
      if (container.querySelector('[data-breakpoint-id="' + blockId + '"]')) return;

      var doc = container.ownerDocument || top.document;
      var emptyMsg = container.querySelector('.no-breakpoints-msg');
      if (emptyMsg) emptyMsg.parentNode.removeChild(emptyMsg);

      var entry = doc.createElement('div');
      entry.style.padding = '4px 8px';
      entry.style.borderBottom = '1px solid #ddd';
      entry.style.cursor = 'pointer';
      entry.style.fontFamily = 'monospace';
      entry.style.fontSize = '1em';
      entry.style.display = 'flex';
      entry.style.justifyContent = 'space-between';
      entry.style.alignItems = 'center';
      entry.setAttribute('data-breakpoint-id', blockId);

      var labelSpan = doc.createElement('span');
      labelSpan.innerText = '\u25cf  ' + getBreakpointBlockLabel(blockId);
      labelSpan.style.color = '#c62828';
      entry.appendChild(labelSpan);

      // × remove button
      var xBtn = doc.createElement('span');
      xBtn.innerText = '\u00d7';
      xBtn.title = 'Remove breakpoint';
      xBtn.style.color = '#aaa';
      xBtn.style.fontSize = '1.5em';
      xBtn.style.padding = '0 4px';
      xBtn.style.cursor = 'pointer';
      xBtn.style.flexShrink = '0';
      xBtn.onmouseover = function() { this.style.color = '#d32f2f'; };
      xBtn.onmouseout  = function() { this.style.color = '#aaa'; };
      xBtn.onclick = function(e) {
        e.stopPropagation();
        try {
          var ws = top.Blockly.common.getMainWorkspace();
          if (ws) {
            var b = ws.getBlockById(blockId);
            if (b) {
              b.removeIcon(top.AI.BreakpointIcon.TYPE);
              top.Blockly.BlocklyEditor.saveBreakpoints(ws);
            }
          }
          if (top.Blockly.ReplMgr && top.Blockly.ReplMgr.notifyBreakpointRemoved) {
            top.Blockly.ReplMgr.notifyBreakpointRemoved(blockId);
          }
        } catch (err) {
          console.error('Error removing breakpoint from panel:', err);
        }
      };
      entry.appendChild(xBtn);

      entry.onclick = function() {
        try {
          var ws = top.Blockly.common.getMainWorkspace();
          if (ws) {
            var warningHandler = ws.getWarningHandler();
            if (warningHandler) {
              if (ws.currentDebugHighlightTimeout) {
                top.clearTimeout(ws.currentDebugHighlightTimeout);
                ws.currentDebugHighlightTimeout = null;
              }
              if (ws.currentDebugBlockId && ws.currentDebugCollapseStack) {
                warningHandler.unHighlightBlock_(ws.currentDebugBlockId, ws.currentDebugCollapseStack);
              }
              ws.currentDebugCollapseStack = warningHandler.highlightBlock_(blockId);
              ws.currentDebugBlockId = blockId;
              ws.currentDebugHighlightTimeout = top.setTimeout(function() {
                try {
                  if (ws.currentDebugBlockId && ws.currentDebugCollapseStack) {
                    warningHandler.unHighlightBlock_(ws.currentDebugBlockId, ws.currentDebugCollapseStack);
                    ws.currentDebugBlockId = null;
                    ws.currentDebugCollapseStack = null;
                  }
                } catch (e) {}
                ws.currentDebugHighlightTimeout = null;
              }, 3000);
            }
          }
        } catch (e) {
          console.error('Error highlighting block from breakpoints panel:', e);
        }
      };
      entry.onmouseover = function() { this.style.backgroundColor = '#fce4e4'; };
      entry.onmouseout  = function() { this.style.backgroundColor = ''; };

      container.appendChild(entry);
    };

    top.DebugPanel_removeBreakpoint = function(blockId) {
      var container = top.document.getElementById('aiBreakpointsPanel');
      if (!container) return;
      var entry = container.querySelector('[data-breakpoint-id="' + blockId + '"]');
      if (entry) entry.parentNode.removeChild(entry);
      // Show empty-state message when no breakpoints remain
      if (!container.querySelector('[data-breakpoint-id]')) {
        var doc = container.ownerDocument || top.document;
        var emptyDiv = doc.createElement('div');
        emptyDiv.className = 'no-breakpoints-msg';
        emptyDiv.style.color = '#999';
        emptyDiv.style.padding = '5px';
        emptyDiv.style.fontStyle = 'italic';
        emptyDiv.innerText = '(No breakpoints set)';
        container.appendChild(emptyDiv);
      }
    };

  }-*/;
}
