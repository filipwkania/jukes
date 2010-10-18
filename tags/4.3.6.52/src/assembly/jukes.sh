#!/bin/sh
################################
#   Jukes Un*x Launch Script   #
#                              #
#   http://www.melloware.com   #
################################
# Modify JAVA_HOME according to your J2SE installation.

#JAVA_HOME=/usr/java/jdk1.6.0


# You should not need to modify the script beyond this point.
# ---------------------------------------------------------------------------------
java -Xms128m -Xmx256m -jar @jar_file@
# ---------------------------------------------------------------------------------