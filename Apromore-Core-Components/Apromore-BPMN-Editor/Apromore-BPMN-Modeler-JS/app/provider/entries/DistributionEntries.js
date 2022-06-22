var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');

var validationHelper = require('../../helper/ValidationErrorHelper');
var normalizeNumber = require('../../utils/Utils').normalizeNumber;
var isValidNumber = require('../../utils/Utils').isValidNumber;

var createDistributionTypeOptions = function(translate) {
  return [{
    name: translate('distribution.fixed'),
    value: 'FIXED'
  },
  {
    name: translate('distribution.normal'),
    value: 'NORMAL'
  },
  {
    name: translate('distribution.exponential'),
    value: 'EXPONENTIAL'
  },
  {
    name: translate('distribution.uniform'),
    value: 'UNIFORM'
  },
  {
    name: translate('distribution.triangular'),
    value: 'TRIANGULAR'
  },
  {
    name: translate('distribution.logNormal'),
    value: 'LOGNORMAL'
  },
  {
    name: translate('distribution.gamma'),
    value: 'GAMMA'
  }
  ];
};

var timeUnits = {
  seconds: {
    value: 'seconds',
    unit: 1
  },
  minutes: {
    value: 'minutes',
    unit: 60
  },
  hours: {
    value: 'hours',
    unit: 3600
  },
  days: {
    value: 'days',
    unit: 86400
  }
};

var createTimeUnitOptions = function(translate) {
  return [{
    name: translate(timeUnits.seconds.value),
    value: timeUnits.seconds.value
  },
  {
    name: translate(timeUnits.minutes.value),
    value: timeUnits.minutes.value
  },
  {
    name: translate(timeUnits.hours.value),
    value: timeUnits.hours.value
  },
  {
    name: translate(timeUnits.days.value),
    value: timeUnits.days.value
  }
  ];
};

const preprocessDistNumber = (distribution, rawKey, key) => {
    let rawValue = distribution[rawKey]

    if (!rawValue) {
        rawValue = distribution[key]
    }
    // Fix any old NaN value
    if (rawValue === 'NaN') {
        rawValue = '';
        distribution[rawKey] = rawValue;
    }
    // use key as identifier of the widget
    return { [key]: rawValue };
};

const postprocessDistNumber = (distribution, values, rawKey, key) => {
    let rawValue = values[key]; // use key as identifier of the widget
    if (isValidNumber(rawValue)) {
      rawValue = normalizeNumber(rawValue); // clean up number
    }
    distribution[rawKey] = rawValue;
    return {
        [rawKey]: rawValue,
        [key]: (rawValue / timeUnits[distribution.timeUnit].unit).toString()
    }
};

