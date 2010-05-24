## pack tomcat log 
rm -f /root/tomcatlog.gz
gzip -c  /tomcat/zuiyidong/logs/catalina.out > /root/tomcatlog.gz

scp -i /y/work/ec2/yangkey.pem root@184.73.171.54:/root/tomcatlog.gz /tmp/


/tomcat/zuiyidong/bin/shutdown.sh
/tomcat/zuiyidong/bin/startup.sh

emacs /tomcat/zuiyidong/webapps/ROOT/js/core/crawler_loader.js 

## mysql 

mysql -uroot -proot -e "select count(*) from crawler.House"
mysql -uroot -proot -e "select count(*) from crawler.PHouse"
mysql -uroot -proot -e "select count(*) from crawler.Link"
mysql -uroot -proot -e "select * from crawler.Link  where errorMsg is not null limit 20"

        ## export to local
mysqldump -uroot -proot crawler > /root/crawler.sql 
gzip -c -best /root/crawler.sql > /root/db.gz
scp -i /y/work/ec2/yangkey.pem root@184.73.171.54:/root/db.gz /y/workspace/webcrawl/tmp/
gunzip  /y/workspace/webcrawl/tmp/db.gz
        
        ## import on local
mysql -uroot -proot < /y/workspace/webcrawl/tmp/db 

## deploy
/y/workspace/webcrawl/doc/script/deploy.prod.sh