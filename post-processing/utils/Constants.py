import enum


class Constants(enum.Enum):
    """
    Constants used in the post-processing scripts.
    """

    DYNAMIC_FILE_NAME = "dynamic.txt"
    STATIC_FILE_NAME = "static.txt"
    ROOM_FILE_NAME = "room.xyz"
    ANIMATION_FILE_NAME = "epidemic.xyz"
    INFECTION_RATE_FILE_NAME_NH = "ej_b_infection_rate.csv"
    INFECTION_RATE_FILE_NAME_VDZ = "ej_c_infection_rate.csv"
