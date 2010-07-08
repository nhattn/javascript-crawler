drop database if EXISTS crawler;
create database crawler;
use crawler;

create table House(
	id					bigint NOT NULL AUTO_INCREMENT,
	lo					double,
	la					double,
	rentalType 			varchar(5),
	subRentalType       varchar(10),
	price		        double,
	paymentType	        varchar(10),
	priceUnit           varchar(10),
	size                double,
	houseType           varchar(30),
	address             varchar(200),
	city				varchar(20),
	district1           varchar(20),
	district3           varchar(20),
	district5			varchar(20),
	tel					varchar(20),
	contact				varchar(20),
	photo				varchar(200),
	description1		varchar(200),
	description2		varchar(5000),
	floor				integer,
    totalFloor			integer,
	isAgent				integer,
	equipment			varchar(100),
	decoration			varchar(20),
    ok					integer DEFAULT 0,
	referer             varchar(300),
 	createTime          datetime,
 	updateTime          datetime,
 	hash                varchar(50),
 	PRIMARY KEY (id)
);
create index House_Index_lo  on House(lo);
create index House_Index_la  on House(la);

 
create view PHouse as select 
	id,					
	lo,				
	la,					
	rentalType, 			
	subRentalType,       
	price,		        
	paymentType,	        
	priceUnit,           
	size,                
	houseType,           
 	createTime,          
	address,             
	city,				
	district1,           
	district3,           
	district5,			
	tel,					
	contact,				
	photo,				
	description1,		
	description2,		
	floor,				
	totalFloor,			
	isAgent,				
	equipment,			
	decoration
from House where ok=1 order by id desc;

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


create table Bus(
    id                  bigint NOT NULL AUTO_INCREMENT,
    name                varchar(100),
    city                varchar(20),
    updateTime          datetime,
    description         varchar(500),
    url                 varchar(800),
    PRIMARY KEY (id)    
);
create index Bus_Index_id  on Bus(id);

create table BusStop(
    id                  bigint NOT NULL AUTO_INCREMENT,
    name                varchar(50),
    city                varchar(20),
    lo                  double,
    la                  double,
    busList             varchar(1000),
    updateTime          datetime,
    PRIMARY KEY (id)    
);
create index BusStop_Index_id  on BusStop(id);

create table BusLine(
    id                  bigint NOT NULL AUTO_INCREMENT,
    seq                 int,
    busId               bigint,
    stopId              bigint,
    updateTime          datetime,    
    PRIMARY KEY (id)    
);
create index BusLine_Index_id  on BusLine(id);
create index BusLine_Index_busId  on BusLine(busId);
create index BusLine_Index_stopId  on BusLine(stopId);

create view Object_BusStation as 
    select BusLine.id ,BusLine.seq, Bus.id as busId, Bus.name as busName, Bus.city, BusStop.name as stopName, BusStop.lo, BusStop.la, Bus.description,Bus.url
        from Bus, BusLine, BusStop 
    where Bus.id = BusLine.busId and BusStop.id = BusLine.stopId 
    order by BusLine.id;    

ALTER TABLE Link AUTO_INCREMENT = 100000000000000;
ALTER TABLE House AUTO_INCREMENT = 100000000000000;
ALTER TABLE AppLog AUTO_INCREMENT = 100000000000000;
ALTER TABLE Bus AUTO_INCREMENT = 100000000000000;
ALTER TABLE BusStop AUTO_INCREMENT = 100000000000000;
ALTER TABLE BusLine AUTO_INCREMENT = 100000000000000;
