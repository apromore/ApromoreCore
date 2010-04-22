
drop table if exists process_versions ;
drop table if exists processes ;
drop table if exists natives ;
drop table if exists canonicals ;
drop table if exists annotations ;
drop table if exists native_types;
drop table if exists search_histories;
drop table if exists users ;

drop view process_ranking;
drop view keywords ;

create table annotations (
	uri varchar(80),
	content text,
	constraint pk_annotations primary key (uri)
) engine=innoDB;

create table canonicals (
	uri varchar(80),
	content text,
	constraint pk_canonicals primary key (uri)
) engine=innoDB;

create table native_types (
	nat_type varchar(20),
	constraint pk_native_types primary key (nat_type)
) engine=innoDB;

create table users (
	userId		int 	auto_increment,
	lastname	varchar(40),
	firstname	varchar(40),
	email		varchar(80),
	username	varchar(10),
	passwd		varchar(80),
	constraint pk_users primary key(userId),
	constraint un_users unique (username)
	)  engine=InnoDB;

create table search_histories (
	userId		int,
	search		varchar(200),
	num			int 	auto_increment,
	constraint pk_searches primary key(num),
	constraint un_searches unique(userId,search),
	constraint fk_searches foreign key(userId) 
		references users(userId) on delete cascade on update cascade
)  engine=InnoDB;


create table processes (
	processId 	int auto_increment,
	name		varchar(40),
	domain		varchar(40),
	owner		int,
	original_type varchar(80),
	index(processId),
	constraint pk_processes primary key(processId),
	constraint fk_processes1 foreign key(owner) references users(userId)
		on delete cascade on update cascade, 
	constraint fk_processes2 foreign key (original_type) references native_types(nat_type)
	on update cascade
) engine=InnoDB;

create table natives (
	uri varchar(80),
	content text,
	nat_type varchar(20),
	canonical varchar(80),
	constraint pk_natives primary key (uri),
	constraint fk_natives foreign key (nat_type) references native_types(nat_type), 
	constraint fk_natives2 foreign key (canonical) references canonicals(uri)
) engine=innoDB;

create table process_versions (
	processId int,
	version_name varchar(40),
	creation_date datetime,
	last_update datetime,
	canonical varchar(80),
	ranking varchar(10),
	constraint pk_versions primary key (processId,version_name),
	constraint fk_versions1 foreign key (processId) references processes(processId)
	on delete cascade on update cascade,
	constraint fk_versions2 foreign key (canonical) references canonicals(uri)
	on delete cascade on update cascade
) engine=InnoDB;

create view process_ranking (processId, ranking) as
	select processId, avg(ranking)
	from process_versions
	group by processId;
	
create view keywords (processId, word) as
	select processId, name from processes
	union
	select processId, domain from processes
	union 
	select processId, original_type from processes
	union
	select processId, firstname
	from processes join users on (owner = userId)
	union
	select processId, lastname
	from processes join users on (owner = userId)
	union
	select processId, version_name
	from process_versions
	;
	
create table nets (
	netId int,
	processId int,
	constraint pk_nets primary key (netId),
	constraint fk_nets foreign key (processId) references canonical (processId)
) engine=InnoDB;

create table edges (
	edgeId int,
	netId int,
	target idNode,
	source idNote,
	condition varchar(256),
	default_ boolean,
	constraint pk_edges primary key (edgeId),
	constraint fk_edges foreign key (netId) references nets(netId)
);

create table messages (
	messageId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_messages primary key (messageId)
) engine =InnoDB;

create table times (
	timeId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_times primary key (timeId)
) engine =InnoDB;

create table tasks (
	taskId int,
	name varchar(80),
	configurable boolean,
	subnetId int,
	netId int,
	constraint pk_tasks primary key (taskId),
	constraint fk_tasks foreign key (subnetId) references nets(netId)
) engine =InnoDB;

create view events (eventId, name, configurable, netId) as
	select timeId, name, configurable, netId from times
	union
	select messageId, name, configurable, netId from messages
	;
	
