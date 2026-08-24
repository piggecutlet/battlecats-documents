- Current Version

```sh
echo "Current Version: $(curl -s -X POST "https://tapi.pureapk.com/v3/get_app_detail" -H "Ual-Access-Businessid: projecta" -H 'Ual-Access-ProjectA: {"device_info":{"os_ver":"33"}}' -H "Content-Type: application/json" -d '{"action": "Download", "package_name": "jp.co.ponos.battlecats"}' | jq -r '.app_detail.version_name')"
```

- Server Assets
```sh
wget https://github.com/piggecutlet/battlecats-server-assets-downloader/releases/download/v0.3.0/battlecats-server-assets-downloader_v0.3.0.jar -O battlecats-server-assets-downloader.jar && java -jar battlecats-server-assets-downloader.jar --url https://nyanko-assets.ponosgames.com/iphone/battlecats/download/battlecats_150600_35_00.zip
```
