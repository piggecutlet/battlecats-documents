import subprocess
import time

print("開始\n")

with open("localizable.tsv", 'r', encoding='utf-8') as f:
  content = f.read()
  lines = content.splitlines()
  for line in lines:
    if not line.startswith("scheme_item_param_"):
      continue
    
    if line.endswith(".html"):
      continue
    
    param = line.split("\t")[1]
    url = "ponos-battlecats4://ponos/" + param
    command = ["adb", "shell", "am", "start", "-a", "android.intent.action.VIEW", "-d", url]
    # デバッグ
    print(" ".join(command))
    print()
    # continue

    response = subprocess.run(command, capture_output=True, text=True)
    # print(response.stdout)
    # if response.stderr:
      # print(response.stderr)
    
    time.sleep(1)
    # subprocess.run(["adb", "shell","input","tap","730","1900"])
    time.sleep(2)
