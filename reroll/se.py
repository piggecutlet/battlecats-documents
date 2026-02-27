import subprocess

# response = subprocess.run(
# 	["python", "-m", "bcsfe", "-i", "SAVE_DATA"],
# 	capture_output=True,
# 	input="6\n" # 6. Account
# 	        "2\n" # 2. Unban Account / Fix Save Used Elsewhere Error
# 	        "1\n" # 1. Save Management
# 	        "2\n" # 2. Save save
# 	        "12\ny", # 12. Exit
# 	text=True,
# )

response = subprocess.run(
	["python", "-m", "bcsfe", "-i", "SAVE_DATA"],
	capture_output=True,
	input="1\n" # 1. Save Management
	        "2\n" # 2. Save save
	        "12\n", # 12. Exit
	text=True,
)

# response = subprocess.run(
# 	["python", "-m", "bcsfe", "-i", "SAVE_DATA"],
# 	capture_output=True,
# 	input="1\n4\nSAVE_DATA2\n",
# 	text=True,
# )

print(response.stdout)
if response.stderr:
	print(response.stderr)
