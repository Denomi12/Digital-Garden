import { useContext, useState } from "react";
import GardenMenu from "./GardenMenu";
import styles from "../../stylesheets/GardenComponent.module.css";
import axios from "axios";
import { UserContext } from "../../UserContext";
import ShowCrops from "./ShowCrops";
import GardenGrid from "./GardenGrid";
import { Garden } from "./Types/Garden";
import { GardenElement } from "./Types/elements";

function GardenComponent() {
  const { user } = useContext(UserContext);
  const [garden, setGarden] = useState<Garden | null>(null);
  const [selectedElement, setSelectedElement] = useState<GardenElement>(
    GardenElement.None
  );

  const createGarden = () => {
    const widthInput = prompt("Enter the width of the garden:");
    const heightInput = prompt("Enter the height of the garden:");
    const nameInput = prompt("Enter garden name:");

    if (widthInput && heightInput) {
      const w = parseInt(widthInput, 10);
      const h = parseInt(heightInput, 10);

      if (!isNaN(w) && !isNaN(h) && nameInput) {
        const newGarden = new Garden(
          w,
          h,
          nameInput,
          null,
          undefined,
          undefined,
          user?.id
        );
        setGarden(newGarden);
      } else {
        alert("Please enter valid numbers for both width and height.");
      }
    } else {
      alert("Please enter both width and height.");
    }
  };

  async function saveGarden() {
    const res = await axios.post(
      `${import.meta.env.VITE_API_BACKEND_URL}/garden`,
      garden?.toJson(),
      { withCredentials: true }
    );
    console.log(res);
  }

  const handleCellClick = (row: number, col: number) => {
    if (!garden) return;

    garden.setElement(row, col, selectedElement);
    setGarden(
      new Garden(
        garden.width,
        garden.height,
        garden.name,
        garden.grid,
        garden.latitude,
        garden.longitude,
        garden.user
      )
    );
  };

  const handleTopClick = () => {
    if (!garden) return;
    garden.addRowTop();
    setGarden(
      new Garden(
        garden.width,
        garden.height,
        garden.name,
        garden.grid,
         garden.latitude,
        garden.longitude,
        garden.user
      )
    );
  };

  const handleBottomClick = () => {
    if (!garden) return;
    garden.addRowBottom();
    setGarden(
      new Garden(
        garden.width,
        garden.height,
        garden.name,
        garden.grid,
         garden.latitude,
        garden.longitude,
        garden.user
      )
    );
  };

  const handleLeftClick = () => {
    if (!garden) return;
    garden.addColumnLeft();
    setGarden(
      new Garden(
        garden.width,
        garden.height,
        garden.name,
        garden.grid,
         garden.latitude,
        garden.longitude,
        garden.user
      )
    );
  };

  const handleRightClick = () => {
    if (!garden) return;
    garden.addColumnRight();
    setGarden(
      new Garden(
        garden.width,
        garden.height,
        garden.name,
        garden.grid,
         garden.latitude,
        garden.longitude,
        garden.user
      )
    );
  };

  return (
    <>
      <div className={styles.Container}>
        <button onClick={createGarden} className={styles.CreateButton}>
          Create Garden
        </button>

        <div className={styles.MainLayout}>
          <div className={styles.SidePanel}>
            <ShowCrops />
          </div>

          <div className={styles.GridContainer}>
            {garden && (
              <GardenGrid
                garden={garden}
                onCellClick={handleCellClick}
                onTopClick={handleTopClick}
                onBottomClick={handleBottomClick}
                onLeftClick={handleLeftClick}
                onRightClick={handleRightClick}
              />
            )}
          </div>

          <div className={styles.SidePanel}>
            <GardenMenu
              selectedElement={selectedElement}
              setSelectedElement={setSelectedElement}
              saveGarden={saveGarden}
            />
          </div>
        </div>
      </div>
    </>
  );
}

export default GardenComponent;

// <div>
//   <button onClick={createGarden}>Create Garden</button>
// </div>
// {JSON.stringify(garden)}
// <ShowCrops/>
// <div className={styles.MainDisplay}>
//   {garden && (
//     <div className={styles.GardenWrapper}>
//       <button
//         className={`${styles.PlusButton} ${styles.PlusTop}`}
//         onClick={() => handleTopClick()}
//       >
//         +
//       </button>
//       <button
//         className={`${styles.PlusButton} ${styles.PlusBottom}`}
//         onClick={() => handleBottomClick()}
//       >
//         +
//       </button>
//       <button
//         className={`${styles.PlusButton} ${styles.PlusLeft}`}
//         onClick={() => handleLeftClick()}
//       >
//         +
//       </button>
//       <button
//         className={`${styles.PlusButton} ${styles.PlusRight}`}
//         onClick={() => handleRightClick()}
//       >
//         +
//       </button>

//       <div className={styles.GardenColumn}>
//         {garden.grid.map((row, rowIndex) => (
//           <div key={rowIndex} className={styles.GardenRow}>
//             {row.map((cell, colIndex) => (
//               <div
//                 key={`${rowIndex}-${colIndex}`}
//                 className={styles.GardenCell}
//                 style={{ backgroundColor: cell.color }}
//                 onClick={() => handleCellClick(rowIndex, colIndex)}
//               >
//                 {`${cell.y}-${cell.x}`}
//               </div>
//             ))}
//           </div>
//         ))}
//       </div>
//     </div>
//   )}
//   {garden && (
//     <GardenMenu
//       selectedElement={selectedElement}
//       setSelectedElement={setSelectedElement}
//       saveGarden={saveGarden}
//     />
//   )}
//     <AddCrop/>

//   <div className={styles.GardenActions}></div>
