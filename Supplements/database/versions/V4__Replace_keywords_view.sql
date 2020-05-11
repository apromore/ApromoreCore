DROP VIEW apromore.keywords;

CREATE VIEW apromore.keywords AS
  select process.id
    AS value, 'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    from process union
  select process.name
    AS value, 'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    from process union
  select process.domain
    AS value, 'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    from process union
  select native_type.nat_type
    AS value, 'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    from process join native_type ON (process.nativeTypeId = native_type.id) union
  select `user`.first_name
    AS value, 'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    from process join `user` ON (process.owner = `user`.username) union
  select `user`.last_name
    AS value, 'process' AS type, process.id AS processId, NULL AS logId, NULL AS folderId
    from process join `user` ON (process.owner = `user`.username) union
  select process_branch.branch_name
    AS value, 'process' AS type, process_branch.processId AS processId, NULL AS logId, NULL AS folderId
    from process_branch union
  select `log`.id
    AS value, 'log' AS type, NULL AS processId, `log`.id AS logId, NULL AS folderId
    from `log` union
  select `log`.name
    AS value, 'log' AS type, NULL AS processId, `log`.id AS logId, NULL AS folderId
    from `log` union
  select `log`.domain
    AS value, 'log' AS type, NULL AS processId, `log`.id AS logId, NULL AS folderId
    from `log` union
  select folder.folder_name
    AS value, 'folder' AS type, NULL AS processId, NULL AS logId, folder.id AS folderId
    from folder;
