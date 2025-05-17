import { Tile, GardenElement } from "./elements";

export class Garden {
  width: number;
  height: number;
  name: string;
  grid: Tile[][];
  latitude?: number;
  longitude?: number;
  user?: string;

  private createTile(x: number, y: number): Tile {
    return { x, y, type: GardenElement.None, color: undefined };
  }

  constructor(
    width: number,
    height: number,
    name: string,
    grid: Tile[][] | null = null,
    latitude?: number,
    longitude?: number,

    user?: string
  ) {
    this.width = width;
    this.height = height;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.user = user;

    if (grid) {
      this.grid = grid.map((row) =>
        row.map((cell) => ({
          ...cell,
        }))
      );
      return;
    }

    this.grid = Array.from({ length: height }, (_, y) =>
      Array.from({ length: width }, (_, x) => this.createTile(x, y))
    );
  }

  toJson() {
    return {
      name: this.name,
      owner: this.user,
      width: this.width,
      height: this.height,
      latitude: this.latitude,
      longitude: this.longitude,
      elements: this.grid
        .flat()
        .filter((tile) => tile.type != GardenElement.None)
        .map((tile) => ({
          x: tile.x,
          y: tile.y,
          type: tile.type,
          crop: tile.crop,
          plantedDate: tile.plantedDate?.toISOString(),
        })),
    };
  }

  getTile(row: number, col: number): Tile | undefined {
    return this.grid?.[row]?.[col];
  }

  setElement(row: number, col: number, element: GardenElement) {
    const tile = this.getTile(row, col);
    if (!tile) return;

    if (element == GardenElement.None) {
      tile.plantedDate = undefined;
      tile.type = GardenElement.None;
      tile.color = undefined;
      tile.imageSrc = undefined;
      return;
    }

    tile.type = element;
    tile.plantedDate = new Date();

    switch (element) {
      case GardenElement.GardenBed:
        tile.color = "#8B4513";
        tile.imageSrc = `/assets/Greda.png`;
        break;
      case GardenElement.RaisedBed:
        tile.color = "#D2B48C";
        break;
      case GardenElement.Path:
        tile.color = "#F5DEB3";
        break;
      default:
        tile.color = undefined;
    }
  }

  addRowTop() {
    const newRow: Tile[] = Array.from({ length: this.width }, (_, x) =>
      this.createTile(x, 0)
    );

    this.grid.unshift(newRow);

    // poveča row index vsem celicam za eno
    for (let rowIndex = 1; rowIndex < this.grid.length; rowIndex++) {
      for (
        let colIndex = 0;
        colIndex < this.grid[rowIndex].length;
        colIndex++
      ) {
        this.grid[rowIndex][colIndex].y = rowIndex;
      }
    }

    this.height += 1;
  }

  addRowBottom() {
    const newRow: Tile[] = Array.from({ length: this.width }, (_, x) =>
      this.createTile(x, this.height)
    );

    this.grid.push(newRow);

    this.height += 1;
  }

  addColumnLeft() {
    for (let rowIndex = 0; rowIndex < this.height; rowIndex++) {
      for (let colIndex = this.width - 1; colIndex >= 0; colIndex--) {
        this.grid[rowIndex][colIndex].x += 1;
      }

      const newCell = this.createTile(0, rowIndex);

      this.grid[rowIndex].unshift(newCell);
    }

    this.width += 1;
  }

  addColumnRight() {
    for (let rowIndex = 0; rowIndex < this.height; rowIndex++) {
      const newCell = this.createTile(this.width, rowIndex);

      this.grid[rowIndex].push(newCell);
    }

    this.width += 1;
  }
}
