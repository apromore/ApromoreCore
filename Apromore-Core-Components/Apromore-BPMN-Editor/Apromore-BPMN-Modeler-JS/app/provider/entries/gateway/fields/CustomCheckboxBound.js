'use strict';

var domify = require('min-dom').domify;

var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;

var entryFieldDescription = require('bpmn-js-properties-panel/lib/factory/EntryFieldDescription');


var checkbox = function(translate, options, defaultParameters) {
  var defaultParameters= setDefaultParameters(options);
  var resource = defaultParameters,
      id = resource.id,
      label = options.label || id,
      fieldLabel = options.fieldLabel,
      canBeDisabled = !!options.disabled && typeof options.disabled === 'function',
      canBeHidden = !!options.hidden && typeof options.hidden === 'function',
      description = options.description;

  resource.html =
    domify(
        '<label for="camunda-' + escapeHTML(id) + '" ' +
         (canBeDisabled ? 'data-disable="isDisabled"' : '') +
         (canBeHidden ? 'data-show="isHidden"' : '') +
         '>' + escapeHTML(fieldLabel) + '</label>'+
        
        '<input id="camunda-' + escapeHTML(id) + '" ' +
         'type="checkbox" ' +
         'name="' + escapeHTML(options.modelProperty) + '" ' +
         (canBeDisabled ? 'data-disable="isDisabled"' : '') +
         (canBeHidden ? 'data-show="isHidden"' : '') +
         ' />' +
        '<label for="camunda-' + escapeHTML(id) + '" ' +
         (canBeDisabled ? 'data-disable="isDisabled"' : '') +
         (canBeHidden ? 'data-show="isHidden"' : '') +
         '>' + escapeHTML(label) + '</label>');

  // add description below checkbox entry field
  if (description) {
    resource.html.appendChild(entryFieldDescription(translate, description, { show: canBeHidden && 'isHidden' }));
  }

  resource.get = function(element) {
    var bo = getBusinessObject(element),
        res = {};

    res[options.modelProperty] = bo.get(options.modelProperty);

    return res;
  };

  resource.set = function(element, values) {
    var res = {};

    res[options.modelProperty] = !!values[options.modelProperty];

    return cmdHelper.updateProperties(element, res);
  };

  if (typeof options.set === 'function') {
    resource.set = options.set;
  }

  if (typeof options.get === 'function') {
    resource.get = options.get;
  }

  if (canBeDisabled) {
    resource.isDisabled = function() {
      return options.disabled.apply(resource, arguments);
    };
  }

  if (canBeHidden) {
    resource.isHidden = function() {
      return !options.hidden.apply(resource, arguments);
    };
  }

  resource.cssClasses = ['bpp-checkbox'];

  return resource;
};


var setDefaultParameters = function(options) {

    // default method to fetch the current value of the input field
    var defaultGet = function(element) {
      var bo = getBusinessObject(element),
          res = {},
          prop = ensureNotNull(options.modelProperty);
      res[prop] = bo.get(prop);
  
      return res;
    };
  
    // default method to set a new value to the input field
    var defaultSet = function(element, values) {
      var res = {},
          prop = ensureNotNull(options.modelProperty);
      if (values[prop] !== '') {
        res[prop] = values[prop];
      } else {
        res[prop] = undefined;
      }
  
      return cmdHelper.updateProperties(element, res);
    };
  
    // default validation method
    var defaultValidate = function() {
      return {};
    };
  
    return {
      id : options.id,
      description : (options.description || ''),
      get : (options.get || defaultGet),
      set : (options.set || defaultSet),
      validate : (options.validate || defaultValidate),
      html: ''
    };
  };

  function ensureNotNull(prop) {
    if (!prop) {
      throw new Error(prop + ' must be set.');
    }
  
    return prop;
  }

module.exports = checkbox;
