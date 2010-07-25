create table GroupBuy(
    id                  bigint NOT NULL AUTO_INCREMENT,
    lo                  double,
    la                  double,    
    title               varchar(200),
    shopName            varchar(100),
    description         varchar(5000),    
    price               double,
    oldPrice            double,    
    website             varchar(20),
    address             varchar(200),
    city                varchar(20),    
    tel                 varchar(200),            
    url                 varchar(300),
    createTime          datetime,
    updateTime          datetime,
    websiteUrl          varchar(300),
    PRIMARY KEY (id)
);

ALTER TABLE GroupBuy AUTO_INCREMENT = 100000000000000;