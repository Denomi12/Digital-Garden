import GardenLayout from "./assets/GardenLayout";
import HeroPage from "./assets/HeroPage";
import { BrowserRouter, Routes, Route } from 'react-router-dom';


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HeroPage/>}/>
        <Route path="/garden" element={<GardenLayout />}/>
        
      </Routes>
    </BrowserRouter>
  );
}

export default App;
