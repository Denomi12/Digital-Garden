import math

import numpy as np
from matplotlib import pyplot as plt

from .imageUtils import is_gray, is_color

class Grid:
    def __init__(self, image: np.ndarray, rows: int = -1, cols: int = -1):
        self.image = image
        self.rows = rows
        self.cols = cols
        self.grid: np.ndarray = divide_grid(self.image, self.rows, self.cols)

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


def display_images(*images, cmap="gray"):
    """
    Displays multiple images using Matplotlib.
    Automatically arranges them into an optimal grid.
    Handles both color and grayscale images.
    """

    N = len(images)
    if N == 0:
        return

    # Compute a roughly square layout
    cols = math.ceil(math.sqrt(N))
    rows = math.ceil(N / cols)

    # Adjust figure size dynamically
    plt.figure(figsize=(6 * cols, 6 * rows))

    for i, img in enumerate(images):
        plt.subplot(rows, cols, i + 1)

        # Detect grayscale vs color
        if is_gray(img):
            plt.imshow(img, cmap='gray')
        else:
            plt.imshow(img)

    plt.axis('off')  # Hide axes for a cleaner view

    plt.tight_layout()
    plt.show()


def display_grid(grid: Grid):
    rows = grid.rows
    cols = grid.cols
    plt.figure(figsize=(6 * cols, 6 * rows))

    for i, img in enumerate(grid.grid):
        plt.subplot(rows, cols, i + 1)

        # Detect grayscale vs color
        if is_gray(img):
            plt.imshow(img, cmap='gray')
        else:
            plt.imshow(img)

    plt.axis('off')  # Hide axes for a cleaner view

    plt.tight_layout()
    plt.show()


def display_grid_with_gaps(grid, gap=5, gap_value=0):
    """
    Display a grid of images with gaps between cells.

    gap: number of pixels between tiles
    gap_value: pixel value for gaps (0 = black, 255 = white)
    """

    rows, cols = grid.rows, grid.cols
    tiles = grid.grid

    tile_h, tile_w = tiles[0].shape[:2]
    is_color = tiles[0].ndim == 3
    channels = tiles[0].shape[2] if is_color else None

    H = rows * tile_h + (rows - 1) * gap
    W = cols * tile_w + (cols - 1) * gap

    if is_color:
        canvas = np.full((H, W, channels), gap_value, dtype=tiles[0].dtype)
    else:
        canvas = np.full((H, W), gap_value, dtype=tiles[0].dtype)

    for idx, tile in enumerate(tiles):
        r = idx // cols
        c = idx % cols

        y = r * (tile_h + gap)
        x = c * (tile_w + gap)

        canvas[y:y + tile_h, x:x + tile_w] = tile

    plt.figure(figsize=(6 * cols, 6 * rows))
    plt.imshow(canvas, cmap='gray' if not is_color else None)
    plt.axis("off")
    plt.show()


def show_color_histogram(image: np.ndarray, bins: int = 256):
    """
    Display color histogram of an image.
    Supports grayscale and RGB images.
    """

    plt.figure(figsize=(8, 4))

    # Grayscale image
    if is_gray(image):
        plt.hist(image.ravel(), bins=bins, range=(0, 256))
        plt.title("Grayscale Histogram")
        plt.xlabel("Intensity")
        plt.ylabel("Pixel count")

    # Color image (RGB)
    elif is_color(image):
        colors = ['r', 'g', 'b']
        labels = ['Red', 'Green', 'Blue']

        for i, (color, label) in enumerate(zip(colors, labels)):
            plt.hist(
                image[:, :, i].ravel(),
                bins=bins,
                range=(0, 256),
                alpha=0.6,
                label=label
            )

        plt.title("RGB Color Histogram")
        plt.xlabel("Intensity")
        plt.ylabel("Pixel count")
        plt.legend()

    else:
        raise ValueError("Unsupported image format")

    plt.tight_layout()
    plt.show()
