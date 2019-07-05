@echo off

REM read ip address
set /p ipaddr= <./server/config/server.cfg

REM read port
for /f "skip=1 Tokens=*" %%G IN (./server/config/server.cfg) DO if not defined port set "port=%%G"

REM get ip address
set "ipaddr=%ipaddr: =" & set "ipaddr=%"

REM get port
set "port=%port: =" & set "port=%"

REM start server
java -jar ./server/Server.jar %ipaddr% %port%
pause