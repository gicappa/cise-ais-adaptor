#!/usr/bin/env bash

source ./common.sh

# FUNCTIONS ###########################################
function usage {
	echo
	echo "Usage: ${0##*/} <certificate type> <country> <system>"
	echo
	echo "       where <country> 	      the country (2 letter code) for which to generate the gateway certificate"
	echo "             <system>           the name or part of the name of the priv/pub key to be included in the keystore."
	echo "             <keystore name>    the name of the keystore that will be generated."
	echo
}

check_presence $1
check_presence $2
check_presence $3

COUNTRY=$1
SYSTEM=$2
JKS_NAME=$3

echo "Creating a keystore file with root and signing certificate"
read -s -p "Enter the Keystore Password: " JKS_PASS
for FILE in $(find ${BUILD_DIR} -name '*.cert.pem'); do
  
  CERT_ALIAS=$(basename ${FILE%.cert.pem})

  keytool -importcert \
  	-noprompt \
  	-storepass $JKS_PASS \
  	-alias ${CERT_ALIAS} \
  	-file ${FILE} \
  	-keystore ${BUILD_DIR}/$JKS_NAME.jks
done

echo "Adding to the created keystore the key pair for the system ${SYSTEM}"
for FILE in $(find ${BUILD_DIR} -name "*${SYSTEM}*.p12"); do

  CERT_ALIAS=$(basename ${FILE%.key.p12})

  keytool -importkeystore \
  	-noprompt \
  	-srcstorepass $JKS_PASS \
  	-deststorepass $JKS_PASS \
  	-srckeystore ${FILE} \
  	-destalias ${CERT_ALIAS} \
  	-srcalias 1 \
  	-destkeystore ${BUILD_DIR}/$JKS_NAME.jks \
  	-srcstoretype pkcs12 
done

echo "Done."