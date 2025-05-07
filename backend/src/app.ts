import express from "express";
import { connectDB } from "./utils/db";
import userRoutes from "./routes/userRoutes"; // Adjust path as needed

const app = express();

app.use(express.json()); // Parse application/json
app.use(express.urlencoded({ extended: true })); // Parse URL-encoded (for forms)

// ROUTERS
app.use("/user", userRoutes);

app.get("/", (req, res) => {
  res.json({ message: "API is running" });
});

connectDB();

export default app;
