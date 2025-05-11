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
    latitude: 46.68,
    longitude: 16.37,
  },
  {
    id: 2,
    name: "Kalia Vrhnika",
    location: "Robova cesta 6, 1360 Vrhnika",
    latitude: 45.964,
    longitude: 14.293,
  },
  {
    id: 3,
    name: "Kalia Beltinci",
    location: "Gregorčičeva ulica 2, 9231 Beltinci",
    latitude: 46.607,
    longitude: 16.242,
  },
  {
    id: 4,
    name: "Kalia Celje",
    location: "Teharje 7, 3000 Celje",
    latitude: 46.238,
    longitude: 15.267,
  },
  {
    id: 5,
    name: "Kalia Kranj",
    location: "Bleiweisova cesta 29, 4000 Kranj",
    latitude: 46.245,
    longitude: 14.355,
  },
  {
    id: 6,
    name: "Kalia Lendava",
    location: "Kolodvorska ulica 42, 9220 Lendava",
    latitude: 46.5538,
    longitude: 16.4466,
  },
  {
    id: 7,
    name: "Kalia Ljubljana Rudnik",
    location: "Dolenjska cesta 242 A, 1000 Ljubljana",
    latitude: 46.019,
    longitude: 14.518,
  },
  {
    id: 8,
    name: "Kalia Murska Sobota Obrtna",
    location: "Obrtna ulica 2, 9000 Murska Sobota",
    latitude: 46.658,
    longitude: 16.16,
  },
  {
    id: 9,
    name: "Kalia Maribor",
    location: "Tržaška cesta 35, 2000 Maribor",
    latitude: 46.5485,
    longitude: 15.6459,
  },
  {
    id: 10,
    name: "Kalia Metlika",
    location: "Cesta 15. brigade 1, 8330 Metlika",
    latitude: 45.65,
    longitude: 15.316,
  },
  {
    id: 11,
    name: "Kalia Murska Sobota Tišinska",
    location: "Tišinska ulica 29e, 9000 Murska Sobota",
    latitude: 46.657,
    longitude: 16.16,
  },
  {
    id: 12,
    name: "Kalia Novo Mesto",
    location: "Velika Bučna vas 3b, 8000 Novo Mesto",
    latitude: 45.803,
    longitude: 15.168,
  },
  {
    id: 13,
    name: "Kalia Ptuj",
    location: "Špindlerjeva ulica 3, 2250 Ptuj",
    latitude: 46.419,
    longitude: 15.87,
  },
  {
    id: 14,
    name: "Kalia Šempeter",
    location: "Vrtojbenska cesta 79, 5290 Šempeter pri Gorici",
    latitude: 45.93,
    longitude: 13.64,
  },
  {
    id: 15,
    name: "Kalia Slovenska Bistrica",
    location: "Žolgerjeva ulica 4, 2310 Slovenska Bistrica",
    latitude: 46.393,
    longitude: 15.576,
  },
  {
    id: 16,
    name: "Kalia Slovenske Konjice",
    location: "Liptovska ulica 8, 3210 Slovenske Konjice",
    latitude: 46.336,
    longitude: 15.422,
  },
  {
    id: 17,
    name: "Kalia Črnomelj",
    location: "Majer 9, 8340 Črnomelj",
    latitude: 45.571,
    longitude: 15.195,
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
