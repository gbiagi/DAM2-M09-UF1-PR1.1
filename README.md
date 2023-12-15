# DAM2-MP09 #


## Compilació i funcionament ##

### Execució senzilla ###

#### Windows ####
```bash
run.ps1 <com.project.Main> <param1> <param2> <param3>
run.sh <com.project.Main> <param1> <param2> <param3>
```
#### Linux ####
```bash
run.ps1 <com.project.Main> <param1> <param2> <param3>
run.sh <com.project.Main> <param1> <param2> <param3>
```

On:
* <com.project.Main>: és la classe principal que vols executar.
* \<param1>, \<param2>, \<param3>: són els paràmetres que necessites passar a la teva aplicació.


### Execució pas a pas ###

Si prefereixes executar el projecte pas a pas, pots seguir les següents instruccions:

Neteja el projecte per eliminar fitxers anteriors:
```bash
mvn clean
```

Compila el projecte:
```bash
mvn compile test
```

Executa la classe principal:
```bash
mvn exec:java -q -Dexec.mainClass="<com.project.Main>" <param1> <param2> <param3>
```

On:
* <com.project.Main>: és la classe principal que vols executar.
* \<param1>, \<param2>, \<param3>: són els paràmetres que necessites passar a la teva aplicació.


