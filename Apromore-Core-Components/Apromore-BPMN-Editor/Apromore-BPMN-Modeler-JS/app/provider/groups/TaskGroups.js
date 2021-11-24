var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    getFlowElementsByType = require('../../helper/FlowElementsHelper'),
    createTaskEntries = require('../entries/task/TaskEntries');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  var tasks = getFlowElementsByType(element, 'bpmn:Task');

  tasks = tasks.filter(function(el) {
    return !el.toBeRemoved;
  });

  function taskToGroup(el) {
    var task = getBusinessObject(el),
        groupId = ['task', task.get('id'), 'group'].join('-'),
        groupLabel = task.name || task.id;

    return {
      id: groupId,
      label: function(_element, _node) {
        return groupLabel;
      },
      entries: createTaskEntries(bpmnFactory, elementRegistry, translate, { task: task })
    };
  }

  return tasks.map(function(el) {
    return taskToGroup(el);
  });
};