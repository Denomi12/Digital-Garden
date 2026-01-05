from utilScripts.imageUtils import *
from utilScripts import *

if __name__ == '__main__':
    print("--- Garden Recognition ---")
    img = load_image("./assets/image_examples/vrt_3.png")

    garden, mask, contour = extract_garden(img)

    blocks = create_blocks(mask, height=20, width=40)

    elements = blocks_to_elements(blocks)

    g = create_garden(elements=elements)

    print(g)
