use crawler;
create table layer_com_zuiyidong_layer_train (
        id                 bigint auto_increment,        
        name               varchar(100),
        trainNum           varchar(30), 
        origin             varchar(30),
        dest               varchar(30),
        leaveAt            varchar(15),
        arriveAt           varchar(15),
        type               varchar(10),
        totalMile          varchar(10),
        totalTime          varchar(10),
        zuo                varchar(20),
        yingwo             varchar(20),
        ruanwo             varchar(20),
        deng               varchar(20), 
        createTime         datetime,
        updateTime         datetime,
        PRIMARY KEY (id)
); 

create table layer_com_zuiyidong_layer_trainstation (
        id                 bigint auto_increment,
        trainId            bigint,        
        seq                int,        
        name               varchar(20),
        lng                double,
        lat                double,
        province           varchar(20),
        city               varchar(20),
        district           varchar(20),
        lat1               double,
        lat2               double,
        lng1               double,
        lng2               double,
        arriveAt           varchar(15),
        leaveAt            varchar(15),
        totalTime          varchar(10),
        totalMile          varchar(10),
        zuo                varchar(20),
        yingwo             varchar(20),
        ruanwo             varchar(20),
        deng               varchar(20), 
        createTime         datetime,
        updateTime         datetime,         
        PRIMARY KEY (id)
); 
 
ALTER TABLE layer_com_zuiyidong_layer_train AUTO_INCREMENT = 100000000000000;
ALTER TABLE layer_com_zuiyidong_layer_trainline AUTO_INCREMENT = 100000000000000;