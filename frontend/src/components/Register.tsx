import axios from "axios";
import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../UserContext";
import styles from "./stylesheets/Header.module.css";

//za popup pri registeru
type LoginProps = {
  onSuccess: () => void;
};

function Register({ onSuccess }: LoginProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const userContext = useContext(UserContext);
  const navigate = useNavigate();

  async function handleRegister(e: { preventDefault: () => void }) {
    e.preventDefault();

    try {
      const res = await axios.post(
        `${import.meta.env.VITE_API_BACKEND_URL}/user`,
        {
          email,
          username,
          password,
        },
        { withCredentials: true }
      );

      const user = res.data.user;

      if (user) {
        // ob registraciji prijavi uporabnika
        userContext.setUserContext({
          username: user.username,
          id: user._id,
          email: user.email,
        });
        navigate("/");
        onSuccess();
      } else {
        throw new Error("Registration failed");
      }
    } catch (err) {
      setUsername("");
      setPassword("");
      setEmail("");
      setError("Registration failed");
    }
  }

  return (
    <>
      <div className={styles.formHeader}>Start Your Journey!</div>
      <form onSubmit={handleRegister}>
        <input
          type="text"
          name="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="text"
          name="username"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <input type="submit" name="submit" value="Register" />
        {error && <label>{error}</label>}
      </form>
    </>
  );
}

export default Register;
