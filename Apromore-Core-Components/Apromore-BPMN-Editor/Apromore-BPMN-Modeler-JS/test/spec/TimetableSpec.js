var customTranslate = require('../../app/translate/customTranslate'),
    simulationModdleDescriptor = require('../../app/descriptors/simulation.json'),
    camundaModdleDescriptor = require('camunda-bpmn-moddle/resources/camunda'),
    apModdleDescriptor = require('../../app/descriptors/ap.json'),
    TestHelper = require('../TestHelper'),
    TestContainer = require('mocha-test-container-support');

var getTimetableByName = require('../helper/modelHelper').getTimetableByName,
    getTimetables = require('../helper/modelHelper').getTimetables;

var propertiesPanelModule = require('bpmn-js-properties-panel/lib'),
    propertiesProviderModule = require('app/provider'),
    domQuery = require('min-dom').query,
    domClasses = require('min-dom').classes,
    coreModule = require('bpmn-js/lib/core').default,
    selectionModule = require('diagram-js/lib/features/selection').default,
    modelingModule = require('bpmn-js/lib/features/modeling').default;

describe('timetable-tab', function() {
  var diagramXML = require('./Timetable.bpmn');

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
      getInputField;

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

    getInputField = function(id) {
      return domQuery('input[id=camunda-' + id + ']', propertiesPanel._container);
    };

    getSelectBox = function(name) {
      return domQuery('[data-entry="' + name + '"] select', propertiesPanel._container);
    };
  }));

  describe('default arrival timetable', function() {

    it('should be created', inject(function(bpmnFactory, elementRegistry) {

      // then
      expect(getTimetableByName(bpmnFactory, elementRegistry, 'Arrival timetable')).to.not.be.undefined;
    }));

    it('should fetch timetable data entry', inject(function(propertiesPanel) {

      var timetableFieldSelectBox = domQuery('select[name=selectedExtensionElement]', propertiesPanel._container);

      // then
      expect(timetableFieldSelectBox.options).to.have.length(1);
      expect(timetableFieldSelectBox.options[0].value).to.equal('DEFAULT_TIMETABLE');
      expect(timetableFieldSelectBox.options[0].textContent).to.equal('Arrival timetable');
    }));

    it('should fetch timeslot properties', inject(function(propertiesPanel) {

      // when
      TestHelper.triggerFormFieldSelection(0, propertiesPanel._container);

      var timeslotEntryContainer = domQuery('[data-entry="timeslotEntry"]');
      TestHelper.triggerFormFieldSelection(0, timeslotEntryContainer);

      // then
      expect(getInputField('timetable-name').value).to.equal('Arrival timetable');

      expect(getInputField('timeslot-name').value).to.equal('Timeslot');
      expect(getSelectBox('timeslot-fromWeekDay').value).to.equal('MONDAY');
      expect(getSelectBox('timeslot-toWeekDay').value).to.equal('FRIDAY');
      expect(getInputField('timeslot-fromTime').value).to.equal('09:00');
      expect(getInputField('timeslot-toTime').value).to.equal('17:00');
    }));

    it('should have correct name', inject(function(propertiesPanel, bpmnFactory, elementRegistry) {

      var timetableFieldSelectBox = domQuery('select[name=selectedExtensionElement]', propertiesPanel._container);

      // when
      TestHelper.triggerFormFieldSelection(0, propertiesPanel._container);
      TestHelper.triggerValue(getInputField('timetable-name'), 'T1', 'change');

      // then
      expect(getInputField('timetable-name').value).to.equal('T1');
      expect(timetableFieldSelectBox.options[0].textContent).to.equal('T1 [Arrival timetable]');
      expect(getTimetableByName(bpmnFactory, elementRegistry, 'T1')).to.not.be.undefined;
    }));

    it('should not be deleted', inject(function(propertiesPanel, bpmnFactory, elementRegistry) {
      var timetableFieldSelectBox = domQuery('select[name=selectedExtensionElement]', propertiesPanel._container);
      var removeButton = domQuery('button[id=cam-extensionElements-remove-timetableEntry]', propertiesPanel._container);

      // when
      TestHelper.triggerFormFieldSelection(0, propertiesPanel._container);
      TestHelper.triggerEvent(removeButton, 'click');

      // then
      expect(timetableFieldSelectBox.options).to.have.length(1);
      expect(timetableFieldSelectBox.options[0].value).to.equal('DEFAULT_TIMETABLE');
      expect(getTimetableByName(bpmnFactory, elementRegistry, 'Arrival timetable')).to.not.be.undefined;
    }));
  });

  describe('new timetable', function() {
    var timetableFieldSelectBox;

    beforeEach(function() {

      timetableFieldSelectBox = domQuery('select[name=selectedExtensionElement]', container);

      // Add new timetable
      var addButton = domQuery('button[id=cam-extensionElements-create-timetableEntry]', container);

      // when
      TestHelper.triggerEvent(addButton, 'click');
      TestHelper.triggerFormFieldSelection(1, container);
    });

    it('should be created', inject(function(bpmnFactory, elementRegistry) {

      // then
      expect(timetableFieldSelectBox.options).to.have.length(2);
      expect(timetableFieldSelectBox.options[0].value).to.equal('DEFAULT_TIMETABLE');
      expect(timetableFieldSelectBox.options[1].textContent).to.equal('Timetable');
      expect(getTimetables(bpmnFactory, elementRegistry).length).to.be.equal(2);
    }));

    it('should set correct name', inject(function(bpmnFactory, elementRegistry) {

      // when
      TestHelper.triggerValue(getInputField('timetable-name'), 'T1000', 'change');

      // then
      expect(getInputField('timetable-name').value).to.equal('T1000');
      expect(timetableFieldSelectBox.options[1].textContent).to.equal('T1000');
      expect(timetableFieldSelectBox.options[1].value).to.be.equal(getTimetableByName(bpmnFactory, elementRegistry, 'T1000').id);
      expect(getTimetableByName(bpmnFactory, elementRegistry, 'T1000')).to.not.be.undefined;
    }));

    it('should not set empty name', function() {

      // when
      TestHelper.triggerValue(getInputField('timetable-name'), '', 'change');

      // then
      expect(getInputField('timetable-name').value).to.equal('');
      expect(timetableFieldSelectBox.options[1].textContent).to.equal('N/A');
      expect(domClasses(getInputField('timetable-name')).has('invalid')).to.be.true;
    });

    it('should change timeslot begin day', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(getSelectBox('timeslot-fromWeekDay').value).to.equal('MONDAY');

      // when
      var timeslotEntryContainer = domQuery('[data-entry="timeslotEntry"]');
      TestHelper.triggerFormFieldSelection(0, timeslotEntryContainer);
      TestHelper.triggerValue(getSelectBox('timeslot-fromWeekDay'), 'TUESDAY', 'change');

      // then
      expect(getSelectBox('timeslot-fromWeekDay').value).to.equal('TUESDAY');
      expect(getTimetables(bpmnFactory, elementRegistry)[1].rules.get('values')[0].fromWeekDay).to.be.equal('TUESDAY');
    }));

    it('should change timeslot end day', inject(function(bpmnFactory, elementRegistry) {
      var timeslotEntryContainer = domQuery('[data-entry="timeslotEntry"]');
      TestHelper.triggerFormFieldSelection(0, timeslotEntryContainer);

      // assume
      expect(getSelectBox('timeslot-toWeekDay').value).to.equal('FRIDAY');

      // when
      TestHelper.triggerValue(getSelectBox('timeslot-toWeekDay'), 'THURSDAY', 'change');

      // then
      expect(getSelectBox('timeslot-toWeekDay').value).to.equal('THURSDAY');
      expect(getTimetables(bpmnFactory, elementRegistry)[1].rules.get('values')[0].toWeekDay).to.be.equal('THURSDAY');
    }));

    it('should change timeslot begin time', inject(function(bpmnFactory, elementRegistry) {
      var timeslotEntryContainer = domQuery('[data-entry="timeslotEntry"]');
      TestHelper.triggerFormFieldSelection(0, timeslotEntryContainer);

      // assume
      expect(getInputField('timeslot-fromTime').value).to.equal('09:00');

      // when
      TestHelper.triggerValue(getInputField('timeslot-fromTime'), '11:25', 'change');

      // then
      expect(getInputField('timeslot-fromTime').value).to.equal('11:25');
      expect(getTimetables(bpmnFactory, elementRegistry)[1].rules.get('values')[0].fromTime).to.be.equal('11:25:00.000+00:00');
    }));

    it('should change timeslot end time', inject(function(bpmnFactory, elementRegistry) {
      var timeslotEntryContainer = domQuery('[data-entry="timeslotEntry"]');
      TestHelper.triggerFormFieldSelection(0, timeslotEntryContainer);

      // assume
      expect(getInputField('timeslot-toTime').value).to.equal('17:00');

      // when
      TestHelper.triggerValue(getInputField('timeslot-toTime'), '18:00', 'change');

      // then
      expect(getInputField('timeslot-toTime').value).to.equal('18:00');
      expect(getTimetables(bpmnFactory, elementRegistry)[1].rules.get('values')[0].toTime).to.be.equal('18:00:00.000+00:00');
    }));

    it('should be removed', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(getTimetables(bpmnFactory, elementRegistry).length).to.equal(2);

      // when
      var removeButton = domQuery('button[id=cam-extensionElements-remove-timetableEntry]', container);
      TestHelper.triggerEvent(removeButton, 'click');

      // then
      expect(getTimetables(bpmnFactory, elementRegistry).length).to.equal(1);
      expect(getTimetables(bpmnFactory, elementRegistry)[1]).to.be.undefined;
    }));
  });

  describe('new timeslot', function() {
    var timeslotFieldSelectBox,
        timeslotEntryContainer;

    beforeEach(function() {
      TestHelper.triggerFormFieldSelection(0, container);

      var addButton = domQuery('button[id=cam-extensionElements-create-timeslotEntry]', container);

      // when
      TestHelper.triggerEvent(addButton, 'click');

      timeslotEntryContainer = domQuery('[data-entry="timeslotEntry"]');
      TestHelper.triggerFormFieldSelection(0, timeslotEntryContainer);
      timeslotFieldSelectBox = domQuery('select[name=selectedExtensionElement]', timeslotEntryContainer);
    });

    it('should be created', inject(function() {

      // then
      expect(timeslotFieldSelectBox.options).to.have.length(2);
      expect(timeslotFieldSelectBox.options[0].value).to.equal('rr4f9148-3d17-4f8a-44f9-8332ee74e999');
      expect(timeslotFieldSelectBox.options[1].textContent).to.equal('Timeslot');
    }));

    it('should not set empty name', function() {

      // when
      TestHelper.triggerValue(getInputField('timeslot-name'), '', 'change');

      // then
      expect(getInputField('timeslot-name').value).to.equal('');
      expect(timeslotFieldSelectBox.options[0].textContent).to.equal('N/A');
      expect(domClasses(getInputField('timeslot-name')).has('invalid')).to.be.true;
    });

    it('should be removed', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(getTimetables(bpmnFactory, elementRegistry)[0].get('rules').values.length).to.equal(2);

      // when
      var removeButton = domQuery('button[id=cam-extensionElements-remove-timeslotEntry]', container);
      TestHelper.triggerEvent(removeButton, 'click');

      // then
      expect(getTimetables(bpmnFactory, elementRegistry)[0].get('rules').values.length).to.equal(1);
    }));
  });
});