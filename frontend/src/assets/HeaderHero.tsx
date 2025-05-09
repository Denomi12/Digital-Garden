import { Link } from "react-router-dom";
import styles from "./HeaderHero.module.css"

const HeaderHero = () => {
    return (
        <nav className={styles.headerHero}>
            <Link to="/">Domov </Link>
            <Link to="/about_us">O nas</Link>
            <Link to="/login">Prijava</Link>
            <Link to="register">Registracija</Link>
        </nav>
    );
};

export default HeaderHero;