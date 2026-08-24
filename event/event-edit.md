# イベントデータ 編集

## 前提条件

- ルート化した Android か エミュレーター

- 参考: BlueStacks ルート化（38秒付近～）
  https://www.youtube.com/watch?v=3bJLNLbomPk&t=38s

## 手順

1. `/data/data/jp.co.ponos.battlecats/files` にある目的のファイルを PC に移動

  002a4b18244f32d7833fd81bc833b97f.dat: sale.tsv
  09b1058188348630d98a08e0f731f6bd.dat: gatya.tsv
  408f66def075926baea9466e70504a3b.dat: item.tsv

  詳細: [その他のファイル](./hash.md)

2. `python -m pip install -U bcdd`

3. `python -m bcdd` で PC に移動したファイルを復号

4. 3 で復号したファイルを好きに編集

5. 4 で編集したファイルを `python -m bcdd` で暗号化

6. `/data/data/jp.co.ponos.battlecats/files` に戻す

## その他

- Java 版のスクリプトは [DatFileCrypter](./DatFileCrypter.java) に作っているので参考にしてください
