drop database if EXISTS crawler;
create database crawler;
use crawler;

create table Link(
	id					bigint NOT NULL AUTO_INCREMENT,
	hash                varchar(20),
	url					varchar(800),	
	createTime          datetime,
	processTime		    datetime,
	tryCount			integer,
	errorMsg            varchar(300),
	isError             integer,	
	PRIMARY KEY (id)
);

create table AppLog(
    id                  bigint not null auto_increment,
    app                 varchar(50),
    action              varchar(50),
    clientId            varchar(50),
    createTime          datetime,    
    ip                  varchar(20),
    PRIMARY KEY (id)
);

create table IpBlockList(
    id                  bigint not null auto_increment,
    ip                  varchar(20),
    createTime          datetime,
    lastAffendTime      datetime,
    PRIMARY KEY (id)       
);


create table ClientInfo(
    id                  bigint not null auto_increment,
    clientkey           varchar(100),
    clientId            varchar(10),
    email               varchar(100),
    companyName         varchar(100),
    level               int,    
    total               bigint,
    totalSinceLastCycle bigint,   
    createTime          datetime,  
    PRIMARY KEY (id)       
);

create table LinkTableMap(
    id integer   NOT NULL AUTO_INCREMENT, 
    uid varchar(200),
    tableId integer,
    PRIMARY KEY (id)    
);

ALTER TABLE LinkTableMap AUTO_INCREMENT = 1000000;
ALTER TABLE IpBlockList AUTO_INCREMENT = 100000000000000;
ALTER TABLE Link AUTO_INCREMENT = 100000000000000;
ALTER TABLE AppLog AUTO_INCREMENT = 100000000000000;