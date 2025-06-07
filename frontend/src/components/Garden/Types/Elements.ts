export enum GardenElement {
  GardenBed = "Greda",
  RaisedBed = "Visoka greda",
  Path = "Potka",
  None = "",
  Delete= "Delete",
}

export interface Crop {
  name: string;
  latinName: string;
  goodCompanions: Crop[];
  badCompanions: Crop[];
  plantingMonth: string;
  watering: {
    frequency: string;
    amount: number;
  };
  imageSrc?: string;
}

export type Tile = {
  x: number;
  y: number;
  type?: GardenElement;
  crop?: Crop;
  plantedDate?: Date;
  wateredDate?: Date;
  color?: string;
  imageSrc?: string;
};
