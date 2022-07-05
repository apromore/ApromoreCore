var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
var  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');    
var LabelFactory = require('bpmn-js-properties-panel/lib/factory/LabelFactory')
var OperatorSelectBox = require('./OperatorSelectBox');
var createUUID = require('../../../../utils/Utils').createUUID;

var Clauses = require('./Clauses');
var ClauseCaseAttributeSelectBox = require('./ClauseCaseAttributeSelectBox');
var ClauseCategorySelectBox = require('./ClauseCategorySelectBox');
var ClauseOperatorSelectBox = require('./ClauseOperatorSelectBox');


module.exports = function (bpmnFactory, elementRegistry, translate, options, element) {
    let entries = [];
  
    let sequenceFlow = SequenceFlowHelper.getSequenceFlowByElementId(bpmnFactory, elementRegistry, options.outgoingElementId, true);

    entries.push(LabelFactory({ labelText: options.title, id: 'condition_title'+createUUID() }));

    entries.push(OperatorSelectBox(bpmnFactory, elementRegistry, translate, options,sequenceFlow));
    
    let clauses = Clauses(element, bpmnFactory, elementRegistry, translate, options,sequenceFlow);
    entries.push(clauses.entries);

    let clauseCaseAttribute =  ClauseCaseAttributeSelectBox(bpmnFactory, elementRegistry, translate,{ getSelectedClause: clauses.getSelectedClause },sequenceFlow);
    entries.push(clauseCaseAttribute.selectBox);

    entries.push(ClauseOperatorSelectBox(bpmnFactory, elementRegistry, translate,{ getSelectedClause: clauses.getSelectedClause},sequenceFlow,));

    entries.push(ClauseCategorySelectBox(bpmnFactory, elementRegistry, translate,{ getSelectedClause: clauses.getSelectedClause , getSelectedCaseAttribute:clauseCaseAttribute.getSelectedCaseAttribute },sequenceFlow));
   
    return entries;
};