import { Garden } from "./Types/Garden";
import styles from "../../stylesheets/GardenList.module.css";
import { GardenElement } from "./Types/Elements";
import { useContext, useEffect, useState } from "react";
import { UserContext } from "../../UserContext";
import axios from "axios";
import GardenCreateModal from "./GardenCreateModal";

type GardenListProps = {
  mapGarden: Garden | null;
  setGarden: (garden: Garden | null) => void;
  gardens: Garden[];
};

function GardenList({ mapGarden, setGarden, gardens }: GardenListProps) {
  const { user } = useContext(UserContext);
  const [modalOpen, setModalOpen] = useState(false);

  async function setSelectedGarden(gardenID: String) {
    try {
      const res = await axios.get(
        `${import.meta.env.VITE_API_BACKEND_URL}/garden/${gardenID}`,
        { withCredentials: true }
      );

      const gardenData: Garden = res.data;

      const selectedGarden = new Garden(
        gardenData.width,
        gardenData.height,
        gardenData.name,
        null,
        gardenData.location,
        gardenData.latitude,
        gardenData.longitude,
        gardenData.owner,
        gardenData._id
      );

      gardenData.elements.flat().forEach((tile) => {
        switch (tile.type) {
          case GardenElement.GardenBed:
            tile.imageSrc = `/assets/Greda.png`;
            break;
          case GardenElement.RaisedBed:
            tile.imageSrc = `/assets/VisokaGreda.png`;
            tile.color = "#D2B48C";
            break;
          case GardenElement.Path:
            tile.imageSrc = `/assets/Pot.png`;
            tile.color = "#F5DEB3";
            break;
          default:
            if (!tile.crop) {
              tile.color = undefined;
              tile.imageSrc = undefined;
            }
        }

        selectedGarden.elements[tile.y][tile.x] = tile;
      });

      setGarden(selectedGarden);
    } catch (error) {
      console.error("Error fetching gardens:", error);
    }
  }

  const handleCreateGarden = (data: {
    width: number;
    height: number;
    name: string;
    lat?: number;
    lon?: number;
  }) => {
    const newGarden = new Garden(
      data.width,
      data.height,
      data.name,
      null,
      undefined,
      data.lat,
      data.lon,
      user || undefined
    );
    setGarden(newGarden);
  };

  useEffect(() => {
    if (mapGarden) {
      if (mapGarden._id) {
        setSelectedGarden(mapGarden._id);
      } else {
        setModalOpen(true);
      }
    }
  }, []);

  if (!gardens) return <div>Loading gardens...</div>;

  return (
    <div className={styles.listContainer}>
      <GardenCreateModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onCreate={handleCreateGarden}
        lat={mapGarden?.latitude}
        lon={mapGarden?.longitude}
      />
      <h2 className={styles.title}>My Gardens</h2>
      <div className={styles.gardensContainer}>
        {gardens.map((garden, index) => (
          <div
            key={index}
            className={styles.gardenCard}
            onClick={() => setSelectedGarden(garden._id!!)}
          >
            <div className={styles.gardenName}><b>{garden.name}</b></div>
            {/* <div className={styles.gardenOwner}>
              Owner: {garden.owner?.username}
            </div> */}
            <div className={styles.gardenLocation}>
              <b>Location:</b> {garden.location}
            </div>
          </div>
        ))}
      </div>
      <button
        onClick={() => setModalOpen(true)}
        className={styles.CreateButton}
      >
        Create Garden
      </button>
    </div>
  );
}

export default GardenList;
