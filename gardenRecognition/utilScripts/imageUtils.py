import math

import cv2
import numpy as np
from matplotlib import pyplot as plt

from .gardenUtils import GardenUtils

"""
    Utility functions for loading, converting, and displaying images.
"""


def load_image(image_path: str, grayscale=False, loud=False) -> np.ndarray:
    """
    Loads an image from a file to a np.ndarray.
    Converts BGR to RGB if needed.
    """

    image = cv2.imread(image_path)
    if image is None:
        raise FileNotFoundError(f"Image not found at path: {image_path}")

    image_format = ""

    # Convert BGR to RGB
    if len(image.shape) == 3 and image.shape[2] == 3:
        image = image[:, :, (2, 1, 0)]
        image_format = "RGB"
        if grayscale:
            image = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
            image_format = "Grayscale"
    elif len(image.shape) == 2:
        image_format = "Grayscale"

    if loud:
        print(
            f"Loaded Image from path: {image_path}\n"
            f"Image shape: {image.shape}\n"
            f"Image dtype: {image.dtype}\n"
            f"Image format: {image_format}\n")
    return image


def to_grayscale(image: np.ndarray) -> np.ndarray:
    """Convert RGB image to grayscale."""
    if len(image.shape) == 3 and image.shape[2] == 3:
        image = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    return image


def is_gray(image: np.ndarray) -> bool:
    """Check if image is grayscale."""
    return len(image.shape) == 2 or (len(image.shape) == 3 and image.shape[2] == 1)


def is_color(image: np.ndarray) -> bool:
    return image.ndim == 3 and image.shape[2] == 3


def image_info(image: np.ndarray):
    """Print image information."""
    if len(image.shape) == 3 and image.shape[2] == 3:
        image_format = "RGB"
    elif len(image.shape) == 2:
        image_format = "Grayscale"
    else:
        image_format = "Unknown"

    print(
        f"Image shape: {image.shape}\n"
        f"Image dtype: {image.dtype}\n"
        f"Image format: {image_format}\n"
    )


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


def display_grid(grid: GardenUtils.Grid):
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


def sobel_edges(image: np.ndarray, ksize: int = 3):
    """
    Apply Sobel filter to an image.
    Returns Sobel X, Sobel Y, and gradient magnitude.
    """

    # Convert to grayscale if needed
    if is_color(image):
        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    else:
        gray = image

    edges = cv2.Sobel(gray, cv2.CV_8U, 1, 1)
    return edges


def gauss_blur(image: np.ndarray, ksize: int = 3):
    gauss = cv2.GaussianBlur(image, (ksize, ksize), 0)
    return gauss


def extract_garden(img: np.ndarray,
                   sat_low = 0,
                   sat_high = 40,
                   smoothing_factor = 0):

    mask, contour = _extract_garden_base(img, sat_low, sat_high, smoothing_factor)

    garden = cv2.bitwise_and(img, img, mask=mask)
    return garden, mask, contour


def _extract_garden_base(img, sat_low, sat_high, smoothing_factor):
    blur = cv2.GaussianBlur(img, (9, 9), 0)
    hsv = cv2.cvtColor(blur, cv2.COLOR_RGB2HSV)
    h, s, v = cv2.split(hsv)
    sat_mask = cv2.inRange(s, sat_low, sat_high)
    display_images(s)

    gray = cv2.cvtColor(blur, cv2.COLOR_RGB2GRAY)

    edges = cv2.Canny(gray, 50, 150)

    combined = cv2.bitwise_or(sat_mask, edges)

    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (15, 15))
    closed = cv2.morphologyEx(combined, cv2.MORPH_CLOSE, kernel)

    contours, _ = cv2.findContours(
        closed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
    )

    contours = sorted(contours, key=contour_score, reverse=True)
    areas = [cv2.contourArea(c) for c in contours]
    max_area = max(areas)

    large_contours = [
        c for c in contours
        if cv2.contourArea(c) > 0.5 * max_area
    ]

    garden_contours = smooth_contour_poly(large_contours, smoothing_factor)

    mask = np.zeros(gray.shape, dtype=np.uint8)
    cv2.drawContours(mask, garden_contours, -1, 255, -1)
    # cv2.drawContours(mask, garden_contours, -1, 128, 5)

    return mask, garden_contours


def contour_score(cnt):
    x, y, w, h = cv2.boundingRect(cnt)
    area = cv2.contourArea(cnt)
    aspect_ratio = max(w, h) / (min(w, h) + 1e-5)
    return area * aspect_ratio


def smooth_contour_poly(contours, factor=0.007):
    smooth_contours = []
    for c in contours:
        epsilon = factor * cv2.arcLength(c, True)
        smooth_contours.append(cv2.approxPolyDP(c, epsilon, True))
    return smooth_contours

# ======================
# Example usage
# ======================
if __name__ == "__main__":
    img = load_image("../assets/image_examples/garden_example_1.jpg", loud=True)
    img_gray = load_image("../assets/image_examples/garden_example_1.jpg", grayscale=True, loud=True)

    display_images(img, img_gray)
