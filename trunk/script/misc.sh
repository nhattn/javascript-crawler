## pack tomcat log 
rm -f /root/tomcatlog.gz
gzip -c  /tomcat/zuiyidong/logs/catalina.out > /root/tomcatlog.gz

scp -i /y/work/ec2/yangkey.pem root@184.73.171.54:/root/tomcatlog.gz /tmp/

/tomcat/zuiyidong/bin/shutdown.sh
/tomcat/zuiyidong/bin/startup.sh

emacs /tomcat/zuiyidong/webapps/ROOT/js/core/crawler_loader.js 

tail -f /tomcat/zuiyidong/logs/catalina.out &
 
## mysql 

mysql -uroot -proot -e "select count(*) from crawler.House"
mysql -uroot -proot -e "select count(*) from crawler.PHouse"
mysql -uroot -proot -e "select count(*) from crawler.Link"
mysql -uroot -proot -e "select count( distinct hash) from crawler.Link"
mysql -uroot -proot -e "select count(*) from crawler.Link where url like '%koubei%'"
mysql -uroot -proot -e "select * from crawler.Link  where errorMsg is not null limit 20"



## to local
rm /root/crawler.sql  /root/db.gz
mysqldump -uroot -proot crawler > /root/crawler.sql 
gzip -c -9 /root/crawler.sql > /root/db.gz
scp -i /y/work/ec2/yangkey.pem root@zuiyidong.com:/root/db.gz /y/workspace/webcrawl/db-2010-06-20.gz
gunzip  /y/workspace/webcrawl/tmp/db.gz

## to usf        
scp -i yangkey.pem root@zuiyidong.com:/root/db.gz db-2010-06-20.gz
        
## import on local
mysql -uroot -proot crawler < /y/workspace/webcrawl/backup/house.db.backup-2009-06-06



## create table on remote 
mysql -uroot -proot < /tomcat/source/crawler/doc/deploy/house.sql

## create database on local
mysql -uroot -proot < /y/workspace/webcrawl/doc/deploy/house.sql

## deploy
/y/workspace/webcrawl/doc/script/deploy.prod.sh

cd /tomcat/source/crawler/

/tomcat/source/crawler/script/production_server_deploy_local.sh

## check out code from server
svn checkout http://javascript-crawler.googlecode.com/svn/trunk/ crawler
svn checkout http://yjcommon.googlecode.com/svn/trunk/ yjcommon


mysql -uroot -pjyang20012001 
use crawler
select * from House where tel like '%l%';




#### valid field is correct
select address from House where address like '%<%';
select address from House where address like '%>%';

select district1 from House where district1 like '%<%';
select district1 from House where district1 like '%>%';

select district3 from House where district3 like '%<%';
select district3 from House where district3 like '%>%';

select district5 from House where district5 like '%<%';
select district5 from House where district5 like '%>%';

select address from House where address like '%<%';
select address from House where address like '%>%';




select count(*) from House where referer like '%koubei%';
select count(*) from Link where url like '%koubei%';

select tel from House  where tel like '%Z%';
update House set tel = replace(tel, 'z', '2') where tel like '%z%';
update House set tel = replace(tel, 'Z', '2') where tel like '%Z%';

select str(tel) from 