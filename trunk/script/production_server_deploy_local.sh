rm -f /tomcat/source/crawler/src/main/resources/config.prop.linux
cp /tomcat/source/crawler/src/main/resources/config.prop.linux_prod /tomcat/source/crawler/src/main/resources/config.prop.linux
mvn clean package tomcat:undeploy tomcat:deploy -DskipTests=true
mvn clean