adb shell "su -c 'cp /data/data/jp.co.ponos.battlecats/files/SAVE_DATA /storage/emulated/0/Download/SAVE_DATA'"

adb pull /storage/emulated/0/Download/SAVE_DATA SAVE_DATA

adb shell rm /storage/emulated/0/Download/SAVE_DATA
