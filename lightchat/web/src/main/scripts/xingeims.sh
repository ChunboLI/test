#!/bin/sh
cd `pwd`

STR=`ps -C java -f | grep "XingeIMSMain" |awk '{print $2}'`
if [ ! -z "$STR" ]; then
    kill $STR > /dev/null 2>&1
else
        echo "没有Java进程"
fi

JETTY_PORT=8060
JETTY_CONTEXT_PATH="/"
JETTY_THREAD_POOL=200

JETTY_HOME=`readlink -f $0`
JETTY_HOME=`dirname $JETTY_HOME`
JETTY_HOME=${JETTY_HOME%/bin*}

echo "[INFO] JETTY_HOME=$JETTY_HOME"
echo "[INFO] JETTY_PORT=$JETTY_PORT"
echo "[INFO] JETTY_CONTEXT_PATH=$JETTY_CONTEXT_PATH"

#DIR_LOG=${JETTY_HOME}/logs
DIR_LOG=/home/admin/logs/xingeims
FILE_STDOUT_LOG=$DIR_LOG/stdout.log
FILE_STDERR_LOG=$DIR_LOG/stderr.log
FILE_START_LOG=$DIR_LOG/start.log


# 根据需要创建日志目录
if [ ! -e $DIR_LOG ] ; then
        mkdir -p $DIR_LOG
fi
NGINX_LOG=/home/admin/logs/nginx
if [ ! -e $NGINX_LOG ] ; then
        mkdir -p $NGINX_LOG
fi

MainClass="com.xinge.ims.web.XingeIMSMain"

CONFIG_DIR=${JETTY_HOME}/webapp/conf
if [ ! -e $CONFIG_DIR ] ; then
        mkdir -p $CONFIG_DIR
fi

sed s/@jetty.home@/${JETTY_HOME//\//\\/}/g ${JETTY_HOME}/bin/backup/nginx/xingeims-nginx.conf > ${JETTY_HOME}/webapp/conf/xingeims-nginx.conf

cp $JETTY_HOME/bin/backup/conf/xingeims-config.properties ${JETTY_HOME}/webapp/conf/xingeims-config.properties
echo "copy conf from ${JETTY_HOME}/bin/backup/conf/xingeims-config.properties" >> $FILE_START_LOG

rm -rf ${JETTY_HOME}/webapp/WEB-INF/spring/dal-context.xml
cp $JETTY_HOME/bin/backup/conf/dal-context.xml ${JETTY_HOME}/webapp/WEB-INF/spring/dal-context.xml

if [ -z "$JAVA_HOME" ]; then
  JAVA_HOME=/usr/java/jdk1.7.0_76
fi

#DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=n"

JAVA_OPTS="${DEBUG_OPTS} -server -Xmx1700m -Xms1700m -Xmn600m -Xss256k -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+UseStringCache -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSClassUnloadingEnabled -XX:+DisableExplicitGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=68 -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/admin/logs -Xloggc:/home/admin/logs/xingeims/gc.log -XX:ErrorFile=/home/admin/logs/xingeims/hs_err_pid%p.log"

APP_OPTS="-Djetty.context.path=/ -Djetty.war=${JETTY_HOME}/webapp -Djetty.port=${JETTY_PORT} -Dxingeims.log4j=${JETTY_HOME}/webapp/xingeims-log4j.xml -Dxingeims.config=${JETTY_HOME}/webapp/conf/xingeims-config.properties $@"

#NOW=`date +%Y%m%d.%H%M%S`
# 滚动STDOUT日志
#if [ -e $FILE_STDOUT_LOG ] ; then
#        mv $FILE_STDOUT_LOG $FILE_STDOUT_LOG.$NOW
#fi

# 滚动STDERR日志
#if [ -e $FILE_STDERR_LOG ] ; then
#        mv $FILE_STDERR_LOG $FILE_STDERR_LOG.$NOW
#fi


${JAVA_HOME}/bin/java -cp "${JETTY_HOME}/lib/*" $JAVA_OPTS $APP_OPTS $MainClass $APP_ARGS > ${FILE_STDOUT_LOG} 2>${FILE_STDERR_LOG} &
