import mongoose, { CallbackError } from "mongoose";
import bcrypt from "bcrypt";

const userSchema = new mongoose.Schema(
  {
    username: { type: String, required: true },
    password: { type: String, required: true },
    email: { type: String, required: true, unique: true },
  },
  { timestamps: true }
);

userSchema.pre("save", async function (next) {
  if (!this.isModified("password")) return next();

  try {
    const salt = await bcrypt.genSalt(10);
    this.password = await bcrypt.hash(this.password, salt);
    next();
  } catch (err) {
    next(err as any);
  }
});

userSchema.methods.matchPassword = async function (enteredPassword: string) {
  return await bcrypt.compare(enteredPassword, this.password);
};

userSchema.statics.authenticate = async function (
  username: string,
  password: string
) {
  const user = await User.findOne({ username: username });
  if (!user) return null;

  const passwordMatch = await bcrypt.compare(password, user.password);
  return passwordMatch ? user : null;
};

const User = mongoose.model("User", userSchema);

export default User;
