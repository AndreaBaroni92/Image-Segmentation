# Image Segmentation
## Scopo del Progetto
Applicare l'algoritmo k-means ai pixel che compongono un'immagine allo scopo di associare ad ogni pixel uno dei k cluster. Il risultato sarà un' immagine composta da k colori. 
## Implementazione
Il progetto è stato implementato tramite il linguaggio Scala avvalendosi del framework Apache Spark. Il file **src/main/scala/Image/ImageSegmentation.scala** si occupa di recuperare i pixel dell'immagine e inserirli in un dataset che ha la seguente struttura:  
| width | height | blue|green|red|
| :---: |:---:| :---: | :---: |:---: |
| 0| 0| 123 |230 |230 |
| 0| 1| 121 |230 |230 |
| 0| 2| ... |... |...|
| ...| ...| ... |... |...|  
  
Prima di essere restituito in output il dataframe viene inserito in una struttura dati denominata VectorAssembler la quale si occupa di inserire le componenti **blue, green, red** in un unico "vettore" che andrà a costituire le features che dovranno essere passate all'algoritmo Kmeans. Sia la struttura dati VectorAssembler che l'algoritmo Kmeans fanno parte della libreria MLib di Apache Spark.  
I file prodotti in output sono:

L'immagine modificata composta da k colori  

Un file in formato **csv** che rappresenta l'immagine con il seguente formato  

| width | height | cluster |
| :---: |:---:| :---: | 
| 0| 0| 1 |
| 0| 1| 2 |
| 0| 2| ...|
| ...| ...|... |  

Un file in formato **csv** che mostra i cluster con il seguente formato:   
| red | green | blue |
| :---: |:---:| :---: | 
| 220| 124| 6 |
| 123| 100| 7 |
| ...| ...|... |  

Un file in formato **csv**  che mostra le dimensioni dell'immagine (come numero di pixel) con il seguente formato:  
| width | height |
| :---: |:---:| 
| 1234| 900|

I file **csv** vengono impiegati dal programma **src/main/util/StampaImmagine.scala** per produrre in output l'immagine. Si è deciso di creare i file csv sebbene l'immagine con i k colori venga già restituita in output in quanto è utile per comprendere il processo di costruzione dell'immagine.  
Il file **project.properties** contiene le seguenti informazioni:  
- Url in cui si trova l'immagine
- numero di cluster
- percorsi in cui verranno salvati i file csv elencati precedentementi
- path dell'immagine in output


## Risulati ottenuti
All'interno della cartella **img** è possibile visionare i risultati ottenuti applicando l'algoritmo all'immagine **img/rome.jpeg**, il numero finale rappresenta il numero di cluster.

|originale|k = 2            |  k = 3 | k = 8|
:---:|:-------------------------:|:------: | :---:|
|![](img/rome.jpeg) |![](img/rome2.jpg)  |  ![](img/rome3.jpg)|![](img/rome8.jpg)|

## Installazione 
Per eseguire il progetto è sufficiente:  
1. Scaricare la cartella presente su GitHub
2. Spostarsi all'interno della cartella dove è presente il file **build.sbt** ed eseguire il comando `sbt`
3. Digitare il comando `package`
4. Modificare il file **project.properties** specificando l'url dell'immagine che si vuole modificare , il numero di cluster, le 3 cartelle dove verranno inseriti i file csv e il path dell'immagine restituita in output
5. Infine eseguire il seguente comando `spark-submit --class KMeans.KMeansImage --driver-memory 7g .\target\scala-2.12\progettoscp_2.12-0.1.jar` .  
<!-- -->
Il progetto è stato testato anche tramite la piattaforma Amazon aws. I passi da seguire sono:  
1. Creare un cluster tramite il servizio emr, in particolare selezionare la versione **emr-6.2.0** selezionando la voce **Spark: Spark 3.0.1 on Hadoop 3.2.1 YARN with and Zeppelin 0.9.0-preview1**
2. Riguardo la configurazione hardware si è utilizzato l'istanza **m5.xlarge** con 1 nodo master e 2 nodi principali
3. Creare un bucket tramite il servizio s3 e caricare il file jar presente nella cartella **target/scala-2.12**, l'immagine che si intende modificare e il file **project.properties** modificando il campo **image.url** con un indirizz0 della forma **s3://<pathImmagine>**. Inoltre è necessario modificare i campi **image.out**, **cluster.out**, **dim.out** con indirizzi che puntano al bucket s3. Il campo **path.out** contiene l'indirizzo dove verrà inserita l'immagine in output, questo non dovrà puntare al bucket in s3 in quanto sarà salvato nell'istance storage del cluster emr.
4. Connetersi tramite il servizio ssh al cluster appena creato, caricare tramite il comando **aws s3 cp** il file **.jar** e il file **project.properties**
5. Modificare tramite un editor come vi il file **project.properties** specificando l'indirizzo in s3 dell'immagine, il numero di cluster, i tre indirizzi s3 delle cartelle **image.out, cluste.out, dim.out**, infine specificare il campo **path.out** senza creare un indirizzo che punti ad s3 in quanto verrà creato in locale.
6. Eseguire il file jar nel cluster appena creato tramite il comando: `spark-submit --class KMeans.KMeansImage progettoscp_2.12-0.1.jar`
7. Copiare in s3 l'immagine in output tramite il comando: ` aws s3 cp imageout.jpg s3://<bucket>/image.jpg`.




