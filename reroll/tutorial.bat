@echo off
chcp 65001 > nul

set OK=1640 760
set IZA_SHUTSUJIN=%OK%
set SLOT_1=715 990

echo 長崎県 いざ出陣
adb shell input tap %OK%

echo 停止
timeout 5 > nul

@REM 敵の城を攻め落とせ！！
adb shell input tap %OK%

timeout 3 > nul

@REM キャラクターについて
@REM このアイコンをタップすれば
@REM キャラクターが生産されるぞ！
adb shell input tap %OK%

timeout 3 > nul

@REM キャラクターについて
@REM ただし、キャラクターの生産には
@REM 画面上のお金が必要！
@REM お金がたまるまで待つのだ！
adb shell input tap %OK%

timeout 7 > nul

@REM キャラクターの生産
@REM さっそくキャラクターの生産だ！
@REM 下のアイコンをタップしよう！
adb shell input tap %OK%

timeout 3 > nul

@REM キャラクターの生産
@REM 生産されたキャラクターは自動的に
@REM 敵や城を攻撃するぞ！
@REM お金をためてどんどん生産するのだ！
adb shell input tap %OK%

timeout 3 > nul

call :production_neko
call :production_neko

@REM お金を増やそう
@REM 左下のボタンを押すと
@REM お金の増えるスピードがアップ！
adb shell input tap %OK%

timeout 3 > nul

@REM お金を増やそう
@REM お金の最大値も増えるので
@REM 戦闘を有利に進めることができるぞ！
adb shell input tap %OK%

timeout 3 > nul

pause
goto:eof

:production_neko
adb shell input tap %SLOT_1%
timeout 3 > nul
goto:eof
