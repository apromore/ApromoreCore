var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper');

  module.exports = function (bpmnFactory, elementRegistry, translate, options) {

  var getSelectedClause = options.getSelectedClause;
  var getSelectedCaseAttribute = options.getSelectedCaseAttribute;

  function createVariableOptions() {
    var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
    let selectedCaseAttributeText = getSelectedCaseAttribute();
    let selectedCaseAttribute =  variables && variables.values && variables.values.filter(function (variable) {
      return variable.name === selectedCaseAttributeText;
    });

    let filteredCaseAttribute;
    if(!selectedCaseAttributeText && (!selectedCaseAttribute || selectedCaseAttribute.length ==0) && variables && variables.values && variables.values.length > 0){
      selectedCaseAttribute = variables.values[0];
    }

    if(!selectedCaseAttribute)
    {
      return [];
    }
    
    if( selectedCaseAttribute.length >0 ){
      filteredCaseAttribute = selectedCaseAttribute[0];
    }else{
      filteredCaseAttribute = selectedCaseAttribute;
    }

    if(filteredCaseAttribute && filteredCaseAttribute.values)
    {
      let obj = [];
      for(let index=0; index < filteredCaseAttribute.values.length;index++){
        obj[index] =  { name: filteredCaseAttribute.values[index].name, value: filteredCaseAttribute.values[index].name };
      }
  
      return obj;

    }
    else {
      return [];
    }

   
  }


  let cluaseCategory = entryFactory.selectBox(translate, {
    id: 'clause-category-'+options.outgoingElementId,
    label: translate('gateway.clause.category.label'),
    modelProperty: 'variableEnumValue',
    selectOptions: createVariableOptions,
    hidden: function(element, node){
      return !getSelectedClause(element, node);
    },
    get: function (_element, _node) {
      let clause = getSelectedClause(_element, _node);
      return { variableEnumValue: clause && clause.variableEnumValue };
    },
    set: function (element, values, _node) {
      let clause = getSelectedClause(element, _node);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableEnumValue: values.variableEnumValue || undefined
      });

    }
  });

  function isNotExistCategory(){
    let filteredCaseAttribute = createVariableOptions();
    if( !filteredCaseAttribute || filteredCaseAttribute.length == 0){
      return translate('gateway.categories.notfound.message')
    } 
    
  }

  return {
    cluaseCategory: cluaseCategory,
    isNotExistCategory: isNotExistCategory
  }
};