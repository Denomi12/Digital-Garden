# Digitalni vrt

Digitalni vrt je spletna aplikacija za načrtovanje in spremljanje domačih vrtov. Uporabnikom omogoča ustvarjanje digitalnega dvojčka svojega vrta, vizualizacijo zasaditev, sledenje pridelkom ter komunikacijo s skupnostjo drugih vrtičkarjev.

---

## PROJEKTNE SPECIFIKACIJE

### 1. Namen projekta

Digitalni vrt je programska rešitev, ki uporabnikom omogoča:
- Načrtovanje in vizualizacijo zasaditev,
- Sledenje rasti in pridelavi vrtnin,
- Učenje o vrtnarskih praksah,
- Izmenjavo mnenj, nasvetov in izkušenj z drugimi uporabniki.

---

### 2. Ciljne skupine uporabnikov in njihove potrebe

#### Vrtičkarji začetniki
- Preprost vstop v svet vrtnarjenja (osnove, dobri/slabi sosedi, čas setve).
- Uporaba intuitivnega vmesnika za načrtovanje gredic.

#### Izkušeni vrtičkarji
- Vizualno spremljanje in natančno načrtovanje vrtov
- Komunikacija z drugimi uporabniki prek foruma.
- Vremenska napoved in statistika zasaditev.

#### Šole in organizacije
- Skupinsko upravljanje vrtov.
- Pregledno beleženje posajenih rastlin.

---

### 3. Opis rešitve

#### 3.1 Funkcionalne zahteve

| Funkcionalnost                           | Opis |
|-------------------------------------------|------|
| Ustvarjanje digitalnega vrta              | Uporabnik ustvari in poimenuje svoj vrt. |
| Geografsko lociranje vrta                 | Uporabnik na zemljevidu določi geografsko lokacijo vrta. |
| Vizualno načrtovanje zasaditev            | Povleci-in-spusti vmesnik za risanje gredic in razporejanje rastlin. |
| Podatki o rastlinah                       | Opis rastlin, čas setve, latinsko ime, dobri/slabi sosedi. |
| Vremenska integracija                     | Lokalna napoved za boljše načrtovanje aktivnosti. |
| Forum/skupnost                            | Uporabniki lahko objavljajo vprašanja in nasvete. |
| Seznam lokalnih trgovin                   | Prikaz bližnjih trgovin s semeni in orodjem glede na lokacijo. |
| Avtentikacija uporabnikov                 | Registracija, prijava, zaščiten dostop (JWT). |

#### 3.2 Sistemske zahteve

| Komponenta        | Tehnologija                        |
|------------------|------------------------------------|
| **Frontend**     | React, Vite, TypeScript            |
| **Backend**      | Node.js, Express, TypeScript       |
| **Baza podatkov**| MongoDB, MongoDB Atlas             |
| **Avtentikacija**| JWT (JSON Web Token)               |
| **API**          | OpenWeather API, Google Maps API   |
| **Kontajnerizacija** | Docker                          |


# Running the app

```bash
npm install
npm run dev
```

# Env file setups

add a `.env` file with structure from `.env.example`


# Docker setup

run ```docker-compose up --build``` to build and start the containers
```docker-compose up``` to start containers