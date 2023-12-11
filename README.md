# ReadableOTP

1. [Sinossi](#Sinossi)
2. [Dettaglio](#Dettaglio)
3. [Implementazione](#Implementazione)
4. [Conclusione e TODO](#Conclusione)

## Sinossi <a name="Sinossi"></a>
L'utilizzo di OTP, One Time Password, ovvero password a tempo spendibili una volta solamente, è ormai diventato molto comune.
Le tecnologie di autenticazione a 2 fattori si basano spesso sulla ricezione di sms o notifiche con degli OTP oppure sulla loro generazione tramite appositi token sincronizzati.
In determinati contesti è necessario inserire OTP in modo frequente (ad esempio per l'autenticazione a sessioni di durata particolarmente breve, oppure nel caso di operazioni che richiedono ripetutamente l'identificazione
come le transazioni bancarie) ed aumenta anche la probabilità di errori di lettura/battitura con conseguente perdita di tempo.
Quando gli errori si sommano in modo anomalo si arriva anche al ban o al blocco dell'utenza, con ovvio disagio dell'utente e dell'assistenza.

L'idea alla base del POC ReadableOTP è proprio volta a verificare quanto sia necessaria una pseudocasualità completa sull'intero dominio delle cifre previste, piuttosto che limitarsi ad un insieme di schemi che rendono 
"maggiormente leggibile" l'OTP. Questo per verificare se è possibile ridurre potenziali errori di lettura/scrittura e la relativa frustrazione, senza introdurre sostanziali vulnerabilità di sicurezza.

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
new OTPRule("xxxxxx", "0,9", PART_2, SCORE_NONE));
new OTPRule("xxxyyy", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xxyxxy", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xyyxyy", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xyxxyx", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xxyyxx", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xyyyyx", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xxxxxy", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xxxxyx", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xxxyxx", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xxyxxx", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xyxxxx", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("yxxxxx", "0,9", "!", PART_2, SCORE_NONE));
new OTPRule("xyzxyz", "0,9", "!", "!", PART_2, SCORE_NONE));
new OTPRule("xyzxyz", "0,7", "+1", "+1", PART_2, SCORE_NONE));
new OTPRule("xyzxyz", "2,9", "-1", "-1", PART_2, SCORE_NONE));
new OTPRule("xxxxyy", "0,9", "!", PART_3, SCORE_NONE));
new OTPRule("xxyyyy", "0,9", "!", PART_3, SCORE_NONE));
new OTPRule("xyxyxy", "0,9", "!", PART_3, SCORE_NONE));
new OTPRule("xxyyzz", "0,9", "!", "!", PART_3, SCORE_NONE));
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

Il cuore del POC è invece il generatore di readable OTP <b>ROTPGenerator</b>. Il generator include tra i membri una lista di ROTPSchema che può essere inizializzata nel costruttore (con un set predefinito di schemi) oppure successivamente.

## Conclusione e TODO <a name="Conclusione"></a>


