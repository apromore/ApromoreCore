var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
var SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');
var ClauseLabelAttribute = require('./ClauseLabelAttribute')
var OperatorSelectBox = require('./OperatorSelectBox');
var LabelFactory = require('bpmn-js-properties-panel/lib/factory/LabelFactory');

var Clauses = require('./Clauses');
var ClauseCaseAttributeSelectBox = require('./ClauseCaseAttributeSelectBox');
var ClauseCategorySelectBox = require('./ClauseCategorySelectBox');
var ClauseOperatorSelectBox = require('./ClauseOperatorSelectBox');
var  ClauseCategoriesErrorLabel = require('./ClauseCategoriesErrorLabel');
var ValueEntry = require('./ValueEntry');
var BetweenValueEntry = require('./BetweenValueEntry');

module.exports = function (bpmnFactory, elementRegistry, translate, options, element) {
    let entries = [];
    let sequenceFlow = SequenceFlowHelper.getExistingSequenceFlowById(bpmnFactory, elementRegistry, options.outgoingElementId, true);
    
    entries.push(LabelFactory({ labelText: options.title, id: 'condition_title_' + options.outgoingElementId }));
    entries.push(OperatorSelectBox(bpmnFactory, elementRegistry, translate, options, sequenceFlow));
    
    let clauses = Clauses(element, bpmnFactory, elementRegistry, translate, options, sequenceFlow);
    entries.push(clauses.entries);

    entries.push(ClauseLabelAttribute(bpmnFactory, elementRegistry, translate, { labelText: translate('gateway.clause.detail'), id: 'clause-details-' + options.outgoingElementId , getSelectedClause: clauses.getSelectedClause}));

    let clauseCaseAttribute = ClauseCaseAttributeSelectBox(bpmnFactory, elementRegistry, translate, { getSelectedClause: clauses.getSelectedClause, outgoingElementId: options.outgoingElementId }, sequenceFlow);
    entries.push(clauseCaseAttribute.selectBox);
    entries.push(ClauseCategoriesErrorLabel({id:'clause-categories-error-message-'+ options.outgoingElementId, getSelectedClause: clauses.getSelectedClause, isNotExistCategories:clauseCaseAttribute.isNotExistCategories}));

    let clauseOperator = ClauseOperatorSelectBox(
        bpmnFactory, elementRegistry, translate,
        {
            getSelectedClause: clauses.getSelectedClause,
            getClauseCount: clauses.getClauseCount,
            getSelectedCaseAttribute: clauseCaseAttribute.getSelectedCaseAttribute,
            outgoingElementId: options.outgoingElementId,
            isNumeric : clauseCaseAttribute.isNumeric
        },
        sequenceFlow
    );
    entries.push(clauseOperator.clauseOperatorSelectBox);
    let ClauseCategory = ClauseCategorySelectBox(bpmnFactory, elementRegistry, translate, { getSelectedClause: clauses.getSelectedClause, getSelectedCaseAttribute: clauseCaseAttribute.getSelectedCaseAttribute, outgoingElementId: options.outgoingElementId , isNumeric : clauseCaseAttribute.isNumeric}, sequenceFlow);
    entries.push(ClauseCategory.cluaseCategory);
    entries.push(ClauseCategoriesErrorLabel({id:'clause-category-error-message-'+ options.outgoingElementId, getSelectedClause: clauses.getSelectedClause, isNotExistCategories:ClauseCategory.isNotExistCategory,  isNumeric : clauseCaseAttribute.isNumeric}));
    entries.push(ValueEntry(bpmnFactory, elementRegistry, translate, { getSelectedClause: clauses.getSelectedClause, getSelectedCaseAttribute: clauseCaseAttribute.getSelectedCaseAttribute, outgoingElementId: options.outgoingElementId , isNumeric : clauseCaseAttribute.isNumeric}));

    entries =entries.concat(BetweenValueEntry(bpmnFactory, elementRegistry, translate, { getSelectedClause: clauses.getSelectedClause, getSelectedCaseAttribute: clauseCaseAttribute.getSelectedCaseAttribute, outgoingElementId: options.outgoingElementId , isNumeric : clauseCaseAttribute.isNumeric}));

    return entries;
};