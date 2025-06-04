import { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { UserContext } from "./UserContext";
import Header from "./components/Header";
import Logout from "./components/Logout";
import Map from "./components/Map";
import Sidebar from "./components/Sidebar";
import styles from "./App.module.css";
import Forum from "./components/Forum/Forum";
import AddQuestion from "./components/Forum/AddQuestion";
import ShowQuestion from "./components/Forum/ShowQuestion";
import WeatherForecast from "./components/WeatherForecast";
import WeatherChart from "./components/WeatherChart";

import { User } from "./types/User";
import HomePage from "./components/HomePage";
import GardenComponent from "./components/Garden/GardenComponent";
import axios from "axios";

function App() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(true);

  useEffect(() => {
    async function verifyUser() {
      try {
        const response = await axios.get(
          `${import.meta.env.VITE_API_BACKEND_URL}/user/verify`,
          { withCredentials: true }
        );

        if (response.data && response.data.user) {
          setUser(response.data.user);
        } else {
          setUser(null);
        }
      } catch (error) {
        setUser(null);
      } finally {
        setLoading(false);
      }
    }

    verifyUser();
  }, []);

  if (loading) return <div>Loading...</div>;

  // v local storage shrani podatke o uporabniku
  const updateUserData = (userInfo: User | null) => {
    setUser(userInfo);
  };

  return (
    <BrowserRouter>
      <UserContext.Provider
        value={{
          user: user,
          setUserContext: updateUserData,
        }}
      >
        <div className={styles.fullScreen}>
          <Sidebar expanded={expanded} setExpanded={setExpanded} />

          <div
            className={`${styles.mainContent} ${
              expanded ? styles.mainContentExpanded : ""
            }`}
          >
            <Header title="Garden" />
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/map" element={<Map />} />
              <Route path="/garden" element={<GardenComponent />} />
              <Route path="/logout" element={<Logout />} />
              <Route path="/forum" element={<Forum />} />
              <Route path="/addQuestion" element={<AddQuestion />} />
              <Route path="/showQuestion/:id" element={<ShowQuestion />} />
              <Route path="/weather" element={<WeatherForecast />} />
              <Route path="/weatherChart" element={<WeatherChart />} />
            </Routes>
          </div>
        </div>
      </UserContext.Provider>
    </BrowserRouter>
  );
}

export default App;
