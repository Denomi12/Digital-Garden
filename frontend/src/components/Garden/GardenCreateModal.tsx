import React, { useState } from "react";
import styles from "../../stylesheets/GardenCreateModal.module.css";

type NewGarden = {
  width: number;
  height: number;
  name: string;
};

type GardenCreateModalProps = {
  onCreate: (garden: NewGarden) => void;
  onClose: () => void;
  isOpen: boolean;
};

const GardenCreateModal: React.FC<GardenCreateModalProps> = ({
  onCreate,
  onClose,
  isOpen,
}) => {
  const [width, setWidth] = useState("");
  const [height, setHeight] = useState("");
  const [name, setName] = useState("");

  const handleSubmit = () => {
    const w = parseInt(width, 10);
    const h = parseInt(height, 10);

    if (
      !Number.isInteger(w) ||
      w <= 0 ||
      !Number.isInteger(h) ||
      h <= 0 ||
      !name.trim()
    ) {
      alert(
        "Please enter valid positive integers for width and height, and a garden name."
      );
      return;
    }

    onCreate({ width: w, height: h, name });
    setWidth("");
    setHeight("");
    setName("");
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        <h2>Create a New Garden</h2>
        <div className={styles.inputs}>
          <input
            type="text"
            placeholder="Garden Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className={styles.input}
          />
          <input
            type="number"
            placeholder="Width"
            value={width}
            onChange={(e) => setWidth(e.target.value)}
            className={styles.input}
            min="1"
            step="1"
            onKeyDown={(e) => {
              if (["e", "E", "+", "-"].includes(e.key)) e.preventDefault();
            }}
          />
          <input
            type="number"
            placeholder="Height"
            value={height}
            onChange={(e) => setHeight(e.target.value)}
            className={styles.input}
            min="1"
            step="1"
            onKeyDown={(e) => {
              if (["e", "E", "+", "-"].includes(e.key)) e.preventDefault();
            }}
          />
        </div>
        <div className={styles.buttons}>
          <button
            className={`${styles.button} ${styles.buttonCreate}`}
            onClick={handleSubmit}
          >
            Create
          </button>
          <button
            className={`${styles.button} ${styles.buttonCancel}`}
            onClick={onClose}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default GardenCreateModal;
