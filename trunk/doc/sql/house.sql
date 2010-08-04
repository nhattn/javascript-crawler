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
ALTER TABLE House AUTO_INCREMENT = 100000000000000;
  