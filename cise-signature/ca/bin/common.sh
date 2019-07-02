#!/usr/bin/env bash

### Helper VARIABLES

# The bash scripts are in the bin directory so the BASE_DIR
# must be one path step before.
BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"

# The build dir is where all the artifacts will be put and
# that should be ignore by the SCM
BUILD_DIR=${BASE_DIR}/build

### Helper FUNCTIONS

# Check the presence of a parameter passed by the user.
# If the parameter is not present it will display the usage
function check_presence {
	if [[ "$1" == "" ]]; then
		usage
		exit 1
	fi
}

# Base usage function explaining how to use the bash script. This
# function must be overwritten by the script using the
# check_presence function
function usage {
	echo
	echo "There is a parameter mismatch when launching ${0##*/}"
	echo "(NOT to implementer: the BASH 'usage()' function should"
	echo "be overridden in the script using the common.sh library)"
	echo
}

# Deleting the build dir. Expects the country to be deleted as a parameter.
function clean_up_build_dir {
    check_presence $1

    rm -rif ${BUILD_DIR}/$1 || true
}

### COMMON BEHAVIOR

# Exit immediately if a command exits with a non-zero status.
# Not always agreed this is a good practice.
# https://stackoverflow.com/questions/19622198/what-does-set-e-mean-in-a-bash-script
set -e

