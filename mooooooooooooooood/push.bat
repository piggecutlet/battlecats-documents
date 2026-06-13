@echo off
chcp 65001 > nul

adb push SAVE_DATA /data/data/jp.co.ponos.battlecats/files/
adb shell chmod 664 /data/data/jp.co.ponos.battlecats/files/SAVE_DATA
adb shell chown u0_a145:u0_a145 /data/data/jp.co.ponos.battlecats/files/SAVE_DATA
adb shell ls -l /data/data/jp.co.ponos.battlecats/files/SAVE_DATA
echo adb shell chown u0_a145:u0_a145 /data/data/jp.co.ponos.battlecats/files/SAVE_DATA

cmd /k
