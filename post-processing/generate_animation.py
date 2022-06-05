import numpy as np
from utils.Constants import Constants
from utils.get_static_parameters import get_static_parameters


def generate_room():
    with open(Constants.STATIC_FILE_NAME, "r") as f:
        for index, line in enumerate(f):
            if index == 0:
                room_radius = float(line.split()[0])

    with open("room.xyz", "w") as f:
        # particles = 300
        # f.write(f"{particles}\ncomment\n")
        # angles = np.linspace(0, 2*np.pi, particles)
        # for angle in angles:
        #     x = room_radius * np.cos(angle)
        #     y = room_radius * np.sin(angle)
        #     f.write("{0:.3f} {1:.3f}\n".format(x, y))
        f.write(f"1\ncomment\n0 0 0 {room_radius}")




def generate_animation():
    room_radius, humans, zombies = get_static_parameters()

    with open(Constants.DYNAMIC_FILE_NAME, "r") as dynamic_file:
        with open(Constants.ANIMATION_FILE_NAME, "w") as epidemic_file:
            particles = humans + zombies
            for index, line in enumerate(dynamic_file):
                line = line.split()
                if len(line) == 1:
                    epidemic_file.write(f"{particles}\ncomment\n")
                else:
                    x = float(line[0])
                    y = float(line[1])
                    radius = float(line[2])
                    pedestrian = line[3]

                    if pedestrian == "h":
                        color = f"{0} {0} {255}"  # blue
                    elif pedestrian == "z":
                        color = f"{0} {255} {0}"  # green
                    else:
                        color = f"{255} {0} {0}"  # red

                    epidemic_file.write(f"{x} {y} 15 {radius} {color}\n")



if __name__ == "__main__":
    generate_room()
    generate_animation()
