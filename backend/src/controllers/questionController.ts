import { Request, Response, NextFunction } from "express";
import Question from "../models/QuestionModel";

const list = async (req: Request, res: Response): Promise<void> => {
  try {
    const questions = await Question.find();
    res.json(questions);
  } catch (error) {
    res.status(500).json({ message: "Error when getting questions", error });
  }
};

const show = async (req: Request, res: Response): Promise<void> => {
  try {
    const question = await Question.findById(req.params.id).populate("owner");
    if (!question) {
      res.status(404).json({ message: "No such question" });
      return;
    }
    res.json(question);
  } catch (error) {
    res.status(500).json({ message: "Error when getting question", error });
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

    const { title, questionMessage } = req.body;

    if (!title) {
      res.status(400).json({ message: "Title is required" });
      return;
    }

    if (!questionMessage) {
      res.status(400).json({ message: "Question is required" });
      return;
    }

    const question = new Question({
      title,
      question: questionMessage,
      likes: 0,
      likedBy: [],
      dislikedBy: [],
      owner,
    });

    const savedQuestion = await question.save();
    res.status(201).json(savedQuestion);
  } catch (error) {
    // console.error(error);
    res.status(500).json({ message: "Error when creating crop", error });
  }
};

export default {
  list,
  create,
  show,
};
