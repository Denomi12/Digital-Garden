// controllers/userController.ts
import { Request, Response } from "express";
import User from "../models/User";

const list = async (req: Request, res: Response): Promise<void> => {
  try {
    const users = await User.find();
    res.json(users);
  } catch (error) {
    res.status(500).json({ message: "Error when getting users", error });
  }
};

const show = async (req: Request, res: Response): Promise<void> => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) {
      res.status(404).json({ message: "No such user" });
      return;
    }
    res.json(user);
  } catch (error) {
    res.status(500).json({ message: "Error when getting user", error });
  }
};

const create = async (req: Request, res: Response): Promise<void> => {
  try {
    const { username, password, email } = req.body;
    const user = new User({ username, password, email });
    const savedUser = await user.save();
    res.status(201).json(savedUser);
  } catch (error) {
    res.status(500).json({ message: "Error when creating user", error });
  }
};

const update = async (req: Request, res: Response): Promise<void> => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) {
      res.status(404).json({ message: "No such user" });
      return;
    }

    user.username = req.body.username ?? user.username;
    user.password = req.body.password ?? user.password;
    user.email = req.body.email ?? user.email;

    const updatedUser = await user.save();
    res.json(updatedUser);
  } catch (error) {
    res.status(500).json({ message: "Error when updating user", error });
  }
};

const remove = async (req: Request, res: Response): Promise<void> => {
  try {
    await User.findByIdAndDelete(req.params.id);
    res.status(204).send();
  } catch (error) {
    res.status(500).json({ message: "Error when deleting the user", error });
  }
};

export default {
  list,
  show,
  create,
  update,
  remove,
};
