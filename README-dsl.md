# Digitalni vrt - domensko specifični jezik

## 1.1 Namen projekta

Izdelava domensko specifičnega jezika za opis parkov in zelenih površin v mestih. Definicija konstruktov, ki opisujejo parke in elemente v njih, gramatika v BNF notaciji, ki predstavlja sintaktična pravila za zapis jezika ter leksikalni, sintaktični in semantični analizator na osnovi te gramatike, ki vrača predstavitev konstruktov jezika v GeoJSON formatu.

---

## 1.2 Ciljne skupine uporabnikov in njihove potrebe

### Načrtovalci parkov in zelenih površin

- Domensko specifični jezik za opis parkov
- Gramatika v BNF notaciji
- Leksikalni analizator
- Sintaktični analizator
- Semantični analizator
- Prikaz parka z GeoJSON formatom

---

## 1.3 Funkcionalne zahteve

| Funkcionalnost         | Opis                                                                                   |
| ---------------------- | -------------------------------------------------------------------------------------- |
| Sintaktična pravila    | Gramatika v BNF notaciji.                                                              |
| Leksikalni analizator  | Pretvorba tekstovnega zapisa v tokene.                                                 |
| Sintaktični analizator | Sintaktična analiza in preverba validnosti zapisane strukture stavka.                  |
| Semantični analizator  | Semantična analiza, pomenska obdelava zapisanih tokenov in pretvorba v GeoJSON format. |
| Prikaz parka           | Formatiran zapis in vizualni prikaz parkov v GeoJSON formatu.                          |

**Gramatika**
```
PROGRAM ::= QUERY
QUERY ::= PARK
PARK ::= park ime { boundary: { POLYGON } ELEMENTS }
ELEMENTS ::= ELEMENT ELEMENTS’ 
ELEMENTS' ::= ELEMENT ELEMENTS’ | ε
ELEMENT ::= KLOP | KOŠ | SIMPLE_LAKE | POT |
| VARIABLE_DECLARATION | IF_STAVEK | VALIDATION
VARIABLE_DECLARATION ::= var ID := VALUE
ID ::= string
VALUE ::= EXPR_VALUE | POLYGON_VALUE | OBJECT_VALUE
EXPR_VALUE ::= EXPR | KOORDINATA
POLYGON_VALUE ::= polygon { POLYGON }
OBJECT_VALUE ::= DREVO | KLOP | KOŠ | SIMPLE_LAKE
DREVO ::= drevo { KOORDINATA }
KLOP ::= klop { KOORDINATA }
KOŠ ::= koš { KOORDINATA }
POT ::= pot { TIP_POTI }
TIP_POTI ::= LINE  | BENT_LINE
LINE ::= line ( KOORDINATA , KOORDINATA )
BENT_LINE ::= bent_line ( KOORDINATA , KOORDINATA , ANGLE )
SIMPLE_LAKE ::= ellip ( KOORDINATA , AXIS , AXIS )
POLYGON ::= KOORDINATA KOORDINATA KOORDINATA KOORDINATE_OPT
KOORDINATE_OPT ::= KOORDINATA KOORDINATE_OPT | ε
KOORDINATA ::= ( EXPR , EXPR ) | ID
ANGLE ::= EXPR
AXIS ::= EXPR
EXPR ::= ADDITIVE
ADDITIVE ::= MULTIPLICATIVE ADDITIVE'
ADDITIVE' ::= plus MULTIPLICATIVE ADDITIVE’ | minus MULTIPLICATIVE ADDITIVE’ | ε
MULTIPLICATIVE ::= UNARY MULTIPLICATIVE'
MULTIPLICATIVE' ::= times UNARY MULTIPLICATIVE’ | divide UNARY MULTIPLICATIVE’ | ε
UNARY ::= plus PRIMARY | minus PRIMARY | PRIMARY
PRIMARY ::= double | ID | ( EXPR )
IF_STAVEK ::= if COND { ELEMENTS } ELSE
ELSE ::= else { ELEMENTS } | ε
COND ::= EXPR COMP EXPR | > | < | >= | <= | == | !=
VALIDATION ::= validate (ID in ID)
```

---

## 2. Primeri uporabe

### 1. Drevo

```
drevo {(46.200020632918836, 15.267449746168296)}
```

![drevo](https://github.com/user-attachments/assets/96c37c97-485b-4d94-a23b-b4e7af1105ba)

### 2. Klop

```
klop {(46.200020632918836, 15.267449746168296)}
```

![klop](https://github.com/user-attachments/assets/9e828672-5699-4640-9e0e-465cc2fe6200)

### 3. Koš

```
kos {(46.56647175408932, 15.646374001950306)}
```

![kos](https://github.com/user-attachments/assets/afae5bf8-2bcf-4911-9464-1a0b366aa1c2)

### 4. Park

```
park mariborski_park {
    boundary: {
        (46.56398346013691, 15.644262632073346)
        (46.56404734413118, 15.648049475617459) (46.56314408259078, 15.648221319753688)
        (46.56310059180661, 15.649967885453473) (46.56556643032902, 15.649835250352192)
        (46.56626079159691, 15.64906986190545) (46.56743910917722, 15.648673403889358)
        (46.56944139560885, 15.648407858916862) (46.57097382823161, 15.647686973290618)
        (46.572202273337155, 15.645346760493103) (46.57068687417919, 15.643776483979083)

        (46.57037533732324, 15.644338667939735) (46.569931529890994, 15.645179284396903)
        (46.57009000885978, 15.645662459592671) (46.57002683305038, 15.64653449855507)
        (46.56970557620113, 15.6463585265667) (46.569450553867654, 15.646814490316615)
        (46.5690838956514, 15.646920518324947) (46.56862784936362, 15.646487173565077)
        (46.56769723252012, 15.647041216335367) (46.56774789479849, 15.64767680067672)
        (46.56714627719242, 15.647787337084083) (46.56690361593175, 15.64745553498733)

        (46.56683472567485, 15.64716657892145)
        (46.56672094532206, 15.64721700366885)
        (46.566647165746616,15.647178215401254)
        (46.566648054657776,15.646861444559107)
        (46.56651738456401, 15.64653562312159)
        (46.56650279073381, 15.64567497863171)
        (46.566373898121384, 15.645646533903317)
        (46.566373898121384, 15.64517848882221)
    }
}
```

![park](https://github.com/user-attachments/assets/2e448d67-983c-46f8-bfbf-509830274ffb)

### 5. Pot

```
pot {
    line((46.550556389310785, 15.628142156413618),(46.54996846984374, 15.629330850021875))

    bent_line((46.54996846984374, 15.629330850021875),(46.550084155937725, 15.629967981095746),-60)
    bent_line((46.550084155937725, 15.629967981095746),(46.55055635696904, 15.629968240079162), -60)

    line((46.55055635696904, 15.629968240079162),(46.5511164527123, 15.628789649670798))

    bent_line((46.5511164527123, 15.628789649670798),(46.55097731110094, 15.62821298477893), -60)
    bent_line((46.55097731110094, 15.62821298477893), (46.550556389310785, 15.628142156413618), -60)
}
```

![pot](https://github.com/user-attachments/assets/213ac39e-371b-470a-bdf5-461442001b52)

### 6. Ribnik

```
ellip((46.56361271051858, 15.64946200395437), 0.0003, 0.00025 )
```

![ellip](https://github.com/user-attachments/assets/90609e01-7a04-4587-9ca1-169d6effc2fa)

### 6. Mariborski park
![Mariborski park](https://github.com/user-attachments/assets/dab1d019-7827-4d95-b02c-ef54bf8f623b)





