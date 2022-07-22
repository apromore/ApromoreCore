'use strict';

var label = function(options) {
    let isNotExistCategories = options.isNotExistCategories;
    let getSelectedClause = options.getSelectedClause;
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
      error = undefined ;
      if(getSelectedClause(element, node)){
         error = isNotExistCategories();
      }
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
