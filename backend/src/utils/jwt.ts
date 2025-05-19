import jwt from "jsonwebtoken";

interface jwtUserPayload {
  id: string;
  username: string;
  email: string;
}

export const generateToken = (payload: jwtUserPayload): string => {
  const JWT_SECRET = process.env.JWT_SECRET;

  if (!JWT_SECRET) {
    throw new Error("JWT_SECRET is not defined");
  }

  return jwt.sign(payload, JWT_SECRET, { expiresIn: '1d' });
};
