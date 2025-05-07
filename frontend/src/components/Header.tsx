import { UserContext } from "../UserContext";
import { Link } from "react-router-dom";

type HeaderProps = {
  title: string;
};

function Header({ title }: HeaderProps) {
  return (
    <header>
      <h1>{title}</h1>
      <nav>
        <ul>
          <li>
            <Link to="/">Home</Link>
          </li>
          <UserContext.Consumer>
            {(context) =>
              context.user ? (
                <>
                  <li>
                    <Link to="/logout">Logout</Link>
                  </li>
                </>
              ) : (
                <>
                  <li>
                    <Link to="/login">Login</Link>
                  </li>
                  <li>
                    <Link to="/register">Register</Link>
                  </li>
                </>
              )
            }
          </UserContext.Consumer>
        </ul>
      </nav>
    </header>
  );
}

export default Header;