create view works (workId, name, configurable, netId) as
	select eventId, name, configurable, netId from events
	union
	select taskId, name, configurable, netId from tasks;

create table orsplits (
	orsplitId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_orsplits primary key (orspliId),
	constraint fk_orsplits foreign key (subnetId) references nets(netId)
) engine =InnoDB;

create table xorsplits (
	xorsplitId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_xorsplits primary key (xorsplitId),
	constraint fk_orsplits foreign key (subnetId) references nets(netId)
) engine =InnoDB;

create table andsplits (
	andsplitId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_andsplits primary key (andsplitId),
	constraint fk_andsplits foreign key (subnetId) references nets(netId)
) engine =InnoDB;

create table orjoins (
	orjoinId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_orjoins primary key (orjoinId),
	constraint fk_andsplits foreign key (subnetId) references nets(netId)
) engine =InnoDB;

create table xorjoins (
	xorjoinId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_xorjoins primary key (xorjoinId),
	constraint fk_orjoins foreign key (subnetId) references nets(netId)
) engine =InnoDB;

create table andjoins (
	andjoinId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_andjoins primary key (andjoinId),
	constraint fk_andjoins foreign key (subnetId) references nets(netId)
) engine =InnoDB;

create table states (
	stateId int,
	name varchar(80),
	configurable boolean,
	netId int,
	constraint pk_states primary key (stateId),
	constraint fk_states foreign key (subnetId) references nets(netId)
) engine =InnoDB;

create view splits (splitId, name, configurable, netId) as
	select orsplitId, name, configurable, netId from orsplits
	union select xorsplitId, name, configurable, netId from xorsplits
	union select ansplitId, name, configurable, netId from andsplits;
	
create view joins (joinId, name, configurable, netId) as
	select orjoinId, name, configurable, netId from orjoins
	union select xorjoinId, name, configurable, netId from xorJoins
	union select andjoinId, name, configurable, netId from andJoins;
	
create view routings (routingId, name, configurable, netId) as
	select splitId, name, configurable, netId from splits
	union select stateId, name, configurable, netId from states
	union select orjoinId, name, configurable, netId from joins;

create view nodes (nodeId, name, configurable, netId) as
	select routingId, name, configurable, netId from routings
	union
	select workId, name, configurable, netId from works;
	
create table humans (
	humanId int,
	name varchar(80),
	configurable boolean,
	super int,
	constraint pk_humans primary key (humanId)
) engine=InnoDB;

create table nonhumans (
	nonhumanId int,
	name varchar(80),
	configurable boolean,
	super int,
	constraint pk_nonhumans primary key (nonhumanId)
) engine=InnoDB;

create view resourcetype (resourcetypeId, name, configurable, super) as
	select humanId, name, configurable, super from humans
	union select nonhumanId, name, configurable, super from nonhumans;
	
create table inputs (
	inputId int,
	optional boolean,
	consumed boolean,
	constraint pk_inputs primary key (inputId)
) engine=InnoDB;

create table Ouputs (
	outputId int,
	optional boolean,
	constraint pk_outputs primary key (outputId)
) engine=InnoDB;

create view ObjectRef (objectrefId, optional) as
	select inputId, optional from Inputs
	union select outputId, optional from Outputs;
	
create table Hards (
	hardId int,
	name varchar(80),
	configurable boolean,
	constraint pk_hards primary key (hardId)
) engine=InnoDB;
	
create table Softs (
	softId int,
	name varchar(80),
	configurable boolean,
	type varchar(80),
	constraint pk_softs primary key (softId)
) engine=InnoDB;

create view Object (objectId, name, configurable) as
	select hardId, name, configurable from Hards
	union
	select softId, name, configurable from Softs;
	
create table Work_resources (
	workId int,
	resourcetypeId int,
	resource typerefId int
	constraint pk_work_resources primary key (workId, resourcetypeId)
) engine=InnoDB;

create table Work_objects (
	workId int,
	objectId int,
	objectrefId int,
	constraint pk_work_objects primary key (workId, objectId)
) engine=InnoDB;
