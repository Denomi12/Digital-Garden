# Digitalni vrt - spletna aplikacija

![image](https://github.com/user-attachments/assets/4a1944ba-825a-46bc-9433-0cf03f5c95bb)

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


![image](https://github.com/user-attachments/assets/20e9fac3-f726-4c1b-9724-9f18bc1ea31a)

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

## 2. Navodila za namestitev

### Zahteve pred namestitvijo

- [Git](https://git-scm.com/)
- [Node.js](https://nodejs.org/)
- [Docker](https://www.docker.com/)
- **MongoDB Atlas** račun _(ali lokalna namestitev MongoDB)_


### 1. Kloniranje projekta

```bash
git clone https://github.com/Denomi12/Digital-Garden.git
cd Digital-Garden
```

### 2. Nastavitve okolja

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

### 3. Namestitev odvisnosti

```bash
cd backend
npm install

cd ../frontend
npm install
```

### 4. Zagon aplikacije

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

### 5. Zagon aplikacije (Docker)

Zagon kontajnerizirane aplikacije z uporabo Docker:

```bash
docker-compose up
```

![image](https://github.com/user-attachments/assets/0d624db8-59ac-4d1d-ba33-91f0e4d09d34)


Aplikacija dostopna na naslovu: `localhost:5173`

---
 
## 3. Primeri uporabe

### 1. Registracija uporabnika

S klikom na gumb ```Register``` lahko uporabnik ustvari nov profil.
![image](https://github.com/user-attachments/assets/6e3160f6-72c9-42af-8cd1-c8bc7591857c)
![registration](https://github.com/user-attachments/assets/30e29b85-a94e-47d1-8665-341769449021)

### 2. Kreiranje novega vrta

Uporabnik v zavihku ```Map``` klikne na gumb ```Create new garden```. Na mapi nato izbere s klikom na želeno lokacijo in klikom na gumb ```Create your garden``` ustvari nov vrt.

![garden creation](https://github.com/user-attachments/assets/b722db88-31e5-443f-837a-98eeb9400024)
![garden editing](https://github.com/user-attachments/assets/8023538d-6f9a-44ef-9ccc-224c5959669f)

### 3. Urejanje vrta

S pomočjo grafičnega vmesnika uporabnik ureja svoj vrt. Z zelenimi gumbi okoli mreže vrta določa njegovo velikost. V meniju na desni strani izbira vrtne gradnike (greda, visoka greda, potka), na levem meniju pa je seznam rastlin. Z desnim klikom na mrežo dobi prikaz podrobnejših podatkov o pridelku, kjer lahko tudi spreminja datum sajenja in zalivanja.

![garden editing](https://github.com/user-attachments/assets/9b088ddc-aada-4a23-94a9-30b68d966bbf)

### 4. Statistika pridelkov

Nad mrežo vrta se avtomatsko prikazuje statistika o posajenih vrtninah in razmerju elementov na vrtu.

![statistics](https://github.com/user-attachments/assets/0bbf79b2-2870-4793-9f94-2261d3044a2e)

### 5. Seznam vrtov

V zavihku ```Gardens``` ima uporabnik hiter dostop do urejanja svojih vrtov.

![gardens](https://github.com/user-attachments/assets/6ea496c1-a842-4fef-87ec-435ed3c7089a)


### 6. Zemljevid vrtov in trgovin

V zavihku ```Map``` ima uporabnik pregled nad svojimi vrtovi na zemljevidu ter prikaz trgovin z orodjem in vrtnarskimi pripomočki.

![map](https://github.com/user-attachments/assets/3ed3f3a8-4a4d-415f-a019-ad24388dc51c)


### 7. Forum 

V zavihku ```Forum``` lahko uporabnik bere vprašanja in komentarje drugih uporabnikov. Objave lahko ocenjuje in dodaja komentarje. Lahko tudi zastavi svoje vprašanje s klikom na ```Ask question```. V tem zavihku ima dostop tudi do klepetalnika z umetno inteligenco (bot), ki odgovarja na vprašanja o vrtnarjenju. Dobljen odgovor lahko tudi deli z drugimi uporabniki.

![image](https://github.com/user-attachments/assets/54ed03d1-806f-4003-ad40-e30e93ba685c)
![image](https://github.com/user-attachments/assets/238a94a5-bc9c-49a5-8e7f-fdf201919e9b)


