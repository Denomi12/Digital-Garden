import mongoose, { Document, Schema } from "mongoose";

export interface Crop extends Document {
  name: string;
}

const cropSchema = new Schema<Crop>(
  {
    name: { type: String, required: true, unique: true },
  },
  { timestamps: true }
);

const CropModel = mongoose.model<Crop>("Crop", cropSchema);

export default CropModel;
