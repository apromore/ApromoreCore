// depends on common/js/index.js

(function() {
  Ap.portal.updateBreadcrumbs = () => {
    let width = $('.ap-bread-crumbs').outerWidth();
    // $('.ap-tab-crumbs').width(width);
    zk.Widget.$('$tabCrumbs').setWidth(`${width}px`);
  }

  Ap.portal.clickBreadcrumb = (id) => {
    zAu.send(new zk.Event(zk.Widget.$('$breadCrumbs'), 'onSelectFolder', id));
  }

  zk.afterMount(function() {
    setTimeout(function() {
      // Must force reload breadcrumbs
      zAu.send(new zk.Event(zk.Widget.$('$breadCrumbs'), 'onReloadBreadcrumbs', {}));
      setTimeout(function() {
        Ap.portal.updateBreadcrumbs();
        try {
            // prevent autocompletion on the search
            jq('$previoussearchescombobox input').attr('autocomplete', 'new-password')
        } catch(e) {
            // pass
        }
      }, 500);
    }, 500);
  });
})();