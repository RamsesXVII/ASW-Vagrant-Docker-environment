#!/bin/bash

# Script per l'accesso al servizio rest 

REST_CONTEXT_ROOT=ProgettoREST
# REST_APPLICATION=productmanager 
# REST_SERVICE=products 
REST_SERVICE_WAR=ProgettoREST.war 
REST_SERVICE_NAME=ProgettoREST
# REST_SERVICE_URL=http://10.11.1.101:8080/${REST_CONTEXT_ROOT}/${REST_SERVICE}
REST_SERVICE_URL=http://localhost:2212/${REST_CONTEXT_ROOT}

echo Accessing service rest ${REST_SERVICE_NAME} at ${REST_SERVICE_URL}

echo 
echo "----------------------------------------------------------------"
echo "GET ${REST_SERVICE_URL}/artists"
echo "----------------------------------------------------------------"
echo
echo $(curl -s -H "Accept:application/json" --get "${REST_SERVICE_URL}/artists")
echo
echo "NB: non sono state effettuate operazioni e quindi non ci sono record"
echo
echo "----------------------------------------------------------------"
echo "POST ${REST_SERVICE_URL}/artists"
echo "----------------------------------------------------------------"
echo
echo $(curl --data "name=PinkFloyd&country=UK" "${REST_SERVICE_URL}/artists")
echo
echo "È stato aggiunto un artista"
echo 
echo "----------------------------------------------------------------"
echo "GET ${REST_SERVICE_URL}/artists"
echo "----------------------------------------------------------------"
echo
echo $(curl -s -H "Accept:application/json" --get "${REST_SERVICE_URL}/artists")
echo
echo "NB: Adesso è presente un artista nel database"
echo
echo "----------------------------------------------------------------"
echo "POST ${REST_SERVICE_URL}/songs"
echo "----------------------------------------------------------------"
echo
ID="$(curl -s -H "Accept:application/json" --get "${REST_SERVICE_URL}/artists" | grep -o -E '[0-9][0-9][0-9]|[0-9]' | head -1)"
echo $(curl --data "name=Money&year=1973&idArtista="${ID}"" "${REST_SERVICE_URL}/songs")
echo
echo "NB: Adesso è presente una canzone nel database"
echo $(curl -s -H "Accept:application/json" --get "${REST_SERVICE_URL}/songs")









