Ap.share = Ap.share || {};

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