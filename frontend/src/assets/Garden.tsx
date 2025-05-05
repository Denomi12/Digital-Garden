import { useState } from "react";
import GardenMenu from "./GardenMenu";
import styles from "./Garden.module.css";

function Garden() {
  const [width, setWidth] = useState<number>(0);
  const [height, setHeight] = useState<number>(0);
  const [garden, setGarden] = useState<any[][]>([]);
  const [selectedTool, setSelectedTool] = useState<string>("");

  const createGarden = () => {
    const widthInput = prompt("Enter the width of the garden:");
    const heightInput = prompt("Enter the height of the garden:");

    if (widthInput && heightInput) {
      const w = parseInt(widthInput, 10);
      const h = parseInt(heightInput, 10);

      if (!isNaN(w) && !isNaN(h)) {
        setWidth(w);
        setHeight(h);

        // Generiramo mrezo, kjer vsaka celica vsebuje svojo koordinato
        const generatedGarden = Array.from(
          { length: h }, // stevilo vrstic = visina vrta
          (_, row) =>
            Array.from({ length: w }, (_, col) => ({
              //sirina vrta
              label: `Cell-${row}-${col}`,
              // vsak element dobi svojo koordinato v obliki "Cell-{row}-{col}"
            }))
        );
        setGarden(generatedGarden);
      } else {
        alert("Please enter valid numbers for both width and height.");
      }
    } else {
      alert("Please enter both width and height.");
    }
  };

  const handleCellClick = (row: number, col: number) => {
    if (!selectedTool) return;

    const updatedGarden = garden.map((row) => row.map((cell) => ({ ...cell }))); //kopija stare tabele, da ne spremenimo starih podatkov
    updatedGarden[row][col].label = selectedTool; //posodobitev nove celice vrta

    if (selectedTool === "Greda") {
      updatedGarden[row][col].color = "#8B4513"; // Rjava za "Greda"
    } else if (selectedTool === "Visoka greda") {
      updatedGarden[row][col].color = "#D2B48C"; // Svetlo rjava za "Visoka greda"
    } else if (selectedTool === "Potka") {
      updatedGarden[row][col].color = "#F5DEB3"; // Najbolj svetlo rjava za "Potka"
    }

    setGarden(updatedGarden);
  };

  return (
    <>
      <div>
        <button onClick={createGarden}>Create Garden</button>
      </div>

      <div className={styles.MainDisplay}>
        <div className={styles.GardenColumn}>
          {garden.map((row, rowIndex) => (
            <div key={rowIndex} className={styles.GardenRow}>
              {row.map((cell, colIndex) => (
                <div
                  key={`${rowIndex}-${colIndex}`}
                  className={styles.GardenCell}
                  style={{ backgroundColor: cell.color }}
                  onClick={() => handleCellClick(rowIndex, colIndex)}
                >
                  {cell.label}
                </div>
              ))}
            </div>
          ))}
        </div>

        {garden.length > 0 && <GardenMenu setSelectedTool={setSelectedTool} />}
      </div>
    </>
  );
}

export default Garden;
