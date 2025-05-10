import express from "express";
import cors from "cors";
import { connectDB } from "./utils/db";
import userRoutes from "./routes/userRoutes"; // Adjust path as needed
// import { env } from "process";
// import dotenv from "dotenv";
// dotenv.config();

const app = express();

app.use(cors({
  origin: process.env.FRONTEND_URL || "http://localhost:5173", // your frontend origin
  credentials: true, // allow cookies and headers
}));

app.use(express.json()); // Parse application/json
app.use(express.urlencoded({ extended: true })); // Parse URL-encoded (for forms)

// ROUTERS
app.use("/user", userRoutes);

app.get("/", (req, res) => {
  res.json({ message: "API is running" });
});

connectDB();

export default app;
