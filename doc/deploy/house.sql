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
	url					varchar(300),	
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