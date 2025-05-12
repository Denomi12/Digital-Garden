import express from "express";
import cors from "cors";
import cookieParser from "cookie-parser";
import { connectDB } from "./utils/db";
import userRoutes from "./routes/userRoutes"; // Adjust path as needed
import gardenRoutes from "./routes/gardenRoutes";
// import { env } from "process";
// import dotenv from "dotenv";
// dotenv.config();

const app = express();

app.use(cors({
  origin: process.env.FRONTEND_URL || "http://localhost:5173", // frontend origin
  credentials: true, // allow cookies and headers
}));

app.use(cookieParser()); // Parse cookies
app.use(express.json()); // Parse application/json
app.use(express.urlencoded({ extended: true })); // Parse URL-encoded (for forms)

// ROUTERS
app.use("/user", userRoutes);
app.use("/garden", gardenRoutes);

app.get("/", (req, res) => {
  res.json({ message: "API is running" });
});

// app.get('/check-token', (req, res) => {
//   const token = req.cookies.token;
//   if (token) {
//     res.json({ message: 'Token received', token });
//   } else {
//     res.status(401).json({ message: 'No token found' });
//   }
// });


connectDB();

export default app;
