use crawler;
create table layer_com_zuiyidong_layer_busline (
        id                 bigint,        
        name               varchar(100), 
        city               varchar(20),
        description        varchar(500),
        updateTime         datetime,        
        PRIMARY KEY (id)
); 

create index layer_busline_name  on layer_com_zuiyidong_layer_busline(name);
create index layer_busline_city  on layer_com_zuiyidong_layer_busline(city);

/*
insert into layer_com_zuiyidong_layer_busline(id, name, city,description, updateTime)
    select id , name, city,description, updateTime
    from Bus order by city;

*/    
    
    