import { Request, Response, NextFunction } from "express";
import Crop from "../models/CropModel";

const list = async (req: Request, res: Response): Promise<void> => {
  try {
    const crops = await Crop.find().populate([
      {
        path: "goodCompanions",
        select: "name latinName",
      },
      {
        path: "badCompanions",
        select: "name latinName",
      },
    ]);
    res.json(crops);
  } catch (error) {
    res.status(500).json({ message: "Error when getting crops", error });
  }
};

const create = async (req: Request, res: Response): Promise<void> => {
  try {
    const {
      name,
      latinName,
      goodCompanions,
      badCompanions,
      plantingMonth,
      watering,
      imageSrc,
    } = req.body;

    if (goodCompanions && goodCompanions > 0) {
      const existingGoodCompanions = await Crop.find({
        _id: { $in: goodCompanions },
      });
      if (existingGoodCompanions.length !== goodCompanions.length) {
        res
          .status(400)
          .json({ message: "One or more good companions not found" });
        return;
      }
    }

    if (badCompanions && badCompanions.length > 0) {
      const existingBadCompanions = await Crop.find({
        _id: { $in: badCompanions },
      });
      if (existingBadCompanions.length !== badCompanions.length) {
        res
          .status(400)
          .json({ message: "One or more bad companions not found" });
        return;
      }
    }

    const existingCrop = await Crop.findOne({ name });
    if (existingCrop) {
      res.status(400).json({ message: "Crop with this name already exists" });
      return;
    }

    const crop = new Crop({
      name,
      latinName,
      goodCompanions,
      badCompanions,
      plantingMonth,
      watering,
      imageSrc,
    });
    const savedCrop = await crop.save();

    res.status(201).json(savedCrop);
  } catch (error) {
    // console.error(error);
    res.status(500).json({ message: "Error when creating crop", error });
  }
};

const update = async (req: Request, res: Response): Promise<void> => {
  try {
    const { id } = req.params;
    const {
      name,
      latinName,
      goodCompanions,
      badCompanions,
      plantingMonth,
      watering,
      imageSrc,
    } = req.body;

    if (goodCompanions && goodCompanions.length > 0) {
      const existingGoodCompanions = await Crop.find({
        _id: { $in: goodCompanions },
      });
      if (existingGoodCompanions.length !== goodCompanions.length) {
        res
          .status(400)
          .json({ message: "One or more good companions not found" });
        return;
      }
    }

    if (badCompanions && badCompanions.length > 0) {
      const existingBadCompanions = await Crop.find({
        _id: { $in: badCompanions },
      });
      if (existingBadCompanions.length !== badCompanions.length) {
        res
          .status(400)
          .json({ message: "One or more bad companions not found" });
        return;
      }
    }

    if (name) {
      const existingCrop = await Crop.findOne({ name, _id: { $ne: id } });
      if (existingCrop) {
        res.status(400).json({ message: "Crop with this name already exists" });
        return;
      }
    }

    const updatedCrop = await Crop.findByIdAndUpdate(
      id,
      {
        $set: {
          name,
          latinName,
          goodCompanions,
          badCompanions,
          plantingMonth,
          watering,
          imageSrc,
        },
      },
      { new: true, runValidators: true }
    ).populate([
      {
        path: "goodCompanions",
        select: "name latinName",
      },
      {
        path: "badCompanions",
        select: "name latinName",
      },
    ]);

    if (!updatedCrop) {
      res.status(404).json({ message: "Crop not found" });
      return;
    }

    res.json(updatedCrop);
  } catch (error) {
    res.status(500).json({ message: "Error when updating crop", error });
  }
};


const createMany = async (req: Request, res: Response): Promise<void> => {
  try {
    const crops = req.body;

    if (!Array.isArray(crops) || crops.length === 0) {
      res.status(400).json({ message: "Invalid or empty crop list" });
      return;
    }

    for (const crop of crops) {
      const {
        name,
        latinName,
        goodCompanions,
        badCompanions,
        plantingMonth,
        watering,
        imageSrc,
      } = crop;

      const existingCrop = await Crop.findOne({ name });
      if (existingCrop) {
        res
          .status(400)
          .json({ message: `Crop with name "${name}" already exists` });
        return;
      }

      const validateIds = async (ids: string[], type: string) => {
        if (ids && ids.length > 0) {
          const found = await Crop.find({ _id: { $in: ids } });
          if (found.length !== ids.length) {
            throw new Error(`One or more ${type} companions not found`);
          }
        }
      };

      await validateIds(goodCompanions, "good");
      await validateIds(badCompanions, "bad");
    }

    const saved = await Crop.insertMany(crops);
    res.status(201).json({ success: true, crops: saved });
  } catch (error) {
    res.status(500).json({ message: "Failed to create crops", error });
  }
};



export default {
  list,
  create,
  update,
  createMany
};
