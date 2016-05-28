# Progetto di Architetture dei sistemi software
#### Tabella dei contenuti

1. [Descrizione del progetto - Cosa è in grado di fare?](#obiettivi)
2. [Applicazione](#applicazione)
    * [Descrizione](#applicazione)
    * [Tecnologie utilizzate](#applicazione)
3. [Configurazione VM](#configurazione)
4. [Provisioning](#provisioning)
    * [Apache TomEE](#apache-tomee)
      * [Il ruolo della cartella condivisa](#cartella-condivisa)
      * [Postgres](#postgres)
      * [Setup](#setup)
      * [Creazione di un database](#creazione-di-un-database)
5. [Installazione](#installazione)
6. [Script di Test](#script-di-test)
7. [Comandi utili](#comandi-utili)
8. [Todo](#todo)
9. [Bug e problemi noti](#bug-e-problemi-noti)
10. [Realizzatori](#realizzatori)

### Obiettivi

Il progetto è stato realizzato nell'ambito del corso di Architetture dei Sistemi Software. La prima parte del progetto ha previsto tre obiettivi principali: 

  - la realizzazione di una semplice applicazione
  - la creazione di un ambiente per mandare in esecuzione l'applicazione
  - la possibilità di accedere all'applicazione, in esecuzione su una delle due macchine, dal proprio browser 

Attraverso l'utilizzo di [Vagrant][vagrant] è stato quindi realizzato un ambiente  costituito da due macchine virtuali.  La prima delle due macchine virtuali svolge la funzione di server e su di essa è installato [Apache TomEE][tomee], mentre sulla seconda è installato [Postgres][postgres].
La prima parte del progetto è disponibile [qui].

La seconda parte del progetto ha invece previsto due obiettivi principali:

 - la migrazione su Docker
 - la modifica della semplice applicazione Artisti-Canzoni, rendendola RESTful
 
Attraverso l'utilizzo di [Vagrant][vagrant] è stato quindi definita una macchina virtuale sulla quale è stato installato Docker. Sono quindi stati definiti su essa due container: il container tomee ([Apache TomEE][tomee]), e il container postgres ([Postgres][postgres]).



### Applicazione

L'applicazione, seguendo le specifiche, è minimale. Nella realizzazione è stato adottato lo stile architetturale REST. Si tratta quindi di un'evoluzione dell'applicazione precedente, in cui invece era stato adottato il pattern architetturale model-view-controller. Si tratta essenzialmente di un gestore di cantanti e canzoni e fornisce possibilità di inserimento e visualizzazione di entrambe le entità coinvolte. Il codice dell'applicazione è in Java ed è stato fatto uso delle API JAX-RS. Per la gestione della persistenza si è fatto uso del framework [JPA].
La differenza con la versione precedente è che non è stata creata nessuna interfaccia. 
Il codice dell'applicazione è disponibile nella cartella **project**.

### Configurazione
La macchina virtuale su cui è stato installato Docker è stata definita attraverso il Vagrantfile. 
Sapendo che il sistema sarebbe stato composto da un numero prefissato di contenitori (tomee e postgres) si è deciso di fare uso di Docker Compose. Per mandare in esecuzione l'ambiente è quindi necessario installare il plugin per Vagrant:
```sh
vagrant plugin install vagrant-docker-compose
```

### Provisioning
Come accennato per la parte di provisioning si è fatto uso di [Docker][Docker]. L'obiettivo principale del progetto è stato infatti quello di lanciare in esecuzione automatica l'intero ambiente attraverso un unico comando. 
Gli obiettivi del provisioning hanno previsto l'installazione delle seguenti componenti:
- nel container "tomee":
   - Apache-tomee-plus-1.7.4
   - Java 8
- nel container "postgres":
   - Postgres 9.3

   
Nel Vagrantfile sono quindi state specificate le condizioni di provisioning:

```sh
config.vm.provision :docker
  config.vm.provision :docker_compose, yml: "/vagrant/docker-compose.yml", rebuild: true, run: "always"
```
All'interno della cartella **environment** è stata quindi definita la seguente struttura :

```
+-- Vagrantfile
+-- docker-compose.yml
+-- _tomee
|   +--Dockerfile
|   +-- _src
|          +-- ProgettoREST.war
|          +-- postgresql-9.4.1208.jre6.jar
|                       
+-- _postgres
|    +--Dockerfile
```
Nel definire i Dockerfile per i contenitori tomee e postgres si è sfruttato il materiale disponibile sul registry pubblico di Docker, [Docker Hub][dockerhub]. 
Nel file **docker-compose.yml** sono quindi state specificate le configurazioni per i due contenitori. Per il contenitore **tomee** è stato necessario specificare un collegamento con il contenitore postgres, al fine di permettere il collegamento con il databese, oltre al port forwarding tra il contenitore stesso e l’host e alla definizione di una cartella condivisa tra host e container.
```sh
tomee:
  build: ./tomee
  ports:
    - "8080:8080"
  links:
    - postgres
  volumes:
    - /vagrant/tomee/src:/code
```      
Per il container **postgres** , non sono state necessarie particolari specifiche, se non il port forwarding sulla porta 8080. desiderata. 

#### Apache TomEE
##### Cartella condivisa

Per facilità d'uso il driver di postgres e l'applicazione sono state inserite nella cartella src presente in tomee  **tomee**. Sono state quindi specificate, nel Dockerfile, le operazioni necessarie sia allo spostamento del driver nella specifica cartella sia al deploy dell'applicazione:
```sh
COPY /src/postgresql-9.4.1208.jre6.jar /usr/local/tomee/lib/
COPY /src/ProgettoREST.war /usr/local/tomee/webapps/
```
Un'alternativa a questa scelta può essere quella di effettuare il download automatico del driver di postgres e, dopo aver caricato il file war dell'applicazione su Github e aver installato Git, effettuare il download in tomee/webapps.

#### Postgres

##### Setup

Alla configurazione base di Postgres, nel file docker-compose.yml, sono state aggiunte alcune specifiche per rendere possibile la connessione con il container **tomee**. In questa configurazione Postgres non impone vincoli sugli indirizzi d'ascolto nè di connessione, consentendo la connessione a **0.0.0.0/32**.

```sh
RUN echo "host all all 0.0.0.0/0 trust" >> /etc/postgresql/9.3/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.3/main/postgresql.conf
  ```
 Ciò che avviene a seguito di queste istruzioni è la modifica dei file postgres.conf e pg_hba.conf.
 
##### Creazione di un database
  
 Dopo aver specificato questi parametri è possibile verificare  le operazioni attraverso il comando:
 ```sh
$ docker exec -i -t DOCKER_ID /bin/bash
$ psql
```
(È possibile visualizzare i contenitori presenti sulla VM attraverso il comando "**docker ps**".

È stato inoltre opportuno creare un db music, attraverso cui l'applicazione sul contenire "tomee" può svolgere le proprie operazioni.
```sh
RUN psql --command "CREATE DATABASE music;"
```
Dalla VM è possibile connettersi al database appena creato per mostrare i dati presenti, attraverso la sequenza di comandi:
  ```sh
$ docker exec -i -t DOCKER_ID /bin/bash
$ psql
$ \connect music
$ SELECT * FROM artist;
$ SELECT * FROM song;
```


### Installazione

Per eseguire l'applicazione è necessaria l'installazione di Vagrant, del plugin Docker compose e VirtualBox. 
Se non si dispone di Docker compose per Vagrant: 
```sh
vagrant plugin install vagrant-docker-compose
```
Una volta installati i due software è sufficiente scaricare il progetto,

```sh
$ git clone https://github.com/RamsesXVII/ASW-Vagrant-Docker-environment.git
```
e dopo essersi posizionati nella cartella environment dare il comando:
```sh
$ vagrant up
```
Questa operazione può richiedere diversi minuti.
A questo punto per utilizzare l'applicazione è sufficiente connettersi alla pagina:
```sh
$ localhost:2212/ProgettoASW
```
ed effettuare richieste POST,GET,PUT ecc.

### Comandi utili
Nelle operazioni di testing possono risultare utili i seguenti comandi:
* Mostrare i contenitori in esecuzione sulla macchina
```sh
$ docker ps
```
Per connettersi ad un contenitore
```sh
$ docker exec -i -t "DOCKER_ID" /bin/bash
```

### Script di Test
Non disponendo di un'interfaccia è stato opportuno effettuare dei test attraverso dei semplici script, con l'utilizzo del comando **curl**. Nella cartella project/script\ client sono quindi disponibili diversi file sh, in grado di effettuare richieste di vario genere.
Per mostrare tutti gli artisti presenti nel database si effettua una richiesta GET alla risorsa artists:

```sh
echo $(curl -s -H "Accept:application/json" --get "${REST_SERVICE_URL}/artists")
```
Per aggiungere un nuovo artista al database:
```sh
echo $(curl --data "name=PinkFloyd&country=UK" "${REST_SERVICE_URL}/artists")
```
Poichè il database attribuisce degli id agli artisti al momento dell'inserimento, non è possibile conoscerli al momento dell'esecuzione dello script. In questa versione di test quindi si è fatto uso del comando **grep** per prendere l'id di un artista a caso ed inserire una canzone relativa all'artista scelto 
```sh
ID="$(curl -s -H "Accept:application/json" --get "${REST_SERVICE_URL}/artists" | grep -o -E '[0-9][0-9][0-9]|[0-9]' | head -1)"
echo $(curl --data "name=Money&year=1973&idArtista="${ID}"" "${REST_SERVICE_URL}/songs")
```



### ToDo
* 
### Bug e problemi noti

*  Dovendo configurare diverse VM con diverse configurazioni, sarebbe opportuno far uso di [Hiera][hiera].


### Realizzatori

 - Davinder Kumar
 - Mattia Iodice
 - Jhohattan Loza
 - Nicholas Tucci
 
[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [vagrant]: <https://www.vagrantup.com>
   [tomee]: <http://tomee.apache.org/index.html>
   [postgres]: <http://www.postgresql.org>
   [JPA]: <https://it.wikipedia.org/wiki/Java_Persistence_API>
   [JSF]: <https://it.wikipedia.org/wiki/Java_Server_Faces>
   [JSP]: <https://it.wikipedia.org/wiki/JavaServer_Pages>
   [Docker]: <https://www.docker.com>
   [hiera]:<https://docs.puppet.com/hiera/3.1/>
   [qui]:<https://github.com/Vzzarr/ASW_VagrantProvision>
   [dockerhub]:<https://hub.docker.com>



