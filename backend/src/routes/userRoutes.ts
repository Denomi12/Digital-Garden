import express from "express";
const router = express.Router();
import userController from "../controllers/userController";
import { requireAuth } from "../middleware/authMiddleware";

/*
 * GET
 */
router.get("/", userController.list);
router.get("/me", requireAuth, userController.me);

/*
 * GET
 */
router.get("/:id", userController.show);

/*
 * POST
 */
router.post("/", userController.create);
router.post("/login", userController.login)
router.post("/logout", userController.logout)


/*
 * PUT
 */
router.put("/:id", userController.update);

/*
 * DELETE
 */
router.delete("/:id", userController.remove);

export default router;
