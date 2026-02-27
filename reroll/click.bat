@echo off

for /L %%i in (1, 1, 1000) do (
  @REM 戦闘開始 
  adb shell input tap 1900 760
  @REM OK チュートリアル 
  adb shell input tap 1540 734
  @REM ネコ 
  adb shell input tap 760 1010
  @REM タンク 
  adb shell input tap 960 1010
  @REM OK 戦闘終了 
  adb shell input tap 1200 1015
  timeout 2 > nul
)

pause