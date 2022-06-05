from itertools import zip_longest
import matplotlib.pyplot as plt
import numpy as np
from utils.parse_simulation_line import parse_simulation_line
from utils.runner import run_simulation
from utils.Constants import Constants


def calculate_fraction(dynamic_filename, n_particles):
    with open(dynamic_filename) as f:
        zombie_count = 0
        fraction = []
        for i, line in enumerate(f):
            # time step number
            if i == 0:
                continue
            if i % (n_particles+1) == 0:
                fraction.append(zombie_count/n_particles)
                zombie_count = 0
            # particle data
            else:
                #print(f"Parsing line {line}")
                x, y, radius, particle_type = parse_simulation_line(line)
                if particle_type != "h":
                    zombie_count += 1
    print(f"Fraction: {fraction[-1]}")
    return fraction


def plot_fraction(fractions, nh, dt, color="red"):
    fractions = np.array(fractions)
    print(f"Plotting fraction for nh={nh} and dt={dt}")
    plt.tight_layout()

    plt.xlabel("Tiempo (s)", fontsize=20)
    plt.ylabel("Fracción de zombies", fontsize=20)

    plt.xticks(fontsize=15)
    plt.yticks(fontsize=15)
    avg_fractions = []
    stdev_fractions = []
    for step_values in zip_longest(*fractions, fillvalue=np.nan):
        new_avg = np.nanmean(step_values)
        avg_fractions = np.append(
            avg_fractions, new_avg)
        new_stdev = np.nanstd(step_values)
        stdev_fractions = np.append(
            stdev_fractions, new_stdev)

    dts = np.arange(0, len(avg_fractions)) * dt
    plt.errorbar(dts, avg_fractions, yerr=stdev_fractions,
                 ecolor="blue", marker="o", color=color, elinewidth=0.5, capsize=5, label=f"$N_h = {nh}$")


if __name__ == "__main__":
    nhs = [2, 10, 40, 80, 140, 200, 260, 320, 380]
    colors = np.array(["#332288", "#88CCEE", "#44AA99",
                       "#117733", "#999933", "#DDCC77",
                       "#CC6677", "#882255", "#AA4499"]).reshape(3, 3).T.flatten()
    num_of_iterations = 5
    dt = 0.01
    duration = 500
    vdz = 3
    dt2 = 5
    for index, nh in enumerate(nhs):
        fractions = []

        for i in range(num_of_iterations):
            run_simulation(nh, vdz=vdz, duration=duration, step_size=dt, seed=i,
                           random_z_coefficient=True, random_h_coefficient=True, random_w_coefficient=True, animation_step=dt2)
            fractions.append(calculate_fraction(
                Constants.DYNAMIC_FILE_NAME.value, nh+1))

        plot_fraction(fractions, nh, dt2, color=colors[index])

    plt.legend(fontsize=13)
    plt.show()
