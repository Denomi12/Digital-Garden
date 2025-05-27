import { Request, Response, NextFunction } from "express";
import Garden from "../models/GardenModel";

const list = async (req: Request, res: Response): Promise<void> => {
  try {
    const gardens = await Garden.find();
    res.json(gardens);
  } catch (error) {
    res.status(500).json({ message: "Error when getting gardens", error });
  }
};

const listByOwner = async (req: Request, res: Response): Promise<void> => {
  try {
    const ownerId = req.params.ownerId;

    if (!ownerId) {
      res.status(400).json({ message: "Missing owner ID" });
      return;
    }

    const gardens = await Garden.find({ owner: ownerId })
      .populate("owner")
      .populate({
        path: "elements.crop",
      });
    res.json(gardens);
  } catch (error) {
    res.status(500).json({ message: "Error when getting gardens", error });
  }
};

const show = async (req: Request, res: Response): Promise<void> => {
  try {
    const garden = await Garden.findById(req.params.id).populate(
      "elements.crop"
    );
    if (!garden) {
      res.status(404).json({ message: "No such garden" });
      return;
    }
    res.json(garden);
  } catch (error) {
    res.status(500).json({ message: "Error when getting garden", error });
  }
};

const create = async (req: Request, res: Response): Promise<void> => {
  try {
    const owner = res.locals.user?.id;

    if (!owner) {
      res.status(400).json({ message: "Invalid garden owner" });
      return;
    }

    if (!req.body) {
      res.status(400).json({ message: "Request body is missing" });
      return;
    }

    const { name, width, height, location, latitude, longitude, elements } =
      req.body;

    if (!name) {
      res.status(400).json({ message: "Name is required" });
      return;
    }

    const existingGarden = await Garden.findOne({ name });
    if (existingGarden) {
      res.status(400).json({ message: "Garden with this name already exists" });
      return;
    }

    const garden = new Garden({
      name,
      owner,
      width,
      height,
      location,
      latitude,
      longitude,
      elements,
    });
    const savedGarden = await garden.save();

    res.status(201).json(savedGarden);
  } catch (error) {
    // console.error(error);
    res.status(500).json({ message: "Error when creating garden", error });
  }
};

export default {
  list,
  show,
  create,
  listByOwner,
};
