
use crawler;

drop table housing;

create table housing(
	id					bigint,
	lo					float,	
	la					float,
	rentalType 			tinyint,
	subRentalType       varchar(10),
	price		        float,
	paymentType	        varchar(10),
	priceUit            varchar(10),
	size                varchar(10);
	houseType           varchar(30)
    createTime			datetime,
	address             varchar(200),
	district1           varchar(20),
	district3           varchar(20),
	district5			varchar(20),
	tel					varchar(20),
	contact				varchar(20),
	photo				varchar(200),
	description1		varchar(100),
	description2		varchar(1000),
	floor				varchar(2000),
	totalFloor			tinyint,
	isAgent				tinyint,
	equipment			varchar(100),
	decoration			varchar(20)
)