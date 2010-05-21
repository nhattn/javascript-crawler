mkdir /vol/etc /vol/lib /vol/log
mv /etc/mysql     /vol/etc/
mv /var/lib/mysql /vol/lib/
mv /var/log/mysql /vol/log/

mkdir /etc/mysql
mkdir /var/lib/mysql
mkdir /var/log/mysql

echo "/vol/etc/mysql /etc/mysql     none bind" | tee -a /etc/fstab
mount /etc/mysql

echo "/vol/lib/mysql /var/lib/mysql none bind" | tee -a /etc/fstab
mount /var/lib/mysql

echo "/vol/log/mysql /var/log/mysql none bind" | tee -a /etc/fstab
mount /var/log/mysql
