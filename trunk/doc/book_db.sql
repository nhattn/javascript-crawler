use crawler;

drop table book;
create table book(
 	book_id    	  varchar(255),
 	name          varchar(255),
 	author        varchar(255),
 	description   varchar(3000),
 	totalChar     int(11),
 	hit           int(11),
 	finished      bit(1),
 	updateTime    datetime
);

drop table chapter;

create table chapter(
    chapter_id      varchar(255),
    name            varchar(255),
    description     varchar(3000),
    totalChar       int(11),
    hit             int(11),
    updateTime      datetime,
    isPicture       bit(1),
    hasContent      bit(1),
    book_id         varchar(255),
    sequence        int(11)
); 	


drop table site;
create table site(
	site_id    		varchar(255),
	name			varchar(255),
	domainName		varchar(255),
	url				varchar(1000)
);

drop table booksite;
create table booksite(
	booksite_id     varchar(255),
	book_id			varchar(255),
	site_id		    varchar(255),
	coverUrl        varchar(1000),
	allChapterUrl	varchar(1000),
	updateTime      datetime
);

drop table chaptersite;
create table chaptersite(
	chaptersite_id 	varchar(255),
	chapter_id		varchar(255),
	site_id			varchar(255),
	updateTime		datetime,
	url				varchar(1000)
);
