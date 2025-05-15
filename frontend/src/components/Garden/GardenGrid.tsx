import styles from "../../stylesheets/Garden.module.css";
import { Garden } from "./types";

interface GardenGridProps {
  garden: Garden;
  onCellClick: (row: number, col: number) => void;
  onTopClick: () => void;
  onBottomClick: () => void;
  onLeftClick: () => void;
  onRightClick: () => void;
}

export default function GardenGrid({
  garden,
  onCellClick,
  onTopClick,
  onBottomClick,
  onLeftClick,
  onRightClick,
}: GardenGridProps) {
  return (
    <div className={styles.GardenWrapper}>
      <div className={styles.GardenGrid}>
        <button
            className={`${styles.PlusButton} ${styles.PlusTop}`}
            onClick={onTopClick}
          >
            +
          </button>
          <button
            className={`${styles.PlusButton} ${styles.PlusBottom}`}
            onClick={onBottomClick}
          >
            +
          </button>
          <button
            className={`${styles.PlusButton} ${styles.PlusLeft}`}
            onClick={onLeftClick}
          >
            +
          </button>
          <button
            className={`${styles.PlusButton} ${styles.PlusRight}`}
            onClick={onRightClick}
          >
            +
          </button>
        <div className={styles.GardenGridScrollArea}>
          <div className={styles.GardenColumn}>
            {garden.grid.map((row, rowIndex) => (
              <div key={rowIndex} className={styles.GardenRow}>
                {row.map((cell, colIndex) => (
                  <div
                    key={`${rowIndex}-${colIndex}`}
                    className={styles.GardenCell}
                    style={{ backgroundColor: cell.color }}
                    onClick={() => onCellClick(rowIndex, colIndex)}
                  >
                    {`${cell.y}-${cell.x}`}
                  </div>
                ))}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
