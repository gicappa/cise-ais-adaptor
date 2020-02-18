#!/usr/bin/env bash

# This script will create six keystores to be used in the projects
source ./common.sh

CUR_DIR=$(pwd)
SEED_DIR=$( dirname "${BASH_SOURCE[0]}" )
SEED_DIR_FULL_PATH=$(cd ${SEED_DIR} && pwd)


cd ${SEED_DIR_FULL_PATH}

# Creating root certificate for Spain
./create-ca.sh es

# Creating ES Guardia Civil Certificates and Keys
./create-cert.sh es node gc-apache.node01
./create-cert.sh es ls gc-ls01.node01
./create-jks.sh es gc-apache.node01 node-es-gc
./create-jks.sh es gc-ls01.node01 adaptor-es-gc

# Creating root certificate for Italy
./create-ca.sh it

# Creating IT Guardia Costiera Certificates and Keys
./create-cert.sh it node gc-apache.node01
./create-cert.sh it ls gc-ls01.node01
./create-jks.sh it gc-apache.node01 node-it-gc
./create-jks.sh it gc-ls01.node01 adaptor-it-gc

# Creating IT Marina Militare Certificates and Keys
./create-cert.sh it node mmi-apache.node01
./create-cert.sh it ls mmi-ls01.node01
./create-jks.sh it mmi-apache.node01 node-it-mmi
./create-jks.sh it mmi-ls01.node01 adaptor-it-mmi

cd ${CUR_DIR}
