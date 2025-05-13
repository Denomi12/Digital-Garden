import { useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { UserContext } from "./UserContext";
import Header from "./components/Header";
import Garden from "./components/gardenComponents/Garden";
import Register from "./components/Register";
import Logout from "./components/Logout";
import Login from "./components/Login";
import Map from "./components/Map";
<link
  rel="stylesheet"
  href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"
/>;

import { User } from "./types/User";
import HomePage from "./components/HomePage";
import AddCrop from "./components/AddCrop";

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
        <div className="App">
          <Header title="Garden - nevem" />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/map" element={<Map />} />
            <Route path="/garden" element={<Garden />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/logout" element={<Logout />} />
          </Routes>
          <AddCrop/>
        </div>
      </UserContext.Provider>
    </BrowserRouter>
  );
}

export default App;
