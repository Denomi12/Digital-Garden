import { useContext } from "react";
import styles from "../../stylesheets/GardenMenu.module.css";
import { GardenElement } from "./Types/Elements";
import { UserContext } from "../../UserContext";

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
    { gardenElement: GardenElement.GardenBed, img: "/assets/Greda.png" },
    { gardenElement: GardenElement.RaisedBed, img: "/assets/VisokaGreda.png" },
    { gardenElement: GardenElement.Path, img: "/assets/Pot.png" },
    { gardenElement: GardenElement.Delete, img: "/assets/Cross.png"},

  ];
  const { user } = useContext(UserContext);

  return (
    <div className={styles.GardenMenu}>
      {elements.map((element) => (
        <button
          key={element.gardenElement}
          className={`${styles.MenuButton} ${
            selectedElement === element.gardenElement
              ? styles.SelectedButton
              : ""
          }`}
          onClick={() =>
            setSelectedElement(
              element.gardenElement != selectedElement
                ? element.gardenElement
                : GardenElement.None
            )
          }
        >
          <div className={styles.ButtonTextContainer}>
            {element.img && (
              <img
                src={element.img}
                alt={element.gardenElement}
                className={styles.ElementIcon}
              />
            )}
            {element.gardenElement}
          </div>
        </button>
      ))}
      {user ? (
        <button className={styles.MenuButton} onClick={saveGarden}>
          Save garden
        </button>
      ) : (
        ""
      )}
    </div>
  );
};

export default GardenMenu;
