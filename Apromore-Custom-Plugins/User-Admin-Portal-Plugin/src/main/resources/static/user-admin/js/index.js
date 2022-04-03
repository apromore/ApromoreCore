/*
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
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

/**
 * Manual single-row toggle for role Listbox
 *
 * @param index {number} Row index
 * @param event {Event} Click Event
 */
Ap.userAdmin.toggleRoleClick = (index, event) => {
  Ap.userAdmin.toggleClick('$roleSelectBtn', { index }, event);
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
