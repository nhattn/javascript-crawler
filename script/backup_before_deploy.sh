DATE=`date +%Y-%m-%d_%H-%M-%S`
BACKUPDIR='/tomcat/backup/'
cp /tomcat/zuiyidong/webapps/ROOT.war  ${BACKUPDIR}${DATE}_ROOT.war

echo Input mysql password for root
mysqldump -uroot -p crawler > ${BACKUPDIR}${DATE}_data.sql

echo Packing data
echo tar -czvf  ${BACKUPDIR}${DATE}_data.tar  -C ${BACKUPDIR} ${DATE}_data.sql

echo removing old file
echo rm  ${BACKUPDIR}${DATE}_data.sql