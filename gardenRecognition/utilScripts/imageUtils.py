
import cv2
import numpy as np


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
    """
    This function extracts garden from an image, returns a binary mask of the garden and a list of contours of that garden.
    :param img:
    :param sat_low:
    :param sat_high:
    :param smoothing_factor:
    """

    mask, contour = _extract_garden_base(img, sat_low, sat_high, smoothing_factor)

    garden = cv2.bitwise_and(img, img, mask=mask)

    return garden, mask, contour


def _extract_garden_base(img, sat_low, sat_high, smoothing_factor):
    blur = cv2.GaussianBlur(img, (9, 9), 0)
    hsv = cv2.cvtColor(blur, cv2.COLOR_RGB2HSV)
    h, s, v = cv2.split(hsv)
    sat_mask = cv2.inRange(s, sat_low, sat_high)

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

