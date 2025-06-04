import { useEffect, useState } from "react";
import { fetchWeatherApi } from "openmeteo";

//kasneje pridobimo lat in lng od vrta
var lat = 46.573158;
var lng = 15.456666;

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
        // forecast_days: 14,
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
    <div>
      <h2>Vremenska napoved</h2>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th>Čas</th>
            <th>Temp (°C)</th>
            <th>Rel. vlaga (%)</th>
            <th>Dež (mm)</th>
            <th>Snežna odeja (cm)</th>
            <th>Sneženje (cm)</th>
            <th>Plohe (mm)</th>
            <th>Temp. tal 0 cm (°C)</th>
            <th>Vlaga tal 0–1 cm</th>
          </tr>
        </thead>
        <tbody>
          {weatherData.time.map((t, i) => (
            <tr key={i}>
              <td>{t.toLocaleString()}</td>
              <td>{weatherData.temperature2m[i]}</td>
              <td>{weatherData.relativeHumidity2m[i]}</td>
              <td>{weatherData.rain[i]}</td>
              <td>{weatherData.snowDepth[i]}</td>
              <td>{weatherData.snowfall[i]}</td>
              <td>{weatherData.showers[i]}</td>
              <td>{weatherData.soilTemperature0cm[i]}</td>
              <td>{weatherData.soilMoisture0To1cm[i]}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default WeatherForecast;
