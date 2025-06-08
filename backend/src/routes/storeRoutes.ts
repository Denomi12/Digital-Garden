import express from "express";
const router = express.Router();
import storeController from "../controllers/storeController";

/*
 * GET
 */
router.get("/", storeController.list);

/*
 * POST
 */
router.post("/", storeController.create);


export default router;
