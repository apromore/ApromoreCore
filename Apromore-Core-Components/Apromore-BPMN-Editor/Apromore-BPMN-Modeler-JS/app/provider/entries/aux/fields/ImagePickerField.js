var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;
var domify = require('min-dom').domify,
    domEvent = require('min-dom').event,
    domQuery = require('min-dom').query;

function ensureNotNull(prop) {
  if (!prop) {
    throw new Error(prop + ' must be set.');
  }

  return prop;
}

function setDefaultParameters(options) {

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
    id: options.id,
    description: (options.description || ''),
    get: (options.get || defaultGet),
    set: (options.set || defaultSet),
    validate: (options.validate || defaultValidate),
    html: '',
    type: 'text'
  };
}

function previewFile(callback) {
  const preview = document.querySelector('.aux-img-preview img');
  const file = document.querySelector('#aux-img-picker-control').files[0];
  const reader = new FileReader();

  reader.addEventListener("load", function () {
    preview.src = reader.result;
    callback(reader.result);
  }, false);

  if (file) {
    reader.readAsDataURL(file); // to base64 string
  }
}

module.exports = function(options) {

  var resource = setDefaultParameters(options),
      label = options.label || resource.id;

  var pickerEl = domify('<div id="ap-bpmn-img-picker"></div>');
  var inputEl = domify('<input id="aux-img-picker-control" type="file">');
  pickerEl.appendChild(inputEl);
  domEvent.bind(inputEl, 'change', function () {
    previewFile(function(dataURL) {
      var input = document.getElementById('camunda-' + escapeHTML(resource.id));
      input.value = dataURL;
      var ev = new Event('change', { 'bubbles': true });
      input.dispatchEvent(ev);
    });
  })
  pickerEl.appendChild(domify('<div class="aux-img-preview"><img src="" alt="Image preview..." /></div>'));
  resource.html = domify(
    '<label for="camunda-' + escapeHTML(resource.id) + '" >' + escapeHTML(label) + '</label><br>' +
    '<input id="camunda-' + escapeHTML(resource.id) + '" type="text" name="' +
    escapeHTML(options.modelProperty) + '" />'
  );
  resource.html.appendChild(pickerEl);
  resource.cssClasses = ['bpp-textbox'];

  return resource;
};
