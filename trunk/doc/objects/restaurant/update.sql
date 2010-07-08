create index House_Index_lo  on House(lo);
create index House_Index_la  on House(la);
create index Bus_Index_id  on Bus(id);
create index BusStop_Index_id  on BusStop(id);
create index BusLine_Index_id  on BusLine(id);
create index BusLine_Index_busId  on BusLine(busId);
create index BusLine_Index_stopId  on BusLine(stopId);

create view Object_BusStation as 
    select BusLine.id ,BusLine.seq, Bus.id as busId, Bus.name as busName, Bus.city, BusStop.name as stopName, BusStop.lo, BusStop.la, Bus.description,Bus.url
        from Bus, BusLine, BusStop 
    where Bus.id = BusLine.busId and BusStop.id = BusLine.stopId 
    order by BusLine.id;  



create table RShop(
        id                  bigint NOT NULL AUTO_INCREMENT,
        Address             varchar(300),
        AddDate             datetime,
        ShopID              integer,
        ShopName            varchar(300),
        CityAreaCode        varchar(30),
        RegionList          varchar(300),
        CityID              varchar(50),
        CityName            varchar(50),
        ShopType            varchar(50),
        CategoryList        varchar(300),
        AltName             varchar(300),
        AvgPrice            integer,
        BranchName          varchar(300),
        Card                varchar(300),
        CategoryName        varchar(300),
        CategoryID          integer,
        Coordinate          varchar(300),
        CrossRoad           varchar(300),
        DefaultPic          varchar(300),
        DishTags            varchar(300),
        District            varchar(300),
        GLat                double,
        GLng                double,
        LastDate            datetime,
        PhoneNo             varchar(300),
        PhoneNo2            varchar(300),
        PhotoCount          integer,
        PriceText           varchar(300),
        PromoID             integer,
        PromoTitle          varchar(300),
        RegionID            integer,
        RegionName          varchar(300),
        SMSPromoID          integer,
        Score               integer,
        Score1              integer,
        Score2              integer,
        Score3              integer,
        Score4              integer,
        ScoreText           varchar(300),
        ShopMapURL          varchar(300),
        ShopPower           integer,
        ShopTags            varchar(300),
        VoteTotal           integer,
        WishTotal           integer,
        WriteUp             varchar(300),
        PRIMARY KEY (id)
);  
ALTER TABLE RShop AUTO_INCREMENT = 100000000000000;
    
    

create view Object_Restaurant as     
    select id, address, shopName, cityName as city, cityAreaCode as areaCode, categoryList, RegionList as region, AltName as shopName2, GLat as la, GLng as lo, PhoneNo2 as tel2, PhoneNo as tel, shopType as shopType, crossRoad as nearBy            
    from RShop order by ShopPower desc, Score desc;
        
        
create index Index_RShop_ShopPower  on RShop(ShopPower);
create index Index_RShop_Score  on RShop(Score);
create index Index_RShop_GLat  on RShop(GLat);
create index Index_RShop_GLng  on RShop(GLng);
    