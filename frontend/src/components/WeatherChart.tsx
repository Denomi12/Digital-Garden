import { useEffect, useState } from "react";
import { fetchWeatherApi } from "openmeteo";
import { LineChart } from "@mui/x-charts/LineChart";

function WeatherChart() {
  const [coords, setCoords] = useState<{ lat: number; lng: number } | null>(
    null
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [labels, setLabels] = useState<string[]>([]);
  const [temperature, setTemperature] = useState<number[]>([]);
  const [rain, setRain] = useState<number[]>([]);

  useEffect(() => {
    if (!navigator.geolocation) {
      setError("Vaš brskalnik ne podpira geolokacije.");
      setLoading(false);
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        setCoords({
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        });
      },
      () => {
        setError("Ni bilo mogoče pridobiti lokacije.");
        setLoading(false);
      }
    );
  }, []);

  async function getWeather(currentCoords: { lat: number; lng: number }) {
    setLoading(true);
    try {
      const params = {
        latitude: currentCoords.lat,
        longitude: currentCoords.lng,
        hourly: ["temperature_2m", "rain"],
        forecast_days: 1,
      };
      const url = "https://api.open-meteo.com/v1/forecast";
      const responses = await fetchWeatherApi(url, params);

      const response = responses[0];
      const utcOffsetSeconds = response.utcOffsetSeconds();
      const hourly = response.hourly()!;

      const variableNames = ["temperature_2m", "rain"];
      const tempIndex = variableNames.indexOf("temperature_2m");
      const rainIndex = variableNames.indexOf("rain");

      const count =
        (Number(hourly.timeEnd()) - Number(hourly.time())) / hourly.interval();

      const times = [...Array(count)].map(
        (_, i) =>
          new Date(
            (Number(hourly.time()) + i * hourly.interval() + utcOffsetSeconds) *
              1000
          )
      );

      const temperatureData =
        tempIndex !== -1 ? hourly.variables(tempIndex)!.valuesArray()! : [];
      const rainData =
        rainIndex !== -1 ? hourly.variables(rainIndex)!.valuesArray()! : [];

      const sliceCount = 24;
      setLabels(times.slice(0, sliceCount).map((t) => `${t.getHours()}:00`));
      setTemperature(Array.from(temperatureData).slice(0, sliceCount));
      setRain(Array.from(rainData).slice(0, sliceCount));

      setLoading(false);
    } catch (err) {
      setError("Napaka pri pridobivanju podatkov o vremenu.");
      setLoading(false);
    }
  }

  useEffect(() => {
    if (coords) {
      getWeather(coords);
    }
  }, [coords]);

  if (loading) return <p>Nalaganje...</p>;
  if (error)
    return (
      <div
        style={{
          width: "100%",
          height: "300px",
          display: "flex",
          backgroundColor: "#d3d3d3",
          justifyContent: "center",
          alignItems: "center",
          textAlign: "center",
        }}
      >
        {error}
      </div>
    );

  return (
    <div>
      <LineChart
        xAxis={[{ scaleType: "point", data: labels }]}
        series={[
          {
            label: "Temperature (°C)",
            data: temperature,
            color: "#4caf50",
            area: true,
          },
          {
            label: "Rain (mm)",
            data: rain,
            color: "#1976d2",
            area: true,
          },
        ]}
        height={300}
        grid={{ vertical: true, horizontal: true }}
      />
    </div>
  );
}

export default WeatherChart;
