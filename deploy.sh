#!/bin/bash -x

export CODE_URL="https://s3.ap-south-1.amazonaws.com/training-downloads/adobe/MyApp-Local.zip"

create_swap(){
    fallocate -l 2G /swapfile
    chmod 600 /swapfile
    mkswap /swapfile
    swapon /swapfile
    echo '/swapfile   none    swap    sw    0   0' >> /etc/fstab
}

install_java_and_tomcat(){
    apt-get update
    apt install python-software-properties -y
    apt-get install -y default-jdk tomcat8 maven unzip
    echo JAVA_HOME=\"$(readlink -f /usr/bin/java | sed "s:/bin/java::")\" >> /etc/environment
    source /etc/environment
}

install_mysql(){
    apt-get update
    export password="password"
    apt-get install -y debconf-utils
    echo "mysql-server mysql-server/root_password password $password" | sudo debconf-set-selections
    echo "mysql-server mysql-server/root_password_again password $password" | sudo debconf-set-selections
    apt-get install -y mysql-server
    mysql -uroot -p$password < schema.sql
}

pull_and_deploy_application(){
    mkdir -p /home/ubuntu/code
    cd /home/ubuntu/code
    wget -O webapp.zip $CODE_URL
    unzip webapp.zip
    mvn package
    cp ./target/webapp-1.0.0.war /var/lib/tomcat8/webapps/myapp.war
}

create_uploads_folder(){
    mkdir -p /var/www/uploads
    chown tomcat8:tomcat8 /var/www/uploads
}



create_swap
install_java_and_tomcat
install_mysql
pull_and_deploy_application
create_uploads_folder
