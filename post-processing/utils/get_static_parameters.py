from .Constants import Constants


def get_static_parameters() -> tuple[float, int, int]:
    """
    Get the static parameters from the static file.
    :return: A tuple of the following:
        - The radius of the room.
        - The number of humans.
        - The number of zombies.
    """
    with open(Constants.STATIC_FILE_NAME.value, "r") as f:
        for index, line in enumerate(f):
            if index == 0:
                room_radius = float(line.split()[0])
            elif index == 1:
                humans = int(line.split()[0])
            elif index == 2:
                zombies = int(line.split()[0])

    return room_radius, humans, zombies
