import { useContext, useEffect, useRef, useState } from "react";
import GardenMenu from "./GardenMenu";
import styles from "../../stylesheets/GardenComponent.module.css";
import axios from "axios";
import { UserContext } from "../../UserContext";
import ShowCrops from "./ShowCrops";
import GardenGrid from "./GardenGrid";
import { Garden } from "./Types/Garden";
import { Crop, GardenElement, Tile } from "./Types/Elements";
import CursorFollower from "../CursorFollower";
import GardenList from "./GardenList";
import { useLocation } from "react-router-dom";
import GardenCellDetails from "./GardenCellDetails";

function GardenComponent() {
  const { user } = useContext(UserContext);
  const [garden, setGarden] = useState<Garden | null>(null);
  const [gardens, setGardens] = useState<Garden[]>([]);
  const [selectedElement, setSelectedElement] = useState<GardenElement>(
    GardenElement.None
  );
  const [elementImage, setElementImage] = useState<string | null>(null);
  const [selectedCrop, setSelectedCrop] = useState<Crop | null>(null);
  const [selectedCell, setSelectedCell] = useState<Tile | null>(null);

  const gardenGridRef = useRef<HTMLDivElement>(null);
  const location = useLocation();


  const scrollToGrid = () => {
    gardenGridRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  async function fetchUserGardens() {
    try {
      const res = await axios.get(
        `${import.meta.env.VITE_API_BACKEND_URL}/garden/ownedBy/${user?.id}`,
        { withCredentials: true }
      );
      setGardens(res.data);
    } catch (error) {
      console.error("Error fetching gardens:", error);
    }
  }

  useEffect(() => {
    setSelectedCell(null);
    if (user?.id) fetchUserGardens();
  }, [user]);

  useEffect(() => {
    setSelectedCell(null);
    if (garden) scrollToGrid();
  }, [garden?._id]);

  useEffect(() => {
    switch (selectedElement) {
      case GardenElement.GardenBed:
        setElementImage(`/assets/Greda.png`);
        break;
      case GardenElement.RaisedBed:
        setElementImage(`/assets/VisokaGreda.png`);
        break;
      case GardenElement.Path:
        setSelectedCrop(null);
        setElementImage(`/assets/Pot.png`);
        break;
      case GardenElement.Delete:
        setSelectedCrop(null);
        setElementImage(`/assets/Cross.png`);
        break;
      default:
        setElementImage(null);
    }
  }, [selectedElement]);

  useEffect(() => {
    if (
      selectedCrop &&
      (selectedElement == GardenElement.Delete ||
        selectedElement == GardenElement.Path)
    ) {
      setSelectedElement(GardenElement.None);
    }
  }, [selectedCrop]);

  async function saveGarden() {
    const data = garden?.toJson();

    if (!data) {
      console.error("No garden data to save.");
      return;
    }

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
    } catch (error) {
      console.error("Error saving garden:", error);
    } finally {
      fetchUserGardens();
    }
  }

  const handleCellClick = (row: number, col: number) => {
    if (!garden) return;

    const tile = garden.setElement(row, col, selectedCrop, selectedElement);
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
    setSelectedCell(tile ? tile : null);
  };

  const handleCellSelect = (row: number, col: number) => {
    if (!garden) return;

    const cell = garden.getTile(row, col);
    setSelectedCell(cell && cell != selectedCell ? cell : null);
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
        {/* TODO Instead of full garden - put in ID and fetch it */}
        {user && (
          <GardenList
            mapGarden={location.state?.garden}
            setGarden={setGarden}
            gardens={gardens}
          />
        )}

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
                cropImage={
                  selectedCrop
                    ? selectedCrop.imageSrc || "/assets/Crop.png"
                    : null
                }
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
                  onCellSelect={handleCellSelect}
                  selectedCell={selectedCell}
                />
                <div className={styles.Menu}>
                  <GardenMenu
                    selectedElement={selectedElement}
                    setSelectedElement={setSelectedElement}
                    saveGarden={saveGarden}
                  />
                  <GardenCellDetails cell={selectedCell} />
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
