import RuleProvider from 'diagram-js/lib/features/rules/RuleProvider';

import { is } from 'bpmn-js/lib/util/ModelUtil';

export default class ResizeTasks extends RuleProvider {
  constructor(bpmnRules, eventBus) {
    super(eventBus);

    this._bpmnRules = bpmnRules;
  }

  init() {
    this.addRule('shape.resize', Infinity, ({ shape, newBounds }) => {
      return (
        is(shape, 'bpmn:Task') ||
        is(shape, 'bpmn:SubProcess') ||
        is(shape, 'bpmn:Gateway') ||
        is(shape, 'bpmn:Event') ||
        is(shape, 'bpmn:DataObject') ||
        is(shape, 'bpmn:DataObjectReference') ||
        is(shape, 'bpmn:DataStoreReference') ||
        this._bpmnRules.canResize(shape, newBounds)
      );
    });
  }
}

ResizeTasks.$inject = [ 'bpmnRules', 'eventBus' ];

