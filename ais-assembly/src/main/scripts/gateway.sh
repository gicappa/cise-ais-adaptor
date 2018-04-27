#!/usr/bin/env bash

# VARIABLES ###
GW_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
JAVA_EXEC=`which java`
JAVA_VERSION=`${JAVA_EXEC} -version 2>&1 | head -n 1 | awk '{ print $3 }'`
NOHUP_EXEC=`which nohup`
PID_DIR=${GW_HOME}/tmp
GW_PID_FILE=${PID_DIR}/gateway.pid
DEBUG_PORT=9999

# FUNCTIONS ###
function log_exit_msg {

test $? -eq 0 && echo ">>> gateway started at `date -Iseconds`" || \
    echo "xxx gateway not started"; exit 1

}

function log_start_msg {

echo ">>> CISE gateway"
echo ">>> Java path:    ${JAVA_EXEC}"
echo ">>> Java version: ${JAVA_VERSION}"

}

function log_debug_start_msg {

log_start_msg
echo ">>> DEBUG is ON listening on port ${DEBUG_PORT}"
echo ">>> the server is now waiting for a connection from a java remote "
echo ">>> debugger to the port ${DEBUG_PORT}"

}

function setup_debug {

export JAVA_OPTS="-Xdebug -agentlib:jdwp=adaptor=dt_socket,address=${DEBUG_PORT},server=y,suspend=y"

}

function start {

${NOHUP_EXEC} ${GW_RUN_CMD} > ${GW_HOME}/logs/localhost.log 2>&1 &
pid=$!
sleep 1
kill -0 ${pid} > /dev/null 2>&1
echo ${pid} > ${GW_PID_FILE}

echo ">>> 'tail -100f logs/localhost.log' will check the server log files"
echo
log_exit_msg

}

function run {

${GW_RUN_CMD} 2>&1

}

function gw_run_cmd {

if [ "$JAVA_VERSION" == "1.9" ]; then
    JAVA_OPTS="${JAVA_OPTS} --add-modules java.xml.bind"
fi

GW_RUN_CMD="${JAVA_EXEC} ${JAVA_OPTS} -Dconfdir=${GW_HOME}/conf/ \
-Djava.io.tmpdir=${GW_HOME}/tmp -jar ${GW_HOME}/lib/gateway.jar server \
${GW_HOME}/conf/gateway.yml"

}

# MAIN ###
cd ${GW_HOME}

echo

gw_run_cmd

case $1 in
    start)
        log_start_msg
        start
        exit 0
        ;;
    run)
        log_start_msg
        run
        exit 0
        ;;
    debug-start)
        setup_debug
        log_debug_start_msg
        gw_run_cmd
        start
        exit 0
        ;;
    debug-run)
        setup_debug
        log_debug_start_msg
        gw_run_cmd
        run
        exit 0
        ;;
    stop)
        GW_PIDS=`ps aux | grep gateway.jar | grep -v grep | awk '{ printf $2" " }'`

        (kill -15 ${GW_PIDS} 2>&1) > /dev/null && \
            echo ">>> gateway has been stopped" || echo "xxx the gateway was not running"

        rm -f ${GW_PID_FILE}
        ;;
    restart)
        $0 stop
        sleep 1
        $0 start
        ;;
    status)
        test `ps aux | grep gateway.jar | grep -v grep | wc -l` -eq 0 && \
            echo "xxx gateway is stopped" || echo ">>> gateway is running"
        ;;
    *)
        echo "Usage: gateway.sh {start|stop|restart|debug-start|debug-run|status}"
        exit 1
        ;;
esac

echo

exit 0