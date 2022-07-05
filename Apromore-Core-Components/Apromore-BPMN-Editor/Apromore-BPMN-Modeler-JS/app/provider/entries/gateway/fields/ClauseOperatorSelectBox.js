var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var ProcessSimulationHelper = require('../../../../helper/ProcessSimulationHelper');
var createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (bpmnFactory, elementRegistry, translate, options) {
  var selectBoxId = ['clause', createUUID(), 'operator'].join('-');
  var getSelectedClause = options.getSelectedClause;

  return entryFactory.selectBox(translate, {
    id: selectBoxId,
    label: 'Operator',
    modelProperty: 'operator',
    selectOptions: createOperatorOptions,

    get: function (_element, _node) {
      let clause = getSelectedClause(_element, _node);
      return { operator: clause && clause.operator };
    },

    set: function (element, values, _node) {
      let clause = getSelectedClause(element, _node);
      return cmdHelper.updateBusinessObject(element, clause, {
        operator: values.operator || undefined
      })

    }
  });
};

  function createOperatorOptions() {
    return [
      {
        name: 'EQ',
        value: 'EQ'
      },
      {
        name: 'NEQ',
        value: 'NEQ'
      }
    ];
  }