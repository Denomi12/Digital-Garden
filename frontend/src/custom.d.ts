// custom.d.ts
import { Session } from "express-session";

declare global {
  namespace Express {
    interface Session {
      userId?: string; // or `userId: string` if it's always a string
    }
  }
}
