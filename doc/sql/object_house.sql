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
	photo				varchar(1000),
	description1		varchar(200),
	description2		varchar(5000),
	floor				integer,
    totalFloor			integer,
	isAgent				integer,
	agentPhoto          varchar(50),
	equipment			varchar(100),
	decoration			varchar(20),
 	createTime          datetime,
 	updateTime          datetime, 	
    link                bigint,    
 	PRIMARY KEY (id)
);
create index Object_House_Index_lng  on Object_House(lng);
create index Object_House_Index_lat  on Object_House(lat);
create index Object_House_Index_rentalType  on Object_House(rentalType);
create index Object_House_Index_updateTime  on Object_House(updateTime);
create index Object_House_Index_city  on Object_House(city);
create index Object_House_Index_district1  on Object_House(district1);
create index Object_House_Index_district3  on Object_House(district3);
create index Object_House_Index_district5  on Object_House(district5);
create index Object_House_Index_price  on Object_House(price);
 
ALTER TABLE Object_House AUTO_INCREMENT = 100000000000000;


create table Object_House_CityList (
    id       integer NOT NULL AUTO_INCREMENT,
    city     varchar(100),
    PRIMARY KEY (id)    
);

/**

   ## get data for city list from the query below.
   insert into Object_House_CityList(city) select distinct city from Object_House where city is not null;
**/

create table Object_House_Data_Day(
    id                      bigint NOT NULL AUTO_INCREMENT,
    city                    varchar(10),
    date                    date,
    saleCount               integer,
    rentCount               integer,
    totalSaleSize           integer,
    totalSalePrice          integer,
    averageSalePrice        integer,
    PRIMARY KEY (id)
);


create table Object_House_Data_By_Month(
    id                      bigint NOT NULL AUTO_INCREMENT,
    city                    varchar(10),
    date                    date,
    saleCount               integer,
    rentCount               integer,
    totalSaleSize           integer,
    totalSalePrice          integer,
    averageSalePrice        integer,
    PRIMARY KEY (id)
);


  