import os
from .Constants import Constants


def run_simulation(nh: int = 10, vdz: float = 3, seed: int = 1, duration: int = 500, step_size: float = 0.01, random_z_coefficient: bool = True,
                   random_h_coefficient: bool = True, random_w_coefficient: bool = True):
    cmd = f"java -Dnh={nh} -Dvdz={vdz} -Dduration={duration} -DstepSize={step_size} -Dseed={seed} -DrandH={random_h_coefficient} -DrandZ={random_z_coefficient} -DrandW={random_w_coefficient} -DdynamicOutputFileName={Constants.DYNAMIC_FILE_NAME.value} -DstaticOutputFileName={Constants.STATIC_FILE_NAME.value} -jar ../target/pedestrian-dynamics-1.0-SNAPSHOT.jar"
    print(cmd)
    os.system(cmd)


def main():
    run_simulation()


if __name__ == "__main__":
    main()
