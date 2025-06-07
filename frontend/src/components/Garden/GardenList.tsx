import { Garden } from "./Types/Garden";
import styles from "../../stylesheets/GardenList.module.css";
import { GardenElement } from "./Types/Elements";
import { useContext, useEffect } from "react";
import { UserContext } from "../../UserContext";
import axios from "axios";

type GardenListProps = {
  mapGarden: Garden | null;
  setGarden: (garden: Garden | null) => void;
  gardens: Garden[];
};

function GardenList({ mapGarden, setGarden, gardens }: GardenListProps) {
  const { user } = useContext(UserContext);

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

  const createGarden = () => {
    const widthInput = prompt("Enter the width of the garden:");
    const heightInput = prompt("Enter the height of the garden:");
    const nameInput = prompt("Enter garden name:");

    if (widthInput && heightInput) {
      const w = parseInt(widthInput, 10);
      const h = parseInt(heightInput, 10);

      if (!isNaN(w) && !isNaN(h) && nameInput) {
        const newGarden = new Garden(
          w,
          h,
          nameInput,
          null,
          undefined,
          undefined,
          undefined,
          user ? user : undefined
        );
        setGarden(newGarden);
      } else {
        alert("Please enter valid numbers for both width and height.");
      }
    } else {
      alert("Please enter both width and height.");
    }
  };

  useEffect(() => {
    if (mapGarden) {
      setSelectedGarden(mapGarden._id!!);
    }
  }, []);

  if (!gardens) return <div>Loading gardens...</div>;

  return (
    <div className={styles.listContainer}>
      <h2 className={styles.title}>My Gardens</h2>
      <div className={styles.gardensContainer}>
        {gardens.map((garden, index) => (
          <div
            key={index}
            className={styles.gardenCard}
            onClick={() => setSelectedGarden(garden._id!!)}
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
      <button onClick={createGarden} className={styles.CreateButton}>
        Create Garden
      </button>
    </div>
  );
}

export default GardenList;
