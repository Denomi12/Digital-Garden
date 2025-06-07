import mongoose, { Document, Model, Schema, Types } from "mongoose";
import { UserInstance } from "./UserModel";
import { CommentInstance } from "./CommentModel";
import { Tile, tileSchema } from "./TileModel";

export interface QuestionInstance extends Document {
  title: string;
  question: string;
  likes: number;
  likedBy: UserInstance[];
  dislikedBy: UserInstance[];
  owner: UserInstance;
  createdAt: Date;
  comments: Types.ObjectId[];
  botGenerated: Boolean;
}

var questionSchema = new Schema<QuestionInstance>(
  {
    title: { type: String },
    question: { type: String },
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
    comments: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: "comment",
      },
    ],
    botGenerated: { type: Boolean, default: false },
  },
  { timestamps: true }
);

const Question = mongoose.model<QuestionInstance>("question", questionSchema);

export default Question;
