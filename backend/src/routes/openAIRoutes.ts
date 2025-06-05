import express from "express";
const router = express.Router();
import openAIController from "../controllers/openAIController";
import { requireAuth } from "../middleware/authMiddleware";

/*
 * POST
 */
router.post("/", requireAuth, openAIController.generateCrop);

export default router;
