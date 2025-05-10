import { useContext } from "react";
import { UserContext } from "../UserContext";
import { Link } from "react-router-dom";

type HeaderProps = {
  title: string;
};

function Header({ title }: HeaderProps) {
  const { user } = useContext(UserContext);

  return (
    <header>
      <h1>{title}</h1>
      <nav>
        <ul>
          <li>
            <Link to="/">Home</Link>
          </li>
          {user ? (
            <li>
              <Link to="/logout">Logout</Link>
            </li>
          ) : (
            <>
              <li>
                <Link to="/login">Login</Link>
              </li>
              <li>
                <Link to="/register">Register</Link>
              </li>
            </>
          )}
        </ul>
        {user ?
        <div>
          Logged in User: {user.username}
        </div>
        : null}
        
      </nav>
    </header>
  );
}

export default Header;
