import axios from "axios";
import { useEffect, useState } from "react";
import { Crop } from "./Types/Elements";
import styles from "../../stylesheets/ShowCrops.module.css";

type ShowCropsProps = {
  selectedCrop: Crop | null;
  setSelectedCrop: (crop: Crop | null) => void;
};

function ShowCrops({ selectedCrop, setSelectedCrop }: ShowCropsProps) {
  const [crops, setCrops] = useState<Crop[] | null>(null);

  async function fetchCrops() {
    try {
      const res = await axios.get(
        `${import.meta.env.VITE_API_BACKEND_URL}/crop`,
        { withCredentials: true }
      );
      setCrops(res.data);
    } catch (error) {
      console.error("Error fetching crops:", error);
    }
  }

  useEffect(() => {
    fetchCrops();
  }, []);

  if (!crops) return <div>Loading crops...</div>;

  return (
    <div className={styles.CropsPannel}>
      <h2>Crops</h2>
      <ul className={styles.CropsList}>
        {crops.map((crop, index) => (
          <li key={index} className={styles.CropItem}>
            {crop.imageSrc && <img src={crop.imageSrc} alt={crop.name} />}
            <strong>{crop.name}</strong> (<em>{crop.latinName}</em>)<br />
            <strong>Planting Month:</strong> {crop.plantingMonth}
            <br />
            <strong>Watering:</strong> {crop.watering.frequency},{" "}
            {crop.watering.amount} L<br />
            <strong>Good Companions:</strong>{" "}
            {crop.goodCompanions.map((c) => c.name).join(", ") || "None"}
            <br />
            <strong>Bad Companions:</strong>{" "}
            {crop.badCompanions.map((c) => c.name).join(", ") || "None"}
            <br />
            <button
              className={styles.SelectButton}
              onClick={() =>
                setSelectedCrop(selectedCrop !== crop ? crop : null)
              }
            >
              {selectedCrop === crop ? "Deselect" : "Select"}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default ShowCrops;
