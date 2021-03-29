Ap.share = Ap.share || {};

Ap.share.revertCombobox = (rowGuid, access) => {
  let combobox = zk.Widget.$(jq("[data-id='" + rowGuid + "']")[0]);
  combobox.setValue(access);
  zAu.send(new zk.Event(combobox, 'onChange', access)); // sync with server
};

/**
 * Update access (Viewer, Editor, Owner)
 *
 * @param rowGuid {String} group rowGuid
 * @param name {String} group/user name
 * @param index {number} index of item in the list
 * @param access {String} Viewer, Editor, Owner
 */
Ap.share.updateAssignee = (rowGuid, name, access) => {
  zAu.send(new zk.Event(zk.Widget.$('$editBtn'), 'onUpdate', { rowGuid, name, access }));
};

/**
 * Remove access
 *
 * @param rowGuid {String} group rowGuid
 * @param name {String} group/user name
 * @param index {number} index of item in the list
 */
Ap.share.removeAssignee = (rowGuid, name) => {
	zAu.send(new zk.Event(zk.Widget.$('$editBtn'), 'onRemove', { rowGuid, name }));
};

/**
 * Indicate whether to share associated user metadata
 *
 * @param rowGuid {String} group rowGuid
 * @param name {String} group/user name
 */
Ap.share.toggleIncludeMetadata = (rowGuid, name) => {
  zAu.send(new zk.Event(zk.Widget.$('$editBtn'), 'onIncludeMetadata', { rowGuid, name }));
};