import numpy as np
from utils.runner import run_simulation
from utils.parse_simulation_line import parse_simulation_line
from utils.Constants import Constants
from utils.get_static_parameters import get_static_parameters
import matplotlib.pyplot as plt


def compute_infection_rate(previous_zombie_count, current_zombie_count, step_size):
    return (current_zombie_count - previous_zombie_count) / step_size


def ej1_infection_rate(nh, vdz, duration, step_size) -> list[float]:
    run_simulation(nh=nh, vdz=vdz, duration=duration, step_size=step_size)

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


if __name__ == "__main__":
    nh_list = [2, 10, 40, 80, 140, 200, 260, 320]

    # initialize plot
    plt.figure(figsize=(10, 5))
    # plt.title("Velocidad de contagio e")
    plt.ylabel("Velocidad de contagio [1/s]")
    plt.xlabel("Tiempo [s]")

    step_size = 0.01
    duration = 200
    vdz = 3

    for nh in nh_list:
        infection_rates = ej1_infection_rate(nh, vdz, duration, step_size)

        time = np.arange(0, len(infection_rates) * step_size, step_size)

        # plot
        plt.plot(time, infection_rates, label=f"$N_h = {nh}$")

    plt.legend()
    plt.show()
