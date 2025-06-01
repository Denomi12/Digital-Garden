import { Request, Response, NextFunction } from "express";
import Question from "../models/QuestionModel";

const list = async (req: Request, res: Response): Promise<void> => {
  try {
    const questions = await Question.find()
      .populate("owner", "username")
      .populate("comments")
      .sort({ createdAt: -1 });
    res.json(questions);
  } catch (error) {
    res.status(500).json({ message: "Error when getting questions", error });
  }
};

const show = async (req: Request, res: Response): Promise<void> => {
  try {
    const question = await Question.findById(req.params.id).populate(
      "owner",
      "username"
    );
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
      res.status(400).json({ message: "Invalid question owner" });
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
      res.status(400).json({ message: "Question body is required" });
      return;
    }

    const question = new Question({
      title: title,
      question: questionMessage,
      likes: 0,
      likedBy: [],
      dislikedBy: [],
      owner: owner,
      createdAt: new Date(),
    });

    const savedQuestion = await question.save();
    res.status(201).json(savedQuestion);
  } catch (error) {
    res.status(500).json({ message: "Error when creating question", error });
  }
};

const handleLike = async (req: Request, res: Response): Promise<void> => {
  try {
    const userId = res.locals.user?.id;

    if (!userId) {
      res.status(401).json({ message: "Unauthorized" });
      return;
    }

    const question = await Question.findById(req.params.id).populate(
      "owner",
      "username"
    );

    if (!question) {
      res.status(404).json({ message: "Question not found" });
      return;
    }

    if (question.likedBy.includes(userId)) {
      question.likes -= 1;
      question.likedBy = question.likedBy.filter(
        (id) => id.toString() !== userId
      );
      const updatedQuestion = await question.save();
      res.status(200).json(updatedQuestion);
      return;
    }

    if (question.dislikedBy.includes(userId)) {
      question.likes += 1;
      question.dislikedBy = question.dislikedBy.filter(
        (id) => id.toString() !== userId
      );
    }

    question.likedBy.push(userId);
    question.likes += 1;

    const updatedQuestion = await question.save();
    res.status(200).json(updatedQuestion);
  } catch (error) {
    res.status(500).json({ message: "Error when liking question", error });
  }
};

const handleDislike = async (req: Request, res: Response): Promise<void> => {
  try {
    const userId = res.locals.user?.id;

    if (!userId) {
      res.status(401).json({ message: "Unauthorized" });
      return;
    }

    const question = await Question.findById(req.params.id).populate(
      "owner",
      "username"
    );

    if (!question) {
      res.status(404).json({ message: "Question not found" });
      return;
    }

    if (question.dislikedBy.includes(userId)) {
      question.likes += 1;
      question.dislikedBy = question.dislikedBy.filter(
        (id) => id.toString() !== userId
      );
      const updatedQuestion = await question.save();
      res.status(200).json(updatedQuestion);
      return;
    }

    if (question.likedBy.includes(userId)) {
      question.likes -= 1;
      question.likedBy = question.likedBy.filter(
        (id) => id.toString() !== userId
      );
    }

    question.dislikedBy.push(userId);
    question.likes -= 1;

    const updatedQuestion = await question.save();
    res.status(200).json(updatedQuestion);
  } catch (error) {
    res.status(500).json({ message: "Error when liking question", error });
  }
};

export default {
  list,
  create,
  show,
  handleLike,
  handleDislike,
};
