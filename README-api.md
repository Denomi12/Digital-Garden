# Digitalni vrt - spletna storitev

### 1.1 Namen projekta

Spletni strežnik aplikacije Digitalni vrt je zgrajen z uporabo **Express.js** in nudi RESTful API, ki omogoča komunikacijo med čelnim delom (frontend), namizno aplikacijo (kotlin) in podatkovno bazo (MongoDB).

---

### 1.2 Ciljne skupine uporabnikov in njihove potrebe

#### Gostitelji spletne aplikacije **Digitalni vrt**

- Spletna storitev ponuja končne dostopne toče za pridobivanje podatkov in izvajanje akcij preko spletne ali namizne aplikacije.

### 1.3 Funkcionalne zahteve

| Funkcionalnost | Opis                                                                    |
| -------------- | ----------------------------------------------------------------------- |
| CRUD           | Ustvarjanje, pridobivanje, urejanje in brisanje vnosov                  |
| Avtentikacija  | Registracija, prijava in preverjanje prijavljenjega uporabnika v sistem |
| JWT            | Preverjanje prijavljenega uporabnika s pomočjo JWT žetona               |
| OpenAI API     | Pošiljanje zahtevkov na OpenAI API                                      |

---

### Avtentikacija uporabnikov (`/user`)

| Metoda | Pot            | Opis                                        | Avtentikacija |
| ------ | -------------- | ------------------------------------------- | ------------- |
| POST   | `/user/`       | Registracija novega uporabnika              | NE            |
| POST   | `/user/login`  | Prijava uporabnika in pridobitev JWT        | NE            |
| POST   | `/user/logout` | Odjava uporabnika                           | NE            |
| GET    | `/user/verify` | Preveri veljavnost JWT žetona               | DA            |
| GET    | `/user/me`     | Pridobi trenutnega prijavljenega uporabnika | DA            |
| GET    | `/user/`       | Pridobi seznam vseh uporabnikov             | NE            |
| GET    | `/user/:id`    | Pridobi podatke za določenega uporabnika    | NE            |
| PUT    | `/user/:id`    | Posodobi podatke uporabnika                 | NE            |
| DELETE | `/user/:id`    | Izbriše uporabnika                          | NE            |

---

### Vrtovi (`/garden`)

| Metoda | Pot                        | Opis                              | Avtentikacija |
| ------ | -------------------------- | --------------------------------- | ------------- |
| GET    | `/garden/`                 | Pridobi seznam vseh vrtov         | NE            |
| GET    | `/garden/ownedBy/:ownerId` | Pridobi vse vrtove uporabnika     | NE            |
| GET    | `/garden/:id`              | Pridobi podatke o posameznem vrtu | DA            |
| POST   | `/garden/`                 | Ustvari nov vrt                   | DA            |
| PUT    | `/garden/:id`              | Posodobi obstoječi vrt            | DA            |

---

### Rastline (`/crop`)

| Metoda | Pot          | Opis                           | Avtentikacija |
| ------ | ------------ | ------------------------------ | ------------- |
| GET    | `/crop/`     | Pridobi seznam vseh rastlin    | NE            |
| GET    | `/crop/:id`  | Pridobi podatke o eni rastlini | NE            |
| POST   | `/crop/`     | Doda novo rastlino             | NE            |
| POST   | `/crop/bulk` | Doda več rastlin naenkrat      | NE            |
| PUT    | `/crop/:id`  | Posodobi rastlino              | NE            |

---

### OpenAI API (`/generate`)

| Metoda | Pot              | Opis                                | Avtentikacija |
| ------ | ---------------- | ----------------------------------- | ------------- |
| POST   | `/generate/chat` | Pošlje vprašanje AI pomočniku       | NE            |
| POST   | `/generate/`     | Generira predlog zasaditve (OpenAI) | NE            |

---

### Vprašanja (`/question`)

| Metoda | Pot                     | Opis                                  | Avtentikacija |
| ------ | ----------------------- | ------------------------------------- | ------------- |
| GET    | `/question/`            | Pridobi seznam vseh vprašanj          | NE            |
| GET    | `/question/hotQuestion` | Pridobi vprašanja z največ interakcij | NE            |
| GET    | `/question/:id`         | Pridobi podrobnosti vprašanja         | NE            |
| POST   | `/question/`            | Objavi novo vprašanje                 | DA            |
| POST   | `/question/like/:id`    | Všečkaj vprašanje                     | DA            |
| POST   | `/question/dislike/:id` | Ne všečkaj vprašanja                  | DA            |

---

### Komentarji (`/comment`)

| Metoda | Pot                    | Opis                                     | Avtentikacija |
| ------ | ---------------------- | ---------------------------------------- | ------------- |
| GET    | `/comment/:id`         | Pridobi komentarje za določeno vprašanje | NE            |
| POST   | `/comment/:id`         | Dodaj komentar k vprašanju               | DA            |
| POST   | `/comment/like/:id`    | Všečkaj komentar                         | DA            |
| POST   | `/comment/dislike/:id` | Ne všečkaj komentar                      | DA            |

---

### Trgovine (`/store`)

| Metoda | Pot       | Opis                   | Avtentikacija |
| ------ | --------- | ---------------------- | ------------- |
| GET    | `/store/` | Pridobi seznam trgovin | NE            |
| POST   | `/store/` | Doda novo trgovino     | NE            |


### 1.4 Sistemske zahteve

- Vsi endpointi sprejemajo zahtevke in vračajo odgovore v **JSON** formatu.
- Privzeti API base URL med razvojem je: `http://localhost:3001`

---
