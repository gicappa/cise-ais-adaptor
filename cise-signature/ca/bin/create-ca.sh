#!/usr/bin/env bash

source ./common.sh

# FUNCTIONS ###########################################
function usage {
	echo
	echo "Usage: ${0##*/} <country>"
	echo "       where <country>    the country (2 letter code) for which to initialize the CA"
	echo
}

check_presence $1

COUNTRY=$1

CUR_DIR=$(pwd)
SEED_DIR=$( dirname "${BASH_SOURCE[0]}" )
SEED_DIR_FULL_PATH="$(cd ${SEED_DIR} && pwd)"
TARGET_DIR=$(cd ${SEED_DIR}/.. && pwd)/build/$1

ROOT_CA_FOLDER="root"
SIGNING_CA_FOLDER="signing"

ROOT_PRIV_KEY_FILE=root-ca.eucise.${COUNTRY}.key.pem
ROOT_PRIV_KEY_PATH=${ROOT_CA_FOLDER}/private/${ROOT_PRIV_KEY_FILE}

SIGNING_PRIV_KEY_FILE=signing-ca.eucise.${COUNTRY}.key.pem
SIGNING_PRIV_KEY_PATH=${SIGNING_CA_FOLDER}/private/${SIGNING_PRIV_KEY_FILE}

ROOT_CERT_FILE=root-ca.eucise.${COUNTRY}.cert.pem
ROOT_CERT_PATH=${ROOT_CA_FOLDER}/certs/${ROOT_CERT_FILE}
ROOT_CERT_SUBJ="/C=${COUNTRY}/O=eucise/CN=root-ca.eucise.${COUNTRY}"

SIGNING_CSR_FILE=signing-ca.eucise.${COUNTRY}.csr.pem
SIGNING_CSR_PATH=${SIGNING_CA_FOLDER}/csr/${SIGNING_CSR_FILE}
SIGNING_CERT_SUBJ="/C=${COUNTRY}/O=eucise/CN=signing-ca.eucise.${COUNTRY}"

SIGNING_CERT_FILE=signing-ca.eucise.${COUNTRY}.cert.pem
SIGNING_CERT_PATH=${SIGNING_CA_FOLDER}/certs/${SIGNING_CERT_FILE}

CERT_CHAIN_FILE=signing-ca-chain.eucise.${COUNTRY}.cert.pem
CERT_CHAIN_PATH=${SIGNING_CA_FOLDER}/certs/${CERT_CHAIN_FILE}

echo "Deleting the build directory"
clean_up_build_dir ${COUNTRY}

echo "Initializing Certification Authority for country: ${COUNTRY} in folder ${TARGET_DIR}"

mkdir -p ${TARGET_DIR}
cd ${TARGET_DIR}
mkdir -p ${ROOT_CA_FOLDER}
mkdir -p ${SIGNING_CA_FOLDER}

ROOT_CA_FOLDER_FULL_PATH="${TARGET_DIR}/${ROOT_CA_FOLDER}"
SIGNING_CA_FOLDER_FULL_PATH="${TARGET_DIR}/${SIGNING_CA_FOLDER}"

cd ${ROOT_CA_FOLDER_FULL_PATH}
mkdir certs crl newcerts private
chmod 700 private
touch index.txt
echo 1000 > serial
cd ${TARGET_DIR}

cd ${SIGNING_CA_FOLDER_FULL_PATH}
mkdir certs crl csr newcerts private
chmod 700 private
touch index.txt
echo 1000 > serial
cd ${TARGET_DIR}

echo "....Successfully created folder structure"
cat ${SEED_DIR_FULL_PATH}/root-openssl.cnf | sed "s|XXBASEDIRXX|$ROOT_CA_FOLDER_FULL_PATH|g" | sed "s|XXCOUNTRYXX|$COUNTRY|g" > ${ROOT_CA_FOLDER_FULL_PATH}/openssl.cnf
cat ${SEED_DIR_FULL_PATH}/signing-openssl.cnf | sed "s|XXBASEDIRXX|$SIGNING_CA_FOLDER_FULL_PATH|g" | sed "s|XXCOUNTRYXX|$COUNTRY|g" > ${SIGNING_CA_FOLDER_FULL_PATH}/openssl.cnf

echo "...Successfully copied openssl configuration files"

echo "Generating Root CA private key to path: ${ROOT_PRIV_KEY_PATH}"
openssl genrsa  -out ${ROOT_PRIV_KEY_PATH}
chmod 400 ${ROOT_PRIV_KEY_PATH}
echo "...Successfully generated private key for Root CA"

echo "Generating Signing CA private key to path: ${SIGNING_PRIV_KEY_PATH}"
openssl genrsa  -out ${SIGNING_PRIV_KEY_PATH}
chmod 400 ${SIGNING_PRIV_KEY_PATH}
echo "...Successfully generated private key for Signing CA"

echo "Generating Certificate for Root CA to path: ${ROOT_CERT_PATH} Subject: [${SIGNING_CERT_SUBJ}]"
openssl req -config ${ROOT_CA_FOLDER}/openssl.cnf \
      -subj "${ROOT_CERT_SUBJ}" \
      -key ${ROOT_PRIV_KEY_PATH} \
      -new -x509 -days 7300 -sha1 -extensions v3_ca \
      -out ${ROOT_CERT_PATH}

chmod 444 ${ROOT_CERT_PATH}
echo "...Successfully generated certificate for Root CA"


echo "Generating CSR file for Signing CA Certificate to path: ${SIGNING_CSR_PATH}. Subject: [${SIGNING_CERT_SUBJ}]"
openssl req -config ${SIGNING_CA_FOLDER}/openssl.cnf -new -sha1 \
      -subj "${SIGNING_CERT_SUBJ}" \
      -key ${SIGNING_PRIV_KEY_PATH} \
      -out ${SIGNING_CSR_PATH}
echo "...Successfully generated CSR file for Signing CA Certificate"

echo "Generating certificate file for Signing CA to path: ${SIGNING_CSR_PATH}"
openssl ca -batch -config  ${ROOT_CA_FOLDER}/openssl.cnf \
      -extensions v3_intermediate_ca \
      -days 3650 -notext -md sha1 \
      -in ${SIGNING_CSR_PATH} \
      -out ${SIGNING_CERT_PATH}

chmod 444 ${SIGNING_CERT_PATH}

echo "...Successfully generated certificate file"


echo "Verifying signature of Signing CA Certificate"
openssl verify -CAfile ${ROOT_CERT_PATH} ${SIGNING_CERT_PATH}
echo "...Finished signature verification"


echo "Generating certificate chain file ${CERT_CHAIN_PATH}"
cat ${ROOT_CERT_PATH} ${SIGNING_CERT_PATH} > ${CERT_CHAIN_PATH}
chmod 444 ${CERT_CHAIN_PATH}
echo "...Successfully generated certificate chain file"

cd ${CUR_DIR}
