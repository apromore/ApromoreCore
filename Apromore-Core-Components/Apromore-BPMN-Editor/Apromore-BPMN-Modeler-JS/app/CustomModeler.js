import 'spectrum-colorpicker2/dist/spectrum';
import inherits from 'inherits';
import { isObject, forEach, filter, reduce } from 'lodash';
import Modeler from 'bpmn-js/lib/Modeler';
import propertiesPanelModule from 'bpmn-js-properties-panel';
import camundaModdleDescriptor from "camunda-bpmn-moddle/resources/camunda";

import simulationModdleDescriptor from './descriptors/simulation';
import propertiesProviderModule from './provider';
import apModdleDescriptor from './descriptors/ap';
import '../styles/customModeler.less';
import customTranslate from './translate/customTranslate';
import ColorPickerModule from './modules/color-picker';
import AttachmentModule from './modules/attachment';
import EmbeddedCommentsModule from './modules/comments';
import ResizeTasksModule from './modules/resize-tasks';
import LinkSubprocessModule from './modules/link-subprocess';
import CustomBpmnRendererModule from './modules/bpmn-renderer';
import LabelEditingModule from './modules/label-editing';

var domify = require('min-dom').domify,
  domQuery = require('min-dom').query,
  domQueryAll = require('min-dom').queryAll,
  domRemove = require('min-dom').remove,
  domClasses = require('min-dom').classes,
  domClosest = require('min-dom').closest,
  domAttr = require('min-dom').attr,
  domDelegate = require('min-dom').delegate,
  domMatches = require('min-dom').matches;

var HIDE_CLASS = 'bpp-hidden';

propertiesPanelModule.propertiesPanel[1].prototype.activateTab = function(tabId) {
  if (isObject(tabId)) {
   tabId = tabId.id;
  }

  var mainContainer = domQuery('#ap-editor-props-container');
  var tabs = domQueryAll('.bpp-properties-tab', this._current.panel),
    tabLinks = domQueryAll('.bpp-properties-tab-link', this._current.panel);
  var isSimulation = domClasses(mainContainer).has('simulation');

  // (1) Deactivate all tabs
  forEach(tabs, function(tab) {
    domClasses(tab).remove('bpp-active');
  });

  forEach(tabLinks, function(tabLink) {
    domClasses(tabLink).remove('bpp-active');
  });

  // (2) Activate tab, fall back to first visible tab
  var visibleTabs = filter(tabs, function(tab) {
    var tabTarget = domAttr(tab, 'data-tab');
    if (isSimulation && (tabTarget === 'customTab' || tabTarget === 'attachmentTab')) {
      return false;
    }
    return !domClasses(tab).has(HIDE_CLASS);
  });

  var activeTab = reduce(visibleTabs, function(activeTab, tab) {
    if (domAttr(tab, 'data-tab') === tabId) {
      return tab;
    }
    return activeTab;
  }, visibleTabs[ 0 ]);

  if (activeTab) {
    domClasses(activeTab).add('bpp-active');
  }

  var visibleTabLinks = filter(tabLinks, function(tabLink) {
    var tabTarget = domAttr(domQuery('a[data-tab-target]', tabLink), 'data-tab-target');
    if (isSimulation && (tabTarget === 'customTab' || tabTarget === 'attachmentTab')) {
      return false;
    }
    return !domClasses(tabLink).has(HIDE_CLASS);
  });

  var activeTabLink = reduce(visibleTabLinks, function(activeTabLink, tabLink) {
    if (domAttr(domQuery('a[data-tab-target]', tabLink), 'data-tab-target') === tabId) {
      return tabLink;
    }
    return activeTabLink;
  }, visibleTabLinks[ 0 ]);

  if (activeTabLink) {
    domClasses(activeTabLink).add('bpp-active');
  }
};

export default function CustomModeler(options) {
  var customTranslateModule = {
    translate: [ 'value', customTranslate(options.langTag) ]
  };
  options.additionalModules = options.additionalModules || [];
  options.additionalModules.push(customTranslateModule);
  options.additionalModules.push(ColorPickerModule);
  options.additionalModules.push(AttachmentModule);
  options.additionalModules.push(EmbeddedCommentsModule);
  options.additionalModules.push(ResizeTasksModule);
  options.additionalModules.push(LinkSubprocessModule);
  options.additionalModules.push(LabelEditingModule);
  options.additionalModules.push(CustomBpmnRendererModule);
  Modeler.call(this, options);
}

inherits(CustomModeler, Modeler);

CustomModeler.prototype._customModules = [
  propertiesPanelModule,
  propertiesProviderModule
];

CustomModeler.prototype._modules = [].concat(
  Modeler.prototype._modules,
  CustomModeler.prototype._customModules
);

CustomModeler.prototype._moddleExtensions = {
  qbp: simulationModdleDescriptor,
  camunda: camundaModdleDescriptor,
  ap: apModdleDescriptor
};

