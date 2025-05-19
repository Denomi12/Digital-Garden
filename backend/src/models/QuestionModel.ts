import mongoose, { Document, Model, Schema } from "mongoose";
import { UserInstance } from "./UserModel";
import { Tile, tileSchema } from "./TileModel";

export interface QuestionInstance extends Document {
  title: string;
  question: string;
  summary: string;
  likes: number;
  likedBy: UserInstance[];
  dislikedBy: UserInstance[];
  owner: UserInstance;
  //posted date?
}

var questionSchema = new Schema<QuestionInstance>(
  {
    title: { type: String },
    question: { type: String },
    summary: { type: String },
    likes: { type: Number },
    likedBy: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
      },
    ],
    dislikedBy: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
      },
    ],

    owner: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
  },
  { timestamps: true }
);

const Question = mongoose.model<QuestionInstance>("question", questionSchema);

export default Question;
