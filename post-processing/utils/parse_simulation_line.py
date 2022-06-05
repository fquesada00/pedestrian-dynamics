def parse_simulation_line(line: str) -> tuple[float, float, float, int, int, int]:
    """
    Parse a line from a simulation file.
    :param line: The line to parse.
    :return: A tuple of the following:
        - The X coordinate of the particle.
        - The Y coordinate of the particle.
        - The radius of the particle.
        - Particle type: h for human, z for zombie, i for human being infected.
    """
    x_str, y_str, radius_str, particle_type = line.split()

    return float(x_str), float(y_str), float(radius_str), particle_type
