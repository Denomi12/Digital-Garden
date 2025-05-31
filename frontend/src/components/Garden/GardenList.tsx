import axios from "axios";
import { useContext, useEffect, useState } from "react";
import { UserContext } from "../../UserContext";
import { Garden } from "./Types/Garden";
import styles from "../../stylesheets/GardenList.module.css";
import { GardenElement } from "./Types/Elements";

type GardenListProps = {
  setGarden: (garden: Garden | null) => void;
};

function GardenList({ setGarden }: GardenListProps) {
  const [gardens, setGardens] = useState<Garden[]>([]);
  const { user } = useContext(UserContext);

  async function fetchUserGardens() {
    try {
      const res = await axios.get(
        `${import.meta.env.VITE_API_BACKEND_URL}/garden/ownedBy/${user?.id}`
      );
      setGardens(res.data);
    } catch (error) {
      console.error("Error fetching crops:", error);
    }
  }

  useEffect(() => {
    if (user?.id) fetchUserGardens();
  }, []);

  function setSelectedGarden(selectedGarden: Garden) {
    console.log(selectedGarden);
    const newGarden = new Garden(
      selectedGarden.width,
      selectedGarden.height,
      selectedGarden.name,
      null,
      selectedGarden.location,
      selectedGarden.latitude,
      selectedGarden.longitude,
      selectedGarden.owner,
      selectedGarden._id
    );

    console.log("Before: ", newGarden);

    selectedGarden.elements.flat().forEach((tile) => {
      switch (tile.type) {
        case GardenElement.GardenBed:
          tile.imageSrc = `/assets/Greda.png`;
          break;
        case GardenElement.RaisedBed:
          tile.color = "#D2B48C";
          break;
        case GardenElement.Path:
          tile.color = "#F5DEB3";
          break;
        default:
          if (!tile.crop) {
            tile.color = undefined;
            tile.imageSrc = undefined;
          }
      }

      newGarden.elements[tile.y][tile.x] = tile;
    });

    console.log("After: ", newGarden);

    setGarden(newGarden);
  }

  if (!gardens) return <div>Loading gardens...</div>;

  return (
    <div className={styles.listContainer}>
      <h2 className={styles.title}>My Gardens</h2>
      <div className={styles.gardensContainer}>
        {gardens.map((garden, index) => (
          <div
            key={index}
            className={styles.gardenCard}
            onClick={() => setSelectedGarden(garden)}
          >
            <div className={styles.gardenName}>{garden.name}</div>
            <div className={styles.gardenOwner}>
              Owner: {garden.owner?.username}
            </div>
            <div className={styles.gardenLocation}>
              Location: {garden.location}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default GardenList;
