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
        is(shape, 'bpmn:Task') || this._bpmnRules.canResize(shape, newBounds)
      );
    });
  }
}

ResizeTasks.$inject = [ 'bpmnRules', 'eventBus' ];

