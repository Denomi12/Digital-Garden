import { Request, Response, NextFunction } from "express";
import Crop from "../models/CropModel";

const list = async (req: Request, res: Response): Promise<void> => {
  try {
    const crops = await Crop.find();
    res.json(crops);
  } catch (error) {
    res.status(500).json({ message: "Error when getting crops", error });
  }
};

const create = async (req: Request, res: Response): Promise<void> => {
  try {
    const { name } = req.body;

    const existingCrop = await Crop.findOne({ name });
    if (existingCrop) {
      res.status(400).json({ message: "Crop with this name already exists" });
      return;
    }

    const crop = new Crop({ name });
    const savedCrop = await crop.save();

    res.status(201).json(savedCrop);
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: "Error when creating crop", error });
  }
};

export default {
  list,
  create,
};
