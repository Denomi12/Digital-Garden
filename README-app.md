# Digitalni vrt - namizna aplikacija

## 1.1 Namen projekta

Namizna aplikacija **Digitalni vrt** je zgrajena z uporabo programskega jezika **Kotlin** in omogoča uporabnikom uporabo funkcionalnosti sistema neposredno iz njihovega operacijskega sistema, brez uporabe spletne aplikacije.
Aplikacija omogoča interaktivno povezovanje z **RESTful API-jem** spletne storitve, shranjevanje, urejanje in prikaz podatkov o vrtovih, rastlinah ter uporabniških profilih, ter generiranje podatkov s pomočjo umetne inteligence (OpenAI API).

---

## 1.2 Ciljne skupine uporabnikov in njihove potrebe

### Gostitelji spletne aplikacije **Digitalni vrt**

- Nadzor in upravljanje podatkovne baze in podatkov preko namizne aplikacije.
- Pridobivanje podatkov s spleta.
- Generiranje podatkov.

---

## 1.3 Funkcionalne zahteve

| Funkcionalnost                          | Opis                                                                                |
| --------------------------------------- | ----------------------------------------------------------------------------------- |
| Upravljanje uporabnikov                 | Dodajanje, urejanje in brisanje uporabniških računov.                               |
| Upravljanje vrtov                       | Dodajanje, ogled, spreminjanje in brisanje vrtov.                                   |
| Upravljanje rastlin                     | Dodajanje novih rastlin ročno ali s pomočjo generatorja.                            |
| Scraper (strgalnik)                     | Samodejno izlušči podatke o rastlinah iz spletnih virov.                            |
| Generator podatkov (OpenAI)             | Uporabi umetno inteligenco za generacijo podatkov o rastlinah.                      |
| Vizualni vmesnik s tabularno navigacijo | Uporabnik preklaplja med zavihki: Uporabniki, Rastline, Vrtovi, Scraper, Generator. |
| Sinhronizacija s strežnikom             | Povezava z REST API strežnikom za pošiljanje in prejem podatkov.                    |

---

## 1.4 Sistemske zahteve

- Dostopna spletna storitev in ustrezno določene poti (API endpoints) v namizni aplikaciji.

---

## 2. Primeri uporabe

### 1. Dodajanje podatkov

V zavihkih ```Add person```, ```Add crop``` in ```Add garden``` lahko uporabnik v vnosna polja zapiše podatke in jih shrani v bazo.

![adding](https://github.com/user-attachments/assets/cf482b6e-d324-4971-94a3-233184856865)


### 2. Urejanje podatkov

V zavihkih ```People```, ```Crops``` in ```Gardens``` lahko uporabnik v vnosnih poljih ureja obstoječe podatke in spremembe shrani v bazo.

![editing](https://github.com/user-attachments/assets/edf9523b-7747-4397-bf72-7284cd89962a)

### 3. Strgalnik

V zavihku ```Scraper``` lahko uporabnik dostopa do podatkov o mesečnem sajenju ter dobrih in slabih sosedih z interneta.

![scraper](https://github.com/user-attachments/assets/e451b218-dfb8-4dc4-b136-620a6436863c)

### 4. Generator

V zaihku ```Generator``` lahko uporabnik z uporabo OpenAI APIja generira podatke o pridelkih in jih shrani v bazo.

![generator](https://github.com/user-attachments/assets/20c36ac5-ed46-4e8e-b3f3-0ea474c207d4)

