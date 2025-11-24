// -*- mode: java; c-basic-offset: 2; -*-
/**
 * Visual Blocks Editor
 *
 * Copyright 2025 Massachusetts Institute of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @license
 * @fileoverview Object representing a breakpoint for MIT App Inventor.
 */

'use strict';

goog.provide('AI.BreakpointIcon');

/**
 * Class for a breakpoint.
 * @param {!Blockly.Block} block The block associated with this breakpoint.
 * @constructor
 */
AI.BreakpointIcon = class extends Blockly.icons.Icon {
  constructor(block) {
    super(block);
  }

  getType() {
    return AI.BreakpointIcon.TYPE;
  }

  initView(listener) {
    if (this.svgRoot) {
      return;
    }

    super.initView(listener);
    /* Here's the markup that will be generated:
    <g class="blocklyIconGroup">
      <circle class="blocklyBreakpointIcon" r="8" cx="8" cy="8"/>
    </g>
    */
    Blockly.utils.dom.createSvgElement('circle',
      {'class': 'blocklyBreakpointIcon',
        'r': AI.BreakpointIcon.ICON_RADIUS,
        'cx': AI.BreakpointIcon.ICON_RADIUS,
        'cy': AI.BreakpointIcon.ICON_RADIUS},
      this.svgRoot);
    Blockly.utils.dom.addClass(this.svgRoot, 'blockly-icon-breakpoint');
  }

  getSize() {
    return AI.BreakpointIcon.SIZE;
  }

  getWeight() {
    return 0;
  }

  isShownWhenCollapsed() {
    return true;
  }

  updateCollapsed() {
    // Do nothing
  }

  onClick() {
    // Toggle breakpoint off when clicked
    this.sourceBlock.removeIcon(AI.BreakpointIcon.TYPE);
  }

  isClickableInFlyout() {
    return false;
  }
};

/**
 * Radius of the breakpoint icon.
 */
AI.BreakpointIcon.ICON_RADIUS = 8;

/**
 * Type for the Breakpoint icon.
 */
AI.BreakpointIcon.TYPE = new Blockly.icons.IconType('breakpoint');

/**
 * Size of the Breakpoint icon.
 */
AI.BreakpointIcon.SIZE = new Blockly.utils.Size(
  AI.BreakpointIcon.ICON_RADIUS * 2, AI.BreakpointIcon.ICON_RADIUS * 2);

Blockly.icons.registry.register(AI.BreakpointIcon.TYPE, AI.BreakpointIcon);
