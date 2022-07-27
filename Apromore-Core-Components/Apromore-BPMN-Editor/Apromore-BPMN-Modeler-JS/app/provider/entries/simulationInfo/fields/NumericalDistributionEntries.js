var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var validationHelper = require('../../../../helper/ValidationErrorHelper');
var normalizeNumber = require('../../../../utils/Utils').normalizeNumber;
var isValidNumber = require('../../../../utils/Utils').isValidNumber;
var NumericalDistributionHelper = require('../../../../helper/NumericalDistributionHelper');

var createDistributionTypeOptions = function (translate) {
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


const preprocessDistNumber = (distribution, rawKey, key) => {
  if (!distribution || !distribution[key]) {
    return { [key]: '' };
  }
  let rawValue = distribution[key]
  // Fix any old NaN value
  if (rawValue === '') {
    rawValue = '0';
  }
  // use key as identifier of the widget
  return { [key]: rawValue };
};

const postprocessDistNumber = (distribution, values, rawKey, key) => {
  let rawValue = values[key]; // use key as identifier of the widget
  if (isValidNumber(rawValue)) {
    rawValue = normalizeNumber(rawValue); // clean up number
  }
  // distribution[rawKey] = rawValue;
  return {
    [rawKey]: rawValue,
    [key]: rawValue
  }
};

module.exports = function (elementRoot, bpmnFactory, elementRegistry, translate, options) {
  var entries = [],
    id = options.id,
    label = options.label,
    elementId = options.elementId,
    distribution;

  let getSelectedVariable = options.getSelectedVariable;

  function meanField(options) {
    var meanLabel = options && options.label || translate('distribution.mean');

    return entryFactory.textField(translate, {
      id: (id || '') + '-mean',
      label: meanLabel,
      modelProperty: 'mean',
      hidden: function (_element, _node) {
        let currentDistribution = getSelectedNumericDistribution(_element, _node);
        return !currentDistribution;
      },
      get: function (_element, _node) {
        let currentDistribution = getSelectedNumericDistribution(_element, _node);
        let ret = preprocessDistNumber(currentDistribution, 'rawMean', 'mean');
        return ret;
      },

      set: function (element, values, _node) {
        let currentDistribution = getSelectedNumericDistribution(element, _node);
        return cmdHelper.updateBusinessObject(
          element,
          currentDistribution,
          postprocessDistNumber(currentDistribution, values, 'rawMean', 'mean')
        );
      }
      ,
      validate: function (element, values, _node) {
        let currentDistribution = getSelectedNumericDistribution(element, _node);
        if (!currentDistribution) {
          return { mean: undefined };
        }
        var validationId = this.id;
        var selectedVariable = getSelectedVariable(element, _node);
        let selectedName = selectedVariable && selectedVariable.name ||'';

        var error = validationHelper.validateDistributionMean(
          bpmnFactory,
          elementRegistry,
          translate,
          Object.assign({
            id: validationId,
            label: meanLabel,
            elementId: selectedName,
            distribution: distribution
          }, postprocessDistNumber(distribution, values, 'rawMean', 'mean'))
        );
      
        if (!error.message) {
          validationHelper.suppressValidationErrorForCaseAttribute(bpmnFactory, elementRegistry, { id: validationId,elementId:selectedName });
        }

        return { mean: error.message };
      }

    });
  }

  function arg1Field(options) {
    var arg1Label = options && options.label || translate('distribution.arg1');

    return entryFactory.textField(translate, {
      id: (id || '') + '-arg1',
      label: arg1Label,
      modelProperty: 'arg1',
      hidden: function (_element, _node) {
        let currentDistribution = getSelectedNumericDistribution(_element, _node);
        return !currentDistribution;
      },
      get: function (_element, _node) {
        let currentDistribution = getSelectedNumericDistribution(_element, _node);
        let ret = preprocessDistNumber(currentDistribution, 'rawArg1', 'arg1');
        return ret;
      },

      set: function (element, values, _node) {
        let currentDistribution = getSelectedNumericDistribution(element, _node);
        return cmdHelper.updateBusinessObject(
          element,
          currentDistribution,
          postprocessDistNumber(currentDistribution, values, 'rawArg1', 'arg1')
        );
      }
      ,

      validate: function (element, values, _node) {
        let currentDistribution = getSelectedNumericDistribution(element, _node);
        if (!currentDistribution) {
          return { arg1: undefined };
        }
        var validationId = this.id;
        var selectedVariable = getSelectedVariable(element, _node);
        let selectedName = selectedVariable && selectedVariable.name ||'';

        var error = validationHelper.validateDistributionArg1(
          bpmnFactory,
          elementRegistry,
          translate,
          Object.assign({
            id: validationId,
            label: arg1Label,
            elementId: selectedName,
            distribution: distribution
          }, postprocessDistNumber(distribution, values, 'rawArg1', 'arg1'))
        );
       
        if (!error.message) {
          validationHelper.suppressValidationErrorForCaseAttribute(bpmnFactory, elementRegistry, { id: validationId,elementId:selectedName });
        }

        return { arg1: error.message };
      }

    });
  }

  function arg2Field(options) {
    var arg2Label = options && options.label || translate('distribution.arg2');

    return entryFactory.textField(translate, {
      id: (id || '') + '-arg2',
      label: arg2Label,
      modelProperty: 'arg2',
      hidden: function (element, _node) {
        let currentDistribution = getSelectedNumericDistribution(element, _node);
        return !currentDistribution;
      },
      get: function (_element, _node) {
        let currentDistribution = getSelectedNumericDistribution(_element, _node);
        let ret = preprocessDistNumber(currentDistribution, 'rawArg2', 'arg2');
        return ret;
      },

      set: function (element, values, _node) {
        let currentDistribution = getSelectedNumericDistribution(element, _node);
        return cmdHelper.updateBusinessObject(
          element,
          currentDistribution,
          postprocessDistNumber(currentDistribution, values, 'rawArg2', 'arg2')
        );
      }
      ,
      validate: function (element, values, _node) {
        let currentDistribution = getSelectedNumericDistribution(element, _node);
        if (!currentDistribution) {
          return { arg2: undefined };
        }
        var validationId = this.id;
        var selectedVariable = getSelectedVariable(element, _node);
        let selectedName = selectedVariable && selectedVariable.name ||'';

        var error = validationHelper.validateDistributionArg2(
          bpmnFactory,
          elementRegistry,
          translate,
          Object.assign({
            id: validationId,
            label: arg2Label,
            elementId: selectedName,
            distribution: distribution
          }, postprocessDistNumber(distribution, values, 'rawArg2', 'arg2'))
        );
        if (error) {
          let currentDistribution = getSelectedNumericDistribution(element, _node);
          if (!currentDistribution) {
            error.message = undefined;
          }
        }

        if (!error.message) {
          validationHelper.suppressValidationErrorForCaseAttribute(bpmnFactory, elementRegistry, { id: validationId,elementId:selectedName });
        }

        return { arg2: error.message };
      }
    });
  }

  function getValidModelValue(value) {
    if (value === '') {
      value = '0';
    }
    return value;
  }

  var createDistributionFields = function () {
   
    // validationHelper.suppressValidationError(bpmnFactory, elementRegistry, { elementId: elementId || label });
    return {
      'fixed': function () {
        if (distribution) {
          distribution.arg1 = getValidModelValue(distribution.arg1);
          distribution.arg2 = getValidModelValue(distribution.arg2);
        }
        return [meanField({ label: translate('distribution.value') })];
      },

      'normal': function () {
        if (distribution) {
          distribution.arg2 = getValidModelValue(distribution.arg2);
        }
        return [meanField(), arg1Field({ label: translate('distribution.stdDeviation') })];
      },

      'exponential': function () {
        if (distribution) {
          distribution.mean = getValidModelValue(distribution.mean);
          distribution.arg2 = getValidModelValue(distribution.arg2);
        }
        return [arg1Field({ label: translate('distribution.mean') })];
      },

      'uniform': function () {
        if (distribution) {
          distribution.mean = getValidModelValue(distribution.mean);
        }
        return [
          arg1Field({ label: translate('distribution.min') }),
          arg2Field({ label: translate('distribution.max') })
        ];
      },

      'triangular': function () {
        return [
          arg1Field({ label: translate('distribution.min') }),
          meanField({ label: translate('distribution.mode') }),
          arg2Field({ label: translate('distribution.max') })
        ];
      },

      'lognormal': function () {
        if (distribution) {
          distribution.arg2 = getValidModelValue(distribution.arg2);
        }
        return [meanField(), arg1Field({ label: translate('distribution.variance') })];
      },

      'gamma': function () {
        if (distribution) {
          distribution.arg2 = getValidModelValue(distribution.arg2);
        }
        return [meanField(), arg1Field({ label: translate('distribution.variance') })];
      },

      'default': function () {
        return [];
      }
    };
  };

  let typeSelectBox = entryFactory.selectBox(translate, {
    id: id + '-type',
    label: label,
    modelProperty: 'type',
    selectOptions: createDistributionTypeOptions(translate),
    hidden: function (element, _node) {
      let currentDistribution = getSelectedNumericDistribution(element, _node);
      return !currentDistribution;
    },
    get: function (_element, _node) {
      let currentDistribution = getSelectedNumericDistribution(_element, _node);
      return {
        type: (currentDistribution && currentDistribution.type) || 'FIXED'
      };
    },

    set: function (element, values, _node) {
      let currentDistribution = getSelectedNumericDistribution(element, _node);
      return cmdHelper.updateBusinessObject(element, currentDistribution, {
        type: values.type
      });
    }

  });

  entries.push(typeSelectBox);

  function getSelectedNumericDistribution(element, node) {
    distribution = undefined;
    var selectedVariable = getSelectedVariable(element, node);
    if (selectedVariable && selectedVariable.type && selectedVariable.type === 'NUMERIC' && selectedVariable.numeric) {
      distribution = selectedVariable.numeric;
    }
    if (!distribution) {
      getDistributionInfo();
    }

    return distribution;
  }

  function getDistributionInfo() {
    let selectedCaseAttributeValue = NumericalDistributionHelper.getCurrentCaseAttribute();
    distribution = undefined;
    if (selectedCaseAttributeValue && selectedCaseAttributeValue.type && selectedCaseAttributeValue.type === 'NUMERIC' && selectedCaseAttributeValue.numeric) {
      distribution = selectedCaseAttributeValue.numeric;
    }
  }
  getDistributionInfo();

  var fields = createDistributionFields();
  let fieldoption = (distribution && distribution.type && distribution.type.toLowerCase()) || 'fixed';
  var distributionEntries = (fields[fieldoption])();
  entries = entries.concat(distributionEntries);

  return entries;
};
