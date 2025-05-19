import express from "express";
const router = express.Router();
import questionController from "../controllers/questionController";

/*
 * GET
 */
router.get("/", questionController.list);

/*
 * GET
 */
router.get("/:id", questionController.show);

/*
 * POST
 */
router.post("/", questionController.create);

export default router;
