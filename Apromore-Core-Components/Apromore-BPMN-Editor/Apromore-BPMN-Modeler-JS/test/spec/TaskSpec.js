var customTranslate = require('../../app/translate/customTranslate'),
    simulationModdleDescriptor = require('../../app/descriptors/simulation.json'),
    TestHelper = require('../TestHelper'),
    TestContainer = require('mocha-test-container-support'),
    getFirstTaskById = require('../helper/modelHelper').getFirstTaskById;

var propertiesPanelModule = require('bpmn-js-properties-panel/lib'),
    propertiesProviderModule = require('app/provider'),
    domQuery = require('min-dom').query,
    coreModule = require('bpmn-js/lib/core').default,
    selectionModule = require('diagram-js/lib/features/selection').default,
    modelingModule = require('bpmn-js/lib/features/modeling').default;

describe('resource-tab', function() {
  var diagramXML = require('./Task.bpmn');

  var customTranslateModule = {
    translate: [ 'value', customTranslate ]
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
      getSelectBoxByTaskId,
      getInputFieldByTaskId;

  beforeEach(function() {
    container = TestContainer.get(this);
  });

  beforeEach(bootstrapModeler(diagramXML, {
    modules: testModules,
    moddleExtensions: {
      qbp: simulationModdleDescriptor
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

    getInputFieldByTaskId = function(taskId, name) {
      return domQuery('input[id=camunda-'+taskId+name+']', propertiesPanel._container);
    };

    getSelectBoxByTaskId = function(taskId, name) {
      return domQuery('select[id=camunda-'+taskId+name+'-select]', propertiesPanel._container);
    };
  }));

  describe('existing task', function() {
    var task;

    beforeEach(inject(function(bpmnFactory, elementRegistry) {
      task = getFirstTaskById(bpmnFactory, elementRegistry, 'Activity_1qp5gba');
    }));

    it('should fetch properties', function() {

      // then
      expect(getSelectBoxByTaskId('task-' + task.elementId, '-resource').value).to.be.equal('qbp_180d89ca-9007-44ca-8058-e0aa5e99acc8');
      expect(getSelectBoxByTaskId('task-' + task.elementId, '-resource')[0].textContent).to.be.equal('R1');
      expect(getSelectBoxByTaskId(task.elementId, 'distribution-type')[0].value).to.be.equal('FIXED');
      expect(getInputFieldByTaskId(task.elementId, 'NORMAL-mean').value).to.be.equal('2');
      expect(getInputFieldByTaskId(task.elementId, 'NORMAL-arg1').value).to.be.equal('1');
      expect(getSelectBoxByTaskId(task.elementId, 'time-unit').value).to.be.equal('minutes');
    });

    it('should set empty resource', function() {

      // assume
      expect(getSelectBoxByTaskId('task-'+task.elementId, '-resource').value).to
        .equal('qbp_180d89ca-9007-44ca-8058-e0aa5e99acc8');

      // when
      TestHelper.triggerValue(getSelectBoxByTaskId('task-' + task.elementId, '-resource'), '', 'change');

      // then
      expect(getSelectBoxByTaskId('task-' + task.elementId, '-resource').value).to.be.equal('');
    });

    it('should change distribution type', function() {

      // assume
      expect(getSelectBoxByTaskId(task.elementId, 'distribution-type').value).to.equal('NORMAL');

      // when
      TestHelper.triggerValue(getSelectBoxByTaskId(task.elementId, 'distribution-type'), 'FIXED', 'change');

      // then
      expect(task.durationDistribution.type).to.be.equal('FIXED');
    });

    it('should set correct distribution parameters', function() {

      // assume
      expect(getInputFieldByTaskId(task.elementId, 'NORMAL-mean').value).to.equal('2');
      expect(getInputFieldByTaskId(task.elementId, 'NORMAL-arg1').value).to.equal('1');
      expect(getSelectBoxByTaskId(task.elementId, 'time-unit').value).to.equal('minutes');

      // when
      TestHelper.triggerValue(getInputFieldByTaskId(task.elementId, 'NORMAL-mean'), '3', 'change');
      TestHelper.triggerValue(getInputFieldByTaskId(task.elementId, 'NORMAL-arg1'), '1', 'change');
      TestHelper.triggerValue(getSelectBoxByTaskId(task.elementId, 'time-unit'), 'hours', 'change');

      // then
      expect(task.durationDistribution.mean).to.be.equal('10800');
      expect(task.durationDistribution.arg1).to.be.equal('3600');
      expect(task.durationDistribution.timeUnit).to.be.equal('hours');
    });
  });
});