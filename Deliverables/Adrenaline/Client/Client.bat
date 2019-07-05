@echo off

REM read server address 
set /p ipaddr= <./client/client.cfg

REM read server port
for /f "skip=1 Tokens=*" %%G IN (./client/client.cfg) DO if not defined port set "port=%%G"

REM read interface
for /f "skip=2 Tokens=*" %%G IN (./client/client.cfg) DO if not defined interface set "interface=%%G"

REM read connection
for /f "skip=3 Tokens=*" %%G IN (./client/client.cfg) DO if not defined connection set "connection=%%G"

REM get ip address
set "ipaddr=%ipaddr: =" & set "ipaddr=%"

REM get port
set "port=%port: =" & set "port=%"

REM get interface
set "interface=%interface: =" & set "interface=%"

REM get connection
set "connection=%connection: =" & set "connection=%"

set "config=%ipaddr% %port% -int %interface% -conn %connection%"
java -jar ./client/Client.jar %config%
pause