module.exports = function(bpmnFactory, elementRegistry, translate, options) {
  var entries = [],
      id = options.id,
      distribution = options.distribution,
      label = options.label,
      elementId = options.elementId;

  function meanField(options) {
    var meanLabel = options && options.label || translate('distribution.mean');

    return entryFactory.textField(translate, {
      id: (id || '') + distribution.type + '-mean',
      label: meanLabel,
      modelProperty: 'mean',

      get: function(_element, _node) {
        return preprocessDistNumber(distribution, 'rawMean', 'mean');
      },

      set: function(element, values, _node) {
        return cmdHelper.updateBusinessObject(
          element,
          distribution,
          postprocessDistNumber(distribution, values, 'rawMean','mean')
        );
      },

      validate: function(element, values, _node) {
        var validationId = this.id;

        var error = validationHelper.validateDistributionMean(
            bpmnFactory,
            elementRegistry,
            translate,
            Object.assign({
              id: validationId,
              label: meanLabel,
              elementId: elementId || label,
              distribution: distribution,
              timeUnits: timeUnits
            }, postprocessDistNumber(distribution, values, 'rawMean','mean'))
        );

        if (!error.message) {
          validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { mean: error.message };
      }
    });
  }

  function arg1Field(options) {
    var arg1Label = options && options.label || translate('distribution.arg1');

    return entryFactory.textField(translate, {
      id: (id || '') + distribution.type + '-arg1',
      label: arg1Label,
      modelProperty: 'arg1',

      get: function(_element, _node) {
        return preprocessDistNumber(distribution, 'rawArg1', 'arg1')
      },

      set: function(element, values, _node) {
        return cmdHelper.updateBusinessObject(
            element,
            distribution,
            postprocessDistNumber(distribution, values, 'rawArg1', 'arg1')
        );
      },

      validate: function(element, values, _node) {
        var validationId = this.id;

        var error = validationHelper.validateDistributionArg1(
            bpmnFactory,
            elementRegistry,
            translate,
            Object.assign({
              id: validationId,
              label: arg1Label,
              elementId: elementId || label,
              distribution: distribution,
              timeUnits: timeUnits
            }, postprocessDistNumber(distribution, values, 'rawArg1', 'arg1'))
        );

        if (!error.message) {
          validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { arg1: error.message };
      }
    });
  }

  function arg2Field(options) {
    var arg2Label = options && options.label || translate('distribution.arg2');

    return entryFactory.textField(translate, {
      id: (id || '') + distribution.type + '-arg2',
      label: arg2Label,
      modelProperty: 'arg2',

      get: function(_element, _node) {
        return preprocessDistNumber(distribution, 'rawArg2', 'arg2');
      },

      set: function(element, values, _node) {
        return cmdHelper.updateBusinessObject(
            element,
            distribution,
            postprocessDistNumber(distribution, values, 'rawArg2', 'arg2')
        );
      },

      validate: function(element, values, _node) {
        var validationId = this.id;

        var error = validationHelper.validateDistributionArg2(
            bpmnFactory,
            elementRegistry,
            translate,
            Object.assign({
              id: validationId,
              label: arg2Label,
              elementId: elementId || label,
              distribution: distribution
            }, postprocessDistNumber(distribution, values, 'rawArg2', 'arg2'))
        );

        if (!error.message) {
          validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { arg2: error.message };
      }
    });
  }

  function getValidModelValue(value) {
    if (isNaN(value) || value === '') {
      value = '';
    }

    return value;
  }

  var createDistributionFields = function() {
    validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { elementId: elementId || label });

    return {
      'fixed': function() {
        distribution.arg1 = getValidModelValue(distribution.arg1);
        distribution.arg2 = getValidModelValue(distribution.arg2);

        return [meanField({ label: translate('distribution.value') })];
      },

      'normal': function() {
        distribution.arg2 = getValidModelValue(distribution.arg2);

        return [meanField(), arg1Field({ label: translate('distribution.stdDeviation') })];
      },

      'exponential': function() {
        distribution.mean = getValidModelValue(distribution.mean);
        distribution.arg2 = getValidModelValue(distribution.arg2);

        return [arg1Field({ label: translate('distribution.mean') })];
      },

      'uniform': function() {
        distribution.mean = getValidModelValue(distribution.mean);

        return [
          arg1Field({ label: translate('distribution.min') }),
          arg2Field({ label: translate('distribution.max') })
        ];
      },

      'triangular': function() {
        return [
          arg1Field({ label: translate('distribution.min') }),
          meanField({ label: translate('distribution.mode') }),
          arg2Field({ label: translate('distribution.max') })
        ];
      },

      'lognormal': function() {
        distribution.arg2 = getValidModelValue(distribution.arg2);

        return [meanField(), arg1Field({ label: translate('distribution.variance') })];
      },

      'gamma': function() {
        distribution.arg2 = getValidModelValue(distribution.arg2);

        return [meanField(), arg1Field({ label: translate('distribution.variance') })];
      },

      'default': function() {
        return [];
      }
    };
  };

  entries.push(entryFactory.selectBox(translate, {
    id: id + 'distribution-type',
    label: label,
    modelProperty: 'type',
    selectOptions: createDistributionTypeOptions(translate),

    get: function(_element, _node) {
      return {
        type: distribution.type || 'FIXED'
      };
    },

    set: function(element, values, _node) {
      return cmdHelper.updateBusinessObject(element, distribution, {
        type: values.type
      });
    }
  }));

  var fields = createDistributionFields();
  var distributionEntries = (fields[distribution.type.toLowerCase()] || fields['default'])();
  entries = entries.concat(distributionEntries);

  entries.push(entryFactory.selectBox(translate, {
    id: id + 'time-unit',
    label: translate('timeUnit'),
    modelProperty: 'timeUnit',
    selectOptions: createTimeUnitOptions(translate),

    get: function(_element, _node) {
      return {
        timeUnit: distribution.timeUnit || 'seconds'
      };
    },

    set: function(element, values, _node) {
      return cmdHelper.updateBusinessObject(element, distribution, {
        mean: (distribution.mean / timeUnits[distribution.timeUnit].unit * timeUnits[values.timeUnit].unit).toString(),
        arg1: (distribution.arg1 / timeUnits[distribution.timeUnit].unit * timeUnits[values.timeUnit].unit).toString(),
        arg2: (distribution.arg2 / timeUnits[distribution.timeUnit].unit * timeUnits[values.timeUnit].unit).toString(),
        timeUnit: values.timeUnit,
      });
    }
  }));

  return entries;
};