//LabelEditingProvider.prototype.getEditingBBox = function(element) {
//  var canvas = this._canvas;
//
//  var target = element.label || element;
//
//  var bbox = canvas.getAbsoluteBBox(target);
//
//  var mid = {
//    x: bbox.x + bbox.width / 2,
//    y: bbox.y + bbox.height / 2
//  };
//
//  // default position
//  var bounds = { x: bbox.x, y: bbox.y };
//
//  var zoom = canvas.zoom();
//
//  var defaultStyle = this._textRenderer.getDefaultStyle(),
//      externalStyle = this._textRenderer.getExternalStyle();
//
//  // take zoom into account
//  var externalFontSize = externalStyle.fontSize * zoom,
//      externalLineHeight = externalStyle.lineHeight,
//      defaultFontSize = defaultStyle.fontSize * zoom,
//      defaultLineHeight = defaultStyle.lineHeight;
//
//  var style = {
//    fontFamily: this._textRenderer.getDefaultStyle().fontFamily,
//    fontWeight: this._textRenderer.getDefaultStyle().fontWeight
//  };
//
//  // adjust for expanded pools AND lanes
//  if (is(element, 'bpmn:Lane') || isExpandedPool(element)) {
//
//    assign(bounds, {
//      width: bbox.height,
//      height: 30 * zoom,
//      x: bbox.x - bbox.height / 2 + (15 * zoom),
//      y: mid.y - (30 * zoom) / 2
//    });
//
//    assign(style, {
//      fontSize: defaultFontSize + 'px',
//      lineHeight: defaultLineHeight,
//      paddingTop: (7 * zoom) + 'px',
//      paddingBottom: (7 * zoom) + 'px',
//      paddingLeft: (5 * zoom) + 'px',
//      paddingRight: (5 * zoom) + 'px',
//      transform: 'rotate(-90deg)'
//    });
//  }
//
//
//  // internal labels for tasks and collapsed call activities,
//  // sub processes and participants
//  if (isAny(element, [ 'bpmn:Task', 'bpmn:CallActivity']) ||
//      isCollapsedPool(element) ||
//      isCollapsedSubProcess(element)) {
//
//    assign(bounds, {
//      width: bbox.width,
//      height: bbox.height
//    });
//
//    assign(style, {
//      fontSize: defaultFontSize + 'px',
//      lineHeight: defaultLineHeight,
//      paddingTop: (7 * zoom) + 'px',
//      paddingBottom: (7 * zoom) + 'px',
//      paddingLeft: (5 * zoom) + 'px',
//      paddingRight: (5 * zoom) + 'px'
//    });
//  }
//
//
//  // internal labels for expanded sub processes
//  if (isExpandedSubProcess(element)) {
//    assign(bounds, {
//      width: bbox.width,
//      x: bbox.x
//    });
//
//    assign(style, {
//      fontSize: defaultFontSize + 'px',
//      lineHeight: defaultLineHeight,
//      paddingTop: (7 * zoom) + 'px',
//      paddingBottom: (7 * zoom) + 'px',
//      paddingLeft: (5 * zoom) + 'px',
//      paddingRight: (5 * zoom) + 'px'
//    });
//  }
//
//  var width = 90 * zoom,
//      paddingTop = 7 * zoom,
//      paddingBottom = 4 * zoom;
//
//  // external labels for events, data elements, gateways, groups and connections
//  if (target.labelTarget) {
//    assign(bounds, {
//      width: width,
//      height: bbox.height + paddingTop + paddingBottom,
//      x: mid.x - width / 2,
//      y: bbox.y - paddingTop
//    });
//
//    assign(style, {
//      fontSize: externalFontSize + 'px',
//      lineHeight: externalLineHeight,
//      paddingTop: paddingTop + 'px',
//      paddingBottom: paddingBottom + 'px'
//    });
//  }
//
//  // external label not yet created
//  if (isLabelExternal(target)
//      && !hasExternalLabel(target)
//      && !isLabel(target)) {
//
//    var externalLabelMid = getExternalLabelMid(element);
//
//    var absoluteBBox = canvas.getAbsoluteBBox({
//      x: externalLabelMid.x,
//      y: externalLabelMid.y,
//      width: 0,
//      height: 0
//    });
//
//    var height = externalFontSize + paddingTop + paddingBottom;
//
//    assign(bounds, {
//      width: width,
//      height: height,
//      x: absoluteBBox.x - width / 2,
//      y: absoluteBBox.y - height / 2
//    });
//
//    assign(style, {
//      fontSize: externalFontSize + 'px',
//      lineHeight: externalLineHeight,
//      paddingTop: paddingTop + 'px',
//      paddingBottom: paddingBottom + 'px'
//    });
//  }
//
//  // text annotations
//  if (is(element, 'bpmn:TextAnnotation')) {
//    assign(bounds, {
//      width: bbox.width,
//      height: bbox.height,
//      minWidth: 30 * zoom,
//      minHeight: 10 * zoom
//    });
//
//    assign(style, {
//      textAlign: 'left',
//      paddingTop: (5 * zoom) + 'px',
//      paddingBottom: (7 * zoom) + 'px',
//      paddingLeft: (7 * zoom) + 'px',
//      paddingRight: (5 * zoom) + 'px',
//      fontSize: defaultFontSize + 'px',
//      lineHeight: defaultLineHeight
//    });
//  }
//
//  return { bounds: bounds, style: style };
//};
