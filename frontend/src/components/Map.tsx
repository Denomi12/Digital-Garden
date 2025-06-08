import React, { useContext, useEffect, useState, useRef } from "react";
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  useMapEvents,
  useMap,
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

const emptyGardenIcon = L.icon({
  iconUrl: "/assets/leaves.png",
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -32],
});

const gardenIcon = L.icon({
  iconUrl: "/assets/leavesDark.png",
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
  const [selectedGarden, setSelectedGarden] = useState<Garden | null>(null);

  const handleAddGarden = (lat: number, lng: number) => {
    setGardens((prev) => [...prev, { id: prev.length + 1, lat, lng }]);
    setAddGarden(false);
  };

  const handleNavigation = (garden: Garden | null) => {
    setSelectedGarden(garden);
  };

  function GardenNavigationHandler({ garden }: { garden: Garden | null }) {
    const map = useMap();
    const navigate = useNavigate();
    useEffect(() => {
      if (garden?.latitude && garden?.longitude) {
        const onMoveEnd = () => {
          map.off("moveend", onMoveEnd);
          navigate("/garden", { state: { garden } });
        };

        map.on("moveend", onMoveEnd);

        map.flyTo([garden.latitude, garden.longitude], 14, {
          duration: 1.5,
        });
      } else {
        navigate("/garden", { state: { garden } });
      }
    }, [garden]);

    return null;
  }

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
        <button
          className={styles.createButton}
          onClick={() => setAddGarden(true)}
        >
          Create new garden
        </button>
      )}

      <div className={className ?? styles.mapWrapper}>
        <MapContainer
          center={centerPosition}
          zoom={9} // start zoomed in reasonably close
          minZoom={9} // zoom out only until whole Slovenia fits
          maxZoom={18}
          maxBounds={[
            [45.4, 13.35],
            [46.9, 16.6],
          ]}
          maxBoundsViscosity={1.0}
          className={styles.map}
          attributionControl={false}
        >
          <TileLayer
            attribution='&copy; <a href="https://carto.com/">CartoDB</a>'
            url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
          />

          {selectedGarden && (
            <GardenNavigationHandler garden={selectedGarden} />
          )}

          {stores.map((store) => (
            <Marker
              key={store.key}
              position={[store.latitude, store.longitude]}
              icon={customIcon}
            >
              <Popup>
                <div className={styles.popupContent}>
                  <h3>{store.name}</h3>
                  <p>{store.location}</p>
                </div>
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
              icon={emptyGardenIcon}
            >
              <Popup>
                <div className={styles.popupContainer}>
                  <div className={styles.popupTitle}>🌱New Garden</div>
                  <div className={styles.popupCoords}>
                    📍 <strong>Lat:</strong> {garden.lat.toFixed(5)}
                    <br />
                    📍 <strong>Lon:</strong> {garden.lng.toFixed(5)}
                  </div>
                  <button
                    onClick={() =>
                      handleNavigation(
                        new Garden(
                          0,
                          0,
                          "",
                          undefined,
                          undefined,
                          garden.lat,
                          garden.lng
                        )
                      )
                    }
                    className={styles.createGardenButton}
                  >
                    Create Your Garden
                  </button>
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
                  <div className={styles.popupContainer}>
                    <div className={styles.popupTitle}>
                      🌱 <strong>{garden.name}</strong>
                      <div id={garden._id}></div>
                    </div>
                    <div className={styles.popupCoords}>
                      📍 <strong>Lat:</strong> {garden.latitude.toFixed(5)}
                      <br />
                      📍 <strong>Lon:</strong> {garden.longitude.toFixed(5)}
                    </div>
                    <button
                      onClick={() => handleNavigation(garden)}
                      className={styles.editGardenButton}
                    >
                      ✏️ Edit Your Garden
                    </button>
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
