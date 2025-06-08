import { useEffect, useState } from "react";
import { fetchWeatherApi } from "openmeteo";
import styles from "../stylesheets/WeatherForecast.module.css";

const lat = 46.573158;
const lng = 15.456666;

interface WeatherData {
  time: Date[];
  temperature2m: number[];
  relativeHumidity2m: number[];
  rain: number[];
  snowDepth: number[];
  snowfall: number[];
  showers: number[];
  soilTemperature0cm: number[];
  soilMoisture0To1cm: number[];
}

function WeatherForecast() {
  const [weatherData, setWeatherData] = useState<WeatherData | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      const params = {
        latitude: lat,
        longitude: lng,
        hourly: [
          "temperature_2m",
          "relative_humidity_2m",
          "rain",
          "snow_depth",
          "snowfall",
          "showers",
          "soil_temperature_0cm",
          "soil_moisture_0_to_1cm",
        ],
      };

      const url = "https://api.open-meteo.com/v1/forecast";
      const responses = await fetchWeatherApi(url, params);
      const response = responses[0];

      const utcOffsetSeconds = response.utcOffsetSeconds();
      const hourly = response.hourly()!;
      const timeArray = Array.from(
        {
          length:
            (Number(hourly.timeEnd()) - Number(hourly.time())) /
            hourly.interval(),
        },
        (_, i) =>
          new Date(
            (Number(hourly.time()) + i * hourly.interval() + utcOffsetSeconds) *
              1000
          )
      );

      setWeatherData({
        time: timeArray,
        temperature2m: Array.from(hourly.variables(0)!.valuesArray()!),
        relativeHumidity2m: Array.from(hourly.variables(1)!.valuesArray()!),
        rain: Array.from(hourly.variables(2)!.valuesArray()!),
        snowDepth: Array.from(hourly.variables(3)!.valuesArray()!),
        snowfall: Array.from(hourly.variables(4)!.valuesArray()!),
        showers: Array.from(hourly.variables(5)!.valuesArray()!),
        soilTemperature0cm: Array.from(hourly.variables(6)!.valuesArray()!),
        soilMoisture0To1cm: Array.from(hourly.variables(7)!.valuesArray()!),
      });
    };

    fetchData();
  }, []);

  if (!weatherData) return <p>Nalaganje vremenskih podatkov...</p>;

  return (
    <div className={styles.container}>
      <h2 className={styles.heading}>Vremenska napoved</h2>
      <div className={styles.grid}>
        {weatherData.time.map((t, i) => {
          if (i % 3 !== 0) return null;
          return (
            <div className={styles.card} key={i}>
              <h3>{t.toLocaleString()}</h3>
              <ul className={styles.list}>
                <li>
                  <strong>Temp:</strong> {weatherData.temperature2m[i]} °C
                </li>
                <li>
                  <strong>Vlaga:</strong> {weatherData.relativeHumidity2m[i]} %
                </li>
                <li>
                  <strong>Dež:</strong> {weatherData.rain[i]} mm
                </li>
                <li>
                  <strong>Sneg:</strong> {weatherData.snowDepth[i]} cm
                </li>
                <li>
                  <strong>Sneženje:</strong> {weatherData.snowfall[i]} cm
                </li>
                <li>
                  <strong>Plohe:</strong> {weatherData.showers[i]} mm
                </li>
                <li>
                  <strong>Temp. tal:</strong>{" "}
                  {weatherData.soilTemperature0cm[i]} °C
                </li>
                <li>
                  <strong>Vlaga tal:</strong>{" "}
                  {weatherData.soilMoisture0To1cm[i]}
                </li>
              </ul>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default WeatherForecast;
