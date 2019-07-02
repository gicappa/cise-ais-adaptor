#!/usr/bin/env bash

CUR_DIR=$(pwd)
SEED_DIR=$( dirname "${BASH_SOURCE[0]}" )
SEED_DIR_FULL_PATH=$(cd ${SEED_DIR} && pwd)

cd ${SEED_DIR_FULL_PATH}

./create-ca.sh fr
./create-cert.sh fr node apache.node01
./create-cert.sh fr ls sim1-node01.node01
./create-jks.sh fr sim1-node01 adaptor
./create-jks.sh fr apache node

cd ${CUR_DIR}
