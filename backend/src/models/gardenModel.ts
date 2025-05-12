import mongoose, { Document, Model, Schema } from "mongoose";
import { UserInstance } from "./UserModel";

export interface GardenInstance extends Document {
  name: string;
  owner: UserInstance;
  location: string;
  elements: string[];
}

var gardenSchema = new Schema<GardenInstance>(
  {
    name: { type: String, required: true, unique: true },
    owner: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    location: { type: String, default: "" },
    elements: { type: [String], default: [] },
  },
  { timestamps: true }
);

const Garden = mongoose.model<GardenInstance>("garden", gardenSchema);

export default Garden;