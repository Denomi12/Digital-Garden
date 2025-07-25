import express from "express";
const router = express.Router();
import openAIController from "../controllers/openAIController";
import { requireAuth } from "../middleware/authMiddleware";

/*
 * POST
 */
router.post("/chat", openAIController.generateChat);
router.post("/", openAIController.generateCrop);


export default router;
