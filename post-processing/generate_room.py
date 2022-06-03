import numpy as np

if __name__ == "__main__":
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
