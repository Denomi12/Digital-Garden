// controllers/userController.ts
import { Request, Response, NextFunction } from "express";
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

const login = async (
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const { username, password } = req.body;
    const user = await User.authenticate(username, password);

    if (!user) {
      const error = new Error("Wrong username or password");

      error.status = 401;
      return next(error);
    }

    req.session.userId = user._id;
    res.json({ message: "Successfully logged in", user });
  } catch (error) {
    next(error);
  }
};

const logout = (req: Request, res: Response, next: NextFunction): void => {
  if (req.session) {
    req.session.destroy((err) => {
      if (err) {
        return next(err);
      } else {
        res.status(200).json({ message: "Successfully logged out" });
      }
    });
  } else {
    res.status(400).json({ message: "No active session" });
  }
};

export default {
  list,
  show,
  create,
  update,
  remove,
  login,
  logout,
};
