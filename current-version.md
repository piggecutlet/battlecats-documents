- Current Version

```sh
echo "Current Version: $(curl -s -X POST "https://tapi.pureapk.com/v3/get_app_detail" -H "Ual-Access-Businessid: projecta" -H 'Ual-Access-ProjectA: {"device_info":{"os_ver":"33"}}' -H "Content-Type: application/json" -d '{"action": "Download", "package_name": "jp.co.ponos.battlecats"}' | jq -r '.app_detail.version_name')"
```
