import styles from "../../stylesheets/GardenMenu.module.css";
import { GardenElement } from "./types";

// Določimo tip za setSelectedTool kot funkcijo, ki sprejme string
type GardenMenuProps = {
  setSelectedElement: (element: GardenElement) => void;
};

const GardenMenu = ({
  setSelectedElement: setSelectedElement,
}: GardenMenuProps) => {

  return (
    <div className={styles.GardenMenu}>
      <button
        className={styles.MenuButton}
        onClick={() => setSelectedElement(GardenElement.GardenBed)}
      >
        {GardenElement.GardenBed}
      </button>
      <button
        className={styles.MenuButton}
        onClick={() => setSelectedElement(GardenElement.Path)}
      >
        {GardenElement.Path}
      </button>
      <button
        className={styles.MenuButton}
        onClick={() => setSelectedElement(GardenElement.RaisedBed)}
      >
        {GardenElement.RaisedBed}
      </button>
    </div>
  );
};

export default GardenMenu;
