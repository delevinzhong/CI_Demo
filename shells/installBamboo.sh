#!/bin/bash

bambooInstallationFile=~/
bambooHome=/app/bambooHome

java -version 
if [ $? != 0 ];then
echo -e "\033[31mYou haven't install JAVA, please install it first\033[0m"
exit 1
fi

gunzip $bambooInstallationFile
tar xvf -C $bambooHome

echo bamboo.home=$bambooHome >> $bambooHome/atlassian-bamboo/WEB-INF/classes/bamboo-init.properties

sh $bambooHome/bin/start-bamboo.sh

if [ $? -eq 0 ];then
echo "\033[33mInstalling finished, please go to http://localhose:8085 to check\033[0m"
fi



