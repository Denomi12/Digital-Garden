import mongoose, { Document, Model, Schema } from "mongoose";
import { UserInstance } from "./UserModel";
import { Tile, tileSchema } from "./TileModel";

export interface GardenInstance extends Document {
  name: string;
  owner: UserInstance;
  width: number;
  height: number;
  latitude: number,
  longitude: number,
  elements: Tile[];
}

var gardenSchema = new Schema<GardenInstance>(
  {
    name: { type: String, required: true, unique: true },
    owner: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    width: {type: Number},
    height: {type: Number},
    latitude: {type: Number},
    longitude: {type: Number},
    elements: [
      { type: tileSchema, default: [] },
    ],
  },
  { timestamps: true }
);

const Garden = mongoose.model<GardenInstance>("garden", gardenSchema);

export default Garden;
