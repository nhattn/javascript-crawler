use crawler;

drop table book;
create table book(
 	book_id    	  varchar(255),
 	name          varchar(255),
 	author        varchar(255),
 	description   varchar(255),
 	totalChar     int(11),
 	hit           int(11),
 	finished      bit(1),
 	updateTime    datetime
);

drop table chapter;

create table chapter(
    chapter_id      varchar(255),
    name            varchar(255),
    description     varchar(255),
    totalChar       int(11),
    hit             int(11),
    updateTime      datetime,
    isPicture       bit(1),
    hasContent      bit(1),
    book_id         varchar(255),
    sequence        int(11)
); 	
