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