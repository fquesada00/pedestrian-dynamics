import numpy as np
from utils.Constants import Constants
from utils.get_static_parameters import get_static_parameters


def generate_room():
    with open(Constants.STATIC_FILE_NAME.value, "r") as f:
        for index, line in enumerate(f):
            if index == 0:
                room_radius = float(line.split()[0])

    with open("room.xyz", "w") as f:
        f.write(f"2\ncomment\n0 0 1 {room_radius+0.25} 0 0 0\n")
        f.write(f"0 0 0 {room_radius} 255 255 255")


def generate_animation():
    room_radius, humans, zombies = get_static_parameters()

    with open(Constants.DYNAMIC_FILE_NAME.value, "r") as dynamic_file:
        with open(Constants.ANIMATION_FILE_NAME.value, "w") as epidemic_file:
            particles = humans + zombies
            for index, line in enumerate(dynamic_file):
                line = line.split()
                if len(line) == 1:
                    epidemic_file.write(f"{particles}\ncomment\n")
                else:
                    x = float(line[0])
                    y = float(line[1])
                    z = -5
                    radius = float(line[2])
                    pedestrian = line[3]

                    if pedestrian == "h":
                        color = f"{0} {0} {255}"  # blue
                    elif pedestrian == "z":
                        color = f"{0} {255} {0}"  # green
                    else:
                        color = f"{255} {0} {0}"  # red

                    epidemic_file.write(f"{x} {y} {z} {radius} {color}\n")


if __name__ == "__main__":
    generate_room()
    generate_animation()
