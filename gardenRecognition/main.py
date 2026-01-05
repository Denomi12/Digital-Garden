from utilScripts.image import *
from utilScripts import GardenUtils as gu
if __name__ == '__main__':
    print("--- Garden Recognition ---")
    img = load_image("./assets/image_examples/garden_example_1.jpg")

    grid = gu.Grid(img, 10, 10)

    display_grid_with_gaps(grid)