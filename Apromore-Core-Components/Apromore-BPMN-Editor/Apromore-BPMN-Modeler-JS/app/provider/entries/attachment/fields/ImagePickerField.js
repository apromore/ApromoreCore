var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;
var domify = require('min-dom').domify;
var domEvent = require('min-dom').event;
var domQuery = require('min-dom').query;
var { ensureNotNull, setDefaultParameters } = require('../../common');

const MAX_IMAGE_SIZE = 1000000; // 1MB max image size

function previewFile(callback) {
  const preview = document.querySelector('.aux-img-preview img');
  const file = document.querySelector('#aux-img-picker-control').files[0];
  const reader = new FileReader();

  reader.addEventListener("load", function () {
    preview.src = reader.result;
    $('.aux-img-preview').show();
    callback(reader.result);
  }, false);

  if (file) {
    if (file.size && file.size > MAX_IMAGE_SIZE) {
      document.querySelector('#aux-img-picker-control').value = '';
      Ap.common.notify('Image is too big');
    } else {
      reader.readAsDataURL(file); // to base64 string
    }
  }
}

module.exports = function(options) {
  var resource = setDefaultParameters(options),
      label = options.label || resource.id;

  var pickerEl = domify('<div id="ap-bpmn-img-picker"></div>');
  var inputEl = domify('<input id="aux-img-picker-control" type="file">');
  var removeEl = domify('<input id="aux-img-picker-remove" value="Remove" type="button">');
  pickerEl.appendChild(inputEl);
  pickerEl.appendChild(removeEl);
  domEvent.bind(inputEl, 'change', function () {
    previewFile(function(dataURL) {
      var input = document.getElementById('camunda-' + escapeHTML(resource.id));
      input.value = dataURL;
      var ev = new Event('change', { 'bubbles': true });
      input.dispatchEvent(ev);
    });
  })
  domEvent.bind(removeEl, 'click', function () {
    var input = document.getElementById('camunda-' + escapeHTML(resource.id));
    input.value = "";
    document.querySelector('#aux-img-picker-control').value = '';
    var ev = new Event('change', { 'bubbles': true });
    input.dispatchEvent(ev);
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
