import { useContext, useState } from "react";
import axios from "axios";
import { UserContext } from "../UserContext";
import { Navigate } from "react-router-dom";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const userContext = useContext(UserContext);

  async function handleLogin(e: { preventDefault: () => void }) {
    e.preventDefault();
    const res = await axios.post(
       `${import.meta.env.VITE_API_BACKEND_URL}/user/login`,
      { username, password },
      { withCredentials: true }
    );
    const { user } = res.data;

    if (user?._id) {
      userContext.setUserContext({
        username: user.username,
        id: user._id,
        email: user.email,
      });
    } else {
      setUsername("");
      setPassword("");
      setError("Invalid username or password");
    }

    if (userContext.user) {
      return <Navigate replace to="/" />;
    }
  }

  return (
    <form onSubmit={handleLogin}>
      {userContext.user ? <Navigate replace to="/" /> : ""}
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
      <input type="submit" value="Log in" />
      <label>{error}</label>
    </form>
  );
}

export default Login;
