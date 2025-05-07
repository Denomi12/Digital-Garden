import { createContext } from "react";
import { User } from "./types/User";

type UserContextType = {
  user: User | null;
  setUserContext: (user: User | null) => void;
};

export const UserContext = createContext<UserContextType>({
  user: null,
  setUserContext: () => {},
});
