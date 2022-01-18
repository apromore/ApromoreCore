import elementHelper from 'bpmn-js-properties-panel/lib/helper/ElementHelper'
import extensionElementsHelper from 'bpmn-js-properties-panel/lib/helper/ExtensionElementsHelper';

export const AUX_PROPS = {
  LEFT: 'aux-left',
  TOP: 'aux-top',
  WIDTH: 'aux-width',
  HEIGHT: 'aux-height',
  LINK_URL: 'aux-link-url',
  LINK_TEXT: 'aux-link-text',
  IMG_SRC: 'aux-img-src',
  IMG_URL: 'aux-img-url',
  ICON_URL: 'aux-icon-url',
  ICON_TEXT: 'aux-icon-text',
  ICON_NAME: 'aux-icon-name',
  ICON_SET: 'aux-icon-set'
}

export function getExtensionElements(element, bpmnFactory) {
  var extensionElements = element.extensionElements;

  if (!extensionElements || !extensionElements.values) {
    extensionElements = elementHelper.createElement('bpmn:ExtensionElements',
      { values: [] }, element, bpmnFactory);
    element.extensionElements = extensionElements;
  }

  return extensionElements;
}

export function getAux(element, bpmnFactory, type, init) {
  var extensionElements = getExtensionElements(element, bpmnFactory);
  var aux = (extensionElementsHelper.getExtensionElements(element,
    type) || [])[0];

  if (!aux) {
    aux = elementHelper.createElement(type, init, element, bpmnFactory);
    extensionElements.values.push(aux);
  }

  return aux;
}

export function refreshOverlay(bpmnjs, element) {
  setTimeout(function () {
    var auxModule = bpmnjs.get('aux');
    auxModule.createAux(element);
  }, 500);
}