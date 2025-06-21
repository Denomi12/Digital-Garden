# Digitalni vrt - namizna aplikacija

### 1.1 Namen projekta

Namizna aplikacija **Digitalni vrt** je zgrajena z uporabo programskega jezika **Kotlin** in omogoča uporabnikom uporabo funkcionalnosti sistema neposredno iz njihovega operacijskega sistema, brez uporabe spletne aplikacije.
Aplikacija omogoča interaktivno povezovanje z **RESTful API-jem** spletne storitve, shranjevanje, urejanje in prikaz podatkov o vrtovih, rastlinah ter uporabniških profilih, ter generiranje podatkov s pomočjo umetne inteligence (OpenAI API).

---

### 1.2 Ciljne skupine uporabnikov in njihove potrebe

#### Gostitelji spletne aplikacije **Digitalni vrt**

- Nadzor in upravljanje podatkovne baze in podatkov preko namizne aplikacije.
- Pridobivanje podatkov s spleta.
- Generiranje podatkov.

---

### 1.3 Funkcionalne zahteve

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

### 1.4 Sistemske zahteve

- Dostopna spletna storitev