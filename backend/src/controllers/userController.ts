// controllers/userController.ts
import { Request, Response, NextFunction } from "express";
import jwt from "jsonwebtoken";
import User from "../models/User";
import { generateToken } from "../utils/jwt";

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

    const existingUser = await User.findOne({ $or: [{ username }, { email }] });
    if (existingUser) {
      res
        .status(400)
        .json({ message: "User with this username or email already exists" });
      return;
    }

    const user = new User({ username, password, email });
    const savedUser = await user.save();

    // ob uspešni prijavi generira JWT token, da se lahko uporabnik takoj prijavi na FE
    const token = generateToken({
      id: savedUser.id,
      username: savedUser.username,
    });
    const { password: _password, ...userWithoutPassword } =
      savedUser.toObject();
    res
      .cookie("token", token, {
        httpOnly: true,
        sameSite: "strict",
        maxAge: 24 * 60 * 60 * 1000, // 1 day
      })
      .status(201)
      .json({ user: userWithoutPassword });
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

const login = async (req: Request, res: Response): Promise<void> => {
  try {
    const { username, password } = req.body;

    if (!username || !password) {
      res.status(400).json({ message: "Username and password are required" });
      return;
    }

    const user = await User.authenticate(username, password);
    if (!user) {
      res.status(401).json({ message: "Invalid credentials" });
      return;
    }

    const token = generateToken({ id: user.id, username: user.username });
    const { password: _password, ...userWithoutPassword } = user.toObject();
    res
      .cookie("token", token, {
        httpOnly: true,
        sameSite: "strict",
        maxAge: 24 * 60 * 60 * 1000, // 1 day
      })
      .json({ user: userWithoutPassword });
  } catch (error) {
    console.error("Login error:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

const logout = async (req: Request, res: Response): Promise<void> => {
  res
    .clearCookie("token", {
      httpOnly: true,
      sameSite: "strict",
    })
    .status(200)
    .json({ message: "Logged out successfully" });
};

const me = async (req: Request, res: Response): Promise<void> => {
  // vrne dekodirane podatke iz JWT žetona
  res.status(200).json({ message: "You exist!", user: res.locals.user});
};

export default {
  list,
  show,
  create,
  update,
  remove,
  login,
  logout,
  me,
};
