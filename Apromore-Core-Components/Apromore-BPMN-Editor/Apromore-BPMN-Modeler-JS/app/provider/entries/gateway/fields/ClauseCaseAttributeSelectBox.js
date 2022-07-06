var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  createUUID = require('../../../../utils/Utils').createUUID;

module.exports = function (bpmnFactory, elementRegistry, translate, options, sequenceFlow) {

  var selectBoxId = ['clause', createUUID(), 'case-attribute'].join('-');
  var getSelectedClause = options.getSelectedClause;

  function createVariableOptions() {
    var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);

    var variableWithNotEmptyName = variables && variables.values && variables.values.filter(function (variable) {
      return variable.name;
    });

    return  variableWithNotEmptyName && variableWithNotEmptyName.length >0 && variableWithNotEmptyName.map(function (variable) {
      return {
        name: variable.name,
        value: variable.name
      };
    });
  }

  let selectedCaseAttribute;
  var entrySelectbox = entryFactory.selectBox(translate, {
    id: selectBoxId,
    label: 'Case Attribute',
    modelProperty: 'variableName',
    selectOptions: createVariableOptions,

    get: function (_element, _node) {
      let clause = getSelectedClause(_element, _node);
      selectedCaseAttribute = clause && clause.variableName;
      return { variableName:clause && clause.variableName || ''};
    },

    set: function (element, values, _node) {
      let clause = getSelectedClause(element, _node);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableName: values.variableName 
      })

    }
  });

  function getSelectedCaseAttribute(){
    return selectedCaseAttribute;
  }
  return {
    selectBox:entrySelectbox,
    getSelectedCaseAttribute:getSelectedCaseAttribute
  }

};