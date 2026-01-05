from utilScripts.imageUtils import *
from utilScripts import GardenUtils as gu

if __name__ == '__main__':
    print("--- Garden Recognition ---")
    img = load_image("./assets/image_examples/test_random.png")

    garden, mask, contour = extract_garden(img, sat_low=70, sat_high=90, smoothing_factor=0.004)
    display_images(garden, mask)


def demo(img: np.ndarray):
    blur = cv2.GaussianBlur(img, (7, 7), 0)
    hsv = cv2.cvtColor(blur, cv2.COLOR_RGB2HSV)
    h, s, v = cv2.split(hsv)



    sat_mask = cv2.inRange(s, 0, 40)


    gray = cv2.cvtColor(blur, cv2.COLOR_RGB2GRAY)


    edges = cv2.Canny(gray, 50, 150)

    combined = cv2.bitwise_or(sat_mask, edges)

    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (15, 15))

    closed = cv2.morphologyEx(combined, cv2.MORPH_CLOSE, kernel)

    contours, _ = cv2.findContours(
        closed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
    )


    def contour_score(cnt):
        x, y, w, h = cv2.boundingRect(cnt)
        area = cv2.contourArea(cnt)
        aspect_ratio = max(w, h) / (min(w, h) + 1e-5)
        return area * aspect_ratio


    garden_contour = max(contours, key=contour_score)

    mask = np.zeros(gray.shape, dtype=np.uint8)
    cv2.drawContours(mask, [garden_contour], -1, 255, thickness=-1)


    garden_only = cv2.bitwise_and(img, img, mask=mask)
    display_images(garden_only, mask)

