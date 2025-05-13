import React, { useState, useContext } from "react";
import { UserContext } from "../UserContext";
import styles from "./stylesheets/Sidebar.module.css";

function Sidebar() {
  const { user } = useContext(UserContext);
  const [expanded, setExpanded] = useState(true);
  const [rotated, setRotated] = useState(false);

  const toggleSidebar = () => {
    setExpanded(!expanded);
    setRotated(!rotated);
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
          <img className={styles.home} src="/public/assets/buildingBlack.png" />
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

      {user && (
        <div className={styles.user}>
          <img className={styles.person} src="/public/assets/person.png" />
          {expanded && <div>{user.username}</div>}
        </div>
      )}
    </div>
  );
}

export default Sidebar;
