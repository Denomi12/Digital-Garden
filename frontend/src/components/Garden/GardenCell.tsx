import React from "react";
import styles from "../../stylesheets/GardenGrid.module.css";
import { Tile } from "./Types/elements";

interface GardenCellProps {
  col: number;
  row: number;
  cell: Tile;
  handleCellClik: (row: number, col: number) => void;
}

export default function GardenCell({
  col: x,
  row: y,
  cell,
  handleCellClik,
}: GardenCellProps) {
  return (
    <div className={styles.GardenCell} onClick={() => handleCellClik(y, x)}>
      {cell.imageSrc && (
        <img src={cell.imageSrc} alt={cell.type} className={styles.CellImage} />
      )}

      {/* Overlay crop image (if any) */}
      {cell.crop?.imageSrc && (
        <img
          src={cell.crop.imageSrc}
          alt={cell.crop.name}
          className={styles.OverlayImage}
        />
      )}

      {cell.crop && (
        <div>
          <div>🌱 {cell.crop.name}</div>
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
      )}
    </div>
  );
}
