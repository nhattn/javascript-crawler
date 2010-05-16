
use crawler;

drop table House;

create table House(
	id					bigint NOT NULL AUTO_INCREMENT,
	lo					double,
	la					double,
	rentalType 			varchar(5),
	subRentalType       varchar(10),
	price		        float,
	paymentType	        varchar(10),
	priceUnit            varchar(10),
	size                varchar(10),
	houseType           varchar(30),
 	createTime          datetime,
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
	floor				varchar(10),
	totalFloor			tinyint,
	isAgent				tinyint,
	equipment			varchar(100),
	decoration			varchar(20),
	ok					tinyint DEFAULT 0,
 	PRIMARY KEY (id)
);


drop table Link;

create table Link(
	id					bigint NOT NULL AUTO_INCREMENT,
	url					varchar(300),	
	createTime          datetime,
	processeTime		datetime,
	tryCount			tinyint,
	PRIMARY KEY (id)
);