import { Garden } from "./Types/Garden";
import styles from "../../stylesheets/GardenList.module.css";
import { GardenElement } from "./Types/Elements";
import { useContext } from "react";
import { UserContext } from "../../UserContext";

type GardenListProps = {
  setGarden: (garden: Garden | null) => void;
  gardens: Garden[];
};

function GardenList({ setGarden, gardens }: GardenListProps) {
  const { user } = useContext(UserContext);

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
      <button onClick={createGarden} className={styles.CreateButton}>
        Create Garden
      </button>
    </div>
  );
}

export default GardenList;
