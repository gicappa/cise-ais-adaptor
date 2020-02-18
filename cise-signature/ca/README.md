# CISE Test-bed Certificate Authority

This is a tool to create certificates for all the components in the JRC CISE Test Bed.

It is basically composed of some BASH scripts that can be found in the _./bin_ folder. 

The scripts are using _OpenSSL_.

For each country there will be created the following private keys and certificates:
  * for the root CA of that country
  * for the gateways of that country
  * for the light clients of that country
  
 Currently the certificates hierarchy is the following
 
 1. Spain (ES)
    * Root CA ES
        * Signing CA ES
            * GW01 (Spanish GW 01)   
                * GC-LS01 (Guardia Civil Legacy System 01) 
 2. Italy (IT)
    * Root CA IT
        * Signing CA IT
            * GW01 (Italian GW 01)
                * GC-LS01 (Guardia Costiera Legacy System 01)
            * GW02 (Italian GW 02) 
                * MM-LS01 (Marina Militare Legacy System 01)
  3. France (FR)
     * Root CA FR
        * Signing CA FR
            * GW01 (French GW01)
                * MN-LS01(Marine Nationale Legacy System 01)
            
            
 The master script that creates all the certificate / private key pairs is found in _./bin/main.sh_ .

 It is in that script that new countries, gateways, or legacy systems can be added.
 
 For each country a new folder is created with the two letter country code.

 Under the country folder there will be created two folders: one for the root CA, and one for the signing CA
 (aka the sub-CA).
 
 Under the root / signing CA there should be two folders: 
  * _certs_ : contains all the certificates issued by that CA, and the public keys (_pem_ files, respective _p12_ file)
  * _private_: contains all the private keys issued by that CA
  
 In there should be 2 _JKS_files: one containing the public certificates, and one containing the private keys
  
 ##Usage
 
 * Open new linux terminal
 * Delete all country folders and the JKS files in this folder (everything by the _bin_ folder and this _README_)
 * run _./bin/do_all.sh_
 * copy the JKS files in the following projects:
    * cise-ais-adaptor
    * cise-conformity-test
    * cise-gateway
    * eucise-com-signature-lib
 * copy the needed _PEM_ files in the cise-light-client project
 * rebuild the Docker containers for all Test-Bed components
 * start the Test-Bed and run some simple integration tests to see that messaging between components work
 
 
 ## TODO
    Long story

 
 

 
 