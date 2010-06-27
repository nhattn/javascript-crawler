#amazon ec2 mysql password jyang20012001/root
#amazon ec2 tomcat user password  jyang20012001

tasksel install lamp-server
apt-get install tesseract-ocr tesseract-ocr-eng imagemagick openjdk-6-jdk tomcat6 tomcat6-admin tomcat6-user libapache2-mod-jk xfsprogs emacs  subversion maven2

/etc/init.d/tomcat6 stop
/etc/init.d/apache2 stop
service mysql stop
update-rc.d -f tomcat6 remove

groupadd tomcat
useradd -g tomcat -d /tomcat tomcat
mkdir /tomcat
mkdir /tomcat/deploy
cd /tomcat
passwd tomcat          # will stop here

echo "/dev/sdf /vol xfs noatime 0 0" | sudo tee -a /etc/fstab
sudo mkdir -m 000 /vol
sudo mount /vol

sudo find /vol/{lib,log}/mysql/ ! -user  root -print0 | sudo xargs -0 -r chown mysql
sudo find /vol/{lib,log}/mysql/ ! -group root -a ! -group adm -print0 | sudo xargs -0 -r chgrp mysql
sudo /etc/init.d/mysql stop
echo "/vol/etc/mysql /etc/mysql     none bind" | sudo tee -a /etc/fstab
sudo mount /etc/mysql
echo "/vol/lib/mysql /var/lib/mysql none bind" | sudo tee -a /etc/fstab
sudo mount /var/lib/mysql
echo "/vol/log/mysql /var/log/mysql none bind" | sudo tee -a /etc/fstab
sudo mount /var/log/mysql
tomcat6-instance-create zuiyidong

# scp -i /y/work/ec2/yangkey.pem /y/workspace/webcrawl/doc/deploy/* root@zuiyidong.com:/tomcat/deploy/

mv /tomcat/deploy/tomcat-users.xml /tomcat/zuiyidong/conf/tomcat-users.xml
mkdir -p /tomcat/zuiyidong/conf/Catalina/localhost/
mv /tomcat/deploy/manager.xml /tomcat/zuiyidong/conf/Catalina/localhost/
mv /tomcat/deploy/httpd.conf /etc/apache2/
mv /tomcat/deploy/workers.properties /etc/apache2/
mv /tomcat/deploy/zuiyidong /etc/apache2/sites-available/
mkdir -p /tomcat/zuiyidong/webapps/ROOT

a2dissite default
a2ensite zuiyidong
chown -R tomcat:tomcat /tomcat
/etc/init.d/apache2 start
service mysql start


emacs /etc/maven2/settings.xml
***
add this part
  <servers>
    <server>
      <id>t410</id>
      <username>yang</username>
      <password>yang</password>
    </server>
  </servers>  
  
  
########## tomcat user part
emacs /tomcat/zuiyidong/conf/server.xml
**
enable 8009 ajp
and uri encoding
<Connector port="8080" URIEncoding="UTF-8"/>
<Connector port="8009" protocol="AJP/1.3" URIEncoding="UTF-8"/>


/tomcat/zuiyidong/bin/startup.sh
tail -f /tomcat/zuiyidong/logs/catalina.out &    # press "ctrl + c" after this

mkdir /tomcat/source
cd /tomcat/source
svn checkout http://javascript-crawler.googlecode.com/svn/trunk/ /tomcat/source/crawler
svn checkout http://yjcommon.googlecode.com/svn/trunk/ /tomcat/source/yjcommon 
cd /tomcat/source/yjcommon
mvn install
cd /tomcat/source/crawler
script/production_server_deploy_local.sh



