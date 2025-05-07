import express from "express";
const router = express.Router();
import userController from "../controllers/userController";

/*
 * GET
 */
router.get("/", userController.list);

/*
 * GET
 */
router.get("/:id", userController.show);
router.get("/logout", userController.logout);

/*
 * POST
 */
router.post("/", userController.create);
router.post("/login", userController.login);

/*
 * PUT
 */
router.put("/:id", userController.update);

/*
 * DELETE
 */
router.delete("/:id", userController.remove);

export default router;
