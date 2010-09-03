use crawler;
create table Object_Film(
    id                  bigint NOT NULL AUTO_INCREMENT,
    theaterName         varchar(100),
    city                varchar(100),
    name                varchar(100),
    description         varchar(500),
    showTime            varchar(1000),
    showDate            date,    
    createTime          datetime,
    updateTime          datetime,       
    PRIMARY KEY (id)
);

ALTER TABLE Object_Film AUTO_INCREMENT = 100000000000000;
