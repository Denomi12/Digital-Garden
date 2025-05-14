import mongoose, { Document, Schema, Types } from "mongoose";
import { ref } from "process";

export interface Crop extends Document {
  name: string;
  latinName: string;
  goodCompanions: Types.ObjectId[];
  badCompanions: Types.ObjectId[];
  plantingMonth: string;
  watering: {
    frequency: string,
    amount: number
  }
}

const cropSchema = new Schema<Crop>(
  {
    name: { type: String, required: true, unique: true },
    latinName: { type: String, required: true, unique: true },
    goodCompanions: [{
      type: Schema.Types.ObjectId,
      ref: 'Crop'
    }],
    badCompanions: [{
      type: Schema.Types.ObjectId,
      ref: 'Crop'
    }],
    plantingMonth: {type: String, required: true},
    watering: {
      frequency: {type: String, required: true},
      amount: {type: Number, required: true},
    }
  },
  { timestamps: true }
);

const CropModel = mongoose.model<Crop>("Crop", cropSchema);

export default CropModel;
