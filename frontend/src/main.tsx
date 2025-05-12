import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import Map from "./components/Map.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
    {/* <Map /> */}
  </StrictMode>
);
