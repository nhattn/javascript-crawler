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
mysql -uroot -proot -e "select count(*) from crawler.Link where url like '%koubei%'"
mysql -uroot -proot -e "select * from crawler.Link  where errorMsg is not null limit 20"

## to local
mysqldump -uroot -proot crawler > /root/crawler.sql 
gzip -c -9 /root/crawler.sql > /root/db.gz
scp -i /y/work/ec2/yangkey.pem root@184.73.171.54:/root/db.gz /y/workspace/webcrawl/tmp/
gunzip  /y/workspace/webcrawl/tmp/db.gz
        
## import on local
mysql -uroot -proot < /y/workspace/webcrawl/tmp/db 


## create table on remote 
mysql -uroot -proot < /tomcat/source/crawler/doc/deploy/house.sql


## deploy
/y/workspace/webcrawl/doc/script/deploy.prod.sh
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