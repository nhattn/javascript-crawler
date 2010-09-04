use crawler;
create table layer_com_zuiyidong_layer_busline (
        id                 bigint,        
        name               varchar(100), 
        city               varchar(20),
        description        varchar(500),
        seq1               integer,
        updateTime         datetime,        
        PRIMARY KEY (id)
); 

create index layer_busline_name  on layer_com_zuiyidong_layer_busline(name);
create index layer_busline_city  on layer_com_zuiyidong_layer_busline(city);
create index layer_busline_seq1  on layer_com_zuiyidong_layer_busline(seq1);

create table layer_com_zuiyidong_layer_busline_citylist (
        id                 int auto_increment,        
        city               varchar(100),
        spelling           varchar(20),        
        PRIMARY KEY (id)
); 

INSERT INTO layer_com_zuiyidong_layer_busline_citylist VALUES 
(10000,'上海','shang.hai'),(10001,'乌鲁木齐','wu.lu.mu.qi'),(10002,'兰州','lan.zhou'),(10003,'北京','bei.jing'),(10004,'南京','nan.jing'),(10005,'南宁','nan.ning'),
(10006,'南昌','nan.chang'),(10007,'厦门','xia.men'),(10008,'合肥','he.fei'),(10009,'呼和浩特','hu.he.hao.te'),(10010,'哈尔滨','ha.er.bin'),(10011,'大连','da.lian'),
(10012,'天津','tan.jin'),(10013,'太原','tai.yuan'),(10014,'宁波','ning.bo'),(10015,'广州','guang.zhou'),(10016,'成都','cheng.du'),(10017,'无锡','wu.xi'),
(10018,'昆明','kun.ming'),(10019,'杭州','hang.zhou'),(10020,'武汉','wu.han'),(10021,'沈阳','shen.yang'),(10022,'济南','ji.nan'),(10023,'海口','hai.kou'),
(10024,'深圳','shen.zhen'),(10025,'温州','wen.zhou'),(10026,'石家庄','shi.jia.zhuang'),(10027,'福州','fu.zhou'),(10028,'苏州','su.zhou'),(10029,'西安','xi.an'),(10030,'贵阳','gui.yang'),
(10031,'郑州','zheng.zhong'),(10032,'重庆','chong.qing'),(10033,'长春','chang.chun'),(10034,'长沙','chang.sha'),(10035,'青岛','qing.dao');

/*
insert into layer_com_zuiyidong_layer_busline(id, name, city,description, updateTime)
    select id , name, city,description, updateTime
    from Bus order by city;

insert into layer_com_zuiyidong_layer_busline_citylist(city) 
    select distinct city 
    from layer_com_zuiyidong_layer_busline;
    
*/    
