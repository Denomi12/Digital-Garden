import express from "express";
const router = express.Router();
import commentController from "../controllers/commentController";
import { requireAuth } from "../middleware/authMiddleware";

router.get("/:id", commentController.show);

/*
 * POST
 */
router.post("/:id", requireAuth, commentController.create);
router.post("/like/:id", requireAuth, commentController.handleLike);
router.post("/dislike/:id", requireAuth, commentController.handleDislike);

export default router;
