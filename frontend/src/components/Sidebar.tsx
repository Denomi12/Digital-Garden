import React, { useState, useContext } from "react";
import { UserContext } from "../UserContext";
import styles from "../stylesheets/Sidebar.module.css";
import { NavLink } from "react-router-dom";

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
            <img className={styles.plant} src="/assets/plant.png" />
            <p className={styles.titleText}>Garden</p>
          </div>
        )}
        <img
          onClick={toggleSidebar}
          className={`${styles.rArrow} ${rotated ? styles.rotated : ""}`}
          src="/assets/Rarrow.png"
        />
      </div>

      <div className={styles.iconContainer}>
        <NavLink
          to="/"
          className={({ isActive }) =>
            `${styles.iconItem} ${isActive ? styles.active : ""}`
          }
        >
          <img className={styles.home} src="/assets/home.png" />
          {expanded && <span className={styles.text}>Home</span>}
        </NavLink>

        <NavLink
          to="/garden"
          className={({ isActive }) =>
            `${styles.iconItem} ${isActive ? styles.active : ""}`
          }
        >
          <img className={styles.trowel} src="/assets/trowel.png" />
          {expanded && <span className={styles.text}>Garden</span>}
        </NavLink>

        <NavLink
          to="/map"
          className={({ isActive }) =>
            `${styles.iconItem} ${isActive ? styles.active : ""}`
          }
        >
          <img className={styles.map} src="/assets/map.png" />
          {expanded && <span className={styles.text}>Map</span>}
        </NavLink>

        <NavLink
          to="/forum"
          className={({ isActive }) =>
            `${styles.iconItem} ${isActive ? styles.active : ""}`
          }
        >
          <img className={styles.discussion} src="/assets/discussion.png" />
          {expanded && <span className={styles.text}>Forum</span>}
        </NavLink>
      </div>

      {user && (
        <div className={styles.user}>
          <img className={styles.person} src="/assets/person.png" />
          {expanded && <div>{user.username}</div>}
        </div>
      )}
    </div>
  );
}

export default Sidebar;
