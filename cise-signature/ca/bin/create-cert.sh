#!/usr/bin/env bash

source ./common.sh

# FUNCTIONS ###########################################
function usage {
	echo
	echo "Usage: ${0##*/} <certificate type> <country> <system id>"
	echo "       where <country>          the country (2 letter code) for which to generate the gateway certificate"
	echo "             <system type>      specify 'node' or 'ls' for gateway/node or legacy systems"
	echo "             <system id>        the system ID (gateway ID or legacy system ID) without the country specific prefix"
	echo
}

CUR_DIR=$(pwd)
SEED_DIR=$( dirname "${BASH_SOURCE[0]}" )
SEED_DIR_FULL_PATH=$(cd ${SEED_DIR} && pwd)

check_presence $1
check_presence $2
check_presence $3

COUNTRY=$1
SYS_TYPE=$2
SYS_ID=$3

TARGET_DIR=$(cd ${SEED_DIR}/.. && pwd)/build/${COUNTRY}
TARGET_CN="${SYS_ID}.eucise.${COUNTRY}"

if [[ ${SYS_TYPE} == 'node' ]]; then
	TARGET_SUBJ_OU="HOSTS"
  elif [[ ${SYS_TYPE} == 'ls' ]]; then
	TARGET_SUBJ_OU="Participants"
  else
     usage
     exit 1
fi

# C=fr,DC=eucise,O=node01,OU=Participants,CN=sim1-node01.node01.eucise.fr
# WARNING: the O is not completely equals to the RTI definition
# for RTI the definition is O=node01 while in this case would be O=sim1-node01.node01
TARGET_SUBJ="/CN=${SYS_ID}.eucise.${COUNTRY}/OU=${TARGET_SUBJ_OU}/O=${SYS_ID}/DC=eucise/C=${COUNTRY}"

cd ${SEED_DIR_FULL_PATH}


SIGNING_CA_PATH="${TARGET_DIR}/signing"
PRIVATE_KEY="${SIGNING_CA_PATH}/private/${TARGET_CN}.key.pem"
CSR_FILE="${SIGNING_CA_PATH}/csr/${TARGET_CN}.csr.pem"
CERT_FILE="${SIGNING_CA_PATH}/certs/${TARGET_CN}.cert.pem"
PKCS12_KEY_STORE="${SIGNING_CA_PATH}/certs/${TARGET_CN}.key.p12"

cd ${SIGNING_CA_PATH}

echo "Creating key and certificate for ${TARGET_SUBJ}"

echo "Creating private key in file ${PRIVATE_KEY}..."
openssl genrsa -out ${PRIVATE_KEY} 2048
chmod 400 ${PRIVATE_KEY}
echo "...Private key was successfully created "


echo "Creating csr in file ${CSR_FILE}..."
openssl req -config openssl.cnf \
      -subj "${TARGET_SUBJ}" \
      -key ${PRIVATE_KEY} \
      -new -sha256 -out ${CSR_FILE}
echo "...Csr file was successfully created"

echo "Creating certificate in file ${CERT_FILE}"
openssl ca -batch -config openssl.cnf \
      -extensions server_cert -days 3650 -notext -md sha256 \
      -in ${CSR_FILE} \
      -out ${CERT_FILE}
chmod 444 ${CERT_FILE}
echo "...Certificate was successfully created"

echo "Verifying the certificate..."
openssl x509 -noout -text \
      -in ${CERT_FILE}

openssl verify -CAfile ./certs/signing-ca-chain.eucise.${COUNTRY}.cert.pem \
      ${CERT_FILE}



echo "Exporting private key and certificate in PKCS12 private keystore"
read -s -p "Enter the Private Key Password: " PRIV_KEY_PASS

openssl pkcs12 -export  -in ${CERT_FILE} -inkey ${PRIVATE_KEY} -passout pass:${PRIV_KEY_PASS} > ${PKCS12_KEY_STORE}

echo "...Successfully exported to P12 keystore"

echo "Certificate generation process is now finished."

cd ${CUR_DIR}