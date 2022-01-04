var customTranslate = require('../../app/translate/customTranslate'),
    simulationModdleDescriptor = require('../../app/descriptors/simulation.json'),
    camundaModdleDescriptor = require('camunda-bpmn-moddle/resources/camunda'),
    apModdleDescriptor = require('../../app/descriptors/ap.json'),
    TestHelper = require('../TestHelper'),
    TestContainer = require('mocha-test-container-support'),
    getResources = require('../helper/modelHelper').getResources,
    getFirstResourceByName = require('../helper/modelHelper').getFirstResourceByName;

var propertiesPanelModule = require('bpmn-js-properties-panel/lib'),
    propertiesProviderModule = require('app/provider'),
    domQuery = require('min-dom').query,
    domClasses = require('min-dom').classes,
    coreModule = require('bpmn-js/lib/core').default,
    selectionModule = require('diagram-js/lib/features/selection').default,
    modelingModule = require('bpmn-js/lib/features/modeling').default;

describe('resource-tab', function() {
  var diagramXML = require('./Resource.bpmn');

  var customTranslateModule = {
    translate: [ 'value', customTranslate('en') ]
  };

  var testModules = [
    coreModule,
    selectionModule,
    modelingModule,
    propertiesPanelModule,
    propertiesProviderModule,
    customTranslateModule
  ];

  var container,
      shape,
      getSelectBox,
      getInputField,
      resourceFieldSelectBox;

  function selectResource(container, idx) {
    resourceFieldSelectBox.options[idx].selected = 'selected';
    TestHelper.triggerEvent(resourceFieldSelectBox, 'change');
  }

  beforeEach(function() {
    container = TestContainer.get(this);
  });

  beforeEach(bootstrapModeler(diagramXML, {
    modules: testModules,
    moddleExtensions: {
      qbp: simulationModdleDescriptor,
      camunda: camundaModdleDescriptor,
      ap: apModdleDescriptor
    }
  }));

  beforeEach(inject(function(commandStack, propertiesPanel, selection, elementRegistry) {

    var undoButton = document.createElement('button');
    undoButton.textContent = 'UNDO';

    undoButton.addEventListener('click', function() {
      commandStack.undo();
    });

    container.appendChild(undoButton);

    propertiesPanel.attachTo(container);

    shape = elementRegistry.get('Process_1');
    selection.select(shape);

    resourceFieldSelectBox = domQuery('div[data-entry=simulationResources] select[name=selectedExtensionElement]', container);

    getInputField = function(id) {
      return domQuery('input[id=camunda-'+id+']', propertiesPanel._container);
    };

    getSelectBox = function(name) {
      return domQuery('[data-entry="'+name+'"] select', propertiesPanel._container);
    };
  }));

  describe('existing resource', function() {

    beforeEach(function() {
      // when
      selectResource(container, 1);
    });

    it('should fetch data entry', function() {

      // then
      expect(resourceFieldSelectBox.options).to.have.length(2);
      expect(resourceFieldSelectBox.options[1].value).to.equal('qbp_fd4f9148-3d17-4f8a-83f9-8332de74e986');
      expect(resourceFieldSelectBox.options[1].textContent).to.equal('Clerk');
    });

    it('should fetch details', function() {

      // then
      expect(getSelectBox('resource-timetable').value).to.equal('DEFAULT_TIMETABLE');
      expect(getInputField('resource-name').value).to.equal('Clerk');
      expect(getInputField('resource-amount').value).to.equal('10');
      expect(getInputField('resource-cost').value).to.equal('25');
    });
  });

  describe('new resource', function() {

    beforeEach(function() {
      var addButton = domQuery('button[id=cam-extensionElements-create-simulationResources]', container);

      // when
      TestHelper.triggerEvent(addButton, 'click');
      selectResource(container, 2);
    });

    it('should be added to the data entry', function() {

      // then
      expect(resourceFieldSelectBox.options).to.have.length(3);
      expect(resourceFieldSelectBox.options[1].value).to.equal('qbp_fd4f9148-3d17-4f8a-83f9-8332de74e986');
      expect(resourceFieldSelectBox.options[1].textContent).to.equal('Clerk');
      expect(resourceFieldSelectBox.options[2].textContent).to.equal('Resource');
    });

    it('should be added to the extension elements', inject(function(bpmnFactory, elementRegistry) {

      // then
      expect(getResources(bpmnFactory, elementRegistry).length).to.equal(3);
    }));

    it('should have empty details and default arrival timetable', function() {

      // then
      expect(getSelectBox('resource-timetable').value).to.equal('DEFAULT_TIMETABLE');
      expect(getInputField('resource-name').value).to.equal('Resource');
      expect(getInputField('resource-amount').value).to.equal('');
      expect(getInputField('resource-cost').value).to.equal('');
    });

    describe('name', function() {
      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(getInputField('resource-name').value).to.equal('Resource');

        // when
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');

        // then
        expect(getInputField('resource-name').value).to.equal('R1');
        expect(resourceFieldSelectBox.options[2].textContent).to.equal('R1');
        expect(resourceFieldSelectBox.options[2].value).to.be.equal(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').id);
      }));
    });

    describe('number of instances', function() {
      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(getInputField('resource-amount').value).to.equal('');

        // when
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');
        TestHelper.triggerValue(getInputField('resource-amount'), '10', 'change');

        // then
        expect(getInputField('resource-amount').value).to.equal('10');
        expect(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').totalAmount).to.be.equal('10');
      }));

      it('should not set negative value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(getInputField('resource-amount').value).to.equal('');

        // when
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');
        TestHelper.triggerValue(getInputField('resource-amount'), '-10', 'change');

        // then
        expect(getInputField('resource-amount').value).to.equal('-10');
        expect(domClasses(getInputField('resource-amount')).has('invalid')).to.be.true;
        expect(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').totalAmount).to.be.equal('-10');
      }));

      it('should not set empty value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(getInputField('resource-amount').value).to.equal('');

        // when
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');
        TestHelper.triggerValue(getInputField('resource-amount'), '', 'change');

        // then
        expect(getInputField('resource-amount').value).to.equal('');
        expect(domClasses(getInputField('resource-amount')).has('invalid')).to.be.true;
        expect(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').totalAmount).to.be.equal('');
      }));

      it('should not set not numeric value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(getInputField('resource-amount').value).to.equal('');

        // when
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');
        TestHelper.triggerValue(getInputField('resource-amount'), 'text', 'change');

        // then
        expect(getInputField('resource-amount').value).to.equal('text');
        expect(domClasses(getInputField('resource-amount')).has('invalid')).to.be.true;
        expect(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').totalAmount).to.be.equal('text');
      }));
    });

    describe('cost per hour', function() {
      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {
        selectResource(container, 1);

        // assume
        expect(getInputField('resource-cost').value).to.equal('25');

        // when
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');
        TestHelper.triggerValue(getInputField('resource-cost'), '10', 'change');

        // then
        expect(getInputField('resource-cost').value).to.equal('10');
        expect(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').costPerHour).to.be.equal('10');
      }));

      it('should not set negative value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(getInputField('resource-cost').value).to.equal('');

        // when
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');
        TestHelper.triggerValue(getInputField('resource-cost'), '-10', 'change');

        // then
        expect(getInputField('resource-cost').value).to.equal('-10');
        expect(domClasses(getInputField('resource-cost')).has('invalid')).to.be.true;
        expect(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').costPerHour).to.be.equal('-10');
      }));

      it('should not set not numeric value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(getInputField('resource-cost').value).to.equal('');

        // when
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');
        TestHelper.triggerValue(getInputField('resource-cost'), 'text', 'change');

        // then
        expect(getInputField('resource-cost').value).to.equal('text');
        expect(domClasses(getInputField('resource-cost')).has('invalid')).to.be.true;
        expect(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').costPerHour).to.be.equal('text');
      }));
    });

    describe('timetable', function() {
      it('should set existing timetable', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(getSelectBox('resource-timetable').value).to.equal('DEFAULT_TIMETABLE');

        // when add new timetable
        var timetableFieldSelectBox = domQuery('select[name=selectedExtensionElement]', container);
        var addButton = domQuery('button[id=cam-extensionElements-create-timetableEntry]', container);
        TestHelper.triggerEvent(addButton, 'click');
        TestHelper.triggerValue(getInputField('timetable-name'), 'T1', 'change');

        // and change resource timetable
        TestHelper.triggerValue(getInputField('resource-name'), 'R1', 'change');
        TestHelper.triggerValue(getSelectBox('resource-timetable'), timetableFieldSelectBox.options[1].value, 'change');

        // then
        expect(getSelectBox('resource-timetable').value).to.equal(timetableFieldSelectBox.options[1].value);
        expect(getFirstResourceByName(bpmnFactory, elementRegistry, 'R1').timetableId).to.be.equal(timetableFieldSelectBox.options[1].value);
      }));
    });


    it('should be deleted', inject(function(bpmnFactory, elementRegistry) {
      var removeButton = domQuery('button[id=cam-extensionElements-remove-simulationResources]', container);

      // assume
      expect(resourceFieldSelectBox.options).to.have.length(3);
      expect(getResources(bpmnFactory, elementRegistry).length).to.equal(3);

      // when
      TestHelper.triggerEvent(removeButton, 'click');

      // then
      expect(resourceFieldSelectBox.options).to.have.length(2);
      expect(resourceFieldSelectBox.options[1].textContent).to.equal('Clerk');
      expect(getResources(bpmnFactory, elementRegistry).length).to.equal(2);
    }));
  });
});