import WeatherChart from "./WeatherChart";
import Map from "./Map";
import HotQuestion from "./HotQuestion";
import Calender from "./Calendar";
import styles from "../stylesheets/HomePage.module.css";

function HomePage() {
  return (
    <div>
      <div className={styles.mainContent}>
        <div className={styles.leftSide}>
          <div className={styles.title2}>Map of gardens:</div>
          <div className={styles.map}>
            <Map className={styles.smallMap} showCreateButton={false} />
          </div>
          <div className={styles.title2}>Weather Forecast:</div>
          <div className={styles.weather}>
            <WeatherChart />
          </div>
        </div>
        <div className={styles.rightSide}>
          <div className={styles.title2}>Hot Questions:</div>
          <div className={styles.hotQuestions}>
            <HotQuestion />
          </div>
          <div className={styles.calender}>

            <Calender />
          </div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;
