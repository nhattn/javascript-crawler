rm -f /y/workspace/webcrawl/src/main/resources/config.prop.linux
cp /y/workspace/webcrawl/src/main/resources/configfiles/config.prop.linux_prod /y/workspace/webcrawl/src/main/resources/config.prop.linux
mvn clean package tomcat:undeploy tomcat:deploy -DskipTests=true
rm -f /y/workspace/webcrawl/src/main/resources/config.prop.linux
cp /y/workspace/webcrawl/src/main/resources/configfiles/config.prop.linux_dev /y/workspace/webcrawl/src/main/resources/config.prop.linux
mvn clean