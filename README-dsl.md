# Digitalni vrt - domensko specifični jezik

### 1.1 Namen projekta

Izdelava domensko specifičnega jezika za opis parkov in zelenih površin v mestih. Definicija konstruktov, ki opisujejo parke in elemente v njih, gramatika v BNF notaciji, ki predstavlja sintaktična pravila za zapis jezika ter leksikalni, sintaktični in semantični analizator na osnovi te gramatike, ki vrača predstavitev konstruktov jezika v GeoJSON formatu.

---

### 1.2 Ciljne skupine uporabnikov in njihove potrebe

#### Načrtovalci parkov in zelenih površin

- Domensko specifični jezik za opis parkov
- Gramatika v BNF notaciji
- Leksikalni analizator
- Sintaktični analizator
- Semantični analizator
- Prikaz parka z GeoJSON formatom

---

### 1.3 Funkcionalne zahteve

| Funkcionalnost         | Opis                                                                                   |
| ---------------------- | -------------------------------------------------------------------------------------- |
| Sintaktična pravila    | Gramatika v BNF notaciji.                                                              |
| Leksikalni analizator  | Pretvorba tekstovnega zapisa v tokene.                                                 |
| Sintaktični analizator | Sintaktična analiza in preverba validnosti zapisane strukture stavka.                  |
| Semantični analizator  | Semantična analiza, pomenska obdelava zapisanih tokenov in pretvorba v GeoJSON format. |
| Prikaz parka           | Formatiran zapis in vizualni prikaz parkov v GeoJSON formatu.                          |

