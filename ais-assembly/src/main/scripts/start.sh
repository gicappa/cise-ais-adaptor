#!/usr/bin/env bash

GW_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"

exec ${GW_HOME}/bin/gateway.sh start