@echo off

echo "This script will create a filelist.txt and you need to create repositories refer to it."

set path=%path%;C:/Program Files (x86)/Git/bin

echo "Please input the path: "

set /p dir=

IF EXIST %dir%\filelist.txt del %dir%\filelist.txt

for /f "delims=" %%i in ('dir /ad/b "%dir%"') do (echo %%i,>>%dir%\filelist.txt)

cd %dir%
