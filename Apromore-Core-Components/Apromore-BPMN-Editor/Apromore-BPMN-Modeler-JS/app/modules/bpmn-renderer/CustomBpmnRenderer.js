import BpmnRenderer from 'bpmn-js/lib/draw/BpmnRenderer';

export default class CustomBpmnRenderer extends BpmnRenderer {}

CustomBpmnRenderer.$inject = [
  'config.bpmnRenderer',
  'eventBus',
  'styles',
  'pathMap',
  'canvas',
  'textRenderer'
];