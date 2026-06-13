adb shell "su -c 'rm /data/data/jp.co.ponos.battlecats/files/SAVE_DATA'"
adb shell "su -c 'rm /data/data/jp.co.ponos.battlecats/files/SAVE_DATA.OLD'"
adb shell "su -c 'rm /data/data/jp.co.ponos.battlecats/shared_prefs/MyActivity.xml'"
adb shell am force-stop jp.co.ponos.battlecats
adb shell am start -n jp.co.ponos.battlecats/jp.co.ponos.battlecats.MyActivity > nul
