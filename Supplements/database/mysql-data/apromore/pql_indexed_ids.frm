TYPE=VIEW
query=select `apromore`.`pql_index_status`.`net_id` AS `net_id`,`apromore`.`jbpt_petri_nets`.`external_id` AS `external_id` from (`apromore`.`jbpt_petri_nets` join `apromore`.`pql_index_status`) where ((`apromore`.`pql_index_status`.`net_id` = `apromore`.`jbpt_petri_nets`.`id`) and (`apromore`.`pql_index_status`.`status` = 1))
md5=7eba47b5ac61f8c0419a47a6f4f18a62
updatable=1
algorithm=0
definer_user=root
definer_host=localhost
suid=1
with_check_option=0
timestamp=2019-09-17 14:03:45
create-version=1
source=select `pql_index_status`.`net_id` AS `net_id`,`jbpt_petri_nets`.`external_id` AS `external_id` from (`jbpt_petri_nets` join `pql_index_status`) where ((`pql_index_status`.`net_id` = `jbpt_petri_nets`.`id`) and (`pql_index_status`.`status` = 1))
client_cs_name=utf8
connection_cl_name=utf8_general_ci
view_body_utf8=select `apromore`.`pql_index_status`.`net_id` AS `net_id`,`apromore`.`jbpt_petri_nets`.`external_id` AS `external_id` from (`apromore`.`jbpt_petri_nets` join `apromore`.`pql_index_status`) where ((`apromore`.`pql_index_status`.`net_id` = `apromore`.`jbpt_petri_nets`.`id`) and (`apromore`.`pql_index_status`.`status` = 1))
