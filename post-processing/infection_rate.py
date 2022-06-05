import numpy as np
from utils.runner import run_simulation
from utils.parse_simulation_line import parse_simulation_line
from utils.Constants import Constants
from utils.get_static_parameters import get_static_parameters
import matplotlib.pyplot as plt
from scipy.ndimage import gaussian_filter1d as gaussian_filter
from utils.get_irregular_mean_and_std import get_irregular_mean_and_std
from utils.export_plot_data_to_csv import export_plot_data_to_csv
from utils.parse_csv_plot_data import parse_csv_plot_data


def compute_infection_rate(previous_zombie_count, current_zombie_count, step_size):
    return (current_zombie_count - previous_zombie_count) / step_size


def get_infection_rate_evolution(nh, vdz, duration, step_size, animation_step, seed) -> list[float]:
    run_simulation(nh=nh, vdz=vdz, duration=duration,
                   step_size=step_size, animation_step=animation_step, seed=seed)

    room_radius, humans, zombies = get_static_parameters()
    total_particles = humans + zombies

    infection_rates = []

    with open(Constants.DYNAMIC_FILE_NAME.value) as f:
        # iterate over the lines of the file
        current_zombie_count = zombies
        previous_zombie_count = zombies

        for line_number, line in enumerate(f):
            # skip the first line of the step
            if len(line.split()) == 1:
                infection_rates.append(compute_infection_rate(
                    previous_zombie_count, current_zombie_count, step_size))

                # update the previous zombie count
                previous_zombie_count = current_zombie_count
                current_zombie_count = 0

                continue

            # parse the line
            x, y, radius, particle_type = parse_simulation_line(line)

            # if the particle is a zombie, increment the zombie count
            if particle_type == "z":
                current_zombie_count += 1

    return infection_rates


def plot_ej_b_infection_rate(nh_list, vdz, duration, step_size, animation_step, executions, with_gaussian_filter=True, restore_plot_data=False):
    plt.figure(figsize=(10, 5))

    fig1, ax1 = plt.subplots()
    fig2, ax2 = plt.subplots()
    # plt.title("Velocidad de contagio e")

    ax1.set_ylabel("Velocidad de contagio [1/s]")
    ax1.set_xlabel("Tiempo [s]")

    ax2.set_ylabel("Velocidad de contagio [1/s]")
    ax2.set_xlabel("Tiempo [s]")

    colors = ["#332288", "#88CCEE", "#44AA99", "#117733",
              "#999933", "#DDCC77", "#CC6677", "#882255", "#AA4499"]

    avg_list = []
    stdev_list = []
    time = np.arange(0, duration + animation_step, animation_step)

    if not restore_plot_data:
        for i, nh in enumerate(nh_list):
            infection_rate_evolutions = []

            for j in range(executions):
                infection_rate_evolutions.append(get_infection_rate_evolution(
                    nh, vdz, duration, step_size, animation_step, seed=j))

            avg, stdev = get_irregular_mean_and_std(
                infection_rate_evolutions)

            avg_list.append(avg)
            stdev_list.append(stdev)

    else:
        time, avg_list, stdev_list = parse_csv_plot_data(
            Constants.INFECTION_RATE_FILE_NAME.value, len(nh_list)
        )

    for i, nh in enumerate(nh_list):
        dts = time[0:len(avg_list[i])]

        ax1.errorbar(dts, avg_list[i], yerr=stdev_list[i],
                     ecolor="blue", marker="o", color=colors[i % len(colors)], elinewidth=0.5, capsize=5, label=f"NH = {nh}")

        if with_gaussian_filter:
            sigma = 3

            ax2.plot(dts, gaussian_filter(avg_list[i], sigma),
                     marker="o", color=colors[i % len(colors)], label=f"$N_h = {nh}$")

    export_plot_data_to_csv(time, avg_list, stdev_list,
                            Constants.INFECTION_RATE_FILE_NAME.value)

    fig1.legend()
    fig2.legend()
    plt.show()


if __name__ == "__main__":
    nh_list = [2, 10, 40, 80]
    step_size = 0.01
    animation_step = 10
    duration = 200
    vdz = 3

    plot_ej_b_infection_rate(nh_list, vdz, duration, step_size,
                             animation_step, executions=5, with_gaussian_filter=True, restore_plot_data=True)
    # initialize plot
