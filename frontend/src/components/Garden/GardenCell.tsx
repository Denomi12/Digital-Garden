import { useEffect, useState } from "react";
import styles from "../../stylesheets/GardenCell.module.css";
import { GardenElement, Tile } from "./Types/Elements";
import GardenCellDetails from "./GardenCellDetails";

interface GardenCellProps {
  col: number;
  row: number;
  cell: Tile;
  handleCellClick: (row: number, col: number) => void;
  handleSelectCell: (row: number, col: number) => void;
  selectedCell: Tile | null;
}

export default function GardenCell({
  col: x,
  row: y,
  cell,
  handleCellClick,
  handleSelectCell,
  selectedCell,
}: GardenCellProps) {
  const showDetails =
    cell.type === GardenElement.GardenBed ||
    cell.type === GardenElement.RaisedBed;
  var isSelected: boolean = selectedCell == cell;

  return (
    <div
      className={`${styles.GardenCell} ${isSelected ? styles.isSelected : ""}`}
      onClick={() => handleCellClick(y, x)}
      style={{ backgroundColor: cell.color || "transparent" }}
      onContextMenu={(e) => {
        e.preventDefault(); // Prevent the default browser context menu
        isSelected = selectedCell == cell;
        handleSelectCell(y, x);
      }}
    >
      {/* Background Image (e.g., garden element) */}
      {cell.imageSrc && (
        <img src={cell.imageSrc} alt={cell.type} className={styles.CellImage} />
      )}

      {/* Crop overlay */}
      {cell.crop && (
        <div className={styles.CropOverlay}>
          {cell.crop.imageSrc ? (
            <img
              src={cell.crop.imageSrc}
              alt={cell.crop.name}
              className={styles.OverlayImage}
            />
          ) : (
            <div className={styles.CropInfo}>
              🌱
              <br />
              {cell.crop.name}
            </div>
          )}
        </div>
      )}

      {/* Hover effect: display extra component */}
      {/* {showDetails && <GardenCellDetails cell={cell}></GardenCellDetails>} */}
    </div>
  );
}
