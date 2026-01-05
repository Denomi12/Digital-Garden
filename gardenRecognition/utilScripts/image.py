import math

import cv2
import numpy as np
from matplotlib import pyplot as plt

from utilScripts import GardenUtils

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



# ======================
# Example usage
# ======================
if __name__ == "__main__":
    img = load_image("../assets/image_examples/garden_example_1.jpg", loud=True)
    img_gray = load_image("../assets/image_examples/garden_example_1.jpg", grayscale=True, loud=True)

    display_images(img, img_gray)