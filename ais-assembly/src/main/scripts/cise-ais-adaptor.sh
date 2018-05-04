#!/usr/bin/env bash

# VARIABLES ###
SW_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
JAVA_EXEC=`which java`
JAVA_VERSION=`${JAVA_EXEC} -version 2>&1 | head -n 1 | awk '{ print $3 }'`
NOHUP_EXEC=`which nohup`
PID_DIR=${SW_HOME}/tmp
PID_FILE=${PID_DIR}/cise-ais-adaptor.pid
DEBUG_PORT=9999

if test -t 1; then
    # see if it supports colors...
    NCOLOR=$(tput colors)

    if test -n "$NCOLOR" && test $NCOLOR -ge 8; then
        NOR="$(tput sgr0)"
        RED="$(tput setaf 1)"
        GRE="$(tput setaf 2)"
    fi
fi

OK="[${GRE}ok${NOR}]"
KO="[${RED}x${NOR}]"

# FUNCTIONS ###
function log_exit_msg {

E=$?
test ${E} -eq 0 && echo "${OK} cise-ais-adaptor started at `date -Iseconds`" || \
    echo "${KO} cise-ais-adaptor not started"; exit ${E}

}

function log_start_msg {

echo "${GRE}>>> CISE AIS Adaptor <<<${NOR}"
echo "> Java path:    ${JAVA_EXEC}"
echo "> Java version: ${JAVA_VERSION}"

}

function log_debug_start_msg {

log_start_msg
echo "> DEBUG is ON listening on port ${DEBUG_PORT}"
echo "> the server is now waiting for a connection from a java remote"
echo "> debugger to the port ${DEBUG_PORT}"

}

function setup_debug {

export JAVA_OPTS="-Xdebug -agentlib:jdwp=adaptor=dt_socket,address=${DEBUG_PORT},server=y,suspend=y"

}

function start {

${NOHUP_EXEC} ${SW_RUN_CMD} > ${SW_HOME}/logs/localhost.log 2>&1 &
pid=$!
sleep 1
kill -0 ${pid} > /dev/null 2>&1
echo ${pid} > ${PID_FILE}

echo "> 'tail -100f logs/localhost.log' will check the server log files"
echo
log_exit_msg

}

function run {

${SW_RUN_CMD} 2>&1

}

function sw_run_cmd {

if [ "$JAVA_VERSION" == "1.9" ]; then
    JAVA_OPTS="${JAVA_OPTS} --add-modules java.xml.bind"
fi

SW_RUN_CMD="${JAVA_EXEC} ${JAVA_OPTS} -Dconfdir=${SW_HOME}/conf/ \
-Djava.io.tmpdir=${SW_HOME}/tmp -jar ${SW_HOME}/lib/cise-ais-adaptor.jar -d"

}

# MAIN ###
cd ${SW_HOME}

echo

sw_run_cmd

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
        sw_run_cmd
        start
        exit 0
        ;;
    debug-run)
        setup_debug
        log_debug_start_msg
        sw_run_cmd
        run
        exit 0
        ;;
    stop)
        PIDS=`ps aux | grep cise-ais-adaptor.jar | grep -v grep | awk '{ printf $2" " }'`

        (kill -15 ${PIDS} 2>&1) > /dev/null && \
            echo "${OK} cise-ais-adaptor has been stopped" || echo "${KO} the cise-ais-adaptor was not running"

        rm -f ${PID_FILE}
        ;;
    restart)
        $0 stop
        sleep 1
        $0 start
        ;;
    status)
        test `ps aux | grep cise-ais-adaptor.jar | grep -v grep | wc -l` -eq 0 && \
            echo "${KO} cise-ais-adaptor is stopped" || echo "${OK} cise-ais-adaptor is running"
        ;;
    *)
        echo "Usage: cise-ais-adaptor.sh {start|stop|restart|debug-start|debug-run|status}"
        exit 0
        ;;
esac

echo

exit 0