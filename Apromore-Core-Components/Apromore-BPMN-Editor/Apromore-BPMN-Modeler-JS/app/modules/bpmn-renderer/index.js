import PathMap from 'bpmn-js/lib/draw//PathMap';
import CustomBpmnRenderer from '../bpmn-renderer/CustomBpmnRenderer';
import CustomTextRenderer from '../text-renderer/CustomTextRenderer';

export default {
  __init__: [ 'bpmnRenderer' ],
  bpmnRenderer: [ 'type', CustomBpmnRenderer ],
  textRenderer: [ 'type', CustomTextRenderer ],
  pathMap: [ 'type', PathMap ]
};