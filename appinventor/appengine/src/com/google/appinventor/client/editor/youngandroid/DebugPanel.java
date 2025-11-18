// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2025 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.youngandroid;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
    setHeight("100%");

    getElement().getStyle().setProperty("borderSpacing", "0");
    getElement().setAttribute("cellpadding", "0");
    getElement().setAttribute("cellspacing", "0");

    HTML variablesHeader = new HTML("<div style='background-color: #f0f0f0; padding: 5px; font-weight: bold; border-bottom: 1px solid #ccc; margin: 0;'>Variables</div>");
    add(variablesHeader);
    variablesPanel = new FlowPanel();
    variablesPanel.getElement().setId("aiVariablesPanel");
    variablesPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    variablesPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    variablesPanel.getElement().getStyle().setProperty("borderBottom", "2px solid #ccc");
    variablesPanel.setHeight("150px");
    add(variablesPanel);
    setCellHeight(variablesPanel, "150px");

    HTML callStackHeader = new HTML("<div style='background-color: #f0f0f0; padding: 5px; font-weight: bold; border-bottom: 1px solid #ccc; margin: 0;'>Call Stack</div>");
    add(callStackHeader);
    callStackPanel = new FlowPanel();
    callStackPanel.getElement().setId("aiCallStackPanel");
    callStackPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    callStackPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    callStackPanel.getElement().getStyle().setProperty("borderBottom", "2px solid #ccc");
    callStackPanel.setHeight("200px");
    add(callStackPanel);
    setCellHeight(callStackPanel, "200px");

    HTML breakpointsHeader = new HTML("<div style='background-color: #f0f0f0; padding: 5px; font-weight: bold; border-bottom: 1px solid #ccc; margin: 0;'>Breakpoints</div>");
    add(breakpointsHeader);
    breakpointsPanel = new FlowPanel();
    breakpointsPanel.getElement().setId("aiBreakpointsPanel");
    breakpointsPanel.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
    breakpointsPanel.getElement().getStyle().setProperty("backgroundColor", "#f9f9f9");
    breakpointsPanel.setHeight("150px");
    add(breakpointsPanel);
    setCellHeight(breakpointsPanel, "150px");
  }

  private static native void exportMethodsToJavascript() /*-{
    top.DebugPanel_setVariables = function(variables) {
      var container = top.document.getElementById('aiVariablesPanel');
      if (!container) return;
      var doc = container.ownerDocument || top.document;
      container.innerHTML = '';
      var hasVars = variables && Object.keys(variables).length > 0;

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
      globalsContent.style.color = '#999';
      globalsContent.style.padding = '5px';
      globalsContent.innerText = '(No global variables tracked)';
      container.appendChild(globalsContent);
    };

    top.DebugPanel_setCallStack = function(stackTrace, errorMessage) {
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

      if (errorMessage) {
        var errorDiv = doc.createElement('div');
        errorDiv.style.padding = '5px';
        errorDiv.style.color = '#d32f2f';
        errorDiv.style.fontSize = '0.95em';
        errorDiv.style.borderBottom = '1px solid #ffcccc';
        errorDiv.style.backgroundColor = '#fff5f5';
        errorDiv.style.marginBottom = '5px';
        errorDiv.innerText = 'Error: ' + errorMessage;
        container.appendChild(errorDiv);
      }

      var title = doc.createElement('div');
      title.style.padding = '5px';
      title.style.fontWeight = 'bold';
      title.style.color = '#d32f2f';
      title.innerText = 'Error Stack Trace:';
      container.appendChild(title);

      var allVars = {};

      for (var i = 0; i < stackTrace.length; i++) {
        var frame = stackTrace[i];
        if (frame.blockIds && frame.blockIds.length > 0) {

          for (var j = frame.blockIds.length - 1; j >= 0; j--) {
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
                  var blk = ws.getBlockById(bid);
                  if (blk) {
                    blk.select();

                    ws.centerOnBlock(bid);
                  }
                }
              } catch (e) {
                console.error('Error selecting block:', e);
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

      top.DebugPanel_setVariables(allVars);
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

      top.DebugPanel_setVariables({});
    };

    top.DebugPanel_setBreakpoints = function(breakpoints) {
      var container = top.document.getElementById('aiBreakpointsPanel');
      if (!container) return;
      var doc = container.ownerDocument || top.document;
      container.innerHTML = '';
      if (!breakpoints || breakpoints.length === 0) {
        var emptyMsg = doc.createElement('div');
        emptyMsg.style.padding = '10px';
        emptyMsg.innerText = '(No breakpoints set)';
        container.appendChild(emptyMsg);
        return;
      }

      for (var i = 0; i < breakpoints.length; i++) {
        var bp = breakpoints[i];
        var entry = doc.createElement('div');
        entry.style.padding = '5px 10px';
        entry.style.borderBottom = '1px solid #ddd';
        entry.style.cursor = 'pointer';
        entry.style.fontFamily = 'monospace';
        entry.innerText = '● ' + (bp.label || bp.blockId);
        entry.onclick = (function(blockId) {
          return function() {
            try {
              var ws = top.Blockly.common.getMainWorkspace();
              if (ws) {
                var blk = ws.getBlockById(blockId);
                if (blk) {
                  blk.select();
                  ws.centerOnBlock(blockId);
                }
              }
            } catch (e) {
              console.error('Error selecting block:', e);
            }
          };
        })(bp.blockId);
        entry.onmouseover = function() {
          this.style.backgroundColor = '#e0e0e0';
        };
        entry.onmouseout = function() {
          this.style.backgroundColor = '';
        };
        container.appendChild(entry);
      }
    };

  }-*/;
}
