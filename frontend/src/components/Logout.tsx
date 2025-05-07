import { useEffect, useContext } from "react";
import { UserContext } from "../userContext";
import { Navigate } from "react-router-dom";

const Logout = () => {
  const userContext = useContext(UserContext);

  useEffect(() => {
    const logout = async () => {
      try {
        await fetch("http://localhost:3001/users/logout", {
          method: "POST",
          credentials: "include",
        });
      } catch (error) {
        console.error("Logout error:", error);
      } finally {
        userContext.setUserContext(null);
      }
    };

    logout();
  }, [userContext]);

  return <Navigate replace to="/" />;
};

export default Logout;
