import { GardenElement, Tile } from "./Types/Elements";
import styles from "../../stylesheets/GardenCellDetails.module.css";
import { useEffect, useState } from "react";
import { isValidDate } from "../../utils/helpers";

interface GardenCellDetailsProps {
  cell: Tile | null;
}

export default function GardenCellDetails({ cell }: GardenCellDetailsProps) {
  const [plantedDate, setPlantedDate] = useState("");
  const [wateredDate, setWateredDate] = useState("");

  useEffect(() => {
    if (cell) {
      setPlantedDate(
        isValidDate(cell.plantedDate)
          ? new Date(cell.plantedDate).toISOString().split("T")[0]
          : ""
      );
      setWateredDate(
        isValidDate(cell.wateredDate)
          ? new Date(cell.wateredDate).toISOString().split("T")[0]
          : ""
      );
    }
  }, [cell]);

  if (!cell) return null;

  const handlePlantedChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = new Date(e.target.value);
    setPlantedDate(e.target.value);
    if (cell) cell.plantedDate = newDate;
  };

  const handleWateredChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = new Date(e.target.value);
    setWateredDate(e.target.value);
    if (cell) cell.wateredDate = newDate;
  };

  return (
    <div className={styles.Container}>
      <div className={styles.Title}>{cell.type || "No Element"}</div>

      {cell.crop && (
        <div>
          <div className={styles.CropNameContainer}>
            {cell.crop.imageSrc && (
              <img src={cell.crop.imageSrc} alt={cell.crop.name} />
            )}
            <div className={styles.CropName}>{cell.crop.name}</div>
          </div>
          <div>
            <em>({cell.crop.latinName})</em>
          </div>
        </div>
      )}
      {cell.type != GardenElement.Path && cell.type != GardenElement.None &&(
        <>
          <div className={styles.DateRow}>
            <label>🌱 Planted:</label>
            <input
              type="date"
              value={plantedDate}
              onChange={handlePlantedChange}
            />
          </div>

          <div className={styles.DateRow}>
            <label>🌧️ Watered:</label>
            <input
              type="date"
              value={wateredDate}
              onChange={handleWateredChange}
            />
          </div>
        </>
      )}
    </div>
  );
}
