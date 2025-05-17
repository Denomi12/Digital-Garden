import styles from "../../stylesheets/GardenMenu.module.css";
import { GardenElement } from "./Types/elements";


// Določimo tip za setSelectedTool kot funkcijo, ki sprejme string
type GardenMenuProps = {
  selectedElement: GardenElement;
  setSelectedElement: (element: GardenElement) => void;
  saveGarden: () => void;
};

const GardenMenu = ({
  selectedElement,
  setSelectedElement: setSelectedElement,
  saveGarden: saveGarden,
}: GardenMenuProps) => {
  const elements = [
    GardenElement.GardenBed,
    GardenElement.Path,
    GardenElement.RaisedBed,
  ];

  return (
    <div className={styles.GardenMenu}>
      {elements.map((element) => (
        <button
          key={element}
          className={`${styles.MenuButton} ${
            selectedElement === element ? styles.SelectedButton : ""
          }`}
          onClick={() =>
            setSelectedElement(
              element != selectedElement ? element : GardenElement.None
            )
          }
        >
          {element}
        </button>
      ))}
      <button className={styles.MenuButton} onClick={saveGarden}>
        Save garden
      </button>
    </div>
  );
};

export default GardenMenu;
