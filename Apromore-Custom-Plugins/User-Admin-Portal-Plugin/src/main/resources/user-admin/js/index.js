Ap.userAdmin = Ap.userAdmin || {}

// https://forum.zkoss.org/question/72022/intercepting-tab-selection/
Ap.userAdmin.switchTab = (notify, init) => {
  if (this.desktop && !init && notify) {
    zAu.send(new zk.Event(this, 'onSwitchTab'));
  } else {
    this.$_sel(notify, init); // call the original method
  }
}

// Common toggle click
Ap.userAdmin.toggleClick = (widgetId, param, event) => {
  if (event.metaKey || event.shiftKey || event.ctrlKey) { return; } // do not toggle for multiple selections
  zAu.send(new zk.Event(zk.Widget.$(widgetId), 'onToggleClick', param));
}

/**
 * Manual single-row toggle for user Listbox
 *
 * @param name {String} Username
 * @param index {number} Row index
 * @param event {Event} Click Event
 */
Ap.userAdmin.toggleUserClick = (name, event) => {
  Ap.userAdmin.toggleClick('$userEditBtn', { name }, event);
}

/**
 * Manual single-row toggle for group Listbox
 *
 * @param name {String} Username
 * @param index {number} Row index
 * @param event {Event} Click Event
 */
Ap.userAdmin.toggleGroupClick = (id, event) => {
  Ap.userAdmin.toggleClick('$groupEditBtn', { id }, event);
}

Ap.userAdmin.editUser = (userName) => {
  zAu.send(new zk.Event(zk.Widget.$('$userEditBtn'), 'onExecute', userName));
}

Ap.userAdmin.editGroup = (groupName) => {
  zAu.send(new zk.Event(zk.Widget.$('$groupEditBtn'), 'onExecute', groupName));
}

Ap.userAdmin.changeGroupNameOK = (groupName, rowGuid) => {
  zAu.send(new zk.Event(zk.Widget.$('$groupEditBtn'), 'onChangeNameOK', { groupName, rowGuid }));
}

Ap.userAdmin.changeGroupNameCancel = (groupName, rowGuid) => {
  zAu.send(new zk.Event(zk.Widget.$('$groupEditBtn'), 'onChangeNameCancel', { groupName, rowGuid }));
}

Ap.userAdmin.tbFocus = (el) => {
  jq(el).next().css('visibility', 'visible');
  jq(el).next().next().css('visibility', 'visible');
  // zk.$(jq(el).next()[0]).setVisible(true);
  // zk.$(jq(el).next().next()[0]).setVisible(true);
}

Ap.userAdmin.tbBlur = (el) => {
  setTimeout(
      function () {
        jq(el).next().css('visibility', 'hidden');
        jq(el).next().next().css('visibility', 'hidden');
      }
      , 1000);
  // zk.$(jq(el).next()[0]).setVisible(false);
  // zk.$(jq(el).next().next()[0]).setVisible(false);
}
