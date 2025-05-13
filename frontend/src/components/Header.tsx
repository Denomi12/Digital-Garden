import { useContext, useState } from "react";
import { UserContext } from "../UserContext";
import { Link } from "react-router-dom";
import styles from "./stylesheets/Header.module.css";
import Register from "./Register";
import Login from "./Login";

type HeaderProps = {
  title: string;
};

function Header({ title }: HeaderProps) {
  const { user } = useContext(UserContext);
  const [showRegister, setShowRegister] = useState(false);
  const [showLogin, setShowLogin] = useState(false);

  return (
    <>
      <header className={styles.header}>
        <div className={styles.title}>{title}</div>
        <nav>
          <ul className={styles.navList}>
            <li>
              <Link className={styles.navButton} to="/">
                Home
              </Link>
            </li>
            {user ? (
              <li>
                <Link className={styles.navButton} to="/logout">
                  Logout
                </Link>
              </li>
            ) : (
              <>
                <li>
                  <button
                    type="button"
                    className={styles.navButton}
                    onClick={() => setShowLogin(true)}
                  >
                    Login
                  </button>
                </li>
                <li>
                  <button
                    type="button"
                    className={styles.navButton}
                    onClick={() => setShowRegister(true)}
                  >
                    Register
                  </button>
                </li>
              </>
            )}
          </ul>
        </nav>
      </header>

      {showRegister && (
        <div
          className={styles.modalOverlay}
          onClick={() => setShowRegister(false)}
        >
          <div
            className={styles.modalContent}
            onClick={(e) => e.stopPropagation()}
          >
            <button
              className={styles.closeButton}
              onClick={() => setShowRegister(false)}
            >
              ✕
            </button>
            <Register onSuccess={() => setShowRegister(false)} />
          </div>
        </div>
      )}

      {showLogin && (
        <div
          className={styles.modalOverlay}
          onClick={() => setShowLogin(false)}
        >
          <div
            className={styles.modalContent}
            onClick={(e) => e.stopPropagation()}
          >
            <button
              className={styles.closeButton}
              onClick={() => setShowLogin(false)}
            >
              ✕
            </button>
            <Login onSuccess={() => setShowLogin(false)} />
          </div>
        </div>
      )}
    </>
  );
}

export default Header;
