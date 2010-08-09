passwd=root
user=root
mysql -u$user -p$passwd < base.sql
mysql -u$user -p$passwd < object_groupbuy.sql
mysql -u$user -p$passwd < object_house.sql
mysql -u$user -p$passwd < com.zuiyidong.layer.wifi.sql
mysql -u$user -p$passwd < com.zuiyidong.layer.busline.sql
mysql -u$user -p$passwd < com.zuiyidong.layer.busstation.sql
mysql -u$user -p$passwd < com.zuiyidong.layer.restaurant.sql