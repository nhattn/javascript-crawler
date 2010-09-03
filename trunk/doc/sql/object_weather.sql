use crawler;
create table Object_Weather(
    id                  bigint NOT NULL AUTO_INCREMENT,
    locationId          varchar(15),
    castDate            date,    
    condition0          varchar(10),
    temp0               integer,
    wind0               varchar(20),
    strength0           varchar(5),               
    condition1          varchar(10),
    temp1               integer,
    wind1               varchar(20),
    strength1           varchar(5),                   
    createTime          datetime,
    updateTime          datetime,       
    PRIMARY KEY (id)
);

ALTER TABLE Object_Weather AUTO_INCREMENT = 100000000000000;
