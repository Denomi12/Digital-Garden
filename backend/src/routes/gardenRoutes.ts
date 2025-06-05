import express from "express";
const router = express.Router();
import gardenController from "../controllers/gardenController";
import { requireAuth } from "../middleware/authMiddleware";

/*
 * GET
 */
router.get("/", gardenController.list);
router.get("/ownedBy/:ownerId", gardenController.listByOwner);

/*
 * GET
 */
router.get("/:id", requireAuth, gardenController.show);

/*
 * POST
 */
router.post("/", requireAuth, gardenController.create);

/*
 * PUT
 */
// router.put("/:id", requireAuth, gardenController.update);
router.put("/:id", gardenController.update);

export default router;
