import mongoose, { Document, Model, Schema } from "mongoose";
import bcrypt from "bcrypt";

// 1. Vmesnik za instanco uporabnika (dokument)
export interface UserInstance extends Document {
  username: string;
  password: string;
  email: string;
  matchPassword(enteredPassword: string): Promise<boolean>;
}

// 2. Vmesnik za model, kjer definiramo tudi `authenticate`
export interface UserModel extends Model<UserInstance> {
  authenticate(username: string, password: string): Promise<UserInstance | null>;
}

// 3. Shema
const userSchema = new Schema<UserInstance>(
  {
    username: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    email: { type: String, required: true, unique: true },
  },
  { timestamps: true }
);

// 4. Pre-save hook za hashanje gesla
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

// 5. Metoda za preverjanje gesla
userSchema.methods.matchPassword = async function (enteredPassword: string) {
  return await bcrypt.compare(enteredPassword, this.password);
};

// 6. Statika za prijavo
userSchema.statics.authenticate = async function (
  username: string,
  password: string
) {
  const user = await this.findOne({ username });
  if (!user) return null;

  const passwordMatch = await bcrypt.compare(password, user.password);
  return passwordMatch ? user : null;
};

// 7. Model
const User = mongoose.model<UserInstance, UserModel>("User", userSchema);

export default User;
