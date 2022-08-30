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
    selectOptions: getOperatorOptions,
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

  function getOperatorOptions() {
    let opetator = [
      {
        name: 'Equal to (=)',
        value: 'EQ'
      },
      {
        name: 'Not equal to (≠)',
        value: 'NEQ'
      }];

    if (isNumeric()) {
      let opetatorNumeric = [
        {
          name: 'Greater than or equal to (≥)',
          value: 'GTE'
        },
        {
          name: 'Greater than (>)',
          value: 'GT'
        },
        {
          name: 'Less than or equal to (≤)',
          value: 'LTE'
        },
        {
          name: 'Less than (<)',
          value: 'LT'
        },
        {
          name: 'Between (|…|)',
          value: 'BTW'
        }
      ];
      opetator = opetator.concat(opetatorNumeric);
    }
    return opetator;
  }

  return { 
    clauseOperatorSelectBox: clauseOperatorSelectBox
    }

};