import { Request, Response, NextFunction } from "express";
import Store from "../models/StoreModel";


const list = async (req: Request, res: Response): Promise<void> => {
  try {
    const stores = await Store.find()
    res.json(stores);
  } catch (error) {
    res.status(500).json({ message: "Error when getting stores", error });
  }
};

// Create a store
const create = async (req: Request, res: Response) => {
  try {
    const { key, name, location, latitude, longitude } = req.body;
    const newStore = new Store({ key, name, location, latitude, longitude });
    await newStore.save();
    res.status(201).json(newStore);
  } catch (error) {
    res.status(500).json({ message: "Failed to create store", error });
  }
};

export default {
    list,
    create,
};
