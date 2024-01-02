# ReadableOTP

1. [Sinossi](#Sinossi)
2. [Dettaglio](#Dettaglio)
3. [Implementazione](#Implementazione)
4. [Conclusione e TODO](#Conclusione)

## Sinossi <a name="Sinossi"></a>
L'utilizzo di OTP, One Time Password, ovvero password a tempo spendibili una volta solamente, è ormai diventato molto comune.
Le tecnologie di autenticazione a 2 fattori si basano spesso sulla ricezione di sms o notifiche con degli OTP oppure sulla loro generazione tramite appositi token sincronizzati.
In determinati contesti è necessario inserire OTP in modo frequente (ad esempio per l'autenticazione a sessioni di durata particolarmente breve, oppure nel caso di operazioni che richiedono ripetutamente l'identificazione come le transazioni bancarie) ed aumenta anche la probabilità di errori di lettura/battitura con conseguente perdita di tempo.
Quando gli errori si sommano in modo anomalo si arriva anche al ban o al blocco dell'utenza, con ovvio disagio dell'utente e dell'assistenza.

L'idea alla base del POC ReadableOTP è proprio volta a verificare quanto sia necessaria una pseudocasualità completa sull'intero dominio delle cifre previste, piuttosto che limitarsi ad un insieme di schemi che rendono "maggiormente leggibile" l'OTP. Questo per verificare se è possibile ridurre potenziali errori di lettura/scrittura e la relativa frustrazione, senza introdurre sostanziali vulnerabilità di sicurezza.

## Dettaglio <a name="Dettaglio"></a>

### Gli ROTP
Tutti possono notare che determinate sequenze numeriche di OTP sono di rapida lettura e memorizzazione. Ho quindi cercato di individuare degli schemi per riprodurli che si basassero su
- poche cifre diverse 
- facilità e rapidità di scrittura
- pattern simmetrici e ripetitivi
- eventuale implicazione logica nella sequenza delle cifre
  
Esempi possono essere "123 123" oppure "00 11 00". Per ricavare questi schemi mi sono basato solo su semplici considerazioni empiriche.
Al fine di concentrarmi sul POC ho considerato esclusivamente OTP di 6 cifre, benché lo stesso approccio possa essere ovviamente esteso al contesto (altrettanto diffuso) degli OTP a 8 cifre.
Inoltre ogni OTP "semplificato", che da qui in poi chiamerò ROTP, potrà comporsi al massimo di 3 cifre diverse. 
La modalità con cui si individuano le cifre (x, y, z) è descritta da alcune regole, in modo posizionale.

Ad esempio
- [0-9] : la cifra può variare tra 0 e 9
- ! : la cifra deve essere differente dalle precedenti
- +1 : la cifra incrementa di una unità la cifra precedente
- -1 : la cifra decrementa di una unità la cifra precedente

Inoltre lo schema comprende anche la suddivisione dell'ROTP in parti, ovvero in 2 o 3 parti.

### ROTP schema
Di seguito gli schemi di ROTP che ho utilizzato nel mio POC

```
new ROTPSchema("xxxxxx", "0,9", PART_2, SCORE_NONE)
new ROTPSchema("xxxyyy", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xxyxxy", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xyyxyy", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xyxxyx", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xxyyxx", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xyyyyx", "0,9", "!", PART_2, SCORE_NONE)

new ROTPSchema("xxxxxy", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xxxxyx", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xxxyxx", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xxyxxx", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("xyxxxx", "0,9", "!", PART_2, SCORE_NONE)
new ROTPSchema("yxxxxx", "0,9", "!", PART_2, SCORE_NONE)

new ROTPSchema("xyzxyz", "0,9", "!", "!", PART_2, SCORE_NONE)
new ROTPSchema("xyzxyz", "0,7", "+1", "+1", PART_2, SCORE_NONE)
new ROTPSchema("xyzxyz", "2,9", "-1", "-1", PART_2, SCORE_NONE)

new ROTPSchema("xxxxyy", "0,9", "!", PART_3, SCORE_NONE)
new ROTPSchema("xxyyyy", "0,9", "!", PART_3, SCORE_NONE)
new ROTPSchema("xyxyxy", "0,9", "!", PART_3, SCORE_NONE)
new ROTPSchema("xxyyzz", "0,9", "!", "!", PART_3, SCORE_NONE)
```
Come si può notare ho previsto anche un punteggio attribuibile ad ogni schema per un eventuale filtro.
Una ipotesi può essere la seguente
- 1 digits --> +3
- 2 digits --> +2
- 3 digits --> +1
- symmetry --> +1 (easy to read)
- repetitive patterns --> +1 (easy to memorize)
- logical steps --> +1 (easy to write)
Oppure il punteggio potrebbe essere basato su test empirici sufficientemente numerosi.

Il meccanismo dovrà includere anche 
- un modo per contare le occorrenze di ROTP generabili in base ad un certo schema
- un modo per gestire gli ROTP duplicabili in schemi diversi
- un modo per "convertire" un OTP in un ROTP
- un modo per verificare l'appartenenza di un OTP ad uno schema

Mentre sono fuori dall'obiettivo le gestione ottimizzata e performante e le eventuali accortezze per salvare e ricaricare gli schemi.

## Implementazione <a name="Implementazione"></a>
La rappresentazione degli schemi di ReadableOTP è data dall'oggetto <b>ROTPSchema</b> il quale include lo schema di rappresentazione
(basato sulle ripetizioni di generiche variabili xyz), le regole per determinare il dominio delle variabili, il numero di parti in cui i relativi ROTP
devono essere rappresentati e lo score complessivo.
Il numero di cifre differenti incluse nell'RTOP va da 1 a 3 mentre per semplicità ogni schema fin qui considerato è di lunghezza 6. 
La principale funzione offerta è quella del metodo ```isMatching()``` che consente di verificare se una generica stringa numerica rientra nel dominio degli ROTP rappresentabili tramite lo schema.
In particolare si verifica se tra schema e candidato sono compatibili
- il numero di cifre diverse 
- la lunghezza 
- le regole dello schema con cui si determinano le cifre
- la sequenza delle cifre

Al fine di identificare uno schema in modo univoco si calcola il suo hash sha256 della concatenazione dello schema e delle rispettive regole.
Sarebbe prevista, ma al momento non implementata, una funzione di validazione.

Il cuore del POC è invece il generatore di readable OTP <b>ROTPGenerator</b>. Il generator include tra i membri una lista di ROTPSchema che può essere inizializzata nel costruttore (con un set predefinito di schemi) oppure successivamente col metodo ```overrideRules()``` .
Il costruttore (e ugualmente il metodo overrideRules) procede ad eseguire una validazione degli schemi. La validazione consiste in:
- verifica del numero di digit dello schema
- verifica della consistenza del pattern dello schema
- verifica dell'assenza di regole duplicate

I principali metodi del generator implementati sono 
- ```countMax()```: fornisce la somma totale degli ROTP generabili dal generator sulla base degli schemi definiti;
- ```generate()```: produce un ROTP;
- ```wrapToNext(String otp)```: trasforma un generico OTP nel suo ROTP più prossimo;

Ho empiricamente verificato che introducendo una frequenza di generazione di ROTP di 0.75 (1.0 = solo ROTP, 0.0 = solo OTP) si rende più "sicuro" il generatore, anche se ovviamente si riduce la sua efficacia in termini di leggibilità. 
La frequenza può essere impostata tramite il metodo ```setRotpFrequency(double rotpFrequency)```.

Infine la classe <b>ROTP</b> rappresenta il readable OTP, dunque il metodo ```get()``` ne ritorna il valore stringa già suddiviso nelle "parti" previste dal relativo schema.

## Conclusione e TODO <a name="Conclusione"></a>
Per testare il POC sono presenti vari test di unità che mi hanno mostrato potenzialità e limiti.
In particolare il POC non considera assolutamente il concetto di seed (https://en.wikipedia.org/wiki/Random_seed) nella generazione degli ROTP, ma si propone piuttosto come wrapper di generatori che invece lo includono. Lo scopo era quella di valutare quanto la riduzione da OTP puro a ROTP fosse impattante in termini di sicurezza. Nel test ```t06BruteForceWithFreq```, considerando una frequenza di 0.75 e le (poche) regole introdotte (solo 2816 ROTP) si ottiene che un ROTP viene centrato in media dopo circa 1800 tentativi. A mio avviso utilizzando gli ROTP la sospensione di un utente potrebbe anche essere portata a tre tentativi falliti entro 10 minuti rispetto ai soliti 5/10, considerandone la maggior facilità di lettura/scrittura. 
In tal caso considerando 10 minuti di sospensione ogni tre errori, si incorrerebbe in circa 4 giorni di stop prima di indovinare un ROTP.
Probabilmente però non sarebbe ancora un approccio sufficientemente sicuro, per lo meno in determinati contesti.

Ho lasciato da implementare, solo come idea, uno "score" da attribuire a ciascuno schema, previa valutazione basata su criteri oggettivi (da individuare) oppure empiricamente tramite un'applicazione esterna di test che annoti tempi ed errori. A quel punto la generazione potrebbe essere basata anche sullo score, come filtro.
Inoltre ho trascurato la parte di validazione degli schemi, che sarebbe da rendere più solida.
