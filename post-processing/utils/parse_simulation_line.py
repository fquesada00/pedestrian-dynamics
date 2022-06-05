def parse_simulation_line(line: str) -> tuple[float, float, float, int, int, int]:
    """
    Parse a line from a simulation file.
    :param line: The line to parse.
    :return: A tuple of the following:
        - The X coordinate of the particle.
        - The Y coordinate of the particle.
        - The radius of the particle.
        - The red component of the particle's color.
        - The green component of the particle's color.
        - The blue component of the particle's color.
    """
    x_str, y_str, radius_str, red_str, green_str, blue_str = line.split()

    return float(x_str), float(y_str), float(radius_str), int(red_str), int(green_str), int(blue_str)
