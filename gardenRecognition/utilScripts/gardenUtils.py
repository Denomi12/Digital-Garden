import numpy as np
import json



def create_blocks(mask: np.ndarray, height: int = 5, width: int = 5) -> np.ndarray:
    mask = mask/mask.max()
    mask_height, mask_width = mask.shape

    cell_height = mask_height // height
    cell_width = mask_width // width


    tilemap = np.zeros((height, width), dtype=np.uint8)


    for i in range(height):
        for j in range(width):
            y_start = i * cell_height
            y_end = (i + 1) * cell_height
            x_start = j * cell_width
            x_end = (j + 1) * cell_width

            # Slice the cell from the mask
            cell = mask[y_start:y_end, x_start:x_end]
            tmp_mask = mask.copy()
            tmp_mask[y_start:y_end, x_start:x_end] = 0.5

            # Compute average and store in tilemap
            avg = np.average(cell)
            tilemap[i, j] = 1 if avg >= 0.5 else 0  # scale back to 0-255 if needed

            # print(f"Cell ({i},{j}): y={y_start}:{y_end}, x={x_start}:{x_end}, avg={tilemap[i, j]}")

    return tilemap

def create_garden(name="Default name", width=5, height=5, location=None, latitude=None, longitude=None, elements=[]):
    garden_dict = {}
    garden_dict["name"] = name

    if elements:
        max_x = max(e["x"] for e in elements)
        max_y = max(e["y"] for e in elements)
        if width < max_x:
            width = max_x+1
        if height < max_y:
            height = max_y+1

    garden_dict["width"] = width
    garden_dict["height"] = height

    if location is not None:
        garden_dict["location"] = location
    if latitude is not None and longitude is not None:
        garden_dict["latitude"] = latitude
        garden_dict["longitude"] = longitude
    garden_dict["elements"] = elements


    garden_json = json.dumps(garden_dict)
    return garden_json

def blocks_to_elements(blocks: np.ndarray) -> dict:
    h, w, = blocks.shape[:2]

    elements = []
    for i in range(h):
        for j in range(w):
            if blocks[i][j] == 1:
                elements.append({"x": j, "y": i, "type": "Greda"})

    return elements