# Digital garden - razpoznavanje vrtov

Ekipa:
    M&M

## Razpoznavalnik vrtov

Aplikacija vsebuje Flask REST API strežnik, ki sprejema slike poslane z mobilne aplikacije. Te nato posreduje v razpoznavalnik, 
ki iz vhodne slike samodejno prepozna območje vrta, ga razdeli na mrežo (tilemap) in rezultate pošlje na zaledni sistem projekta.

Razpoznavalnik deluje na podlagi segmentacije slike. V sliki razlikuje med vrtom in okolico na podlagi saturacije.

### Funkcionalnosti

- Nalaganje slike vrta (JPG / PNG)
- Razpoznavanje območja vrta
- Izdelava binarne maske in kontur vrta
- Pretvorba slike v mrežo (tilemap)
- Pretvorba mreže v vrtne elemente (JSON)
- Pošiljanje prepoznanega vrta v zunanji sistem prek uporabniške prijave

### Tehnologije
- Flask - REST API strežnik
- OpenCV - računalniški vid
- NumPY - delo z matrikami
- HTTP / JSON - mrežna komunikacija


## Namestitev
`git clone https://github.com/Denomi12/Digital-Garden.git`
`cd gardenRecognition`
`pip install requirements`
`python server.py`

## API Dokumentacija

`GET /` - Endpoint za testiranje delovanja

`POST /upload` - Endpoint za nalaganje slike vrta
    Parametri:
        image	
        username	
        password	
        width	
        height	


### Potek obdelave slike


1. Nalaganje slike

2. Izločanje vrta (extract_garden)

   - zameglitev (Gaussian Blur)
    
   - pretvorba v HSV
    
   - filtriranje po saturaciji
    
   - zaznava robov (Canny)
    
   - morfološko zapiranje
    
   - iskanje največjih kontur

3. Izdelava maske vrta

4. Pretvorba v tilemap (diskretna mreža)

5. Pretvorba blokov v elemente

6. Gradnja JSON strukture vrta

7. Pošiljanje vrta v zunanji sistem



