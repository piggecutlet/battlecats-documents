import os
import subprocess

def process(command):
  response = subprocess.run(command, capture_output=True, text=True)
  print(response.stdout)
  if response.stderr:
    print(response.stderr)

def adb(command):
  process(["adb"] + command)

def shell(command):
  adb(["shell"] + command)

def checkRareSeed():
  response = subprocess.run(
      ["python", "-m", "bcsfe", "--input-path", "SAVE_DATA"],
      capture_output=True,
      input="7\n" # 7. Gacha
            "2\n" # 2. Rare Gacha Seed
            "\n" # Enter a value for Rare Gacha Seed
            "12", # 12. Exit
      text=True,
  )

  lines = response.stdout.splitlines()

  for line in lines:
    # Input:Enter a value for Rare Gacha Seed (current value: 3575149195):
    if line.startswith("Input:Enter"):
      seed = line.replace("Input:Enter a value for Rare Gacha Seed (current value: ", "").replace("):", "") 
      print("レアガチャシード: " + seed)
      # print(f"https://bc.godfat.org/?seed={seed}&event=2025-10-11_1007&lang=jp\n")
      print()

# adb pull /data/data/jp.co.ponos.battlecats/files/SAVE_DATA
# adb(["pull", "/data/data/jp.co.ponos.battlecats/files/SAVE_DATA"])

# shell(["rm", "/data/data/jp.co.ponos.battlecats/files/SAVE_DATA"])
# shell(["rm", "/data/data/jp.co.ponos.battlecats/files/SAVE_DATA.OLD"])
# shell(["rm", "/data/data/jp.co.ponos.battlecats/shared_prefs/MyActivity.xml"])

if os.path.exists("SAVE_DATA"):
  checkRareSeed()
