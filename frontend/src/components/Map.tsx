import React, { useState } from "react";
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  useMapEvents,
} from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import { useNavigate } from "react-router-dom";
import axios from "axios";

// Tip trgovine (pripravljen za kasnejšo uporabo)
interface Store {
  key: number;
  name: string;
  location: string;
  latitude: number;
  longitude: number;
}

const customIcon = L.icon({
  iconUrl: "/assets/store.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

const gardenIcon = L.icon({
  iconUrl: "/assets/leaves.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

function MapClickHandler({
  onClick,
}: {
  onClick: (lat: number, lng: number) => void;
}) {
  useMapEvents({
    click(e) {
      onClick(e.latlng.lat, e.latlng.lng);
    },
  });

  return null;
}

// znotraj komponente Map
function Map() {
  const navigate = useNavigate();
  const centerPosition: [number, number] = [46.1512, 14.9955];
  const [stores, setStores] = useState<Store[]>([]) 
  const [addGarden, setAddGarden] = useState(false);
  const [gardens, setGardens] = useState<
    { id: number; lat: number; lng: number }[]
  >([]);

  const handleAddGarden = (lat: number, lng: number) => {
    setGardens((prev) => [...prev, { id: prev.length + 1, lat, lng }]);
    setAddGarden(false);
  };

  const handleNavigation = (lat: number, lng: number) => {
    console.log("Navigacija do:", lat, lng);
    navigate("/garden", { state: { lat, lng } });
  };

  async function fetchStores() {
    try {
      const res = await axios.get(
        `${import.meta.env.VITE_API_BACKEND_URL}/store`
      );
      setStores(res.data);
    } catch (error) {
      console.error("Error fetching crops:", error);
    }
  }

  fetchStores();
  return (
    <>
      <button onClick={() => setAddGarden(true)}>Create new garden</button>
      <div style={{ height: "800px", width: "100%" }}>
        <MapContainer
          center={centerPosition}
          zoom={9}
          style={{ height: "100%", width: "100%" }}
        >
          <TileLayer
            attribution='&copy; <a href="https://carto.com/">CartoDB</a>'
            url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
          />

          {stores.map((store) => (
            <Marker
              key={store.key}
              position={[store.latitude, store.longitude]}
              icon={customIcon}
            >
              <Popup>
                <strong>{store.name}</strong>
                <br />
                {store.location}
              </Popup>
            </Marker>
          ))}

          {addGarden && (
            <MapClickHandler
              onClick={(lat, lng) => handleAddGarden(lat, lng)}
            />
          )}

          {gardens.map((garden) => (
            <Marker
              key={`garden-${garden.id}`}
              position={[garden.lat, garden.lng]}
              icon={gardenIcon}
            >
              <Popup>
                <strong>Moj vrt #{garden.id}</strong>
                <br />
                Lat: {garden.lat.toFixed(5)}, Lng: {garden.lng.toFixed(5)}
                <div
                  onClick={() => handleNavigation(garden.lat, garden.lng)}
                  style={{ cursor: "pointer", color: "blue", marginTop: "5px" }}
                >
                  Create your garden
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </>
  );
}

export default Map;
