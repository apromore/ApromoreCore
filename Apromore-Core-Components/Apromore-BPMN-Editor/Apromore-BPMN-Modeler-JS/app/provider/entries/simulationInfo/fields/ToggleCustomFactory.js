'use strict';

var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;

var entryFieldDescription = require('bpmn-js-properties-panel/lib/factory/EntryFieldDescription');

var domify = require('min-dom').domify;

var toggleSwitch = function(translate, options, defaultParameters) {
   var defaultParameters= setDefaultParameters(options);
  var resource = defaultParameters,
      id = resource.id,
      label = options.label || id,
      canBeHidden = !!options.hidden && typeof options.hidden === 'function',
      isOn = options.isOn,
      descriptionOn = options.descriptionOn,
      descriptionOff = options.descriptionOff,
      labelOn = options.labelOn,
      labelText = options.labelText,
      labelOff = options.labelOff;

  resource.html = document.createDocumentFragment();

  resource.html.appendChild(domify('<div class="bpp-field-wrapper"' +
    (canBeHidden ? 'data-show="shouldShow"' : '') +
    '>' +
      '<label class="ap-sub-title-label">' + escapeHTML(labelText) +'</label>' +
      '<label class="bpp-toggle-switch__switcher">' +
        '<span>Categorical</span>' +
        '<input id="' + escapeHTML(id) + '" ' +
            'type="checkbox" ' +
            'name="' + escapeHTML(options.modelProperty) + '"  title = "' + escapeHTML(label) + '" />' +
        '<span title = "' + escapeHTML(label) + '" class="bpp-toggle-switch__slider"></span>' +
        '<span>Numerical</span>' +
      '</label>' +
    '</div>'));

  if (descriptionOn) {
    resource.html.appendChild(entryFieldDescription(translate, descriptionOn, { show: 'isOn' }));
  }

  if (descriptionOff) {
    resource.html.appendChild(entryFieldDescription(translate, descriptionOff, { show: 'isOff' }));
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

  if (canBeHidden) {
    resource.shouldShow = function() {
      return !options.hidden.apply(resource, arguments);
    };
  }

  resource.isOn = function() {
    if (canBeHidden && !resource.shouldShow()) {
      return false;
    }

    return isOn.apply(resource, arguments);
  };

  resource.isOff = function() {
    if (canBeHidden && !resource.shouldShow()) {
      return false;
    }

    return !resource.isOn();
  };

  resource.cssClasses = ['bpp-toggle-switch'];

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

module.exports = toggleSwitch;
