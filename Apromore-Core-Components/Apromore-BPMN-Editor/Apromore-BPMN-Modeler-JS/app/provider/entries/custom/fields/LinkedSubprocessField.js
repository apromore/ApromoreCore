var domify = require('min-dom').domify;
var domEvent = require('min-dom').event;
var is = require('bpmn-js/lib/util/ModelUtil').is;
var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;

module.exports = function(element, bpmnFactory, elementRegistry, translate) {
    let html = domify('<div id="link-subprocess-details"></div>');

    if (is(element, 'bpmn:SubProcess')) {
        var bo = getBusinessObject(element);
        let linkedProcessName = bo.get("linked-process") || 'None';
        let isLinked = false;

        if (linkedSubProcesses) {
            linkedProcessName = linkedSubProcesses[element.id] || 'None';
            isLinked = typeof linkedSubProcesses[element.id] === 'string';
        }

        let linkedProcessLabel = domify(`<label id="link-subprocess-label">Linked subprocess:</label>`);
        let linkedProcessLink = domify(`<span id="link-subprocess-view">${linkedProcessName}</span>`);
        let editBtn = domify(`<input id="link-subprocess-edit" value="Edit" type="button">`);
        let unlinkBtn = domify(`<input id="link-subprocess-remove" value="Unlink" type="button">`);

        if (isLinked) {
            linkedProcessLink.classList.add('link-subprocess-view-link');
        } else {
            linkedProcessLink.classList.remove('link-subprocess-view-link');
        }

        html.appendChild(linkedProcessLabel);
        html.appendChild(linkedProcessLink);
        html.appendChild(editBtn);
        html.appendChild(unlinkBtn);

        domEvent.bind(linkedProcessLink, 'click', function () {
            if (Apromore.BPMNEditor.viewSubprocess) {
                Apromore.BPMNEditor.viewSubprocess(element.id);
            } else {
                console.log('view subprocess function not found');
            }
        })

        domEvent.bind(editBtn, 'click', function () {
            if (Apromore.BPMNEditor.linkSubprocess) {
                Apromore.BPMNEditor.linkSubprocess(element.id);
            } else {
                console.log('link subprocess function not found');
            }
        })

        domEvent.bind(unlinkBtn, 'click', function () {
            if (Apromore.BPMNEditor.unlinkSubprocess) {
                Apromore.BPMNEditor.unlinkSubprocess(element.id);
            } else {
                console.log('unlink subprocess function not found');
            }
        })

    }

    // default validation method
    var defaultFunction = function() {
      return {};
    };

    return {
        id: 'link-subprocess',
        description: '',
        get: defaultFunction,
        set: defaultFunction,
        validate: defaultFunction,
        html: html,
        type: 'text',
        cssClasses: ['linked-subprocess']
    };
}
