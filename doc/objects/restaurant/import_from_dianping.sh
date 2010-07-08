#!/bin/bash
## this script is used to import data from dianping, run this script from a stand alone folder then delete it
EXPORT_DB_NAME=dianping
IMPORT_DB_NAME=crawler
TABLES=(Category Shop ShopType)
len=${#TABLES[*]}
i=0
while [ $i -lt $len ]; do
    echo "mysqldump -uroot -proot $EXPORT_DB_NAME ${TABLES[$i]} > ${TABLES[$i]}.sql"
    mysqldump -uroot -proot $EXPORT_DB_NAME ${TABLES[$i]} > ${TABLES[$i]}.sql
    echo "mysql -uroot -proot $IMPORT_DB_NAME < ${TABLES[$i]}.sql"   
    mysql -uroot -proot $IMPORT_DB_NAME < ${TABLES[$i]}.sql
     
    let i++
done