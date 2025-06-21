# Digitalni vrt - spletna aplikacija

## 1.1 Namen projekta

Digitalni vrt je spletna aplikacija za načrtovanje in spremljanje domačih vrtov. Uporabnikom omogoča ustvarjanje digitalnega dvojčka svojega vrta, vizualizacijo zasaditev, sledenje rasti pridelkov ter komunikacijo s skupnostjo drugih vrtičkarjev z namenom izmenjevanja mnenj, nasvetov, izkušenj ter učenja dobrih vrtnarskih praks.

---

## 1.2 Ciljne skupine uporabnikov

### Vrtičkarji začetniki

- Preprost vstop v svet vrtnarjenja (osnove, dobri/slabi sosedi, čas setve).
- Uporaba intuitivnega vmesnika za načrtovanje gredic.

### Izkušeni vrtičkarji

- Vizualno spremljanje in natančno načrtovanje vrtov
- Komunikacija z drugimi uporabniki prek foruma.
- Vremenska napoved in statistika zasaditev.

### Šole in organizacije

- Skupinsko upravljanje vrtov.
- Pregledno beleženje posajenih rastlin.

---

## 1.3 Funkcionalne zahteve

| Funkcionalnost                 | Opis                                                                 |
| ------------------------------ | -------------------------------------------------------------------- |
| Ustvarjanje digitalnega vrta   | Uporabnik ustvari in poimenuje svoj vrt.                             |
| Geografsko lociranje vrta      | Uporabnik na zemljevidu določi geografsko lokacijo vrta.             |
| Vizualno načrtovanje zasaditev | Povleci-in-spusti vmesnik za risanje gredic in razporejanje rastlin. |
| Podatki o rastlinah            | Opis rastlin, čas setve, latinsko ime, dobri/slabi sosedi.           |
| Vremenska integracija          | Lokalna napoved za boljše načrtovanje aktivnosti.                    |
| Forum/skupnost                 | Uporabniki lahko objavljajo vprašanja in nasvete.                    |
| Seznam lokalnih trgovin        | Prikaz bližnjih trgovin s semeni in orodjem glede na lokacijo.       |
| Avtentikacija uporabnikov      | Registracija, prijava, zaščiten dostop (JWT).                        |

## 1.4 Sistemske zahteve

| Komponenta                | Tehnologija                      |
| ------------------------- | -------------------------------- |
| **Čelni del - Frontend**  | React, Vite, TypeScript          |
| **Zaledni del - Backend** | Node.js, Express, TypeScript     |
| **Baza podatkov**         | MongoDB, MongoDB Atlas           |
| **Avtentikacija**         | JWT (JSON Web Token)             |
<!-- | **API**                   | OpenWeather API, Google Maps API |  -->
| **Kontajnerizacija**      | Docker                           |

---

### Navodila za namestitev

#### Zahteve pred namestitvijo

- [Git](https://git-scm.com/)
- [Node.js](https://nodejs.org/)
- [Docker](https://www.docker.com/)
- **MongoDB Atlas** račun _(ali lokalna namestitev MongoDB)_

#### Namestitev aplikacije

##### 1. Kloniranje projekta

```bash
git clone https://github.com/Denomi12/Digital-Garden.git
cd Digital-Garden
```

##### 2. Nastavitve okolja

Kopiraj predloge `.env` datotek:

```bash
cp ./backend/.env.example ./backend/.env
cp ./frontend/.env.example ./frontend/.env
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

Uredi datoteko ./frontend/.env po naslednjem vzorcu:

    ```env
    VITE_API_BACKEND_URL=http://localhost:3001
    ```

##### 3. Namestitev odvisnosti

```bash
cd backend
npm install

cd ../frontend
npm install
```

##### 4. Zagon aplikacije

V dveh ločenih terminalih

```bash

# Terminal 1 - Backend
cd backend
npm run dev
```

```bash

# Terminal 2 - Frontend
cd frontend
npm run dev
```

Aplikacija dostopna na naslovu: `localhost:5173`

##### 5. Zagon aplikacije (Docker)

Zagon kontajnerizirane aplikacije z uporabo Docker:

```bash
docker-compose up
```

Aplikacija dostopna na naslovu: `localhost:5173`
