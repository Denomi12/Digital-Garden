export enum GardenElement {
  GardenBed = "Greda",
  RaisedBed = "Visoka greda",
  Path = "Potka",
  None = "",
}

export type Cell = {
  row: number;
  col: number;
  element?: GardenElement;
  color?: string;
};

export class Garden {
  width: number;
  height: number;
  grid: Cell[][];

  constructor(width: number, height: number, grid: Cell[][] | null = null) {
    this.width = width;
    this.height = height;

    if (grid) {
      this.grid = grid.map((row) =>
        row.map((cell) => ({
          ...cell,
        }))
      );
      return;
    }

    this.grid = Array.from({ length: height }, (_, row) =>
      Array.from({ length: width }, (_, col) => ({
        row,
        col,
        element: GardenElement.None,
        color: undefined,
      }))
    );
  }

  getCell(row: number, col: number): Cell | undefined {
    return this.grid?.[row]?.[col];
  }

  setElement(row: number, col: number, element: GardenElement) {
    const cell = this.getCell(row, col);
    if (!cell) return;

    cell.element = element;

    switch (element) {
      case GardenElement.GardenBed:
        cell.color = "#8B4513";
        break;
      case GardenElement.RaisedBed:
        cell.color = "#D2B48C";
        break;
      case GardenElement.Path:
        cell.color = "#F5DEB3";
        break;
      default:
        cell.color = undefined;
    }
  }

  addRowTop() {
    const newRow: Cell[] = Array.from({ length: this.width }, (_, col) => ({
      row: 0,
      col,
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
        this.grid[rowIndex][colIndex].row = rowIndex;
      }
    }

    this.height += 1;
  }

  addRowBottom() {
    const newRow: Cell[] = Array.from({ length: this.width }, (_, col) => ({
      row: this.height,
      col,
      element: GardenElement.None,
      color: undefined,
    }));

    this.grid.push(newRow);

    this.height += 1;
  }

  addColumnLeft() {
    for (let rowIndex = 0; rowIndex < this.height; rowIndex++) {
      for (let colIndex = this.width - 1; colIndex >= 0; colIndex--) {
        this.grid[rowIndex][colIndex].col += 1;
      }

      const newCell: Cell = {
        row: rowIndex,
        col: 0,
        element: GardenElement.None,
        color: undefined,
      };

      this.grid[rowIndex].unshift(newCell);
    }

    this.width += 1;
  }

  addColumnRight() {
    for (let rowIndex = 0; rowIndex < this.height; rowIndex++) {
      const newCell: Cell = {
        row: rowIndex,
        col: this.width,
        element: GardenElement.None,
        color: undefined,
      };

      this.grid[rowIndex].push(newCell);
    }

    this.width += 1;
  }
}
