import { Request, Response, NextFunction } from "express";
import { Types } from "mongoose";
import Comment, { CommentInstance } from "../models/CommentModel";
import Question from "../models/QuestionModel";

const show = async (req: Request, res: Response): Promise<void> => {
  try {
    const comment = await Comment.findById(req.params.id).populate(
      "owner",
      "username"
    );
    if (!comment) {
      res.status(404).json({ message: "No such comment99" });
      return;
    }
    res.json(comment);
  } catch (error) {
    res.status(500).json({ message: "Error when getting comment", error });
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

    const { body } = req.body;

    if (!body) {
      res.status(400).json({ message: "Comment body is required" });
      return;
    }

    const comment = new Comment({
      body: body,
      likes: 0,
      likedBy: [],
      dislikedBy: [],
      owner: owner,
      createdAt: new Date(),
    });

    const savedComment = (await comment.save()) as CommentInstance & {
      _id: Types.ObjectId;
    };

    const question = await Question.findById(req.params.id);

    if (!question) {
      res.status(404).json({ message: "Question not found" });
      return;
    }

    question.comments.push(savedComment._id);

    await question.save();

    // await comment.populate("owner", "username");

    res.status(201).json(savedComment);
  } catch (error: any) {
    res.status(500).json({
      message: "Error when creating comment",
      error: error.message || error,
    });
  }
};

const handleLike = async (req: Request, res: Response): Promise<void> => {
  try {
    const userId = res.locals.user?.id;

    if (!userId) {
      res.status(401).json({ message: "Unauthorized" });
      return;
    }

    const comment = await Comment.findById(req.params.id).populate(
      "owner",
      "username"
    );

    if (!comment) {
      res.status(404).json({ message: "Comment not found" });
      return;
    }

    if (comment.likedBy.includes(userId)) {
      comment.likes -= 1;
      comment.likedBy = comment.likedBy.filter(
        (id) => id.toString() !== userId
      );
      const updatedComment = await comment.save();
      res.status(200).json(updatedComment);
      return;
    }

    if (comment.dislikedBy.includes(userId)) {
      comment.likes += 1;
      comment.dislikedBy = comment.dislikedBy.filter(
        (id) => id.toString() !== userId
      );
    }

    comment.likedBy.push(userId);
    comment.likes += 1;

    const updatedComment = await comment.save();
    res.status(200).json(updatedComment);
  } catch (error) {
    res.status(500).json({ message: "Error when liking comment", error });
  }
};

const handleDislike = async (req: Request, res: Response): Promise<void> => {
  try {
    const userId = res.locals.user?.id;

    if (!userId) {
      res.status(401).json({ message: "Unauthorized" });
      return;
    }

    const comment = await Comment.findById(req.params.id).populate(
      "owner",
      "username"
    );

    if (!comment) {
      res.status(404).json({ message: "Comment not found" });
      return;
    }

    if (comment.dislikedBy.includes(userId)) {
      comment.likes += 1;
      comment.dislikedBy = comment.dislikedBy.filter(
        (id) => id.toString() !== userId
      );
      const updatedComment = await comment.save();
      res.status(200).json(updatedComment);
      return;
    }

    if (comment.likedBy.includes(userId)) {
      comment.likes -= 1;
      comment.likedBy = comment.likedBy.filter(
        (id) => id.toString() !== userId
      );
    }

    comment.dislikedBy.push(userId);
    comment.likes -= 1;

    const updatedComment = await comment.save();
    res.status(200).json(updatedComment);
  } catch (error) {
    res.status(500).json({ message: "Error when liking comment", error });
  }
};

export default {
  show,
  create,
  handleLike,
  handleDislike,
};
