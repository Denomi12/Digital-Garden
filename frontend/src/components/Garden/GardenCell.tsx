import styles from "../../stylesheets/GardenGrid.module.css";
import { Tile } from "./Types/Elements";

interface GardenCellProps {
  col: number;
  row: number;
  cell: Tile;
  handleCellClick: (row: number, col: number) => void;
}

export default function GardenCell({
  col: x,
  row: y,
  cell,
  handleCellClick,
}: GardenCellProps) {
  return (
    <div
      className={styles.GardenCell}
      onClick={() => handleCellClick(y, x)}
      style={{ backgroundColor: cell.color || "transparent" }}
    >
      {/* Background Image (e.g., garden element) */}
      {cell.imageSrc && (
        <img src={cell.imageSrc} alt={cell.type} className={styles.CellImage} />
      )}

      {/* Crop overlay */}
      {cell.crop && (
        <div className={styles.CropOverlay}>
          {cell.crop.imageSrc && (
            <img
              src={cell.crop.imageSrc}
              alt={cell.crop.name}
              className={styles.OverlayImage}
            />
          )}
          <div className={styles.CropInfo}>
            🌱 {cell.crop.name}
            {cell.plantedDate && (
              <div>
                Planted: {new Date(cell.plantedDate).toLocaleDateString()}
              </div>
            )}
            {cell.wateredDate && (
              <div>
                Watered: {new Date(cell.wateredDate).toLocaleDateString()}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
