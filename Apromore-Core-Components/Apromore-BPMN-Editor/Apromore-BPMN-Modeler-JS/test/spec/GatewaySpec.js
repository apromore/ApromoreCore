var customTranslate = require('../../app/translate/customTranslate'),
    simulationModdleDescriptor = require('../../app/descriptors/simulation.json'),
    camundaModdleDescriptor = require('camunda-bpmn-moddle/resources/camunda'),
    apModdleDescriptor = require('../../app/descriptors/ap.json'),
    TestHelper = require('../TestHelper'),
    TestContainer = require('mocha-test-container-support'),
    getSequenceFlowById = require('../helper/modelHelper').getSequenceFlowById;

var propertiesPanelModule = require('bpmn-js-properties-panel/lib'),
    propertiesProviderModule = require('app/provider'),
    domQuery = require('min-dom').query,
    domClasses = require('min-dom').classes,
    coreModule = require('bpmn-js/lib/core').default,
    selectionModule = require('diagram-js/lib/features/selection').default,
    modelingModule = require('bpmn-js/lib/features/modeling').default;

describe('simulation-tab', function() {
  var diagramXML = require('./Gateway.bpmn');

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
      getLabel,
      getInputField,
      getErrorMessage;

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
      return domQuery('input[id=camunda-probability-field-' + id + ']', propertiesPanel._container);
    };

    getLabel = function(id) {
      return domQuery('label[for=camunda-probability-field-' + id + ']', propertiesPanel._container);
    };

    getErrorMessage = function(id) {
      return domQuery('[data-entry=probability-field-'+id+'] div[class=bpp-error-message]', propertiesPanel._container);
    };
  }));

  describe('existing gateway with 2 outgoing tasks', function() {

    it('should fetch probabilities', function() {

      // then
      expect(getInputField('Flow_0gp9p1v').value).to.equal('60');
      expect(getInputField('Flow_0hbj45g').value).to.equal('40');
    });

    it('should fetch labels', function() {

      // then
      expect(getLabel('Flow_0gp9p1v').textContent).to.equal('Task 1');
      expect(getLabel('Flow_0hbj45g').textContent).to.equal('Task 2');
    });

    it('should set correct probabilities', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(getInputField('Flow_0gp9p1v').value).to.equal('60');
      expect(getInputField('Flow_0hbj45g').value).to.equal('40');

      // when
      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '', 'change');
      TestHelper.triggerValue(getInputField('Flow_0hbj45g'), '', 'change');

      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '30', 'change');
      TestHelper.triggerValue(getInputField('Flow_0hbj45g'), '70', 'change');

      // then
      expect(getSequenceFlowById(bpmnFactory, elementRegistry, 'Flow_0gp9p1v').executionProbability).to.be.equal('0.3');
      expect(getSequenceFlowById(bpmnFactory, elementRegistry, 'Flow_0hbj45g').executionProbability).to.be.equal('0.7');
    }));

    it('should display correct probabilities', function() {

      // when
      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '', 'change');
      TestHelper.triggerValue(getInputField('Flow_0hbj45g'), '', 'change');

      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '30', 'change');
      TestHelper.triggerValue(getInputField('Flow_0hbj45g'), '70', 'change');

      // then
      expect(getInputField('Flow_0gp9p1v').value).to.equal('30');
      expect(getInputField('Flow_0hbj45g').value).to.equal('70');
      expect(domClasses(getInputField('Flow_0gp9p1v')).has('invalid')).to.be.false;
    });

    it('should not set negative probability', function() {

      // when
      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '-10', 'change');

      // then
      expect(getInputField('Flow_0gp9p1v').value).to.equal('-10');
      expect(domClasses(getInputField('Flow_0gp9p1v')).has('invalid')).to.be.true;
      expect(getErrorMessage('Flow_0gp9p1v').textContent).to.be.equal('Probability must be a valid positive number');
    });

    it('should not set a probability greater than 100', function() {

      // when
      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '200', 'change');

      // then
      expect(getInputField('Flow_0gp9p1v').value).to.equal('200');
      expect(domClasses(getInputField('Flow_0gp9p1v')).has('invalid')).to.be.true;
      expect(getErrorMessage('Flow_0gp9p1v').textContent).to.be.equal('Probability must not exceed 100%');
    });

    it('should not set a sum probability greater than 100', function() {

      // when
      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '60', 'change');
      TestHelper.triggerValue(getInputField('Flow_0hbj45g'), '60', 'change');

      // then
      expect(domClasses(getInputField('Flow_0hbj45g')).has('invalid')).to.be.true;
      expect(getErrorMessage('Flow_0hbj45g').textContent).to.be.equal('Sum of exclusive gateway probabilities must be equal to 100%');
    });

    it('should not set a sum probability less than 100', function() {

      // when
      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '', 'change');
      TestHelper.triggerValue(getInputField('Flow_0hbj45g'), '', 'change');

      TestHelper.triggerValue(getInputField('Flow_0gp9p1v'), '40', 'change');
      TestHelper.triggerValue(getInputField('Flow_0hbj45g'), '20', 'change');

      // then
      expect(domClasses(getInputField('Flow_0hbj45g')).has('invalid')).to.be.true;
      expect(getErrorMessage('Flow_0hbj45g').textContent).to.be.equal('Sum of exclusive gateway probabilities must be equal to 100%');
    });
  });
});