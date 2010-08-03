use crawler;
create table layer_com_zuiyidong_layer_restaurant (
        id                 bigint,
        lng                double,
        lat                double,
        shopName           varchar(200), 
        shopName2          varchar(300),
        city               varchar(10), 
        address            varchar(200), 
        district1          varchar(30),
        district2          varchar(30),
        district3          varchar(30),
        tel                varchar(20),
        tel2               varchar(20),
        areaCode           varchar(6), 
        categoryList       varchar(300),                    
        nearBy             varchar(300),
        PRIMARY KEY (id)
); 

create index layer_restaurant_lng  on layer_com_zuiyidong_layer_restaurant(lng);
create index layer_restaurant_lat  on layer_com_zuiyidong_layer_restaurant(lat);

insert into layer_com_zuiyidong_layer_restaurant (id, lng, lat, shopName, shopName2, city, address, tel, tel2, areaCode, categoryList, nearBy) 
    select id , GLng as lng, GLat as lat, shopName, AltName as shopName2, cityName as city, address, PhoneNo as tel, PhoneNo2 as tel2, cityAreaCode as areaCode, categoryList, crossRoad as nearBy            
    from RShop where ShopType='美食' order by ShopPower desc, Score desc;

    
    
    