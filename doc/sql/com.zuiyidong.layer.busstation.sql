use crawler;
create table layer_com_zuiyidong_layer_busstation (
        id                 bigint,        
        lng                double,        
        lat                double,
        busId              bigint,
        seq                int, 
        busName            varchar(200), 
        city               varchar(300),
        stopName           varchar(50),         
        description        varchar(200),
        PRIMARY KEY (id)
); 

create index layer_busstation_lng  on layer_com_zuiyidong_layer_busstation(lng);
create index layer_busstation_lat  on layer_com_zuiyidong_layer_busstation(lat);
create index layer_busstation_busId  on layer_com_zuiyidong_layer_busstation(busId);

/*
insert into layer_com_zuiyidong_layer_busstation(id, lng, lat, busId, seq, busName, city, stopName, description) 
    select id , lo as lng, la as lat, busId as busId, seq, busName, city, stopName, description
    from Object_BusStation order by city , busId, seq asc;
*/
    
    
    