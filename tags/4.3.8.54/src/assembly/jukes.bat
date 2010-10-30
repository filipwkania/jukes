@echo off
REM ################################
REM #   Jukes Windows Launch Script  #
REM #                              #
REM #   http://www.melloware.com   #
REM ################################
REM Modify JAVA_HOME according to your J2SE installation.

REM #JAVA_HOME=/usr/java/jdk1.6.0


REM You should not need to modify the script beyond this point.

java -Xms128m -Xmx512m -jar @jar_file@
