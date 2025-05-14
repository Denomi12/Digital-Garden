export enum GardenElement {
  GardenBed = "Greda",
  RaisedBed = "Visoka greda",
  Path = "Potka",
  None = "",
}

export type Tile = {
  x: number;
  y: number;
  type?: GardenElement;
  crop?: string;
  plantedDate?: Date;
  color?: string;
};

export class Garden {
  width: number;
  height: number;
  name: string;
  grid: Tile[][];
  location?: string;
  user?: string;

  constructor(
    width: number,
    height: number,
    name: string,
    grid: Tile[][] | null = null,
    location?: string,
    user?: string
  ) {
    this.width = width;
    this.height = height;
    this.name = name;
    this.location = this.location;
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
      Array.from({ length: width }, (_, x) => ({
        x,
        y,
        type: GardenElement.None,
        color: undefined,
      }))
    );
  }

  toJson() {
    return {
      name: this.name,
      owner: this.user,
      width: this.width,
      height: this.height,
      location: this.location,
      elements: this.grid.flat().filter((tile) => tile.type != GardenElement.None).map((tile) => ({
        x: tile.x,
        y: tile.y,
        type: tile.type,
        crop: tile.crop,
        plantedDate: tile.plantedDate
          ? tile.plantedDate.toISOString()
          : undefined,
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
      return;
    }

    tile.type = element;
    tile.plantedDate = new Date();

    switch (element) {
      case GardenElement.GardenBed:
        tile.color = "#8B4513";
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
    const newRow: Tile[] = Array.from({ length: this.width }, (_, x) => ({
      x,
      y: 0,
      element: GardenElement.None,
      color: undefined,
    }));

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
    const newRow: Tile[] = Array.from({ length: this.width }, (_, x) => ({
      x,
      y: this.height,
      element: GardenElement.None,
      color: undefined,
    }));

    this.grid.push(newRow);

    this.height += 1;
  }

  addColumnLeft() {
    for (let rowIndex = 0; rowIndex < this.height; rowIndex++) {
      for (let colIndex = this.width - 1; colIndex >= 0; colIndex--) {
        this.grid[rowIndex][colIndex].x += 1;
      }

      const newCell: Tile = {
        x: 0,
        y: rowIndex,
        type: GardenElement.None,
        color: undefined,
      };

      this.grid[rowIndex].unshift(newCell);
    }

    this.width += 1;
  }

  addColumnRight() {
    for (let rowIndex = 0; rowIndex < this.height; rowIndex++) {
      const newCell: Tile = {
        x: this.width,
        y: rowIndex,
        type: GardenElement.None,
        color: undefined,
      };

      this.grid[rowIndex].push(newCell);
    }

    this.width += 1;
  }
}
