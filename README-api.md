# Digitalni vrt - spletna storitev

## 1.1 Namen projekta

Spletna storitev aplikacije Digitalni vrt je zgrajen z uporabo **Express.js** in nudi RESTful API, ki omogoča komunikacijo med čelnim delom (frontend), namizno aplikacijo (kotlin) in podatkovno bazo (MongoDB).

---

## 1.2 Ciljne skupine uporabnikov in njihove potrebe

### Gostitelji spletne aplikacije **Digitalni vrt**

- Spletna storitev ponuja končne dostopne toče za pridobivanje podatkov in izvajanje akcij preko spletne ali namizne aplikacije.

---

## 1.3 Funkcionalne zahteve

| Funkcionalnost | Opis                                                                    |
| -------------- | ----------------------------------------------------------------------- |
| CRUD           | Ustvarjanje, pridobivanje, urejanje in brisanje vnosov                  |
| Avtentikacija  | Registracija, prijava in preverjanje prijavljenjega uporabnika v sistem |
| JWT            | Preverjanje prijavljenega uporabnika s pomočjo JWT žetona               |
| OpenAI API     | Pošiljanje zahtevkov na OpenAI API                                      |

---

### **Končne točke spletnega API-ja** 

**Avtentikacija uporabnikov (`/user`)**



| Metoda | Pot            | Opis                                        | Avtentikacija | JSON Telo                                                                                |
| ------ | -------------- | ------------------------------------------- | ------------- | ---------------------------------------------------------------------------------------- |
| POST   | `/user/`       | Registracija novega uporabnika              | NE            | `{ "username": "ime", "email": "email@example.com", "password": "geslo" }`               |
| POST   | `/user/login`  | Prijava uporabnika in pridobitev JWT        | NE            | `{ "username": "ime", "password": "geslo" }`                                             |
| POST   | `/user/logout` | Odjava uporabnika                           | NE            |                                                                                          |
| GET    | `/user/verify` | Preveri veljavnost JWT žetona               | DA            |                                                                                          |
| GET    | `/user/me`     | Pridobi trenutnega prijavljenega uporabnika | DA            |                                                                                          |
| GET    | `/user/`       | Pridobi seznam vseh uporabnikov             | NE            |                                                                                          |
| GET    | `/user/:id`    | Pridobi podatke za določenega uporabnika    | NE            |                                                                                          |
| PUT    | `/user/:id`    | Posodobi podatke uporabnika                 | NE            | `{ "username": "novo_ime", "email": "nov_email@example.com", "password": "novo_geslo" }` |
| DELETE | `/user/:id`    | Izbriše uporabnika                          | NE            |                                                                                          |

---

**Vrtovi (`/garden`)**

| Metoda | Pot                        | Opis                              | Avtentikacija |
| ------ | -------------------------- | --------------------------------- | ------------- |
| GET    | `/garden/`                 | Pridobi seznam vseh vrtov         | NE            |
| GET    | `/garden/ownedBy/:ownerId` | Pridobi vse vrtove uporabnika     | NE            |
| GET    | `/garden/:id`              | Pridobi podatke o posameznem vrtu | DA            |
| POST   | `/garden/`                 | Ustvari nov vrt                   | DA            |
| PUT    | `/garden/:id`              | Posodobi obstoječi vrt            | DA            |

---

**Rastline (`/crop`)**

| Metoda | Pot          | Opis                           | Avtentikacija |
| ------ | ------------ | ------------------------------ | ------------- |
| GET    | `/crop/`     | Pridobi seznam vseh rastlin    | NE            |
| GET    | `/crop/:id`  | Pridobi podatke o eni rastlini | NE            |
| POST   | `/crop/`     | Doda novo rastlino             | NE            |
| POST   | `/crop/bulk` | Doda več rastlin naenkrat      | NE            |
| PUT    | `/crop/:id`  | Posodobi rastlino              | NE            |

---

**OpenAI API (`/generate`)**

| Metoda | Pot              | Opis                                | Avtentikacija |
| ------ | ---------------- | ----------------------------------- | ------------- |
| POST   | `/generate/chat` | Pošlje vprašanje AI pomočniku       | NE            |
| POST   | `/generate/`     | Generira predlog zasaditve (OpenAI) | NE            |

---

**Vprašanja (`/question`)**

| Metoda | Pot                     | Opis                                  | Avtentikacija |
| ------ | ----------------------- | ------------------------------------- | ------------- |
| GET    | `/question/`            | Pridobi seznam vseh vprašanj          | NE            |
| GET    | `/question/hotQuestion` | Pridobi vprašanja z največ interakcij | NE            |
| GET    | `/question/:id`         | Pridobi podrobnosti vprašanja         | NE            |
| POST   | `/question/`            | Objavi novo vprašanje                 | DA            |
| POST   | `/question/like/:id`    | Všečkaj vprašanje                     | DA            |
| POST   | `/question/dislike/:id` | Ne všečkaj vprašanja                  | DA            |

---

**Komentarji (`/comment`)**

| Metoda | Pot                    | Opis                                     | Avtentikacija |
| ------ | ---------------------- | ---------------------------------------- | ------------- |
| GET    | `/comment/:id`         | Pridobi komentarje za določeno vprašanje | NE            |
| POST   | `/comment/:id`         | Dodaj komentar k vprašanju               | DA            |
| POST   | `/comment/like/:id`    | Všečkaj komentar                         | DA            |
| POST   | `/comment/dislike/:id` | Ne všečkaj komentar                      | DA            |

---

**Trgovine (`/store`)**

| Metoda | Pot       | Opis                   | Avtentikacija |
| ------ | --------- | ---------------------- | ------------- |
| GET    | `/store/` | Pridobi seznam trgovin | NE            |
| POST   | `/store/` | Doda novo trgovino     | NE            |


## 1.4 Sistemske zahteve


### Zahteve pred namestitvijo

- [Git](https://git-scm.com/)
- [Node.js](https://nodejs.org/)
- **MongoDB Atlas** račun _(ali lokalna namestitev MongoDB)_

### Namestitev aplikacije

#### 1. Kloniranje projekta

```bash
git clone https://github.com/Denomi12/Digital-Garden.git
cd Digital-Garden
```

#### 2. Nastavitve okolja

Kopiraj predloge `.env` datotek:

```bash
cp ./backend/.env.example ./backend/.env
```

Uredi datoteko ./backend/.env po naslednjem vzorcu:

    ```env
    PORT=3001
    MONGO_URI=mongodb://localhost:27017/digital-garden-db
    JWT_SECRET=secret_token
    FRONTEND_URL=http://localhost:5173
    OPENAI_API_URL=https://api.openai.com/v1/chat/completions
    OPENAI_API_KEY=your_api_key_here
    ```

#### 3. Namestitev odvisnosti

```bash
cd backend
npm install
```

#### 4. Zagon aplikacije

```bash
cd backend
npm run dev
```

- Privzeti API base URL med razvojem je: `http://localhost:3001`
- Vsi endpointi sprejemajo zahtevke in vračajo odgovore v **JSON** formatu.

