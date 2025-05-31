import { useEffect, useContext } from "react";
import { Navigate } from "react-router-dom";
import { UserContext } from "../UserContext";
import axios from "axios";

const Logout = () => {
  const userContext = useContext(UserContext);

  useEffect(() => {
    const logout = async () => {
      try {
        await axios.post(
          `${import.meta.env.VITE_API_BACKEND_URL}/user/logout`,
          {},
          {withCredentials: true,}
        );
      } catch (error) {
        console.error("Logout error:", error);
      } finally {
        userContext.setUserContext(null);
      }
    };

    logout();
  }, []);

  return <Navigate replace to="/" />;;
};

export default Logout;
