DROP VIEW IF EXISTS `Object_Restaurant`;

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


/**
does not use view, create a table directly 

create view Object_Restaurant as     
    select id, address, shopName, cityName as city, cityAreaCode as areaCode, categoryList, RegionList as region, AltName as shopName2, GLat as la, GLng as lo, PhoneNo2 as tel2, PhoneNo as tel, shopType as shopType, crossRoad as nearBy            
    from RShop where ShopType='美食' order by ShopPower desc, Score desc;
**/

create table Object_Restaurant (
        id                 bigint,
        address            varchar(300), 
        shopName           varchar(300), 
        city               varchar(30), 
        areaCode           varchar(10), 
        categoryList       varchar(300),            
        region             varchar(300),
        shopName2          varchar(300),
        la                 double,
        lo                 double,
        tel                varchar(300),
        tel2               varchar(300),
        ShopType           varchar(50),
        nearBy             varchar(300)
);
                                 
        
INSERT INTO Object_Restaurant  
    select id, address, shopName, cityName as city, cityAreaCode as areaCode, categoryList, RegionList as region, AltName as shopName2, GLat as la, GLng as lo, PhoneNo2 as tel2, PhoneNo as tel, shopType as shopType, crossRoad as nearBy            
    from RShop where ShopType='美食' order by ShopPower desc, Score desc;
    
    
            
create index Index_RShop_ShopPower  on RShop(ShopPower);
create index Index_RShop_Score  on RShop(Score);
create index Index_RShop_GLat  on RShop(GLat);
create index Index_RShop_GLng  on RShop(GLng);
