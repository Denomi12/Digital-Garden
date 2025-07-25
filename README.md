# Digitalni vrt
    
**Digitalni vrt** je večkomponentna platforma, ki omogoča digitalno načrtovanje, spremljanje in izmenjavo znanj o domačih vrtovih. Projekt vključuje spletno aplikacijo, namizno aplikacijo, spletno storitev (REST API) ter domensko specifični jezik za opis parkov.

---

## Projektne specifikacije

**Struktura repozitorija**
```
Digital-Garden/
├── backend/             # Express.js API
├── frontend/            # React aplikacija
├── KotlinApp/           # Kotlin namizna aplikacija
├── dsl/                 # Domensko specifični jezik
└── docker-compose.yml   # Docker konfiguracija
```

| Komponenta                        | Tehnologija                                               |
| --------------------------------- | --------------------------------------------------------- |
| **Frontend (spletna aplikacija)** | React, Vite, TypeScript                                   |
| **Backend (REST API)**            | Node.js, Express.js, TypeScript                           |
| **Baza podatkov**                 | MongoDB, MongoDB Atlas                                    |
| **Namizna aplikacija**            | Kotlin (desktop GUI)                                      |
| **Domensko specifični jezik**     | BNF gramatika, leksikalni/sintaktični analizator, GeoJSON |

---

### [Spletna aplikacija (React)](https://github.com/Denomi12/Digital-Garden/wiki/Spletna-aplikacija)

- Načrtovanje zasaditev z vizualnim vmesnikom
- Geolokacija vrta
- Dostop do vremenske napovedi
- Forum za vprašanja in nasvete
- Prikaz bližnjih trgovin s semeni in orodjem
- Avtentikacija z JWT


### [Spletna storitev (Express.js)](https://github.com/Denomi12/Digital-Garden/wiki/Spletna-storitev)

- Celovit CRUD sistem za uporabnike, vrtove, rastline, trgovine, vprašanja in komentarje
- JWT avtentikacija
- Endpoints za OpenAI API (generacija vsebine)
- JSON komunikacija

### [Namizna aplikacija (Kotlin)](https://github.com/Denomi12/Digital-Garden/wiki/Namizna-aplikacija)

- Upravljanje uporabnikov, rastlin in vrtov
- Strganje podatkov o rastlinah s spletnih virov (scraper)
- Generacija podatkov o rastlinah z OpenAI API
- Povezava z REST API
- Vizualna tabularna navigacija

### [Domensko specifični jezik](https://github.com/Denomi12/Digital-Garden/wiki/Domensko-specifi%C4%8Dni-jezik)

- DSL za opis parkov in zelenih površin
- Gramatika v BNF notaciji
- Leksikalni in sintaktični analizator
- Pretvorba v GeoJSON format za vizualizacijo parkov

---

## Ciljne skupine uporabnikov

- **Začetniki v vrtnarstvu**: učenje osnov, pomoč pri zasaditvah
- **Izkušeni vrtičkarji**: natančno načrtovanje in forum
- **Šole/organizacije**: skupinsko beleženje vrtov
- **Načrtovalci parkov**: uporaba DSL za načrtovanje parkov

---

## Namestitev

### Zahteve pred namestitvijo

- [Node.js](https://nodejs.org/)
- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
- MongoDB lokalno ali MongoDB Atlas račun

### Kloniranje projekta

```bash
git clone https://github.com/Denomi12/Digital-Garden.git
cd Digital-Garden
```
