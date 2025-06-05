export type CropAI = {
  name: string;
  latinName: string;
  watering: {
    frequency: string;
    amount: number;
  };
};

export interface CropData {
  crops: CropAI[];
}
