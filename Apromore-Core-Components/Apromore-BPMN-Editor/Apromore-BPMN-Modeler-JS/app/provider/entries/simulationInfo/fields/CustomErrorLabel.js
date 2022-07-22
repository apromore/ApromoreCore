'use strict';

var label = function(options) {
    let doValidation = options.doValidation;
    let error ;
  return {
    id: options.id,
    html: '<label data-value="label" ' +
            'data-show="showLabel" ' +
            'class="invalid-message' + (options.divider ? ' divider' : '') + '">' +
          '</label>',
    get: function(element, node) {
      if (typeof options.get === 'function') {
        return options.get(element, node);
      }
      error = doValidation(element, node);
      return { label: error };
    },
    showLabel: function(element, node) {
      if (typeof options.showLabel === 'function') {
        return options.showLabel(element, node);
      }
      return !!error;
    }
  };

};

module.exports = label;
