import numpy as np


class GardenUtils:

    class Grid:
        def __init__(self, image: np.ndarray, rows: int = -1, cols: int = -1):
            self.image = image
            self.rows = rows
            self.cols = cols
            self.grid: np.ndarray = GardenUtils.divide_grid(self.image, self.rows, self.cols)


    @staticmethod
    def divide_grid(image: np.ndarray, rows: int = -1, cols: int = -1) -> np.ndarray:
        """
        Divide an image into a grid of smaller images.

        Parameters:
            image (np.ndarray): Input image (2D grayscale or 3D color).
            rows (int): Number of rows in the grid. If -1, compute roughly square.
            cols (int): Number of columns in the grid. If -1, compute roughly square.

        Returns:
            np.ndarray: Array of images with shape (rows*cols,) containing sub-images.
        """

        H, W = image.shape[:2]

        # Auto compute rows/cols if not provided
        if rows <= 0 and cols <= 0:
            cols = int(np.ceil(np.sqrt(H * W / max(H, W))))  # rough square
            rows = int(np.ceil(H / (W / cols)))
        elif rows <= 0:
            rows = int(np.ceil(H / (W / cols)))
        elif cols <= 0:
            cols = int(np.ceil(W / (H / rows)))

        # Compute each cell size
        cell_h = H // rows
        cell_w = W // cols

        # Store sub-images
        sub_images = []

        for r in range(rows):
            for c in range(cols):
                start_h = r * cell_h
                end_h = (r + 1) * cell_h
                start_w = c * cell_w
                end_w = (c + 1) * cell_w

                sub_img = image[start_h:end_h, start_w:end_w]
                sub_images.append(sub_img)


        grid = np.array(sub_images)
        return grid


