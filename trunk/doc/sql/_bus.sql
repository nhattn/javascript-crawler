## these sql are for aibang bus
 
create table Bus(
    id                  bigint NOT NULL AUTO_INCREMENT,
    name                varchar(100),
    city                varchar(20),
    updateTime          datetime,
    description         varchar(500),
    url                 varchar(800),
    PRIMARY KEY (id)    
);

create table BusStop(
    id                  bigint NOT NULL AUTO_INCREMENT,
    name                varchar(50),
    city                varchar(20),
    lng                 double,
    lat                 double,
    busList             varchar(1000),
    updateTime          datetime,
    PRIMARY KEY (id)    
);

create table BusLine(
    id                  bigint NOT NULL AUTO_INCREMENT,
    seq                 int,
    busId               bigint,
    stopId              bigint,
    updateTime          datetime,    
    PRIMARY KEY (id)    
);

ALTER TABLE Bus AUTO_INCREMENT = 100000000000000;
ALTER TABLE BusStop AUTO_INCREMENT = 100000000000000;
ALTER TABLE BusLine AUTO_INCREMENT = 100000000000000;

/*
create view Object_BusStation as 
    select BusLine.id ,BusLine.seq, Bus.id as busId, Bus.name as busName, Bus.city, BusStop.name as stopName, BusStop.lo, BusStop.la, Bus.description,Bus.url
        from Bus, BusLine, BusStop 
    where Bus.id = BusLine.busId and BusStop.id = BusLine.stopId 
    order by BusLine.id;    

*/
 
create table Object_BusStation(
        id                 bigint,        
        lo                 double,        
        la                 double,
        busId              bigint,
        seq                int, 
        busName            varchar(200), 
        city               varchar(300),
        stopName           varchar(50),         
        description        varchar(500),
        PRIMARY KEY (id)    
);

create index Object_BusStation_lo on Object_BusStation(lo);
create index Object_BusStation_la  on Object_BusStation(la);
create index Object_BusStation_busId  on Object_BusStation(busId);
/*
insert into Object_BusStation(id, seq, busId, busName, city, stopName, lo, la, description)
  select BusLine.id ,BusLine.seq, Bus.id as busId, Bus.name as busName, Bus.city, BusStop.name as stopName, BusStop.lo, BusStop.la, Bus.description
        from Bus, BusLine, BusStop 
    where Bus.id = BusLine.busId and BusStop.id = BusLine.stopId 
    order by BusLine.id;    

 
*/