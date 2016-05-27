#!/bin/bash

# Script per l'accesso al servizio rest 

REST_CONTEXT_ROOT=ProgettoREST/
# REST_APPLICATION=productmanager 
# REST_SERVICE=products 
REST_SERVICE_WAR=ProgettoREST.war 
REST_SERVICE_NAME=ProgettoREST
# REST_SERVICE_URL=http://10.11.1.101:8080/${REST_CONTEXT_ROOT}/${REST_SERVICE}
REST_SERVICE_URL=http://localhost:2212/${REST_CONTEXT_ROOT}

echo Accessing service rest ${REST_SERVICE_NAME} at ${REST_SERVICE_URL}

# accede a tutti i prodotti (JSON) 
echo "--------------------------------"
echo "POST ${REST_SERVICE_URL}/artists"
echo "--------------------------------"
echo
echo $(curl --data "name=PinkFloyd&country=UK" "${REST_SERVICE_URL}/artists")



