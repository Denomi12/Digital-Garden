import WeatherChart from "./WeatherChart";
import Map from "./Map";
import HotQuestion from "./hotQuestion";
import Calender from "./Calendar";
import styles from "../stylesheets/HomePage.module.css";

function HomePage() {
  return (
    <div>
      <div className={styles.title}>Welcome to home page!</div>
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
          <div className={styles.calender}>
            <Calender />
          </div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;
