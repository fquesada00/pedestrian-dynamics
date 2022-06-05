import os
from .Constants import Constants


def run_simulation(nh: int = 200, vdz: float = 3, seed: int = 1, duration: int = 500, step_size: float = 0.01, random_z_coefficient: bool = False,
                   random_h_coefficient: bool = False, random_w_coefficient: bool = False, animation_step: float = 0.5):
    cmd = f"java -Dnh={nh} -Dvdz={vdz} -Dduration={duration} -DstepSize={step_size} "\
        f"-Dseed={seed} -DrandH={random_h_coefficient} -DrandZ={random_z_coefficient} "\
        f"-DrandW={random_w_coefficient} -DdynamicOutputFileName={Constants.DYNAMIC_FILE_NAME.value} " \
        f"-DstaticOutputFileName={Constants.STATIC_FILE_NAME.value} -DanimationStep={animation_step} "\
        f"-jar ../target/pedestrian-dynamics-1.0-SNAPSHOT.jar"

    print(cmd)
    os.system(cmd)


def main():
    run_simulation()


if __name__ == "__main__":
    main()
