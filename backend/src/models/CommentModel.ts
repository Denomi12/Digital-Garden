import mongoose, { Document, Model, Schema, Types } from "mongoose";
import { UserInstance } from "./UserModel";
import { Tile, tileSchema } from "./TileModel";

export interface CommentInstance extends Document {
  body: string;
  likes: number;
  likedBy: UserInstance[];
  dislikedBy: UserInstance[];
  owner: Types.ObjectId | UserInstance;
  createdAt: Date;
}

var commentSchema = new Schema<CommentInstance>(
  {
    body: { type: String },
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

const Comment = mongoose.model<CommentInstance>("comment", commentSchema);

export default Comment;
