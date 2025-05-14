import { useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { UserContext } from "./UserContext";
import Header from "./components/Header";
import Garden from "./components/Garden";
import Logout from "./components/Logout";
import Map from "./components/Map";
import Sidebar from "./components/Sidebar";
import HomePage from "./components/HomePage";
import styles from "./App.module.css";
import Forum from "./components/Forum";

<link
  rel="stylesheet"
  href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"
/>;

import { User } from "./types/User";

function App() {
  const [user, setUser] = useState<User | null>(() => {
    const storedUser = localStorage.getItem("user");
    return storedUser ? JSON.parse(storedUser) : null;
  });

  // v local storage shrani podatke o uporabniku
  const updateUserData = (userInfo: User | null) => {
    if (userInfo) {
      localStorage.setItem("user", JSON.stringify(userInfo));
    } else {
      localStorage.removeItem("user");
    }
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
          <Sidebar />
          <div className={styles.mainContent}>
            <Header title="Garden" />
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/map" element={<Map />} />
              <Route path="/garden" element={<Garden />} />
              <Route path="/logout" element={<Logout />} />
              <Route path="/forum" element={<Forum />} />
            </Routes>
          </div>
        </div>
      </UserContext.Provider>
    </BrowserRouter>
  );
}

export default App;
