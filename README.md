# ReadableOTP

1. [Sinossi](#Sinossi)
2. [Dettaglio](#Dettaglio)
3. [Implementazione](#Implementazione)
4. [TO-DO](#TO-DO]

## Sinossi <a name="Sinossi"></a>
L'utilizzo di OTP, One Time Password, ovvero password a tempo spendibili una volta solamente, è ormai diventato molto comune.
Le tecnologie di autenticazione a 2 fattori si basano spesso sulla ricezione di sms o messaggi con degli OTP oppure sulla loro generazione tramite appositi token sincronizzati.
In determinati contesti è necessario inserire OTP in modo frequente (ad esempio per l'autenticazione a sessioni di durata particolarmente breve, oppure nel caso di operazioni che richiedono ripetutamente l'identificazione
come le transazioni bancarie) tendono a verificarsi errori di lettura/battitura con conseguente perdita di tempo.
Quando gli errori sono ripetuti si arriva anche al ban o al blocco dell'utenza, con ovvio disagio dell'utente e/o dell'assistenza.

L'idea alla base del POC ReadableOTP è proprio volta a verificare quanto sia necessaria una pseudocasualità completa sull'intero dominio delle cifre, piuttosto che variare all'interno di un insieme di schemi che rendono 
"maggiormente leggibile" l'OTP. Questo per verificare se è possibile ridurre potenziali errori di lettura/scrittura e la relativa frustrazione, senza introdurre sostanziali vulnerabilità di sicurezza.

## Dettaglio <a name="Dettaglio"></a>
### OTP schema
### Rules score classification
Using up to 3 different digits rules
- 1 digits --> +2 Points
- 2 digits --> +1 Points
- 3 digits --> +0.5 Point
  
Including as facilities 
- symmetry (1) --> +1 Point (easy to read)
- repetitive patterns (2) --> +1 Point (easy to memorize)
- logical steps (3) --> +0.5 Point (easy to write)

### six digit

#### two parts
- xxx xxx, 111 111, [0-9]
- xxx yyy, 111 444, [0-9], !
- xxy xxy, 004 004, [0-9], !
- xyy xyy, 733 733, [0-9], !
- xyx xyx, 414 414, [0-9], !
- xxy yxx, 885 588, [0-9], !
- xyy yyx, 766 667, [0-9], !

- xyz xyz, 234 234, [0-7], +1, +1

- xyz xyz, 321, 321, [2-9], -1, -1
- xyz zyx, 123 321, [0-7], +1, +1
- xxx xxy, 000 004, [0-9], !
- xxx xyx, 000 040, [0-9], !
- xxx yxx, 000 400, [0-9], !
- xxy xxx, 004 000, [0-9], !
- xyx xxx, 040 000, [0-9], !
- yxx xxx, 400 000, [0-9], !

--> test to verify rotp not duplicated by rules

#### three parts
- xx xx yy, 66 66 33, [0-9], !  
- xx yy yy, 00 44 44, [0-9], !
- xy xy xy, 93 93 93, [0-9], !

- xx yy zz, 77 22 44, [0-9], !, !

### eight digit
#### two parts
- xxxx xxxx, 5555 5555, x=[0-9]

- xxxx yyyy, 1111 0000, [0-9], !
- xyxy xyxy, 2121 2121, [0-9], !
- xxyy xxyy, 0099 0099, [0-9], !
- xxxx xxxy, 2222 2227, [0-9], !
- xyyy yyyy, 4666 6666, [0-9], !
- xxyy yyxx, 8822 2288, [0-9], !

(+ subset)
- xxxx yyyy, 1111 2222, [0-8], +1
- xyxy xyxy, 3434 3434, [0-8], +1
- xxyy xxyy, 8899 8899, [0-8], +1
- xxxx xxxy, 2222 2223, [0-8], +1
- xyyy yyyy, 4555 5555, [0-8], +1
- xxyy yyxx, 8899 9988, [0-8], +1

- xyzz xyzz, 1322 1322, [0-9], !, !
- xyyz xyyz, 4116 4116, [0-9], !, !
- xyyy zzzz, 0999 3333, [0-9], !, !

#### three parts?
- xx yyyy xx
- xy yyyy xy

## Implementazione <a name="Implementazione"></a>

## TO-DO <a name="TO-DO"></a>
