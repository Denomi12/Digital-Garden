import { useContext, useEffect, useRef, useState } from "react";
import GardenMenu from "./GardenMenu";
import styles from "../../stylesheets/GardenComponent.module.css";
import axios from "axios";
import { UserContext } from "../../UserContext";
import ShowCrops from "./ShowCrops";
import GardenGrid from "./GardenGrid";
import { Garden } from "./Types/Garden";
import { Crop, GardenElement } from "./Types/Elements";
import CursorFollower from "../CursorFollower";
import GardenList from "./GardenList";

function GardenComponent() {
  const { user } = useContext(UserContext);
  const [garden, setGarden] = useState<Garden | null>(null);
  const [gardens, setGardens] = useState<Garden[]>([]);
  const [selectedElement, setSelectedElement] = useState<GardenElement>(
    GardenElement.None
  );
  const [elementImage, setElementImage] = useState<string | null>(null);
  const [selectedCrop, setSelectedCrop] = useState<Crop | null>(null);
  const gardenGridRef = useRef<HTMLDivElement>(null);

  const scrollToGrid = () => {
    gardenGridRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  async function fetchUserGardens() {
    try {
      const res = await axios.get(
        `${import.meta.env.VITE_API_BACKEND_URL}/garden/ownedBy/${user?.id}`
      );
      setGardens(res.data);
    } catch (error) {
      console.error("Error fetching crops:", error);
    }
  }

  useEffect(() => {
    if (user?.id) fetchUserGardens();
  }, [user]);

  useEffect(() => {
    if (garden) scrollToGrid();
  }, [garden?._id]);

  useEffect(() => {
    switch (selectedElement) {
      case GardenElement.GardenBed:
        setElementImage(`/assets/Greda.png`);
        break;
      case GardenElement.RaisedBed:
        setElementImage(`/assets/Greda.png`);
        break;
      case GardenElement.Path:
        setElementImage(`/assets/Greda.png`);
        break;
      default:
        setElementImage(null);
    }
  }, [selectedCrop, selectedElement]);

  async function saveGarden() {
    const data = garden?.toJson();

    if (!data) {
      console.error("No garden data to save.");
      return;
    }

    console.log("Trying to save: ", data);

    const url = data._id
      ? `${import.meta.env.VITE_API_BACKEND_URL}/garden/${data._id}`
      : `${import.meta.env.VITE_API_BACKEND_URL}/garden`;

    const method = data._id ? "put" : "post";

    try {
      const res = await axios({
        method,
        url,
        data,
        withCredentials: true,
      });
      console.log("Saved garden: ", res.data);
    } catch (error) {
      console.error("Error saving garden:", error);
    } finally {
      fetchUserGardens();
    }
  }

  const handleCellClick = (row: number, col: number) => {
    if (!garden) return;

    garden.setElement(row, col, selectedCrop, selectedElement);
    setGarden(
      new Garden(
        garden.width,
        garden.height,
        garden.name,
        garden.elements,
        garden.location,
        garden.latitude,
        garden.longitude,
        garden.owner,
        garden._id
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
        garden.elements,
        garden.location,
        garden.latitude,
        garden.longitude,
        garden.owner,
        garden._id
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
        garden.elements,
        garden.location,
        garden.latitude,
        garden.longitude,
        garden.owner,
        garden._id
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
        garden.elements,
        garden.location,
        garden.latitude,
        garden.longitude,
        garden.owner,
        garden._id
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
        garden.elements,
        garden.location,
        garden.latitude,
        garden.longitude,
        garden.owner,
        garden._id
      )
    );
  };

  function updateGardenField(
    field: "location" | "latitude" | "longitude",
    value: string
  ) {
    if (!garden) return;

    const updatedGarden = new Garden(
      garden.width,
      garden.height,
      garden.name,
      garden.elements,
      field === "location" ? value : garden.location,
      field === "latitude" ? parseFloat(value) || 0 : garden.latitude,
      field === "longitude" ? parseFloat(value) || 0 : garden.longitude,
      garden.owner,
      garden._id
    );

    setGarden(updatedGarden);
  }

  return (
    <>
      <div className={styles.GardenComponentContainer}>
        {user && <GardenList setGarden={setGarden} gardens={gardens} />}

        {garden && (
          <div className={styles.SelectedGarden}>
            <div className={styles.GardenInfo}>
              <div className={styles.NameInfo}>{garden?.name}</div>

              <div className={styles.LocationInfo}>
                <div className={styles.InputWrapper}>
                  <span className={styles.InputIcon}>📍</span>
                  <input
                    className={styles.InfoText}
                    value={garden.location || ""}
                    onChange={(e) =>
                      updateGardenField("location", e.target.value)
                    }
                    placeholder="Location"
                  />
                </div>

                <div className={styles.InputWrapper}>
                  <span className={styles.InputIcon}>🌐</span>
                  <input
                    className={styles.InfoText}
                    value={garden.latitude?.toString() || ""}
                    onChange={(e) =>
                      updateGardenField("latitude", e.target.value)
                    }
                    placeholder="Latitude"
                  />
                </div>

                <div className={styles.InputWrapper}>
                  <span className={styles.InputIcon}>🌐</span>
                  <input
                    className={styles.InfoText}
                    value={garden.longitude?.toString() || ""}
                    onChange={(e) =>
                      updateGardenField("longitude", e.target.value)
                    }
                    placeholder="Longitude"
                  />
                </div>
              </div>
            </div>

            <div className={styles.MainLayout}>
              <CursorFollower
                cropImage={selectedCrop?.imageSrc}
                elementImage={elementImage}
              />
              <div className={styles.SidePanel}>
                <div className={styles.ScrollablePanel}>
                  <ShowCrops
                    selectedCrop={selectedCrop}
                    setSelectedCrop={setSelectedCrop}
                  />
                </div>
              </div>

              <div className={styles.GridContainer} ref={gardenGridRef}>
                <GardenGrid
                  garden={garden}
                  onCellClick={handleCellClick}
                  onTopClick={handleTopClick}
                  onBottomClick={handleBottomClick}
                  onLeftClick={handleLeftClick}
                  onRightClick={handleRightClick}
                />
                <div className={styles.Menu}>
                  <GardenMenu
                    selectedElement={selectedElement}
                    setSelectedElement={setSelectedElement}
                    saveGarden={saveGarden}
                  />
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
}

export default GardenComponent;
