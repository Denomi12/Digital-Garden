import * as React from "react";
import { PieChart } from "@mui/x-charts/PieChart";
import { GardenElement, Tile } from "./Types/Elements";
import { Garden } from "./Types/Garden";
import { Stack, Typography } from "@mui/material";

interface GardenGraphProps {
  garden?: Garden;
}

function GardenGraph({ garden }: GardenGraphProps) {
  const crops: Tile[] = garden?.elements.flat().filter((tile) => tile) || [];

  const typeColors: Record<string, string> = {
    [GardenElement.GardenBed]: "#4CAF50",      // Green
    [GardenElement.RaisedBed]: "#8BC34A",      // Light Green
    [GardenElement.Path]: "#935814",           // Path
    [GardenElement.Delete]: "#F44336",         // Red
    [GardenElement.None]: "#E0E0E0",           // Light Grey
  };

  const cropPalette = [
    "#FF9800", "#FF5722", "#FFC107", "#00BCD4", "#3F51B5", "#673AB7", "#009688",
  ];

  // Count tile types
  const typeCounts = crops.reduce((acc, tile) => {
    const type = tile.type ?? "";
    acc[type] = (acc[type] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  const typeSeries = Object.entries(typeCounts).map(([type, count]) => ({
    id: type || "(empty)",
    value: count,
    label: type || "(empty)",
    color: typeColors[type] || "#BDBDBD",
  }));

  // Only count crops in Greda or Visoka greda tiles
  const validTypes = [GardenElement.GardenBed, GardenElement.RaisedBed];

  const cropCounts = crops.reduce((acc, tile) => {
    if (tile.type && validTypes.includes(tile.type) && tile.crop?.name) {
      const crop = tile.crop.name;
      acc[crop] = (acc[crop] || 0) + 1;
    }
    return acc;
  }, {} as Record<string, number>);

  const cropSeries = Object.entries(cropCounts).map(([crop, count], index) => ({
    id: crop,
    value: count,
    label: crop,
    color: cropPalette[index % cropPalette.length],
  }));

  return (
    <Stack spacing={3} alignItems="center">

      <PieChart
        series={[
          {
            data: typeSeries,
            innerRadius: 0,
            outerRadius: 100,
            id: "tile-types",
            valueFormatter: ({ value }) => {
              const total = typeSeries.reduce((sum, item) => sum + item.value, 0);
              const percent = (value / total) * 100;
              return `${percent.toFixed(1)}%`;
            },
          },
          {
            data: cropSeries,
            innerRadius: 110,
            outerRadius: 160,
            id: "crops-on-gredas",
            valueFormatter: ({ value }) => {
              const total = cropSeries.reduce((sum, item) => sum + item.value, 0);
              const percent = (value / total) * 100;
              return `${percent.toFixed(1)}%`;
            },
          },
        ]}
        width={600}
        height={500}
      />
    </Stack>
  );
}

export default GardenGraph;
