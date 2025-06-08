import styles from "../../stylesheets/GardenGrid.module.css";
import GardenCell from "./GardenCell";
import { Tile } from "./Types/Elements";
import { Garden } from "./Types/Garden";
interface GardenGridProps {
  garden: Garden;
  onCellClick: (row: number, col: number) => void;
  onCellSelect: (row: number, col: number) => void;
  onTopClick: () => void;
  onBottomClick: () => void;
  onLeftClick: () => void;
  onRightClick: () => void;
  selectedCell: Tile | null;
}

export default function GardenGrid({
  garden,
  onCellClick,
  onTopClick,
  onBottomClick,
  onLeftClick,
  onRightClick,
  onCellSelect,
  selectedCell,
}: GardenGridProps) {
  return (
    <div className={styles.GardenWrapper}>
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
      <div className={styles.GardenGrid}>
        <div className={styles.ElementsGrid}>
          {garden.elements.map((row, rowIndex) => (
            <div key={rowIndex} className={styles.GridRow}>
              {row.map((cell, colIndex) => (
                <span key={`${rowIndex}-${colIndex}`}>
                  <GardenCell
                    row={rowIndex}
                    col={colIndex}
                    cell={cell}
                    handleCellClick={() => onCellClick(rowIndex, colIndex)}
                    handleSelectCell={() => onCellSelect(rowIndex, colIndex)}
                    selectedCell={selectedCell}
                  />
                </span>
              ))}
            </div>
          ))}
        </div>

        {/* <div className={styles.GardenGridScrollArea}>
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
        </div> */}
      </div>
    </div>
  );
}
