@echo off
chcp 65001 > nul

setlocal enabledelayedexpansion

REM 実行するURLが書かれたファイル
set URL_FILE=result.txt
set URL_FILE=reward_fail_8.6.txt
set URL_FILE=result_13_ok.txt
set URL_FILE=reward_fail_13.0.txt
set URL_FILE=result.txt

REM urls.txtが存在するか確認
if not exist "%URL_FILE%" (
    echo %URL_FILE% が見つかりません。
    exit /b 1
)

echo URLリストを読み込み、コマンドを実行します...
echo.

REM result.txtの各行をループで読み込む
for /f "usebackq delims=" %%a in ("%URL_FILE%") do (
    set "url=%%a"
    
    echo 実行中: "!url!"
    
    REM adbコマンドを実行
    adb shell am start -a android.intent.action.VIEW -d "!url!" > nul 2>&1
    @REM echo adb shell am start -a android.intent.action.VIEW -d "!url!"
    
    REM 待機時間（任意）
    timeout 1  > nul
    @REM pause
    
    echo.
)

echo 処理が完了しました。
endlocal
pause