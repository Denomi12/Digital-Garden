import WeatherChart from "./WeatherChart";
import Map from "./Map";
import HotQuestion from "./hotQuestion";
import styles from "../stylesheets/HomePage.module.css";

function HomePage() {
  return (
    <div>
      <h2>Welcome to home page!</h2>
      <div className={styles.mainContent}>
        <div className={styles.leftSide}>
          <div className={styles.weather}>
            <WeatherChart />
          </div>
          <div className={styles.map}>
            <Map className={styles.smallMap} showCreateButton={false} />
          </div>
        </div>
        <div className={styles.rightSide}>
          <div className={styles.hotQuestions}>
            <HotQuestion />
          </div>
          <div className={styles.calender}>Calender</div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;
