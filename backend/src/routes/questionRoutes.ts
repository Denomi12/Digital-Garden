import express from "express";
const router = express.Router();
import questionController from "../controllers/questionController";
import { requireAuth } from "../middleware/authMiddleware";

/*
 * GET
 */
router.get("/", questionController.list);
router.get("/hotQuestion", questionController.hotQuestion);

/*
 * GET
 */
router.get("/:id", questionController.show);

/*
 * POST
 */
router.post("/", requireAuth, questionController.create);
router.post("/like/:id", requireAuth, questionController.handleLike);
router.post("/dislike/:id", requireAuth, questionController.handleDislike);

export default router;
