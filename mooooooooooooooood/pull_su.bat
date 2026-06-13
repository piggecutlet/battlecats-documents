@echo off

adb shell su -c cp /data/data/com.example.battlecats/files/SAVE_DATA /sdcard/download/SAVE_DATA
adb pull /sdcard/download/SAVE_DATA SAVE_DATA
adb shell rm /sdcard/download/SAVE_DATA

sleep 3
