import { User } from "../../../types/User";
import { Tile, GardenElement, Crop } from "./Elements";

export class Garden {
  width: number;
  height: number;
  name: string;
  elements: Tile[][];
  location?: string;
  latitude?: number;
  longitude?: number;
  owner?: User;
  _id?: string;

  private createTile(x: number, y: number): Tile {
    return { x, y, type: GardenElement.None, color: undefined };
  }

  constructor(
    width: number,
    height: number,
    name: string,
    elements: Tile[][] | null = null,
    location?: string,
    latitude?: number,
    longitude?: number,

    owner?: User,
    _id?: string,
  ) {
    this.width = width;
    this.height = height;
    this.name = name;
    this.location = location;
    this.latitude = latitude;
    this.longitude = longitude;
    this.owner = owner;
    this._id = _id;
    if (elements) {
      this.elements = elements.map((row) =>
        row.map((cell) => ({
          ...cell,
        }))
      );
      return;
    }

    this.elements = Array.from({ length: height }, (_, y) =>
      Array.from({ length: width }, (_, x) => this.createTile(x, y))
    );
  }

  toJson() {
    return {
      _id: this._id,
      name: this.name,
      owner: this.owner,
      width: this.width,
      height: this.height,
      location: this.location,
      latitude: this.latitude,
      longitude: this.longitude,
      elements: this.elements
        .flat()
        .filter((tile) => tile.type != GardenElement.None)
        .map((tile) => ({
          x: tile.x,
          y: tile.y,
          type: tile.type,
          crop: tile.crop,
          plantedDate: tile.plantedDate?.toISOString(),
          wateredDate: tile.wateredDate?.toISOString(),

        })),
    };
  }

  getTile(row: number, col: number): Tile | undefined {
    return this.elements?.[row]?.[col];
  }

  setElement(
    row: number,
    col: number,
    crop: Crop | null,
    element: GardenElement
  ) {
    const tile = this.getTile(row, col);
    if (!tile) return;

    //TODO Upoštevaj, da ne povoziš datumov
    //TODO Dodaj gumb za brisanje

    // 1. Oboje prazno => pobriši vse
    if (element == GardenElement.None && !crop) {
      tile.plantedDate = undefined;
      tile.wateredDate = undefined;
      tile.crop = undefined;
      tile.type = GardenElement.None;
      tile.color = undefined;
      tile.imageSrc = undefined;
      return;
    }

    // 2. Crop na obstoječo gredo => dodaj crop in planted date
    else if (
      crop &&
      (tile.type == GardenElement.GardenBed ||
        tile.type == GardenElement.RaisedBed)
    ) {
      tile.crop = crop;
      tile.plantedDate = new Date();
    }

    //3. Crop in element => dodaj element in crop
    else if (
      crop &&
      (element == GardenElement.GardenBed || element == GardenElement.RaisedBed)
    ) {
      tile.type = element;
      tile.crop = crop;
      tile.plantedDate = new Date();
    }

    //4. Samo element
    else if (!crop) {
      tile.crop = undefined;
      tile.plantedDate = undefined;
      tile.wateredDate = undefined;
      tile.type = element;
      tile.color = undefined;
    }

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
        if (!crop) {
          tile.color = undefined;
          tile.imageSrc = undefined;
        }
    }
  }

  addRowTop() {
    const newRow: Tile[] = Array.from({ length: this.width }, (_, x) =>
      this.createTile(x, 0)
    );

    this.elements.unshift(newRow);

    // poveča row index vsem celicam za eno
    for (let rowIndex = 1; rowIndex < this.elements.length; rowIndex++) {
      for (
        let colIndex = 0;
        colIndex < this.elements[rowIndex].length;
        colIndex++
      ) {
        this.elements[rowIndex][colIndex].y = rowIndex;
      }
    }

    this.height += 1;
  }

  addRowBottom() {
    const newRow: Tile[] = Array.from({ length: this.width }, (_, x) =>
      this.createTile(x, this.height)
    );

    this.elements.push(newRow);

    this.height += 1;
  }

  addColumnLeft() {
    for (let rowIndex = 0; rowIndex < this.height; rowIndex++) {
      for (let colIndex = this.width - 1; colIndex >= 0; colIndex--) {
        this.elements[rowIndex][colIndex].x += 1;
      }

      const newCell = this.createTile(0, rowIndex);

      this.elements[rowIndex].unshift(newCell);
    }

    this.width += 1;
  }

  addColumnRight() {
    for (let rowIndex = 0; rowIndex < this.height; rowIndex++) {
      const newCell = this.createTile(this.width, rowIndex);

      this.elements[rowIndex].push(newCell);
    }

    this.width += 1;
  }
}
