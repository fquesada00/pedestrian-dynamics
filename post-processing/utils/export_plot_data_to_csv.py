from itertools import zip_longest

import numpy as np


def export_plot_data_to_csv(time: list[float], avgs_list: list[list[float]], stdevs_list: list[list[float]], file_name):
    with open(file_name, 'w') as f:
        columns = zip_longest(time, *avgs_list, *stdevs_list, fillvalue=np.nan)
        for column in columns:
            f.write(",".join(map(str, column)))
            f.write("\n")
