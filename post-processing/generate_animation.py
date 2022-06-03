import numpy as np


def generate_room():
    with open("static.txt", "r") as f:
        for index, line in enumerate(f):
            if index == 0:
                room_radius = float(line.split()[0])
    
    with open("room.xyz", "w") as f:
        particles = 100
        f.write(f"{particles}\ncomment\n")
        angles = np.linspace(0, 2*np.pi, particles)
        for angle in angles:
            x = room_radius * np.cos(angle)
            y = room_radius * np.sin(angle)
            f.write("{0:.3f} {1:.3f}\n".format(x, y))

def generate_animation():
    with open("static.txt", "r") as f:
        for index, line in enumerate(f):
            if index == 0:
                room_radius = float(line.split()[0])
            elif index == 1:
                humans = int(line.split()[0])
            elif index == 2:
                zombies = int(line.split()[0])

    with open("dynamic.txt", "r") as dynamic_file:
        with open("epidemic.xyz", "w") as epidemic_file:
            particles = humans + zombies
            for index, line in enumerate(dynamic_file):
                line = line.split()
                if index == 0 or line % (particles + 2) == 0:
                    particles = int(line.split()[0])
                    epidemic_file.write(f"{particles}\ncomment\n")
                else:
                    x = float(line.split()[0])
                    y = float(line.split()[1])
                    epidemic_file.write(f"{x} {y}\n")

if __name__ == "__main__":
    generate_room()
    # generate_animation()