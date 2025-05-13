import { useState } from "react";
import GardenMenu from "./GardenMenu";
import styles from "../../stylesheets/Garden.module.css";
import { Garden, GardenElement } from "./types";

function GardenComponent() {
  const [garden, setGarden] = useState<Garden | null>(null);
  const [selectedElement, setSelectedElement] = useState<GardenElement>(
    GardenElement.None
  );

  const createGarden = () => {
    const widthInput = prompt("Enter the width of the garden:");
    const heightInput = prompt("Enter the height of the garden:");

    if (widthInput && heightInput) {
      const w = parseInt(widthInput, 10);
      const h = parseInt(heightInput, 10);

      if (!isNaN(w) && !isNaN(h)) {
        const newGarden = new Garden(w, h);
        setGarden(newGarden);
      } else {
        alert("Please enter valid numbers for both width and height.");
      }
    } else {
      alert("Please enter both width and height.");
    }
  };

  const handleCellClick = (row: number, col: number) => {
    if (!garden || !selectedElement) return;

    garden.setElement(row, col, selectedElement);
    setGarden(new Garden(garden.width, garden.height, garden.grid));
  };

  const handleTopClick = () => {
    if (!garden) return;
    garden.addRowTop();
    setGarden(new Garden(garden.width, garden.height, garden.grid));
  };

  const handleBottomClick = () => {
    if (!garden) return;
    garden.addRowBottom();
    setGarden(new Garden(garden.width, garden.height, garden.grid));
  };

  const handleLeftClick = () => {
    if (!garden) return;
    garden.addColumnLeft();
    setGarden(new Garden(garden.width, garden.height, garden.grid));
  };

  const handleRightClick = () => {
    if (!garden) return;
    garden.addColumnRight();
    setGarden(new Garden(garden.width, garden.height, garden.grid));
  };

  return (
    <>
      <div>
        <button onClick={createGarden}>Create Garden</button>
      </div>

      <div className={styles.MainDisplay}>
        {garden && (
          <div className={styles.GardenWrapper}>
            <button
              className={`${styles.PlusButton} ${styles.PlusTop}`}
              onClick={() => handleTopClick()}
            >
              +
            </button>
            <button
              className={`${styles.PlusButton} ${styles.PlusBottom}`}
              onClick={() => handleBottomClick()}
            >
              +
            </button>
            <button
              className={`${styles.PlusButton} ${styles.PlusLeft}`}
              onClick={() => handleLeftClick()}
            >
              +
            </button>
            <button
              className={`${styles.PlusButton} ${styles.PlusRight}`}
              onClick={() => handleRightClick()}
            >
              +
            </button>

            <div className={styles.GardenColumn}>
              {garden.grid.map((row, rowIndex) => (
                <div key={rowIndex} className={styles.GardenRow}>
                  {row.map((cell, colIndex) => (
                    <div
                      key={`${rowIndex}-${colIndex}`}
                      className={styles.GardenCell}
                      style={{ backgroundColor: cell.color }}
                      onClick={() => handleCellClick(rowIndex, colIndex)}
                    >
                      {`${cell.row}-${cell.col}`}
                    </div>
                  ))}
                </div>
              ))}
            </div>
          </div>
        )}
        {selectedElement}
        {garden && (
          <GardenMenu setSelectedElement={setSelectedElement} />
        )}
      </div>
    </>
  );
}

export default GardenComponent;
