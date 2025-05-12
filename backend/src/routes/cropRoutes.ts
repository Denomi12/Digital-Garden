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

export default router;