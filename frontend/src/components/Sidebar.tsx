import React, { useState } from "react";
import styles from "./stylesheets/Sidebar.module.css";

const Sidebar: React.FC = () => {
  const [expanded, setExpanded] = useState(false);
  const [rotated, setRotated] = useState(false); // Dodamo stanje za vrtenje

  const toggleSidebar = () => {
    setExpanded(!expanded);
    setRotated(!rotated); // Ko kliknemo na puščico, se stanje za vrtenje spremeni
  };

  return (
    <div className={`${styles.sidebar} ${expanded ? styles.expanded : ""}`}>
      <div
        className={`${styles.toggleArrow} ${
          expanded ? styles.toggleArrowExpanded : styles.toggleArrowCollapsed
        }`}
      >
        {expanded && (
          <div className={styles.title}>
            <img className={styles.plant} src="/public/assets/plant.png" />
            <p className={styles.titleText}>Garden</p>
          </div>
        )}
        <img
          onClick={toggleSidebar}
          className={`${styles.rArrow} ${rotated ? styles.rotated : ""}`}
          src="/public/assets/Rarrow.png"
        />
      </div>

      <div className={styles.iconContainer}>
        <div className={styles.iconItem}>
          <img className={styles.home} src="/public/assets/building.png" />
          {expanded && <span className={styles.text}>Home</span>}
        </div>
        <div className={styles.iconItem}>
          <img className={styles.trowel} src="/public/assets/trowel.png" />
          {expanded && <span className={styles.text}>Garden</span>}
        </div>
        <div className={styles.iconItem}>
          <img
            className={styles.discussion}
            src="/public/assets/discussion.png"
          />
          {expanded && <span className={styles.text}>Forum</span>}
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
