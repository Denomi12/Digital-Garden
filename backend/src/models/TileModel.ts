import mongoose, { Document, Schema } from "mongoose";
import { Crop } from "./CropModel";

export interface Tile extends Document {
  x: number;
  y: number;
  type: string;
  crop?: Crop;
  plantedDate?: Date;
  wateredDate?: Date;
}

export const tileSchema = new Schema<Tile>(
  {
    x: { type: Number, required: true },
    y: { type: Number, required: true },
    type: { type: String, required: true },
    crop: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Crop",
      required: false,
    },
    plantedDate: {
      type: Date,
      required: function (this: Tile) {
        return this.crop != null;
      },
    },
    wateredDate: {
      type: Date,
    },
  },
  { id: false }
);
