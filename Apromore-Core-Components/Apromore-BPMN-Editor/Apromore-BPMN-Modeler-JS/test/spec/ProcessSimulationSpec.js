var customTranslate = require('../../app/translate/customTranslate'),
    simulationModdleDescriptor = require('../../app/descriptors/simulation.json'),
    camundaModdleDescriptor = require('camunda-bpmn-moddle/resources/camunda'),
    apModdleDescriptor = require('../../app/descriptors/ap.json'),
    TestHelper = require('../TestHelper'),
    TestContainer = require('mocha-test-container-support');

var getProcessSimulationInfo = require('../../app/helper/ProcessSimulationHelper').getProcessSimulationInfo,
    getStatsOptions = require('../../app/helper/ProcessSimulationHelper').getStatsOptions,
    getInterArrivalTimeDistribution = require('../helper/modelHelper').getInterArrivalTimeDistribution;

var propertiesPanelModule = require('bpmn-js-properties-panel/lib'),
    propertiesProviderModule = require('app/provider'),
    domQuery = require('min-dom').query,
    domClasses = require('min-dom').classes,
    coreModule = require('bpmn-js/lib/core').default,
    selectionModule = require('diagram-js/lib/features/selection').default,
    modelingModule = require('bpmn-js/lib/features/modeling').default;

function getEntry(container, entryId) {
  return domQuery('div[data-entry="' + entryId + '"]', container);
}

