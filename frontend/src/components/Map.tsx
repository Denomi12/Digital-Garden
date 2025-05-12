import React from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";

// Tip trgovine (pripravljen za kasnejšo uporabo)
interface Store {
  id: number;
  name: string;
  location: string;
  latitude: number;
  longitude: number;
}

//potem gre to v bazo pa beremo ven
const stores: Store[] = [
  {
    id: 1,
    name: "Kalia Dobrovnik",
    location: "Dobrovnik 255, 9223 Dobrovnik",
    latitude: 46.65249250534264,
    longitude: 16.347810586284975,
  },
  {
    id: 2,
    name: "Kalia Vrhnika",
    location: "Robova cesta 6, 1360 Vrhnika",
    latitude: 45.967650315929546,
    longitude: 14.297217197149026,
  },
  {
    id: 3,
    name: "Kalia Beltinci",
    location: "Gregorčičeva ulica 2, 9231 Beltinci",
    latitude: 46.60740280584615,
    longitude: 16.237570064713918,
  },
  {
    id: 4,
    name: "Kalia Celje",
    location: "Teharje 7, 3000 Celje",
    latitude: 46.232831446764806,
    longitude: 15.297988842327026,
  },
  {
    id: 5,
    name: "Kalia Kranj",
    location: "Bleiweisova cesta 29, 4000 Kranj",
    latitude: 46.25636482814234,
    longitude: 14.34938235483213,
  },
  {
    id: 6,
    name: "Kalia Lendava",
    location: "Kolodvorska ulica 42, 9220 Lendava",
    latitude: 46.553951144751856,
    longitude: 16.446597295982457,
  },
  {
    id: 7,
    name: "Kalia Ljubljana Rudnik",
    location: "Dolenjska cesta 242 A, 1000 Ljubljana",
    latitude: 46.02309512865861,
    longitude: 14.540108930200201,
  },
  {
    id: 8,
    name: "Kalia Murska Sobota Obrtna",
    location: "Obrtna ulica 2, 9000 Murska Sobota",
    latitude: 46.66795125688331,
    longitude: 16.17608758368522,
  },
  {
    id: 9,
    name: "Kalia Maribor",
    location: "Tržaška cesta 35, 2000 Maribor",
    latitude: 46.5304129650721,
    longitude: 15.646574012495869,
  },
  {
    id: 10,
    name: "Kalia Metlika",
    location: "Cesta 15. brigade 1, 8330 Metlika",
    latitude: 45.64708714588623,
    longitude: 15.314947392508403,
  },
  {
    id: 11,
    name: "Kalia Murska Sobota Tišinska",
    location: "Tišinska ulica 29e, 9000 Murska Sobota",
    latitude: 46.65173225675397,
    longitude: 16.14498755070838,
  },
  {
    id: 12,
    name: "Kalia Novo Mesto",
    location: "Velika Bučna vas 3b, 8000 Novo Mesto",
    latitude: 45.82232266032823,
    longitude: 15.1571851548136,
  },
  {
    id: 13,
    name: "Kalia Ptuj",
    location: "Špindlerjeva ulica 3, 2250 Ptuj",
    latitude: 46.42728173629497,
    longitude: 15.891418301137177,
  },
  {
    id: 14,
    name: "Kalia Šempeter",
    location: "Vrtojbenska cesta 79, 5290 Šempeter pri Gorici",
    latitude: 45.922027488264774,
    longitude: 13.640727863750264,
  },
  {
    id: 15,
    name: "Kalia Slovenska Bistrica",
    location: "Žolgerjeva ulica 4, 2310 Slovenska Bistrica",
    latitude: 46.38691237638533,
    longitude: 15.569766947104156,
  },
  {
    id: 16,
    name: "Kalia Slovenske Konjice",
    location: "Liptovska ulica 8, 3210 Slovenske Konjice",
    latitude: 46.341047643227654,
    longitude: 15.428212486120696,
  },
  {
    id: 17,
    name: "Kalia Črnomelj",
    location: "Majer 9, 8340 Črnomelj",
    latitude: 45.56975005020772,
    longitude: 15.196321696417924,
  },
];

function Map() {
  const centerPosition: [number, number] = [46.1512, 14.9955];

  return (
    <div style={{ height: "800px", width: "100%" }}>
      <MapContainer
        center={centerPosition}
        zoom={9}
        scrollWheelZoom={false}
        style={{ height: "100%", width: "100%" }}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {stores.map((store) => (
          <Marker key={store.id} position={[store.latitude, store.longitude]}>
            <Popup>
              <strong>{store.name}</strong>
              <br />
              {store.location}
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}

export default Map;
