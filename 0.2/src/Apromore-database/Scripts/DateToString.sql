alter table process_versions change creation_date creation_prev datetime;
alter table process_versions change last_update last_prev datetime;
alter table process_versions add column creation_date varchar(20);
alter table process_versions add column last_update varchar(20);
update process_versions set creation_date = date_format(creation_prev, '%e/%m/%Y %H:%i:%s');
update process_versions set last_update = date_format(last_prev, '%e/%m/%Y %H:%i:%s');
alter table process_versions drop last_prev;
alter table process_versions drop creation_prev;
