from itertools import zip_longest
import numpy as np


def get_irregular_mean_and_std(data: list):
    avg = []
    stdev = []
    for step_values in zip_longest(*data, fillvalue=np.nan):
        new_avg = np.nanmean(step_values)
        avg = np.append(
            avg, new_avg)
        new_stdev = np.nanstd(step_values)
        stdev = np.append(
            stdev, new_stdev)

    return avg, stdev
