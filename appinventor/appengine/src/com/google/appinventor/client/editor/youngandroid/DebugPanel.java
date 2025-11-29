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
        onContinue();
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
        onStepOver();
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
        onStepDown();
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
        onStepUp();
      }
    });
    debugToolbar.add(stepUpButton);

    breakpointsContent.add(debugToolbar);

    breakpointsPanel = new FlowPanel();
    breakpointsPanel.getElement().setId("aiBreakpointsPanel");
    breakpointsPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    breakpointsPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    breakpointsPanel.getElement().getStyle().setProperty("maxHeight", "300px");
    breakpointsContent.add(breakpointsPanel);

    DisclosurePanel breakpointsDisclosure = new DisclosurePanel("Breakpoints");
    breakpointsDisclosure.setContent(breakpointsContent);
    breakpointsDisclosure.setWidth("100%");
    breakpointsDisclosure.setOpen(true);
    container.add(breakpointsDisclosure);

    add(container);
  }

  private void onContinue() {
    System.out.println("Continue clicked");
    sendDebugContinue();
  }

  private void onStepOver() {
    System.out.println("Step Over clicked");
    sendDebugStepOver();
  }

  private void onStepDown() {
    System.out.println("Step Into clicked");
    sendDebugStepDown();
  }

  private void onStepUp() {
    System.out.println("Step Out clicked");
    sendDebugStepUp();
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

      var title = doc.createElement('div');
      title.style.padding = '5px';
      title.style.fontWeight = 'bold';
      title.style.color = '#d32f2f';
      container.appendChild(title);

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

            var blockInfo = 'Block ID: ' + blockId;
            try {
              var workspace = top.Blockly.common.getMainWorkspace();
              if (workspace) {
                var block = workspace.getBlockById(blockId);
                if (block) {
                  var blockType = block.type || 'unknown';
                  var blockLabel = '';
                  if (block.type === 'component_event') {
                    var componentName = block.getFieldValue && block.getFieldValue('COMPONENT_SELECTOR');
                    var eventName = block.eventName || 'unknown';
                    blockLabel = (componentName || 'unknown') + '.' + eventName + ' event';
                  } else if (block.type === 'procedures_defnoreturn' || block.type === 'procedures_defreturn') {
                    var procName = block.getFieldValue && block.getFieldValue('NAME');
                    blockLabel = 'procedure "' + (procName || 'unknown') + '"';
                  } else if (block.type === 'procedures_callnoreturn' || block.type === 'procedures_callreturn') {
                    var procCallName = block.getFieldValue && block.getFieldValue('PROCNAME');
                    blockLabel = 'call "' + (procCallName || 'unknown') + '"';
                  } else if (block.type && block.type.indexOf('component_set_get') === 0) {
                    var comp = block.instanceName || block.typeName || 'unknown';
                    var prop = block.propertyName || 'unknown';
                    blockLabel = 'set ' + comp + '.' + prop;
                  } else if (block.type && block.type.indexOf('component_method') === 0) {
                    var compName = block.instanceName || block.typeName || 'unknown';
                    var methodName = block.methodName || 'unknown';
                    blockLabel = compName + '.' + methodName;
                  } else if (block.type && block.type.indexOf('component_') === 0) {
                    var cName = block.instanceName || block.typeName || 'unknown';
                    blockLabel = blockType.replace('component_', '').replace(/_/g, ' ') + ' ' + cName;
                  } else {
                    blockLabel = blockType.replace(/_/g, ' ');
                  }
                  blockInfo = blockLabel;
                }
              }
            } catch (e) {
              // If we can't get block info, just use the ID
            }
            entry.innerText = '  at ' + blockInfo;

            entry.setAttribute('data-block-id', blockId);
            entry.onclick = function() {
              var bid = this.getAttribute('data-block-id');
              try {
                var ws = top.Blockly.common.getMainWorkspace();
                if (ws) {
                  var warningHandler = ws.getWarningHandler();
                  if (warningHandler) {
                    if (ws.currentDebugBlockId && ws.currentDebugCollapseStack) {
                      warningHandler.unHighlightBlock_(ws.currentDebugBlockId, ws.currentDebugCollapseStack);
                    }
                    ws.currentDebugCollapseStack = warningHandler.highlightBlock_(bid);
                    ws.currentDebugBlockId = bid;
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
        if (ws && ws.currentDebugBlockId && ws.currentDebugCollapseStack) {
          var warningHandler = ws.getWarningHandler();
          if (warningHandler) {
            warningHandler.unHighlightBlock_(ws.currentDebugBlockId, ws.currentDebugCollapseStack);
            ws.currentDebugBlockId = null;
            ws.currentDebugCollapseStack = null;
          }
        }
      } catch (e) {
        console.error('Error clearing highlight:', e);
      }

      top.DebugPanel_setVariables({}, {});
    };

  }-*/;
}
