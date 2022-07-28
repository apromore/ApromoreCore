var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  validationErrorHelper = require('../../../../helper/ValidationErrorHelper'),
  CustomCheckBox = require('./CustomCheckboxBound'),
  isValidNumber = require('../../../../utils/Utils').isValidNumber;
module.exports = function (bpmnFactory, elementRegistry, translate, options) {

  var entries = [];

  var getSelectedClause = options.getSelectedClause;
  var isNumeric = options.isNumeric;
  var labelLoweValue = translate('gateway.attribute.lower.bound');
  var outgoingElementId = options.outgoingElementId;

  function getCurrentSelectedClause(element, node) {
    let clause = getSelectedClause(element, node);
    return clause;
  }

  function hide(element, node) {
    let numeric = isNumeric();
    let clause = getCurrentSelectedClause(element, node);
    return !numeric || !clause || clause.operator != 'BTW';
  }

  var lowerValueTextField = entryFactory.textField(translate, {
    id: 'gateway-attribute-lower-value-' + options.outgoingElementId,
    label: labelLoweValue,
    modelProperty: 'lowerVariableNumValue',
    hidden: function (element, node) {
      return hide(element, node);
    }
    ,
    get: function (element, node) {
      let clause = getCurrentSelectedClause(element, node);
      let lowerValue = getLowerValue(clause);
      return { lowerVariableNumValue: lowerValue && lowerValue || '0' };
    },

    set: function (element, values, node) {
      let clause = getCurrentSelectedClause(element, node);
      let combinedValue = getCombinedValue(clause, values.lowerVariableNumValue, 1);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableNumValue: combinedValue || '0...0'
      });
    }
  });

  lowerValueTextField.cssClasses.push('apromore-between-container');

  var lowerBoundCheckbox = CustomCheckBox(translate, {
    id: 'gateway-attribute-lower-checkbox-' + options.outgoingElementId,
    label: translate('gateway.attribute.lower.bound.including'),
    fieldLabel: labelLoweValue,
    modelProperty: 'isLowerBoundInclude',
    hidden: function (element, node) {
      return hide(element, node);
    },
    get: function (element, node) {
      let clause = getCurrentSelectedClause(element, node);
      return { isLowerBoundInclude: getLowerBound(clause) };
    },
    set: function (element, values) {
      let clause = getCurrentSelectedClause(element, values);
      let combinedValue = getCombinedValue(clause, values.isLowerBoundInclude, 0);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableNumValue: combinedValue || '0...0'
      });
    }
  });
  lowerValueTextField.cssClasses.push('apromore-bound-checkbox');
  entries.push(lowerBoundCheckbox);
  entries.push(lowerValueTextField);

  var labelUpperValue = translate('gateway.attribute.upper.bound');
  var upperValueTextField = entryFactory.textField(translate, {
    id: 'gateway-attribute-uppper-value-' + options.outgoingElementId,
    label: labelUpperValue,
    modelProperty: 'upperVariableNumValue'
    ,
    hidden: function (element, node) {
      return hide(element, node);
    }
    ,
    get: function (element, node) {
      let clause = getCurrentSelectedClause(element, node);
      let upperValue = getUpperValue(clause);
      return { upperVariableNumValue: upperValue && upperValue || '0' };
    },

    set: function (element, values, node) {
      let clause = getCurrentSelectedClause(element, node);
      let combinedValue = getCombinedValue(clause, values.upperVariableNumValue, 2);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableNumValue: combinedValue || '0...0'
      });

    }
  });

  upperValueTextField.cssClasses.push('apromore-between-container');

  var upperBoundCheckbox = CustomCheckBox(translate, {
    id: 'gateway-attribute-upper-checkbox-' + options.outgoingElementId,
    label: translate('gateway.attribute.upper.bound.including'),
    fieldLabel: labelUpperValue,
    modelProperty: 'isUpperBoundInclude',
    hidden: function (element, node) {
      return hide(element, node);
    },
    get: function (element, node) {
      let clause = getCurrentSelectedClause(element, node);
      return { isUpperBoundInclude: getUpperBound(clause) };
    },
    set: function (element, values) {
      let clause = getCurrentSelectedClause(element, values);
      let combinedValue = getCombinedValue(clause, values.isUpperBoundInclude, 3);
      return cmdHelper.updateBusinessObject(element, clause, {
        variableNumValue: combinedValue || '0...0'
      });
    }
  });

  upperBoundCheckbox.cssClasses.push('apromore-bound-checkbox');

  entries.push(upperBoundCheckbox);
  entries.push(upperValueTextField);


  let LabelFactory = function () {
    return {
      id: 'gateway-attribute-error-' + outgoingElementId,
      html: '<label data-value="label" ' +
        'data-show="showLabel" ' +
        'class="invalid-message' + (options.divider ? ' divider' : '') + '">' +
        '</label>',
      get: function (element, node) {
        let elementId = 'Bound Value';
        let clause = getCurrentSelectedClause(element, node);
        let skipValue = !isNumeric() || !clause || clause.operator != 'BTW';
        validationId = outgoingElementId;
        if (skipValue) {
          validationErrorHelper.suppressValidationErrorWithOnlyId(bpmnFactory, elementRegistry, { id: validationId });
          return { label: '' };
        }
        let lowerCurrentValue = getLowerValue(clause);
        let upperCurrentValue = getUpperValue(clause);
        let labelText = undefined;
        if (isValidNumber(lowerCurrentValue) && isValidNumber(upperCurrentValue)) {
          if (lowerCurrentValue >= upperCurrentValue) {
            labelText = translate('gateway.attribute.invalid.uppper.lower.value');
          }
        } else {
          labelText = translate('gateway.attribute.invalid.bound.value');
        }
        if (!labelText) {
          validationErrorHelper.suppressValidationErrorWithOnlyId(bpmnFactory, elementRegistry, { id: validationId });
        } else {
          validationErrorHelper.createValidationError(bpmnFactory, elementRegistry, {
            id: validationId,
            elementId: elementId,
            message: labelText
          });
        }
        return { label: labelText };
      },
      showLabel: function (element, node) {
        let numeric = isNumeric();
        let clause = getCurrentSelectedClause(element, node);
        return numeric && clause && clause.operator == 'BTW';
      }
    };

  };

  entries.push(LabelFactory());

  function getCombinedValue(clause, updateValue, position) {
    let value = '';
    let lowerCurrentValue = getLowerValue(clause);
    let upperCurrentValue = getUpperValue(clause);
    let currentLowerIncluding = getLowerBound(clause) ? '(' : '';
    let currentUpperIncluding = getUpperBound(clause) ? ')' : '';

    if (clause && position >= 0) {
      if (position == 0) {
        if (updateValue && updateValue == true) {
          currentLowerIncluding = "(";
        } else {
          currentLowerIncluding = "";
        }
      }
      else if (position == 1) {
        lowerCurrentValue = updateValue;
      }
      if (position == 2) {
        upperCurrentValue = updateValue;
      }
      if (position == 3) {
        if (updateValue && updateValue == true) {
          currentUpperIncluding = ")";
        } else {
          currentUpperIncluding = "";
        }
      }
    }

    value = currentLowerIncluding + lowerCurrentValue + "..." + upperCurrentValue + currentUpperIncluding;
    return value;
  }

  function getLowerValue(clause) {
    let startIndex = 0;
    let value = 0;
    if (clause && clause.variableNumValue && clause.variableNumValue.length > 0) {
      if (clause.variableNumValue.charAt(0) === '(') {
        startIndex = 1;
      }
      let endIndex = clause.variableNumValue.indexOf('...');
      if (endIndex == -1) {
        endIndex = clause.variableNumValue.length - 1;
      }
      value = clause.variableNumValue.substr(startIndex, endIndex - startIndex);
    }
    return value;
  }

  function getUpperValue(clause) {
    let startIndex = 0;
    let endIndex = 0;
    let value = 0;
    if (clause && clause.variableNumValue && clause.variableNumValue.length > 1) {
      if (clause.variableNumValue.indexOf(')') > -1) {
        endIndex = clause.variableNumValue.indexOf(')');
      } else {
        endIndex = clause.variableNumValue.length;
      }

      startIndex = clause.variableNumValue.indexOf('...');
      if (startIndex == -1) {
        startIndex = 0;
      } else {
        startIndex = startIndex + 3;
      }
      if (startIndex >= 0 && endIndex > 0 && startIndex < endIndex) {
        value = clause.variableNumValue.substr(startIndex, (endIndex - startIndex));
      }
    }
    return value;
  }

  function getLowerBound(clause) {
    if (clause && clause.variableNumValue && clause.variableNumValue.length > 0) {
      if (clause.variableNumValue.charAt(0) === '(') {
        return true;
      }
    }
    return false;
  }

  function getUpperBound(clause) {
    if (clause && clause.variableNumValue && clause.variableNumValue.length >= 1) {
      if (clause.variableNumValue.charAt(clause.variableNumValue.length - 1) === ')') {
        return true;
      }
    }
    return false;
  }

  return entries;

};