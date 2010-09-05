use crawler;
create table layer_com_zuiyidong_layer_wifi (
        id                 bigint,
        lng                double,
        lat                double,
        type               varchar(10), 
        description        varchar(2000),
        city               varchar(10),
        province           varchar(10), 
        address            varchar(200),         
        isp                varchar(20),
        name               varchar(200),                
        PRIMARY KEY (id)
); 

create index layer_wifi_lng  on layer_com_zuiyidong_layer_wifi(lng);
create index layer_wifi_lat  on layer_com_zuiyidong_layer_wifi(lat);    


/*
create table wifiok (
        id                 bigint NOT NULL AUTO_INCREMENT,
        oid                integer,
        lng                double,
        lat                double,
        type               varchar(10), 
        description        varchar(2000),
        city               varchar(10),
        province           varchar(10), 
        address            varchar(200),         
        isp                varchar(20),
        name               varchar(200),                
        PRIMARY KEY (id)
); 

insert into layer_com_zuiyidong_layer_wifi(id,lng,lat,type,description,city,province,address,isp,name) 
select id,lng,lat,type,description,city,province,address,isp,name from wifiok;


alter table layer_com_zuiyidong_layer_wifi change lng lat1 double;
alter table layer_com_zuiyidong_layer_wifi change lat lng double;
alter table layer_com_zuiyidong_layer_wifi change lat1 lat double;

*/