import jwt from "jsonwebtoken";

interface jwtUserPayload {
  id: string;
  username: string;
}

export const generateToken = (payload: jwtUserPayload): string => {
  const JWT_SECRET = process.env.JWT_SECRET;
  const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN ?? 1;

  if (!JWT_SECRET) {
    throw new Error("JWT_SECRET is not defined");
  }

  return jwt.sign(payload, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN as number });
};
