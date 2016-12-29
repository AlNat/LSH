#!/bin/bash
#initscript
apt-get update
apt-get install default-jdk

#Create DB
apt-get install postgresql-9.5
psql -f DB.sql

#Install tomcat
groupadd tomcat
useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat
wget http://www.us.apache.org/dist/tomcat/tomcat-8/v8.0.29/bin/apache-tomcat-8.0.39.tar.gz
tar zxf apache-tomcat-8.0.39.tar.gz

#Download LSH
wget http://github.com/AlNat/LSH/releases/download/v1.6.0/LSH.1.6.tar
tar zxf 1.6.tar

#Move postgresql jdbc to lib
cp postgresql-9.4.1212.jar /var/lib/tomcat8/lib/

#Move config to bin
cp config.xml /var/lib/tomcat8/bin/

#Fix config
nano /var/lib/tomcat8/bin/config.xml
#TODO Изменить ip

#Move and run war
service tomcat8 stop
cp LSHv1.6.war /var/lib/tomcat8/webapps/
service tomcat8 start
