import mongoose, { Document, Schema } from "mongoose";

export interface StoreDocument extends Document {
  key: number;
  name: string;
  location: string;
  latitude: number;
  longitude: number;
}

const StoreSchema = new Schema<StoreDocument>({
  key: { type: Number, required: true, unique: true },
  name: { type: String, required: true },
  location: { type: String, required: true },
  latitude: { type: Number, required: true },
  longitude: { type: Number, required: true },
});

const Store = mongoose.model<StoreDocument>("Store", StoreSchema);




export default Store;
