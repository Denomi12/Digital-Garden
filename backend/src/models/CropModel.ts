import mongoose, { Document, Schema } from "mongoose";

export interface Crop extends Document {
  name: string;
  goodCompanions: string[];
  plantingMonth: string;
}

const cropSchema = new Schema<Crop>(
  {
    name: { type: String, required: true, unique: true },
    goodCompanions: [{type: String}],
    plantingMonth: {type: String, required: true},
  },
  { timestamps: true }
);

const CropModel = mongoose.model<Crop>("Crop", cropSchema);

export default CropModel;
