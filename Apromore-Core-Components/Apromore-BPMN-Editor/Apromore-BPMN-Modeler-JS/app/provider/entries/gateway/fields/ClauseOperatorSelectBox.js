var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper');
const { default: translate } = require('diagram-js/lib/i18n/translate');

module.exports = function (bpmnFactory, elementRegistry, translate, options) {
  var getSelectedClause = options.getSelectedClause;
  var getSelectedCaseAttribute = options.getSelectedCaseAttribute;
  var getClauseCount = options.getClauseCount;
  var isNumeric = options.isNumeric;

  var clauseOperatorSelectBox = entryFactory.selectBox(translate, {
    id: 'clause-operator-' + options.outgoingElementId,
    label: translate('gateway.clause.operator.label'),
    modelProperty: 'operator',
    selectOptions: getOperatorOptions(translate),
    hidden: function (element, node) {
      return !getSelectedClause(element, node);
    },
    get: function (_element, _node) {
      let clause = getSelectedClause(_element, _node);
      return { operator: clause && clause.operator || 'EQ' };
    },

    set: function (element, values, _node) {
      let clause = getSelectedClause(element, _node);
      return cmdHelper.updateBusinessObject(element, clause, {
        operator: values.operator || undefined
      })

    }
  });

  function getOperatorOptions(translate) {
    let operator = [
      {
        name: translate('gateway.expression.operator.equal.label'),
        value: 'EQ'
      },
      {
        name: translate('gateway.expression.operator.notEqual.label'),
        value: 'NEQ'
      }];

    if (isNumeric()) {
      let operatorNumeric = [
        {
          name: translate('gateway.expression.operator.greaterThanEqual.label'),
          value: 'GTE'
        },
        {
          name: translate('gateway.expression.operator.greaterThan.label'),
          value: 'GT'
        },
        {
          name: translate('gateway.expression.operator.lessThanEqual.label'),
          value: 'LTE'
        },
        {
          name: translate('gateway.expression.operator.lessThan.label'),
          value: 'LT'
        },
        {
          name: translate('gateway.expression.operator.between.label'),
          value: 'BTW'
        }
      ];
      operator = operator.concat(operatorNumeric);
    }
    return operator;
  }

  return { 
    clauseOperatorSelectBox: clauseOperatorSelectBox
    }

};