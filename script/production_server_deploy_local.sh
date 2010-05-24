rm -f /tomcat/svn/src/main/resources/config.prop.linux
cp /tomcat/svn/src/main/resources/config.prop.linux_prod /tomcat/svn/src/main/resources/config.prop.linux
mvn clean package tomcat:undeploy tomcat:deploy -DskipTests=true
mvn clean