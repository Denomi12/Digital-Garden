from utilScripts import *


if __name__ == '__main__':
    print("--- Garden Recognition ---")
    garden_image = load_image("./assets/image_examples/vrt_1.png")

    garden, mask, contour = extract_garden(garden_image)

    tilemap = create_tilemap(mask, height=20, width=40)
    elements = blocks_to_elements(tilemap)

    garden_json = create_garden(elements=elements)

    display_images(tilemap)