describe('simulation-tab', function() {
  var diagramXML = require('./ProcessSimulation.bpmn');

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
      shape;

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
  }));

  describe('total number of instances', function() {

    var getProcessInstancesField,
        processInstancesField,
        processInstancesFieldLabel;

    beforeEach(inject(function(commandStack, propertiesPanel) {

      getProcessInstancesField = function() {
        return domQuery('input[name=processInstances]', propertiesPanel._container);
      };

      processInstancesField = getProcessInstancesField();

      processInstancesFieldLabel = domQuery('label[for=camunda-processInstances]', getEntry(container, 'processInstances'));
    }));

    it('should have correct label', function() {
      // then
      expect(processInstancesFieldLabel.textContent).to.equal('Total number of cases');
    });

    it('should fetch the value',function() {
      // then
      expect(processInstancesField.value).to.equal('1000');
    });

    it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(processInstancesField.value).to.equal('1000');

      // when
      TestHelper.triggerValue(processInstancesField, '10', 'change');

      // then
      expect(processInstancesField.value).to.equal('10');
      expect(domClasses(processInstancesField).has('invalid')).to.be.false;
      expect(getProcessSimulationInfo(bpmnFactory, elementRegistry).processInstances).to.equal('10');
    }));

    it('should not set negative value', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(processInstancesField.value).to.equal('1000');

      // when
      TestHelper.triggerValue(processInstancesField, '-10', 'change');

      // then
      expect(processInstancesField.value).to.equal('-10');
      expect(domClasses(processInstancesField).has('invalid')).to.be.true;
      expect(getProcessSimulationInfo(bpmnFactory, elementRegistry).processInstances).to.equal('-10');
    }));

    it('should not set not numeric value', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(processInstancesField.value).to.equal('1000');

      // when
      TestHelper.triggerValue(processInstancesField, 'text', 'change');

      // then
      expect(processInstancesField.value).to.equal('text');
      expect(domClasses(processInstancesField).has('invalid')).to.be.true;
      expect(getProcessSimulationInfo(bpmnFactory, elementRegistry).processInstances).to.equal('text');
    }));

    it('should not set empty value', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(processInstancesField.value).to.equal('1000');

      // when
      TestHelper.triggerValue(processInstancesField, '', 'change');

      // then
      expect(processInstancesField.value).to.equal('');
      expect(domClasses(processInstancesField).has('invalid')).to.be.true;
      expect(getProcessSimulationInfo(bpmnFactory, elementRegistry).processInstances).to.equal('');
    }));
  });

  describe('inter arrival time distribution', function() {

    describe('type', function() {
      var getDistributionNameField,
          distributionNameField,
          label;

      beforeEach(inject(function(commandStack, propertiesPanel) {

        getDistributionNameField = function() {
          return domQuery('select[name=type]', propertiesPanel._container);
        };

        distributionNameField = getDistributionNameField();

        label = domQuery('label[for=camunda-qbp_58cd21a1-714b-4389-9f41-31ab32a2dfd6distribution-type]', getEntry(container, 'type'));
      }));

      it('should have correct label', function() {
        // then
        expect(label.textContent).to.equal('Inter arrival time');
      });

      it('should fetch the value',function() {
        // then
        expect(distributionNameField.value).to.equal('NORMAL');
      });

      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(distributionNameField.value).to.equal('NORMAL');

        // when
        TestHelper.triggerValue(distributionNameField, 'FIXED', 'change');

        // then
        expect(distributionNameField.value).to.equal('FIXED');
        expect(domClasses(distributionNameField).has('invalid')).to.be.false;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).type).to.equal('FIXED');
      }));
    });

    describe('mean', function() {
      var getMeanField,
          meanField,
          label;

      beforeEach(inject(function(commandStack, propertiesPanel) {

        getMeanField = function() {
          return domQuery('input[name=mean]', propertiesPanel._container);
        };

        meanField = getMeanField();

        label = domQuery('label[for=camunda-qbp_58cd21a1-714b-4389-9f41-31ab32a2dfd6NORMAL-mean]', getEntry(container, 'mean'));
      }));

      it('should have correct label', function() {
        // then
        expect(label.textContent).to.equal('Mean');
      });

      it('should fetch the value',function() {
        // then
        expect(meanField.value).to.equal('1');
      });

      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(meanField.value).to.equal('1');

        // when
        TestHelper.triggerValue(meanField, '2', 'change');

        // then
        expect(meanField.value).to.equal('2');
        expect(domClasses(meanField).has('invalid')).to.be.false;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).mean).to.equal('120');
      }));

      it('should not set negative value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(meanField.value).to.equal('1');

        // when
        TestHelper.triggerValue(meanField, '-1', 'change');

        // then
        expect(domClasses(meanField).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).mean).to.equal('-60');
      }));

      it('should not set not numeric value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(meanField.value).to.equal('1');

        // when
        TestHelper.triggerValue(meanField, 'text', 'change');

        // then
        expect(domClasses(meanField).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).mean).to.equal('text');
      }));

      it('should not set empty value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(meanField.value).to.equal('1');

        // when
        TestHelper.triggerValue(meanField, '', 'change');

        // then
        expect(domClasses(meanField).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).mean).to.equal('');
      }));
    });

    describe('arg1', function() {
      var getArg1Field,
          arg1Field,
          label;

      beforeEach(inject(function(commandStack, propertiesPanel) {

        getArg1Field = function() {
          return domQuery('input[name=arg1]', propertiesPanel._container);
        };

        arg1Field = getArg1Field();

        label = domQuery('label[for=camunda-qbp_58cd21a1-714b-4389-9f41-31ab32a2dfd6NORMAL-arg1]', getEntry(container, 'arg1'));
      }));

      it('should have correct label', function() {
        // then
        expect(label.textContent).to.equal('Std deviation');
      });

      it('should fetch the value',function() {
        // then
        expect(arg1Field.value).to.equal('0.5');
      });

      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(arg1Field.value).to.equal('0.5');

        // when
        TestHelper.triggerValue(arg1Field, '60', 'change');

        // then
        expect(arg1Field.value).to.equal('60');
        expect(domClasses(arg1Field).has('invalid')).to.be.false;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).arg1).to.equal('3600');
      }));

      it('should not set negative value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(arg1Field.value).to.equal('0.5');

        // when
        TestHelper.triggerValue(arg1Field, '-1', 'change');

        // then
        expect(domClasses(arg1Field).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).arg1).to.equal('-60');
      }));

      it('should not set not numeric value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(arg1Field.value).to.equal('0.5');

        // when
        TestHelper.triggerValue(arg1Field, 'text', 'change');

        // then
        expect(domClasses(arg1Field).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).arg1).to.equal('text');
      }));

      it('should not set empty value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(arg1Field.value).to.equal('0.5');

        // when
        TestHelper.triggerValue(arg1Field, '', 'change');

        // then
        expect(domClasses(arg1Field).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).arg1).to.equal('');
      }));
    });

    describe('arg2', function() {
      var distributionNameField,
          getArg2Field,
          arg2Field,
          label;

      beforeEach(inject(function(commandStack, propertiesPanel) {

        distributionNameField = domQuery('select[name=type]', propertiesPanel._container);
        TestHelper.triggerValue(distributionNameField, 'UNIFORM', 'change');

        getArg2Field = function() {
          return domQuery('input[name=arg2]', propertiesPanel._container);
        };

        arg2Field = getArg2Field();

        label = domQuery('label[for=camunda-qbp_58cd21a1-714b-4389-9f41-31ab32a2dfd6UNIFORM-arg2]', getEntry(container, 'arg2'));
      }));

      it('should have correct label', function() {
        // then
        expect(label.textContent).to.equal('Maximum');
      });

      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(arg2Field.value).to.equal('');

        // when
        TestHelper.triggerValue(arg2Field, '100', 'change');

        // then
        expect(arg2Field.value).to.equal('100');
        expect(domClasses(arg2Field).has('invalid')).to.be.false;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).arg2).to.equal('6000');
      }));

      it('should not set negative value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(arg2Field.value).to.equal('');

        // when
        TestHelper.triggerValue(arg2Field, '-1', 'change');

        // then
        expect(domClasses(arg2Field).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).arg2).to.equal('-60');
      }));

      it('should not set not numeric value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(arg2Field.value).to.equal('');

        // when
        TestHelper.triggerValue(arg2Field, 'text', 'change');

        // then
        expect(domClasses(arg2Field).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).arg2).to.equal('text');
      }));

      it('should not set empty value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(arg2Field.value).to.equal('');

        // when
        TestHelper.triggerValue(arg2Field, '', 'change');

        // then
        expect(domClasses(arg2Field).has('invalid')).to.be.true;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).arg2).to.equal('NaN');
      }));
    });

    describe('time unit', function() {
      var getTimeUnitField,
          timeUnitField,
          label;

      beforeEach(inject(function(commandStack, propertiesPanel) {

        getTimeUnitField = function() {
          return domQuery('select[name=timeUnit]', propertiesPanel._container);
        };

        timeUnitField = getTimeUnitField();

        label = domQuery('label[for=camunda-qbp_58cd21a1-714b-4389-9f41-31ab32a2dfd6time-unit]', getEntry(container, 'timeUnit'));
      }));

      it('should have correct label', function() {
        // then
        expect(label.textContent).to.equal('Time unit');
      });

      it('should fetch the value',function() {
        // then
        expect(timeUnitField.value).to.equal('minutes');
      });

      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(timeUnitField.value).to.equal('minutes');

        // when
        TestHelper.triggerValue(timeUnitField, 'hours', 'change');

        // then
        expect(timeUnitField.value).to.equal('hours');
        expect(domClasses(timeUnitField).has('invalid')).to.be.false;
        expect(getInterArrivalTimeDistribution(bpmnFactory, elementRegistry).timeUnit).to.equal('hours');
      }));
    });
  });

  describe('scenario start date', function() {

    var getStartDateField,
        startDateField,
        startDateLabel;

    beforeEach(inject(function(commandStack, propertiesPanel) {

      getStartDateField = function() {
        return domQuery('input[name=startDate]', propertiesPanel._container);
      };

      startDateField = getStartDateField();

      startDateLabel = domQuery('label[for=camunda-startDate]', getEntry(container, 'startDate'));
    }));

    it('should have correct label', function() {
      // then
      expect(startDateLabel.textContent).to.equal('Scenario start date');
    });

    it('should fetch the value',function() {
      // then
      expect(startDateField.value).to.equal('2020-08-05');
    });

    it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(startDateField.value).to.equal('2020-08-05');

      // when
      TestHelper.triggerValue(startDateField, '2020-07-21', 'change');

      // then
      expect(startDateField.value).to.equal('2020-07-21');
      expect(domClasses(startDateField).has('invalid')).to.be.false;
      expect(getProcessSimulationInfo(bpmnFactory, elementRegistry).startDateTime).to.equal('2020-07-21T10:00:00.000Z');
    }));
  });

  describe('scenario start time', function() {

    var getStartTimeField,
        startTimeField,
        startTimeLabel;

    beforeEach(inject(function(commandStack, propertiesPanel) {

      getStartTimeField = function() {
        return domQuery('input[name=startTime]', propertiesPanel._container);
      };

      startTimeField = getStartTimeField();

      startTimeLabel = domQuery('label[for=camunda-startTime]', getEntry(container, 'startTime'));
    }));

    it('should have correct label', function() {
      // then
      expect(startTimeLabel.textContent).to.equal('Scenario start time');
    });

    it('should fetch the value',function() {
      // then
      expect(startTimeField.value).to.equal('20:00');
    });

    it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(startTimeField.value).to.equal('20:00');

      // when
      TestHelper.triggerValue(startTimeField, '08:30', 'change');

      // then
      expect(startTimeField.value).to.equal('08:30');
      expect(domClasses(startTimeField).has('invalid')).to.be.false;
      expect(getProcessSimulationInfo(bpmnFactory, elementRegistry).startDateTime).to.equal('2020-08-04T22:30:00.000Z');
    }));
  });

  describe('% to exclude from stats', function() {

    describe('from start', function() {

      var getExcludeFromStartFields,
          excludeFromStartField,
          excludeFromStartLabel;

      beforeEach(inject(function(commandStack, propertiesPanel) {

        getExcludeFromStartFields = function() {
          return domQuery('input[name=trimStartProcessInstances]', propertiesPanel._container);
        };

        excludeFromStartField = getExcludeFromStartFields();

        excludeFromStartLabel = domQuery('label[for=camunda-trimStartProcessInstances]', getEntry(container, 'trimStartProcessInstances'));
      }));

      it('should have correct label', function() {
        // then
        expect(excludeFromStartLabel.textContent).to.equal('% to exclude from stats at the start');
      });

      it('should fetch the value',function() {
        // then
        expect(excludeFromStartField.value).to.equal('10');
      });

      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromStartField.value).to.equal('10');

        // when
        TestHelper.triggerValue(excludeFromStartField, '5', 'change');

        // then
        expect(excludeFromStartField.value).to.equal('5');
        expect(domClasses(excludeFromStartField).has('invalid')).to.be.false;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimStartProcessInstances).to.equal('5');
      }));

      it('should not set negative value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromStartField.value).to.equal('10');

        // when
        TestHelper.triggerValue(excludeFromStartField, '-5', 'change');

        // then
        expect(excludeFromStartField.value).to.equal('-5');
        expect(domClasses(excludeFromStartField).has('invalid')).to.be.true;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimStartProcessInstances).to.be.equal('-5');
      }));

      it('should not set not numeric value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromStartField.value).to.equal('10');

        // when
        TestHelper.triggerValue(excludeFromStartField, 'text', 'change');

        // then
        expect(domClasses(excludeFromStartField).has('invalid')).to.be.true;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimStartProcessInstances).to.be.equal('text');
      }));

      it('should set empty value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromStartField.value).to.equal('10');

        // when
        TestHelper.triggerValue(excludeFromStartField, '', 'change');

        // then
        expect(domClasses(excludeFromStartField).has('invalid')).to.be.false;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimStartProcessInstances).to.be.undefined;
      }));

      it('should not set value greater than 40', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromStartField.value).to.equal('10');

        // when
        TestHelper.triggerValue(excludeFromStartField, '45', 'change');

        // then
        expect(domClasses(excludeFromStartField).has('invalid')).to.be.true;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimStartProcessInstances).to.be.equal('45');
      }));
    });

    describe('from end', function() {

      var getExcludeFromEndFields,
          excludeFromEndField,
          excludeFromEndLabel;

      beforeEach(inject(function(commandStack, propertiesPanel) {

        getExcludeFromEndFields = function() {
          return domQuery('input[name=trimEndProcessInstances]', propertiesPanel._container);
        };

        excludeFromEndField = getExcludeFromEndFields();

        excludeFromEndLabel = domQuery('label[for=camunda-trimEndProcessInstances]', getEntry(container, 'trimEndProcessInstances'));
      }));

      it('should have correct label', function() {
        // then
        expect(excludeFromEndLabel.textContent).to.equal('% to exclude from stats at the end');
      });

      it('should fetch the value',function() {
        // then
        expect(excludeFromEndField.value).to.equal('5');
      });

      it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromEndField.value).to.equal('5');

        // when
        TestHelper.triggerValue(excludeFromEndField, '10', 'change');

        // then
        expect(excludeFromEndField.value).to.equal('10');
        expect(domClasses(excludeFromEndField).has('invalid')).to.be.false;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimEndProcessInstances).to.equal('10');
      }));

      it('should not set negative value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromEndField.value).to.equal('5');

        // when
        TestHelper.triggerValue(excludeFromEndField, '-5', 'change');

        // then
        expect(excludeFromEndField.value).to.equal('-5');
        expect(domClasses(excludeFromEndField).has('invalid')).to.be.true;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimEndProcessInstances).to.be.equal('-5');
      }));

      it('should not set not numeric value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromEndField.value).to.equal('5');

        // when
        TestHelper.triggerValue(excludeFromEndField, 'text', 'change');

        // then
        expect(domClasses(excludeFromEndField).has('invalid')).to.be.true;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimEndProcessInstances).to.be.equal('text');
      }));

      it('should set empty value', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromEndField.value).to.equal('5');

        // when
        TestHelper.triggerValue(excludeFromEndField, '', 'change');

        // then
        expect(domClasses(excludeFromEndField).has('invalid')).to.be.false;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimEndProcessInstances).to.be.undefined;
      }));

      it('should not set value greater than 40', inject(function(bpmnFactory, elementRegistry) {

        // assume
        expect(excludeFromEndField.value).to.equal('5');

        // when
        TestHelper.triggerValue(excludeFromEndField, '45', 'change');

        // then
        expect(domClasses(excludeFromEndField).has('invalid')).to.be.true;
        expect(getStatsOptions(bpmnFactory, elementRegistry).trimEndProcessInstances).to.be.equal('45');
      }));
    });
  });

  describe('currency', function() {
    var getCurrencyField,
        currencyField,
        currencyLabel;

    beforeEach(inject(function(commandStack, propertiesPanel) {

      getCurrencyField = function() {
        return domQuery('select[name=currency]', propertiesPanel._container);
      };

      currencyField = getCurrencyField();

      currencyLabel = domQuery('label[for=camunda-currency]', getEntry(container, 'currency'));
    }));

    it('should have correct label', function() {
      // then
      expect(currencyLabel.textContent).to.equal('Currency');
    });

    it('should fetch the value',function() {
      // then
      expect(currencyField.value).to.equal('USD');
    });

    it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(currencyField.value).to.equal('USD');

      // when
      TestHelper.triggerValue(currencyField, 'EUR', 'change');

      // then
      expect(currencyField.value).to.equal('EUR');
      expect(domClasses(currencyField).has('invalid')).to.be.false;
      expect(getProcessSimulationInfo(bpmnFactory, elementRegistry).currency).to.equal('EUR');
    }));
  });


  describe('Arrival time', function() {
    var getTimetableField,
        timeTableField,
        timeTableLabel;

    beforeEach(inject(function(commandStack, propertiesPanel) {

      getTimetableField = function() {
        return domQuery('select[name=arrivalTimetable]', propertiesPanel._container);
      };

      timeTableField = getTimetableField();

      timeTableLabel = domQuery('label[for=camunda-arrivalTimetable]', getEntry(container, 'arrivalTimetable'));
    }));

    it('should have correct label', function() {
      // then
      expect(timeTableLabel.textContent).to.equal('Timetables');
    });

    it('should fetch the value',function() {
      // then
      expect(timeTableField.value).to.equal('DEFAULT_TIMETABLE');
    });

    it('should set correct value', inject(function(bpmnFactory, elementRegistry) {

      // assume
      expect(timeTableField.value).to.equal('DEFAULT_TIMETABLE');

      // when
      TestHelper.triggerValue(timeTableField, '', 'change');

      // then
      expect(timeTableField.value).to.equal('');
     
    }));
  });

});