import React, { useContext, useEffect, useState } from "react";
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
import { Garden } from "./Garden/Types/Garden";
import { UserContext } from "../UserContext";
import styles from "../stylesheets/Map.module.css";

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

function Map({
  className,
  showCreateButton = true,
}: {
  className?: string;
  showCreateButton?: boolean;
}) {
  const navigate = useNavigate();
  const centerPosition: [number, number] = [46.1512, 14.9955];

  const [stores, setStores] = useState<Store[]>([]);
  const [addGarden, setAddGarden] = useState(false);
  const [gardens, setGardens] = useState<
    { id: number; lat: number; lng: number }[]
  >([]);
  const [userGardens, setUserGardens] = useState<Garden[]>([]);
  const { user } = useContext(UserContext);

  const handleAddGarden = (lat: number, lng: number) => {
    setGardens((prev) => [...prev, { id: prev.length + 1, lat, lng }]);
    setAddGarden(false);
  };

  const handleNavigation = (garden: Garden | null) => {
    navigate("/garden", { state: { garden } });
  };

  async function fetchStores() {
    try {
      const res = await axios.get(
        `${import.meta.env.VITE_API_BACKEND_URL}/store`,
        { withCredentials: true }
      );
      setStores(res.data);
    } catch (error) {
      console.error("Error fetching stores:", error);
    }
  }

  async function fetchUserGardens() {
    try {
      const res = await axios.get(
        `${import.meta.env.VITE_API_BACKEND_URL}/garden/ownedBy/${user?.id}`,
        { withCredentials: true }
      );
      setUserGardens(res.data);
    } catch (error) {
      console.error("Error fetching user gardens:", error);
    }
  }

  useEffect(() => {
    fetchStores();
    fetchUserGardens();
  }, []);

  return (
    <>
      {showCreateButton && (
        <button onClick={() => setAddGarden(true)}>Create new garden</button>
      )}

      <div className={className ?? styles.mapWrapper}>
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
                  onClick={() => handleNavigation(null)}
                  style={{ cursor: "pointer", color: "blue", marginTop: "5px" }}
                >
                  Create your garden
                </div>
              </Popup>
            </Marker>
          ))}

          {userGardens.map((garden) => {
            if (!garden.latitude || !garden.longitude) return null;

            return (
              <Marker
                key={`garden-${garden._id}`}
                position={[garden.latitude, garden.longitude]}
                icon={gardenIcon}
              >
                <Popup>
                  <strong>
                    Moj vrt <div id={garden._id}></div>
                  </strong>
                  <br />
                  Lat: {garden.latitude.toFixed(5)}, Lng:{" "}
                  {garden.longitude.toFixed(5)}
                  <div
                    onClick={() => handleNavigation(garden)}
                    style={{
                      cursor: "pointer",
                      color: "blue",
                      marginTop: "5px",
                    }}
                  >
                    Create your garden
                  </div>
                </Popup>
              </Marker>
            );
          })}
        </MapContainer>
      </div>
    </>
  );
}

export default Map;
