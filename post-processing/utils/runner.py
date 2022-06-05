import os
from Constants import Constants


def run_simulation(nh: int = 100, vdz: float = 3, duration: int = 200, step_size: float = 0.01):
    cmd = f"java -Dnh={nh} -Dvdz={vdz} -Dduration={duration} -DstepSize={step_size} -DdynamicOutputFileName={Constants.DYNAMIC_FILE_NAME} -DstaticOutputFileName={Constants.STATIC_FILE_NAME} -jar ./target/pedestrian-dynamics-1.0-SNAPSHOT.jar"
    print(cmd)
    os.system(cmd)


def main():
    run_simulation()


if __name__ == "__main__":
    main()
