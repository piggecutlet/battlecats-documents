# Battle Cats

## リワードリンク

- カスタム URL スキームを使用
- BCJP 11.7.0 で対策
- ファイル
  localizable.tsv
  `ponos-battlecats4://ponos/scheme_item_param_{Number}`
- 関連ファイル
  schemeItemData.tsv
  GatyaitemName.csv

## リセマラ

- 10秒くらいでリセマラ完了した記憶
- reroll.py

## Native

6.2.2 なし
6.7.4 libbattlecats-jni.so
7.0.1 libnative-lib.so

## Ban

- 他の場所エラー系の調査
- オンラインで起動するとレスポンスによっては他の場所と表示される

# Fix Elsewhere Error

Elsewhere error occur when the statusCode for https://nyanko-auth.ponosgames.com/v1/user/password is 101.

If offline and `/data/data/jp.co.ponos.battlecats/cache/{Inquiry Code}.json` exists,
This can be avoided by eliminating the need to communicate with https://nyanko-auth.ponosgames.com/v1/user/password at startup.

Content : `/data/data/jp.co.ponos.battlecats/cache/{Inquiry Code}.json`

```json
{ "password": "example", "token": "example" }
```

Rresponse : https://nyanko-auth.ponosgames.com/v1/user/password
Normal

```json
{
  "statusCode": 1,
  "nonce": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "payload": {
    "password": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
    "passwordRefreshToken": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  },
  "timestamp": xxxxxxxxxx
}
```

Elsewhere

```json
{ "statusCode": 101, "nonce": null, "payload": null, "timestamp": xxxxxxxxxx }
```

If you delete these files, A new account will be created.
No need to delete storage or uninstall !
`/data/data/jp.co.ponos.battlecats/files/SAVE_DATA`
`/data/data/jp.co.ponos.battlecats/files/SAVE_DATA.OLD`
`/data/data/jp.co.ponos.battlecats/shared_prefs/MyActivity.xml`
