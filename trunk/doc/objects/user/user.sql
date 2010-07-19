create table RegisterUser(
    id                  bigint NOT NULL AUTO_INCREMENT,
    name                varchar(40),
    count               integer default 0,
    PRIMARY KEY (id)        
);

ALTER TABLE RegisterUser AUTO_INCREMENT = 100000000000000;



create table SiteUser(
    id              bigint NOT NULL AUTO_INCREMENT,
    name            varchar(40),
    count           integer default 0,
    site            varchar(40),
    email           varchar(50),
    PRIMARY KEY (id)
);

ALTER TABLE SiteUser AUTO_INCREMENT = 100000000000000;

