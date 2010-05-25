## tomcat pass wo1234

apt-get install tesseract-ocr tesseract-ocr-eng imagemagick openjdk-6-jdk apache2 tomcat6 tomcat6-admin tomcat6-user libapache2-mod-jk mysql-server xfsprogs emacs openssh-server subversion maven2

/etc/init.d/tomcat6 stop
/etc/init.d/apache2 stop
service mysql stop
update-rc.d -f tomcat6 remove

mkdir /tomcat/deploy  

groupadd tomcat
useradd -g tomcat -d /tomcat tomcat
passwd tomcat
mkdir /tomcat

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

cd /tomcat
tomcat6-instance-create zuiyidong

mv /tomcat/deploy/tomcat-users.xml /tomcat/zuiyidong/conf/tomcat-users.xml
mkdir -p /tomcat/zuiyidong/conf/Catalina/localhost/
mv /tomcat/deploy/manager.xml /tomcat/zuiyidong/conf/Catalina/localhost/
emacs /tomcat/zuiyidong/conf/server.xml

mv /tomcat/deploy/httpd.conf /etc/apache2/
mv /tomcat/deploy/workers.properties /etc/apache2/

mv /tomcat/deploy/zuiyidong /etc/apache2/sites-available/
mkdir -p /tomcat/zuiyidong/webapps/ROOT
a2dissite default
a2ensite zuiyidong

/etc/init.d/apache2 start
service mysql start
chown -R tomcat:tomcat /tomcat




########## tomcat user part
emacs /etc/maven2/settings.xml

add this part
  <servers>
    <server>
      <id>t410</id>
      <username>yang</username>
      <password>yang</password>
    </server>
  </servers>