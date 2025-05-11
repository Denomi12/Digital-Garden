import { useState } from "react";
import GardenMenu from "./GardenMenu";
import styles from "./stylesheets/Garden.module.css";

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

    const updatedGarden = garden.map((r) => r.map((c) => ({ ...c })));
    updatedGarden[row][col].tool = selectedTool; // shrani ime orodja
    updatedGarden[row][col].label = selectedTool;

    if (selectedTool === "Greda") {
      updatedGarden[row][col].color = "#8B4513";
    } else if (selectedTool === "Visoka greda") {
      updatedGarden[row][col].color = "#D2B48C";
    } else if (selectedTool === "Potka") {
      updatedGarden[row][col].color = "#F5DEB3";
    }

    setGarden(updatedGarden);
  };

  const handleTopClick = () => {
    //kreiramo zgornjo vrstico
    const newRow = Array.from({ length: width }, (_, colIndex) => ({
      label: `Cell-0-${colIndex}`,
    }));

    //vrstico prilepimo na vrh gardena
    const updatedGarden = [newRow, ...garden];

    //posodobi labele vseh tistih cellov, ki nimajo dolocenih toolov
    const updatedGardenWithLabels = updatedGarden.map((row, rowIndex) =>
      row.map((cell, colIndex) => ({
        ...cell,
        label: cell.tool ? cell.tool : `Cell-${rowIndex}-${colIndex}`,
      }))
    );

    setGarden(updatedGardenWithLabels);
    setHeight((prevHeight) => prevHeight + 1);
  };

  const handleBottomClick = () => {
    const newRow = Array.from({ length: width }, (_, colIndex) => ({
      label: `Cell-${height}-${colIndex}`,
    }));

    setGarden((prevGarden) => [...prevGarden, newRow]);
    setHeight((prev) => prev + 1);
  };

  const handleLeftClick = () => {
    // Kreiramo novo celico na levi strani vsake vrstice
    const updatedGarden = garden.map((row, rowIndex) => {
      const newCell = {
        label: `Cell-${rowIndex}-0`,
      };
      return [newCell, ...row];
    });

    // posodobi labele vseh tistih cellov, ki nimajo dolocenih toolov
    const updatedGardenWithLabels = updatedGarden.map((row, rowIndex) =>
      row.map((cell, colIndex) => ({
        ...cell,
        label: cell.tool ? cell.tool : `Cell-${rowIndex}-${colIndex}`,
      }))
    );

    setGarden(updatedGardenWithLabels);
    setWidth((prevWidth) => prevWidth + 1);
  };

  const handleRightClick = () => {
    const updatedGarden = garden.map((row, rowIndex) => {
      const newCell = {
        label: `Cell-${rowIndex}-0`,
      };
      return [...row, newCell];
    });

    // posodobi labele vseh tistih cellov, ki nimajo dolocenih toolov
    const updatedGardenWithLabels = updatedGarden.map((row, rowIndex) =>
      row.map((cell, colIndex) => ({
        ...cell,
        label: cell.tool ? cell.tool : `Cell-${rowIndex}-${colIndex}`,
      }))
    );

    setGarden(updatedGardenWithLabels);
    setWidth((prevWidth) => prevWidth + 1);
  };

  return (
    <>
      <div>
        <button onClick={createGarden}>Create Garden</button>
      </div>

      <div className={styles.MainDisplay}>
        {garden.length > 0 && (
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
          </div>
        )}

        {garden.length > 0 && <GardenMenu setSelectedTool={setSelectedTool} />}
      </div>
    </>
  );
}

export default Garden;
