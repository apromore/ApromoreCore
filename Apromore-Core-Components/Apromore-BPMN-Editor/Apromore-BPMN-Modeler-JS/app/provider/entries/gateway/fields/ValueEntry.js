var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedClause = options.getSelectedClause;
  var isNumeric  = options.isNumeric;

  var label = translate('gateway.attribute.value.label');

  return entryFactory.textField(translate, {
    id: 'gateway-attribute-value-'+options.outgoingElementId,
    label: label,
    modelProperty: 'variableNumValue'
    ,
    hidden: function(element, node){
      let numeric = isNumeric();
      let clause = getSelectedClause(element, node);
      return !numeric || !clause || clause.operator == 'BTW';
    }
    ,
    get: function(element, node) {
      let clause = getSelectedClause(element, node);
      return { variableNumValue: clause && clause.variableNumValue };
    },

    set: function(element, values, node) {
      let clause = getSelectedClause(element, node);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableNumValue: values.variableNumValue || '0'
      });
    },

    validate: function(element, values, node) {
      let clause = getSelectedClause(element, node);
      let isToskip = !isNumeric() || !clause ;
      if(isToskip){
        validationErrorHelper.suppressValidationErrorWithOnlyId(bpmnFactory, elementRegistry, { id: validationId });
        return { variableNumValue: undefined };
      }

      if (clause) {
        var validationId =  options.outgoingElementId;
        var error = validationErrorHelper.validateGatewayNumValue(bpmnFactory, elementRegistry, translate, {
          id: validationId,
          label: label,
          elementId: label,
          clause: clause,
          variableNumValue: values.variableNumValue,
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationErrorWithOnlyId(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { variableNumValue: error.message };
      }
    }
  });
};