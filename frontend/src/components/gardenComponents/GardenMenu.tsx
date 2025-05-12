import styles from "../../stylesheets/GardenMenu.module.css";

// Določimo tip za setSelectedTool kot funkcijo, ki sprejme string
type GardenMenuProps = {
  setSelectedTool: (tool: string) => void;
};

const GardenMenu = ({ setSelectedTool }: GardenMenuProps) => {
  return (
    <div className={styles.GardenMenu}>
      <button
        className={styles.MenuButton}
        onClick={() => setSelectedTool("Greda")}
      >
        Greda
      </button>
      <button
        className={styles.MenuButton}
        onClick={() => setSelectedTool("Potka")}
      >
        Potka
      </button>
      <button
        className={styles.MenuButton}
        onClick={() => setSelectedTool("Visoka greda")}
      >
        Visoka greda
      </button>
    </div>
  );
};

export default GardenMenu;
