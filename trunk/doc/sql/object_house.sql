use crawler;

create table Object_House(
	id					bigint NOT NULL AUTO_INCREMENT,
	lng					double,
	lat					double,
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
 	createTime          datetime,
 	updateTime          datetime, 	
    ok					integer default 0,
    link                bigint,	
 	PRIMARY KEY (id)
);
create index House_Index_lo  on Object_House(lng);
create index House_Index_la  on Object_House(lat); 
ALTER TABLE Object_House AUTO_INCREMENT = 100000000000000;
  