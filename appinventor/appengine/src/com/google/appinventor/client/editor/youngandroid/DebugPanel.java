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

  static {
    exportMethodsToJavascript();
  }

  public DebugPanel() {
    setSpacing(0);
    setWidth("100%");

    VerticalPanel container = new VerticalPanel();
    container.setWidth("100%");
    container.setStylePrimaryName("ode-CollapsablePanel");

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

    container.add(debugToolbar);

    // Variables
    FlowPanel variablesPanel = new FlowPanel();
    variablesPanel.getElement().setId("aiVariablesPanel");
    variablesPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    variablesPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    variablesPanel.getElement().getStyle().setProperty("maxHeight", "300px");
    DisclosurePanel variablesDisclosure = new DisclosurePanel("Variables");
    variablesDisclosure.setContent(variablesPanel);
    variablesDisclosure.setWidth("100%");
    variablesDisclosure.setOpen(true);
    variablesDisclosure.getElement().setId("aiVariablesSection");
    variablesDisclosure.getElement().getStyle().setProperty("border", "1px solid #ccc");
    variablesDisclosure.getElement().getStyle().setProperty("display", "none");
    container.add(variablesDisclosure);

    // Properties
    FlowPanel propertiesPanel = new FlowPanel();
    propertiesPanel.getElement().setId("aiPropertiesPanel");
    propertiesPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    propertiesPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    propertiesPanel.getElement().getStyle().setProperty("maxHeight", "300px");
    DisclosurePanel propertiesDisclosure = new DisclosurePanel("Properties");
    propertiesDisclosure.setContent(propertiesPanel);
    propertiesDisclosure.setWidth("100%");
    propertiesDisclosure.setOpen(false);
    propertiesDisclosure.getElement().setId("aiPropertiesSection");
    propertiesDisclosure.getElement().getStyle().setProperty("border", "1px solid #ccc");
    propertiesDisclosure.getElement().getStyle().setProperty("display", "none");
    container.add(propertiesDisclosure);

    // Call Stack
    FlowPanel callStackPanel = new FlowPanel();
    callStackPanel.getElement().setId("aiCallStackPanel");
    callStackPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    callStackPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    callStackPanel.getElement().getStyle().setProperty("maxHeight", "300px");
    DisclosurePanel callStackDisclosure = new DisclosurePanel("Call Stack");
    callStackDisclosure.setContent(callStackPanel);
    callStackDisclosure.setWidth("100%");
    callStackDisclosure.setOpen(true);
    callStackDisclosure.getElement().setId("aiCallStackSection");
    callStackDisclosure.getElement().getStyle().setProperty("border", "1px solid #ccc");
    callStackDisclosure.getElement().getStyle().setProperty("display", "none");
    container.add(callStackDisclosure);

    // Breakpoints
    FlowPanel breakpointsPanel = new FlowPanel();
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

    DisclosurePanel breakpointsDisclosure = new DisclosurePanel("Breakpoints");
    breakpointsDisclosure.setContent(breakpointsPanel);
    breakpointsDisclosure.setWidth("100%");
    breakpointsDisclosure.setOpen(true);
    breakpointsDisclosure.getElement().getStyle().setProperty("border", "1px solid #ccc");
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
      var v = top.document.getElementById('aiVariablesSection');
      if (v) v.style.display = '';
      var p = top.document.getElementById('aiPropertiesSection');
      if (p) p.style.display = '';
      var cs = top.document.getElementById('aiCallStackSection');
      if (cs) cs.style.display = '';
    };

    top.DebugPanel_hideDebugToolbar = function() {
      var toolbar = top.document.getElementById('aiDebugToolbar');
      if (toolbar) {
        toolbar.style.display = 'none';
      }
      var v = top.document.getElementById('aiVariablesSection');
      if (v) v.style.display = 'none';
      var p = top.document.getElementById('aiPropertiesSection');
      if (p) p.style.display = 'none';
      var cs = top.document.getElementById('aiCallStackSection');
      if (cs) cs.style.display = 'none';
    };

    var makeEntryRow = function(name, value) {
      var entry = top.document.createElement('div');
      entry.style.padding = '3px 5px';
      entry.style.fontFamily = 'monospace';
      entry.style.fontSize = '0.95em';
      entry.style.borderBottom = '1px solid #eee';
      entry.style.wordBreak = 'break-all';
      entry.style.overflowWrap = 'break-word';
      var nameSpan = top.document.createElement('span');
      nameSpan.innerText = name;
      var valueSpan = top.document.createElement('span');
      valueSpan.innerText = ' = ' + value;
      entry.appendChild(nameSpan);
      entry.appendChild(valueSpan);
      return entry;
    };

    var makeEmptyMsg = function(msg) {
      var content = top.document.createElement('div');
      content.style.color = '#999';
      content.style.padding = '5px';
      content.innerText = msg;
      return content;
    };

    top.DebugPanel_setVariables = function(variables, globalVariables, returnVariables) {
      var container = top.document.getElementById('aiVariablesPanel');
      if (!container) return;
      container.innerHTML = '';

      var renderSection = function(label, vars, emptyMsg) {
        var header = top.document.createElement('div');
        header.style.padding = '5px';
        header.style.fontWeight = 'bold';
        header.style.borderBottom = '1px solid #ddd';
        header.style.cursor = 'pointer';
        header.style.userSelect = 'none';
        header.innerText = label;

        var content = top.document.createElement('div');
        content.style.paddingLeft = '15px';
        content.style.display = 'block';
        if (!vars || Object.keys(vars).length === 0) {
          content.appendChild(makeEmptyMsg(emptyMsg));
        } else {
          for (var k in vars) {
            if (vars.hasOwnProperty(k)) content.appendChild(makeEntryRow(k, vars[k]));
          }
        }

        header.onclick = (function(d) {
          return function() {
            d.style.display = d.style.display === 'none' ? 'block' : 'none';
          };
        })(content);

        container.appendChild(header);
        container.appendChild(content);
      };

      renderSection('Locals', variables, '(No local variables)');
      renderSection('Return', returnVariables, '(No return values)');
      renderSection('Globals', globalVariables, '(No global variables)');
    };

    top.DebugPanel_setProperties = function(componentProperties) {
      var container = top.document.getElementById('aiPropertiesPanel');
      if (!container) return;

      var openComps = {};
      var existingHeaders = container.querySelectorAll('[data-comp-name]');
      for (var h = 0; h < existingHeaders.length; h++) {
        var eh = existingHeaders[h];
        var sib = eh.nextSibling;
        if (sib && sib.style.display !== 'none') {
          openComps[eh.getAttribute('data-comp-name')] = true;
        }
      }

      container.innerHTML = '';

      var designerProps = (top.Blockly && top.Blockly.BlocklyEditor)
          ? (top.Blockly.BlocklyEditor.designerProperties || {})
          : {};

      var filtered = {};
      for (var compName in componentProperties) {
        if (!componentProperties.hasOwnProperty(compName)) continue;
        var runtimeProps = componentProperties[compName];
        if (!runtimeProps) continue;
        var designerCompProps = designerProps[compName] || {};
        var diffProps = {};
        for (var propName in runtimeProps) {
          if (!runtimeProps.hasOwnProperty(propName)) continue;
          var runtimeVal = String(runtimeProps[propName]);
          var designerVal = designerCompProps.hasOwnProperty(propName)
              ? String(designerCompProps[propName]) : null;
          if (designerVal === null || runtimeVal.toLowerCase() !== designerVal.toLowerCase()) {
            diffProps[propName] = runtimeProps[propName];
          }
        }
        if (Object.keys(diffProps).length > 0) {
          filtered[compName] = diffProps;
        }
      }
      componentProperties = filtered;

      if (Object.keys(componentProperties).length === 0) {
        var emptyMsg = makeEmptyMsg('(No modified properties)');
        emptyMsg.style.fontStyle = 'italic';
        container.appendChild(emptyMsg);
        return;
      }

      var designerOrder = Object.keys(designerProps);
      var compNames = Object.keys(componentProperties);
      compNames.sort(function(a, b) {
        var idxA = designerOrder.indexOf(a);
        var idxB = designerOrder.indexOf(b);
        if (idxA === -1) idxA = designerOrder.length;
        if (idxB === -1) idxB = designerOrder.length;
        return idxA - idxB;
      });

      for (var i = 0; i < compNames.length; i++) {
        var compName = compNames[i];
        var props = componentProperties[compName];
        if (!props || Object.keys(props).length === 0) continue;

        var header = top.document.createElement('div');
        header.style.padding = '5px';
        header.style.fontWeight = 'bold';
        header.style.borderBottom = '1px solid #ddd';
        header.style.cursor = 'pointer';
        header.style.userSelect = 'none';
        header.setAttribute('data-comp-name', compName);
        header.innerText = compName;

        var propsDiv = top.document.createElement('div');
        propsDiv.style.paddingLeft = '15px';
        propsDiv.style.display = openComps[compName] ? 'block' : 'none';
        for (var propName in props) {
          if (props.hasOwnProperty(propName)) {
            propsDiv.appendChild(makeEntryRow(propName, props[propName]));
          }
        }

        (function(h, d) {
          h.onclick = function() {
            d.style.display = d.style.display === 'none' ? 'block' : 'none';
          };
        })(header, propsDiv);

        container.appendChild(header);
        container.appendChild(propsDiv);
      }
    };

    var getBreakpointBlockLabel = function(blockId) {
      try {
        var ws = top.Blockly.common.getMainWorkspace();
        if (!ws) {
          return blockId;
        }
        var block = ws.getBlockById(blockId);
        if (!block) {
          return blockId;
        }
        var t = block.type;
        if (t === 'component_event') {
          var compSel = block.getFieldValue && block.getFieldValue('COMPONENT_SELECTOR');
          if (compSel) {
            return compSel + '.' + block.eventName;
          }
          return 'any ' + block.typeName + '.' + block.eventName;
        }
        if (t === 'procedures_defnoreturn' || t === 'procedures_defreturn') {
          return (block.getFieldValue && block.getFieldValue('NAME'));
        }
        if (t === 'procedures_callnoreturn' || t === 'procedures_callreturn') {
          return 'call ' + (block.getFieldValue && block.getFieldValue('PROCNAME'));
        }
        if (t === 'lexical_variable_get') {
          var varName = block.getFieldValue && block.getFieldValue('VAR');
          return 'get ' + (varName || 'variable');
        }
        if (t === 'lexical_variable_set') {
          var varName = block.getFieldValue && block.getFieldValue('VAR');
          return 'set ' + (varName || 'variable');
        }
        if (t.indexOf('component_set_get') === 0) {
          return block.setOrGet + ' ' + (block.instanceName || block.typeName) + '.' + block.propertyName;
        }
        if (t.indexOf('component_method') === 0) {
          return (block.instanceName || block.typeName) + '.' + block.methodName;
        }
        if (t.indexOf('component_') === 0) {
          return t.replace('component_', '').replace(/_/g, ' ') + ' ' + (block.instanceName || block.typeName);
        }
        return t.replace(/_/g, ' ');
      } catch (e) {
        return blockId;
      }
    };

    var highlightBlockTemporarily = function(blockId) {
      try {
        var ws = top.Blockly.common.getMainWorkspace();
        if (!ws) return;
        var warningHandler = ws.getWarningHandler();
        if (!warningHandler) return;
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
      } catch (e) {
        console.error('Error highlighting block:', e);
      }
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

      var globalsForPanel = globalVariables || {};

      for (var i = 0; i < stackTrace.length; i++) {
        var frame = stackTrace[i];
        if (frame.blockIds && frame.blockIds.length > 0) {
          var blockId = frame.blockIds[frame.blockIds.length - 1];
          var frameLocals = (frame.vars && typeof frame.vars === 'object') ? frame.vars : {};
          var frameReturnVals = (frame.returnValues && typeof frame.returnValues === 'object') ? frame.returnValues : {};
          var entry = doc.createElement('div');
          entry.style.padding = '5px 10px';
          entry.style.borderBottom = '1px solid #ddd';
          entry.style.cursor = 'pointer';
          entry.style.fontFamily = 'monospace';
          entry.innerText = getBreakpointBlockLabel(blockId);
          entry.setAttribute('data-block-id', blockId);
          (function(bid, localsForFrame, returnValsForFrame) {
            entry.onclick = function() {
              try {
                top.DebugPanel_setVariables(localsForFrame, globalsForPanel, returnValsForFrame);
              } catch (eVars) {
                console.error('Error updating variables for stack frame:', eVars);
              }
              highlightBlockTemporarily(bid);
            };
          })(blockId, frameLocals, frameReturnVals);
          entry.onmouseover = function() {
            this.style.backgroundColor = '#e0e0e0';
          };
          entry.onmouseout = function() {
            this.style.backgroundColor = '';
          };
          container.appendChild(entry);
        }
      }

      var innermostVars = (stackTrace.length > 0 && stackTrace[0].vars) ? stackTrace[0].vars : {};
      var innermostReturnVals = (stackTrace.length > 0 && stackTrace[0].returnValues) ? stackTrace[0].returnValues : {};
      top.DebugPanel_setVariables(innermostVars, globalsForPanel, innermostReturnVals);
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

      top.DebugPanel_setVariables({}, {}, {});
    };

    // ── Breakpoints panel ────────────────────────────────────────────────

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

      entry.onclick = function() { highlightBlockTemporarily(blockId); };
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

    top.DebugPanel_clearBreakpoints = function() {
      var container = top.document.getElementById('aiBreakpointsPanel');
      if (!container) return;
      container.innerHTML = '';
      var doc = container.ownerDocument || top.document;
      var emptyDiv = doc.createElement('div');
      emptyDiv.className = 'no-breakpoints-msg';
      emptyDiv.style.color = '#999';
      emptyDiv.style.padding = '5px';
      emptyDiv.style.fontStyle = 'italic';
      emptyDiv.innerText = '(No breakpoints set)';
      container.appendChild(emptyDiv);
    };

  }-*/;
}
