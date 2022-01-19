import 'spectrum-colorpicker2/dist/spectrum';
import inherits from 'inherits';

import Modeler from 'bpmn-js/lib/Modeler';

import propertiesPanelModule from 'bpmn-js-properties-panel';
import propertiesProviderModule from './provider';
import camundaModdleDescriptor from "camunda-bpmn-moddle/resources/camunda";
import simulationModdleDescriptor from './descriptors/simulation';
import apModdleDescriptor from './descriptors/ap';

import '../styles/customModeler.less';
import customTranslate from './translate/customTranslate';
import ColorPickerModule from './modules/color-picker';
import AttachmentModule from './modules/attachment';
import EmbeddedCommentsModule from './modules/comments';

export default function CustomModeler(options) {
  var customTranslateModule = {
    translate: [ 'value', customTranslate(options.langTag) ]
  };
  options.additionalModules = options.additionalModules || [];
  options.additionalModules.push(customTranslateModule);
  options.additionalModules.push(ColorPickerModule);
  options.additionalModules.push(AttachmentModule);
  options.additionalModules.push(EmbeddedCommentsModule);
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