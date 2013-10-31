#!/bin/sh
KEYSTORE=src/main/keystore/signing-jar.keystore
keytool -genkey -alias applet -keystore $KEYSTORE -storepass applet -keypass applet -dname "CN=developer, OU=apromore, O=com.processconfiguration, L=Brisbane, ST=Australia, C=AU"
keytool -selfcert -alias applet -keystore $KEYSTORE -storepass applet -keypass applet
