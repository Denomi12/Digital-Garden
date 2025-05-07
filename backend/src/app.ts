import express from "express";
import { connectDB } from "./utils/db";
import userRoutes from "./routes/userRoutes"; // Adjust path as needed
// import { env } from "process";
// import dotenv from "dotenv";
// dotenv.config();

const app = express();

app.use(express.json()); // Parse application/json
app.use(express.urlencoded({ extended: true })); // Parse URL-encoded (for forms)

// ROUTERS
app.use("/user", userRoutes);

app.get("/", (req, res) => {
  res.json({ message: "API is running" });
});

connectDB();

// var session = require("express-session");
// var MongoStore = require("connect-mongo");
// app.use(
//   session({
//     secret: "work hard",
//     resave: true,
//     saveUninitialized: false,
//     store: MongoStore.create({ mongoUrl: env.MONGO_URI }),
//   })
// );

export default app;
