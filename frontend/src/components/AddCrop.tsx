import { useState, useContext, useEffect } from "react";
import axios from "axios";
import { UserContext } from "../UserContext";
import { Navigate } from "react-router-dom";

interface CropOption {
  _id: string;
  name: string;
}

function AddCrop() {
  const [name, setName] = useState("");
  const [latinName, setLatinName] = useState("");
  const [availableCrops, setAvailableCrops] = useState<CropOption[]>([]);
  const [companionInput, setCompanionInput] = useState("");
  const [badCompanionInput, setBadCompanionInput] = useState("");
  const [companions, setCompanions] = useState<string[]>([]);
  const [badCompanions, setBadCompanions] = useState<string[]>([]);
  const [plantingMonth, setPlantingMonth] = useState("");
  const [wateringFrequency, setWateringFrequency] = useState("");
  const [wateringAmount, setWateringAmount] = useState("");
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [isLoadingCrops, setIsLoadingCrops] = useState(true);
  const userContext = useContext(UserContext);

  useEffect(() => {
    const fetchCrops = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_BACKEND_URL}/crop`);
        setAvailableCrops(response.data);
        setIsLoadingCrops(false);
      } catch (error) {
        console.error("Error fetching crops:", error);
        setIsLoadingCrops(false);
      }
    };
    fetchCrops();
  }, []);

  const addCompanion = () => {
    if (companionInput && !companions.includes(companionInput)) {
      setCompanions([...companions, companionInput]);
      setCompanionInput("");
    }
  };

  const removeCompanion = (companionId: string) => {
    setCompanions(companions.filter((id) => id !== companionId));
  };

  const addBadCompanion = () => {
    if (badCompanionInput && !badCompanions.includes(badCompanionInput)) {
      setBadCompanions([...badCompanions, badCompanionInput]);
      setBadCompanionInput("");
    }
  };

  const removeBadCompanion = (badCompanionId: string) => {
    setBadCompanions(badCompanions.filter((id) => id !== badCompanionId));
  };

  async function handleSubmit(e: { preventDefault: () => void }) {
    e.preventDefault();

    if (!userContext.user) {
      return <Navigate to={"/login"} />;
    }

    try {
      const res = await axios.post(
        `${import.meta.env.VITE_API_BACKEND_URL}/crop`,
        {
          name,
          latinName,
          goodCompanions: companions,
          badCompanions: badCompanions,
          plantingMonth,
          watering: {
            frequency: wateringFrequency,
            amount: parseFloat(wateringAmount),
          },
        },
        { withCredentials: true }
      );

      if (res.status === 201) {
        setSuccessMessage("Rastlina je bila uspešno dodana");
        setName("");
        setLatinName("");
        setCompanions([]);
        setCompanionInput("");
        setBadCompanions([]);
        setBadCompanionInput("");
        setPlantingMonth("");
        setWateringFrequency("");
        setWateringAmount("");
        
        const response = await axios.get(`${import.meta.env.VITE_API_BACKEND_URL}/crop`);
        setAvailableCrops(response.data);
      }
    } catch (error: any) {
      setError("Napaka pri dodajanju rastline");
      console.error(error);
    }
  }

  const months = [
    "Januar",
    "Februar",
    "Marec",
    "April",
    "Maj",
    "Junij",
    "Julij",
    "Avgust",
    "September",
    "Oktober",
    "November",
    "December",
  ];

  const frequencies = [
    "Vsak dan",
    "1-krat na teden",
    "2-krat na teden",
    "redko",
  ];

  if (!userContext.user) {
    return <Navigate replace to="/login" />;
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Dodaj novo rastlino</h2>

      <input
        type="text"
        name="name"
        placeholder="Ime rastline"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
      />
      <br />
      <input
        type="text"
        name="latinName"
        placeholder="Latinsko ime rastline"
        value={latinName}
        onChange={(e) => setLatinName(e.target.value)}
        required
      />


      <div style={{ marginTop: "12px" }}>
        <label htmlFor="goodCompanion">Dodaj dobrega soseda:</label>
        <br />
        <select
          id="goodCompanion"
          value={companionInput}
          onChange={(e) => setCompanionInput(e.target.value)}
          disabled={isLoadingCrops || availableCrops.length === 0}
        >
          <option value="">-- Izberi rastlino --</option>
          {availableCrops
            .filter(crop => !companions.includes(crop._id))
            .map((crop) => (
              <option key={crop._id} value={crop._id}>
                {crop.name}
              </option>
            ))}
        </select>
        <button 
          type="button" 
          onClick={addCompanion} 
          disabled={!companionInput || isLoadingCrops || availableCrops.length === 0}
        >
          dodaj
        </button>
        {availableCrops.length === 0 && (
          <p>Najprej dodajte nekaj rastlin, da jih lahko izberete kot sosede</p>
        )}
      </div>

      <div style={{ marginTop: "8px" }}>
        <h4>Dobri sosedje:</h4>
        {companions.length === 0 ? (
          <p>Ni dodanih dobrih sosedov</p>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {companions.map((companionId) => {
              const companion = availableCrops.find(c => c._id === companionId);
              return (
                <li key={companionId} style={{ marginBottom: "4px" }}>
                  <span style={{ marginRight: "8px" }}>
                    {companion ? companion.name : 'Neznana rastlina'}
                  </span>
                  <button 
                    type="button" 
                    onClick={() => removeCompanion(companionId)}
                    style={{ marginLeft: "8px" }}
                  >
                    Odstrani
                  </button>
                </li>
              );
            })}
          </ul>
        )}
      </div>

      <div style={{ marginTop: "12px" }}>
        <label htmlFor="badCompanion">Dodaj slabega soseda:</label>
        <br />
        <select
          id="badCompanion"
          value={badCompanionInput}
          onChange={(e) => setBadCompanionInput(e.target.value)}
          disabled={isLoadingCrops || availableCrops.length === 0}
        >
          <option value="">-- Izberi rastlino --</option>
          {availableCrops
            .filter(crop => !badCompanions.includes(crop._id))
            .map((crop) => (
              <option key={crop._id} value={crop._id}>
                {crop.name}
              </option>
            ))}
        </select>
        <button 
          type="button" 
          onClick={addBadCompanion} 
          disabled={!badCompanionInput || isLoadingCrops || availableCrops.length === 0}
        >
          dodaj
        </button>
        {availableCrops.length === 0 && (
          <p style={{ color: 'gray' }}>Najprej dodajte nekaj rastlin, da jih lahko izberete kot sosede</p>
        )}
      </div>

      <div style={{ marginTop: "8px" }}>
        <h4>Slabi sosedje:</h4>
        {badCompanions.length === 0 ? (
          <p>Ni dodanih slabih sosedov</p>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {badCompanions.map((badCompanionId) => {
              const badCompanion = availableCrops.find(c => c._id === badCompanionId);
              return (
                <li key={badCompanionId} style={{ marginBottom: "4px" }}>
                  <span style={{ marginRight: "8px" }}>
                    {badCompanion ? badCompanion.name : 'Neznana rastlina'}
                  </span>
                  <button 
                    type="button" 
                    onClick={() => removeBadCompanion(badCompanionId)}
                    style={{ marginLeft: "8px" }}
                  >
                    Odstrani
                  </button>
                </li>
              );
            })}
          </ul>
        )}
      </div>

      <div style={{ marginTop: "12px" }}>
        <label htmlFor="plantingMonth">Mesec sajenja:</label>
        <br />
        <select
          id="plantingMonth"
          value={plantingMonth}
          onChange={(e) => setPlantingMonth(e.target.value)}
          required
        >
          <option value="">-- Izberi mesec --</option>
          {months.map((month) => (
            <option key={month} value={month}>
              {month}
            </option>
          ))}
        </select>
      </div>

      <div style={{ marginTop: "12px" }}>
        <label htmlFor="wateringFrequency">Pogostost zalivanja:</label>
        <br />
        <select
          id="wateringFrequency"
          value={wateringFrequency}
          onChange={(e) => setWateringFrequency(e.target.value)}
          required
        >
          <option value="">-- Izberi pogostost --</option>
          {frequencies.map((frequency) => (
            <option key={frequency} value={frequency}>
              {frequency}
            </option>
          ))}
        </select>
      </div>

      <div style={{ marginTop: "12px" }}>
        <label htmlFor="wateringAmount">Količina vode (v cm):</label>
        <br />
        <input
          type="number"
          id="wateringAmount"
          placeholder="npr. 2"
          value={wateringAmount}
          onChange={(e) => setWateringAmount(e.target.value)}
          required
          min="0"
          step="0.1"
        />
      </div>

      {error && <p>{error}</p>}
      {successMessage && <p>{successMessage}</p>}

      <button 
        type="submit" 
        style={{ marginTop: "16px", padding: "8px 16px" }}
      >
        Dodaj rastlino
      </button>
    </form>
  );
}

export default AddCrop;