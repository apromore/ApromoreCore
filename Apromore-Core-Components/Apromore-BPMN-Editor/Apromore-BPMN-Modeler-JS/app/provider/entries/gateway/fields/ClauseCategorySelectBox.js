var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper'),
  ElementHelper = require('../../../../helper/ElementHelper');
var createUUID = require('../../../../utils/Utils').createUUID;
module.exports = function (bpmnFactory, elementRegistry, translate, options) {

  var selectBoxId = ['clause', createUUID(), 'category'].join('-');
  var getSelectedClause = options.getSelectedClause;
  var getSelectedCaseAttribute = options.getSelectedCaseAttribute;

  function createVariableOptions() {
    var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
    let selectedCaseAttributeText = getSelectedCaseAttribute();
    let selectedCaseAttribute =  variables && variables.values && variables.values.filter(function (variable) {
      return variable.name === selectedCaseAttributeText;
    });

    if(!selectedCaseAttributeText && (!selectedCaseAttribute || selectedCaseAttribute.length ==0) && variables && variables.values && variables.values.length > 0){
      selectedCaseAttribute = variables.values[0];
    }

    if(!selectedCaseAttribute || !selectedCaseAttribute.values || selectedCaseAttribute.values.length ==0)
    {
      return [];
    }
    return selectedCaseAttribute.values.map(function (value) {
      return {
        name: value.name,
        value: value.name
      };
    });
  }


  return entryFactory.selectBox(translate, {
    id: selectBoxId,
    label: 'Category',
    modelProperty: 'variableEnumValue',
    selectOptions: createVariableOptions,

    get: function (_element, _node) {
      let clause = getSelectedClause(_element, _node);
      return { variableEnumValue: clause && clause.variableEnumValue };
    },

    set: function (element, values, _node) {
      let clause = getSelectedClause(element, _node);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableEnumValue: values.variableEnumValue || undefined
      })

    }
  });
};