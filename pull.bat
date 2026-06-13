@echo off
chcp 65001 > nul

pause

adb push ./files/ /data/data/jp.co.ponos.battlecats/files/

timeout 3
