import WeatherChart from "./WeatherChart";
import styles from "../stylesheets/HomePage.module.css";

function HomePage() {
  return (
    <div>
      <h1>Welcome to home pages!</h1>
      <div className={styles.weather}>
        <WeatherChart />
      </div>
      <div className={styles.map}>MAP</div>
    </div>
  );
}

export default HomePage;
