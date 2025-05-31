import express from "express";
const router = express.Router();
import cropController from "../controllers/cropController";


/*
 * GET
 */
router.get("/", cropController.list);

/*
 * POST
 */
router.post("/", cropController.create);

router.put("/:id", cropController.update);

export default router;