import { Request, Response, NextFunction } from "express";
import jwt from "jsonwebtoken";


export function requireAuth(req: Request, res: Response, next: NextFunction) {
  const token = req.cookies.token;  
  if (!token) {
    res.status(401).json({ message: "Unauthorized: No token" });
    return;
}
  try {
    const decoded =jwt.verify(token, process.env.JWT_SECRET!); // throws if invalid
    res.locals.user = decoded;
    next();
  } catch (err) {
    // console.error(err)
    res.status(403).json({ message: "Forbidden: Invalid token" });

  }
}
