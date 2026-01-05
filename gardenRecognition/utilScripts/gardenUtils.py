import numpy as np




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