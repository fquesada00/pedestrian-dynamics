import pandas as pd


def parse_csv_plot_data(file_name, columns):
    data = pd.read_csv(file_name, header=None)

    time = data.iloc[:, 0].values
    avg_list = []
    stdev_list = []

    for i in range(columns):
        avg_list.append(data.iloc[:, i + 1].values)
        stdev_list.append(data.iloc[:, i + 1 + columns].values)

    return time, avg_list, stdev_list
    # with open(file_name, 'r') as f:
    #     lines = f.readlines()
    #     time = [float(line.split(',')[0]) for line in lines]
    #     avgs_list = [list(map(float, line.split(',')[1:columns+1]))
    #                  for line in lines]
    #     stdevs_list = [list(map(float, line.split(',')[columns+1:]))
    #                    for line in lines]

    #     return time, avgs_list, stdevs_list
