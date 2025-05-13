import { useState, useContext } from "react";
import axios from "axios";
import { UserContext } from "../UserContext";
import { Navigate } from "react-router-dom";

function AddCrop() {
  const [name, setName] = useState("");
  const [companionInput, setCompanionInput] = useState("");
  const [companions, setCompanions] = useState<string[]>([]);
  const [plantingMonth, setPlantingMonth] = useState("");
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const userContext = useContext(UserContext);

  const addCompanion = () => {
    const trimmed = companionInput.trim();
    if (trimmed && !companions.includes(trimmed)) {
      setCompanions([...companions, trimmed]);
      setCompanionInput("");
    }
  };

  const removeCompanion = (companion: string) => {
    setCompanions(companions.filter((c) => c !== companion));
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
            goodCompanions: companions,
            plantingMonth, 
        },
        { withCredentials: true }
      );

      if (res.status === 201) {
        setSuccessMessage("Rastlina je bila uspešno dodana");
        setName("");
        setCompanions([""]);
        setCompanionInput("");
        setPlantingMonth("");
      }
    } catch (error: any) {
      setError("Napaka pri dodajanju rastline");
    }
  }

  const months = [
    "Januar", "Februar", "Marec", "April", "Maj", "Junij", "Julij", "Avgust", "September", "Oktober", "November", "December" 
  ];

  return (
    <form onSubmit={handleSubmit}>
      {userContext.user ? "" : <Navigate replace to="/login" />}

      <h2>Dodaj novo rastlino</h2>

      <input
        type="text"
        name="name"
        placeholder="Ime rastline"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
      />

      <div>
        <input
          type="text"
          name="companion"
          placeholder="Dodaj dobrega soseda"
          value={companionInput}
          onChange={(e) => setCompanionInput(e.target.value)}
        />
        <button type="button" onClick={addCompanion}>
          dodaj
        </button>
      </div>

      <div>
        <h4>Dobri sosedje:</h4>
        {companions.length === 0 && <p>Ni dodanih sosedov</p>}
        {companions.map((companion, index) => (
          <div
            key={index}
            
          >
            <span style={{ marginRight: "8px" }}>{companion}</span>
            <button type="button" onClick={() => removeCompanion(companion)}>
              Izbrisi
            </button>
          </div>
        ))}
      </div>

      <div style={{ marginTop: "12px" }}>
        <label htmlFor="plantingMonth">Mesec sajenja:</label><br />
        <select
          id="plantingMonth"
          value={plantingMonth}
          onChange={(e) => setPlantingMonth(e.target.value)}
          required
        >
          <option value="">-- Izberi mesec --</option>
          {months.map((month) => (
            <option key={month} value={month}>{month}</option>
          ))}
        </select>
      </div>

      {error && <p>{error}</p>}
      {successMessage && <p>{successMessage}</p>}

      <input type="submit" value="Dodaj rastlino" />
    </form>
  );
}

export default AddCrop;
