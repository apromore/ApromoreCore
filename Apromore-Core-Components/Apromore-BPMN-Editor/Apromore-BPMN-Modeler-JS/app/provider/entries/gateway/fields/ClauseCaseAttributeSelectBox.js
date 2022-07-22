var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');

module.exports = function (bpmnFactory, elementRegistry, translate, options, sequenceFlow) {

  var getSelectedClause = options.getSelectedClause;
  let categoryList = [];

  function createCaseAttributeOptions() {
    var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);

    var variableWithNotEmptyName = variables && variables.values && variables.values.filter(function (variable) {
      return variable.name;
    });

    categoryList= variableWithNotEmptyName && variableWithNotEmptyName.length > 0 && variableWithNotEmptyName.map(function (variable) {
      return {
        name: variable.name,
        value: variable.name
      };
    });
    return categoryList;
  }

  let selectedCaseAttribute;
  var entrySelectbox = entryFactory.selectBox(translate, {
    id: 'clause-case-attribute-'+options.outgoingElementId,
    label: translate('gateway.clause.case.attribute.label'),
    modelProperty: 'variableName',
    selectOptions: createCaseAttributeOptions,
    hidden: function(element, node){
        return !getSelectedClause(element, node);
    },
    get: function (_element, _node) {
      let clause = getSelectedClause(_element, _node);
      selectedCaseAttribute = clause && clause.variableName;
      return { variableName: clause && clause.variableName || '' };
    },

    set: function (element, values, _node) {
      let clause = getSelectedClause(element, _node);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableName: values.variableName
      });

    }
  });

  function getSelectedCaseAttribute() {
    return selectedCaseAttribute;
  }

  function isNotExistCategories(){
    if( !categoryList || categoryList.length == 0){
      return translate('gateway.caseAttribute.notfound.message')
    } 
    
  }
  return {
    selectBox: entrySelectbox,
    getSelectedCaseAttribute: getSelectedCaseAttribute,
    isNotExistCategories : isNotExistCategories
  }

};