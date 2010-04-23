
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
	uri 		int 	auto_increment,
	content text,
	constraint pk_annotations primary key (uri)
) engine=innoDB;

create table canonicals (
	uri		int 	auto_increment,
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
	uri		int 	auto_increment,
	content text,
	nat_type varchar(20),
	canonical int,
	annotation int,
	constraint pk_natives primary key (uri),
	constraint fk_natives foreign key (nat_type) references native_types(nat_type), 
	constraint fk_natives2 foreign key (canonical) references canonicals(uri),
	constraint fk_natives3 foreign key (annotations) references annotations(uri)
) engine=innoDB;

create table process_versions (
	processId int,
	version_name varchar(40),
	creation_date datetime,
	last_update datetime,
	canonical int,
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
