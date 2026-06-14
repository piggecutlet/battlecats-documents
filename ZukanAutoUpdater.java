

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Fをformattするからstringにする
public class ZukanAutoUpdater {

  // https://git.fyhenry.uk/henry/BCData/raw/branch/main/game_data/jp/%s/%s/%s
  private static final String F;
  // https://ponosgames.com/information/appli/battlecats/gacha/img/chara_icon/uni%s_f00.png
  private static final String P;
  private static final String NEXT_VERSION;

  private static final Path DATALOCAL = Paths.get("DataLocal");
  private static final Path RESLOCAL = Paths.get("resLocal");

  private static final Path PICTUREBOOK = DATALOCAL.resolve("nyankoPictureBookData.csv");
  private static final Path UNITBUY = DATALOCAL.resolve("unitbuy.csv");
  private static final String EXPLANATION = RESLOCAL.toString() + "Unit_Explanation%s_ja.csv";

  private static final Path VERSION_FILE = Paths.get("version.txt");
  private static final Path OUTPUT_FILE = Paths.get("unit.csv");

  static {
    F = System.getenv("F");
    if (null == F) {
      System.err.println("環境変数 F が定義されていません。");
      System.exit(1);
    }

    P = System.getenv("P");
    if (null == P) {
      System.err.println("環境変数 P が定義されていません。");
      System.exit(1);
    }

    String readString = null;
    try {
      readString = Files.readString(VERSION_FILE);
    } catch (IOException e) {
      System.err.println("version.txt の読み取りに失敗しました。");
      System.exit(1);
    }

    NEXT_VERSION = readString.trim();

    if (NEXT_VERSION.isBlank()) {
      System.err.println("NEXT_VERSION が空です。");
      System.exit(1);
    }
  }

  public static void main(String[] args) throws Exception {
    if (request(PICTUREBOOK) && request(UNITBUY)) {
      for (int i = 0; i < 1000; i++) {

        // ゼロパディングなし
        String url = String.format(EXPLANATION, i);

        if (!request(url)) {
          break;
        }
      }
    }
  }

  private static boolean request(String url) throws Exception {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

    HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());

    int statusCode = response.statusCode();
    if (200 != statusCode) {
      String fileName = Paths.get(url).getFileName().toString();
      System.err.println(statusCode + ": " + fileName);
      return false;
    }

    byte[] body = response.body();

    Path fileName = Paths.get(url).getFileName();
    Files.write(fileName, body);
    return true;
  }

  private static boolean request(Path url) throws Exception {
    return request(url.toString());
  }

}
