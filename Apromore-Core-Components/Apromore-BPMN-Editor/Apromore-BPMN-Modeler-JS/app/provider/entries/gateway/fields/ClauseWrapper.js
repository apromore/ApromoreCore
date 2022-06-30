var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
    var labelFactory = require('bpmn-js-properties-panel/lib/factory/LabelFactory')
var OperatorSelectBox = require('./OperatorSelectBox');
var Clauses = require('./Clauses');
var ClauseAttributeSelectBox = require('./ClauseAttributeSelectBox');
var ClauseCategorySelectBox = require('./ClauseCategorySelectBox');
var ClauseOperatorSelectBox = require('./ClauseOperatorSelectBox');

module.exports = function (bpmnFactory, elementRegistry, translate, options,element) {

    var entries = [];
    entries.push(labelFactory({labelText: options.title ,id:'condition_title'}));

    entries.push(OperatorSelectBox(bpmnFactory, elementRegistry, translate, options));

    entries.push(Clauses(element, bpmnFactory, elementRegistry, translate,options).entries);

    entries.push(ClauseAttributeSelectBox(bpmnFactory, elementRegistry, translate,options));

    entries.push(ClauseCategorySelectBox(bpmnFactory, elementRegistry, translate, options));

    entries.push(ClauseOperatorSelectBox(bpmnFactory, elementRegistry, translate, options));
    
        return entries;
};