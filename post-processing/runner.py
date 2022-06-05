import os


def run(nh: int = 300, vdz: float = 3, duration: int = 200, step_size: float = 0.01):
    cmd = f"java -Dnh={nh} -Dvdz={vdz} -Dduration={duration} -DstepSize={step_size} -jar ./target/pedestrian-dynamics-1.0-SNAPSHOT.jar"
    print(cmd)
    os.system(cmd)

def main():
    run()

if __name__ == "__main__":
    main()