package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class EventFileGrabber {
  public static final String[] LOCAL_ARRAY = {"", "kr", "en", "tw"};

  public static final String[] FILE_TYPE_ARRAY = {"gatya.tsv", "item.tsv", "sale.tsv"};

  public static final String EVENT_BASE_URL =
      "https://nyanko-events.ponosgames.com/battlecats%s_production/%s?jwt=";

  public static final String CREATE_ACCOUNT_URL =
      "https://nyanko-backups.ponosgames.com/?action=createAccount&referenceId=";

  public static final String USERS_URL = "https://nyanko-auth.ponosgames.com/v1/users";

  public static final String PASSWORD_URL = "https://nyanko-auth.ponosgames.com/v1/user/password";

  public static final String TOKENS_URL = "https://nyanko-auth.ponosgames.com/v1/tokens";

  public static final Path USER_FILE_PATH = Paths.get("data/user.txt");

  public static final Path TOKEN_FILE_PATH = Paths.get("data/token.txt");

  public static final String BASE_TSV = "data/%s_%s";

  public static final String LINE_SEPARATOR = System.lineSeparator();

  public static String accountId;
  public static String password;
  public static String passwordRefreshToken;
  public static String token;
  public static Long timestamp;

  // １時間
  @Scheduled(fixedRate = 1000 * 60 * 60)
  public static void eventMain() throws Exception {
    initialize();

    String newToken = getToken();

    for (String local : LOCAL_ARRAY) {
      for (String type : FILE_TYPE_ARRAY) {
        String url = String.format(EVENT_BASE_URL, local, type) + token;

        String newLocal = null;
        if ("".equals(local)) {
          newLocal = "jp";
        } else {
          newLocal = local;
        }
        Path path = Paths.get(String.format(BASE_TSV, newLocal, type));
        writeTsv(url, path);
      }
    }
  }

  /** 初期化処理. */
  public static void initialize() throws Exception {
    if (Files.isReadable(USER_FILE_PATH)) {
      List<String> userLineList = Files.readAllLines(USER_FILE_PATH);

      if (3 <= userLineList.size()) {
        accountId = userLineList.get(0).split(" ")[1];
        password = userLineList.get(1).split(" ")[1];
        passwordRefreshToken = userLineList.get(2).split(" ")[1];

        if (accountId != null && password != null && passwordRefreshToken != null) {
          toStringUser();
        }
      }
    }

    if (Files.isReadable(TOKEN_FILE_PATH)) {
      List<String> tokenLineList = Files.readAllLines(TOKEN_FILE_PATH);

      if (2 <= tokenLineList.size()) {
        String timestampStr = tokenLineList.get(0).split(" ")[1];
        // プリミティブ型にはnullを代入できないのでラッパークラスを使用する
        timestamp = Long.valueOf(timestampStr);

        token = tokenLineList.get(1).split(" ")[1];
      }

      if (timestamp != null && token != null) {
        toStringToken();
      }
    }
  }

  public static String getToken() throws Exception {
    // アカウントがない場合は新しく作成する
    if (accountId == null || password == null || passwordRefreshToken == null) {
      createAccount();

      System.out.println("新しいアカウント");
      toStringUser();

      if (accountId == null || password == null || passwordRefreshToken == null) {
        System.err.println("新しいアカウントの作成に失敗しました。");
        return null;
      }
    }

    long nowTimestamp = Instant.now().getEpochSecond();

    // トークンは24時間で無効になるので1時間余裕を持たせる
    long maxTimestamp = 60 * 60 * 23;

    long timestampDiff = nowTimestamp - Instant.now().getEpochSecond();

    // トークンが空、期限切れの場合は新しく作成する
    if (token == null || maxTimestamp < timestampDiff) {
      generateJWTToken();
    }

    // JWTトークンがまだない場合は、パスワードを更新し、再度トークンを生成する
    if (token == null) {
      boolean passwordRefreshed = refreshPassword();

      // パスワードの更新に失敗した場合は、アカウントがBANされた可能性があるので新しく生成する
      if (!passwordRefreshed) {
        System.out.println("アカウントがBanされた可能性があります。");
        createAccount();

        if (accountId != null) {
          System.out.println("新しいアカウント");
          toStringUser();
        } else {
          return null;
        }
      } else {
        System.out.println("更新: パスワード");
      }

      generateJWTToken();

      // パスワードを更新したのにトークン生成に失敗した場合、アカウントを再生成
      if (token == null && passwordRefreshed) {
        createAccount();

        if (accountId != null) {
          System.out.println("生成: アカウント");
        } else {
          return null;
        }
      }

      generateJWTToken();

      if (token != null) {
        timestamp = System.currentTimeMillis();

        System.out.println("Successfully refreshed JWT token\n\nToken : " + token);
      } else {
        return null;
      }
    }

    return token;

  }

  private static void createAccount() throws Exception {
    accountId = null;
    password = null;
    passwordRefreshToken = null;

    // アカウント作成用のURLに接続
    HttpURLConnection connection =
        (HttpURLConnection) URI.create(CREATE_ACCOUNT_URL).toURL().openConnection();
    connection.setRequestMethod("GET");
    connection.connect();

    // 接続が成功したかチェック
    if (connection.getResponseCode() != 200) {
      connection.disconnect();
      return;
    }

    // レスポンスを読み込む
    StringBuilder result = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      result.append(line).append("\n");
    }
    reader.close();
    connection.disconnect();

    String accountData = result.toString().trim();
    if (accountData.isBlank())
      return;

    // JSONをパースして、アカウントコードを取得
    JsonElement element = JsonParser.parseString(accountData);
    if (element == null || !element.isJsonObject())
      return;

    JsonObject obj = element.getAsJsonObject();
    if (!obj.has("success") || !obj.get("success").getAsBoolean())
      return;

    accountId = obj.get("accountId").getAsString();

    // パスワードを生成
    long currentTime = System.currentTimeMillis() / 1000;
    JsonObject passwordHeaderData = new JsonObject();
    passwordHeaderData.addProperty("accountCode", accountId);
    passwordHeaderData.addProperty("accountCreatedAt", String.valueOf(currentTime));
    passwordHeaderData.addProperty("nonce", generateRandomHex(32));

    CloseableHttpClient client = HttpClientBuilder.create().build();
    CloseableHttpResponse passwordResponse =
        getPostResponse(client, USERS_URL, passwordHeaderData.toString());

    if (passwordResponse.getStatusLine().getStatusCode() != 200) {
      passwordResponse.close();
      client.close();
      return;
    }

    // パスワードのレスポンスを読み込む
    StringBuilder passwordResult = new StringBuilder();
    BufferedReader passwordReader =
        new BufferedReader(new InputStreamReader(passwordResponse.getEntity().getContent()));
    while ((line = passwordReader.readLine()) != null) {
      passwordResult.append(line).append("\n");
    }

    passwordReader.close();
    passwordResponse.close();
    client.close();

    // JSONをパースして、パスワードとリフレッシュトークンを取得
    String passwordText = passwordResult.toString();
    if (passwordText.isBlank())
      return;

    JsonElement passwordElement = JsonParser.parseString(passwordText);
    if (passwordElement == null || !passwordElement.isJsonObject())
      return;

    JsonObject passwordObject = passwordElement.getAsJsonObject();
    if (!passwordObject.has("payload"))
      return;

    JsonElement passwordPayload = passwordObject.get("payload");
    if (!passwordPayload.isJsonObject())
      return;

    JsonObject payloadObject = passwordPayload.getAsJsonObject();
    if (!payloadObject.has("password") || !payloadObject.has("passwordRefreshToken"))
      return;

    password = payloadObject.get("password").getAsString();
    passwordRefreshToken = payloadObject.get("passwordRefreshToken").getAsString();

    writeUser();
  }

  /**
   * JWTトークンを生成する
   * 
   * @return 生成されたJWTトークン
   * @throws Exception
   */
  private static void generateJWTToken() throws Exception {
    if (accountId == null || password == null) {
      System.err.println("トークンの再生成にはaccountIdとpasswordが必要です。");
      return;
    }

    // JWTトークン生成用のJSONデータを作成
    JsonObject tokenData = new JsonObject();
    tokenData.addProperty("accountCode", accountId);

    JsonObject client = new JsonObject();
    client.addProperty("countryCode", "ja");
    client.addProperty("version", "999999");

    JsonObject device = new JsonObject();
    device.addProperty("model", "XQ-BC52");

    JsonObject os = new JsonObject();
    os.addProperty("type", "android");
    os.addProperty("version", "Android 13");

    JsonObject clientInfo = new JsonObject();
    clientInfo.add("client", client);
    clientInfo.add("device", device);
    clientInfo.add("os", os);

    tokenData.add("clientInfo", clientInfo);
    tokenData.addProperty("nonce", generateRandomHex(32));
    tokenData.addProperty("password", password);

    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    CloseableHttpResponse response = getPostResponse(httpClient, TOKENS_URL, tokenData.toString());

    // レスポンスコードをチェック
    StatusLine responseCode = response.getStatusLine();
    if (responseCode.getStatusCode() != 200) {
      response.close();
      httpClient.close();
      return;
    }

    // レスポンスからJWTトークンを抽出
    StringBuilder result = new StringBuilder();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    String line;
    while ((line = reader.readLine()) != null) {
      result.append(line);
    }

    reader.close();
    response.close();
    httpClient.close();

    String text = result.toString();
    if (text.isBlank()) {
      return;
    }

    JsonElement element = JsonParser.parseString(text);
    if (element == null || !element.isJsonObject()) {
      return;
    }

    JsonObject obj = element.getAsJsonObject();
    if (!obj.has("payload")) {
      return;
    }

    JsonElement payloadElement = obj.get("payload");
    if (!payloadElement.isJsonObject()) {
      return;
    }

    JsonObject payload = payloadElement.getAsJsonObject();
    if (!payload.has("token")) {
      return;
    }

    timestamp = Instant.now().getEpochSecond();
    token = payload.get("token").getAsString();

    writeToken();

    return;
  }

  /**
   * パスワードをリフレッシュする
   * 
   * @return 成功した場合はtrue、失敗した場合はfalse
   * @throws Exception
   */
  private static boolean refreshPassword() throws Exception {
    // パスワードリフレッシュ用のJSONデータを作成
    JsonObject authData = new JsonObject();
    authData.addProperty("accountCode", accountId);
    authData.addProperty("passwordRefreshToken", passwordRefreshToken);
    authData.addProperty("nonce", generateRandomHex(32));

    CloseableHttpClient client = HttpClientBuilder.create().build();
    CloseableHttpResponse response = getPostResponse(client, PASSWORD_URL, authData.toString());

    // レスポンスコードをチェック
    StatusLine statusLine = response.getStatusLine();
    if (statusLine.getStatusCode() != 200) {
      System.out.println(statusLine.getStatusCode() + " : " + statusLine.getReasonPhrase());
      response.close();
      client.close();
      return false;
    }

    // レスポンスから新しいパスワードとリフレッシュトークンを抽出
    StringBuilder result = new StringBuilder();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    String line;
    while ((line = reader.readLine()) != null) {
      result.append(line);
    }

    String text = result.toString();
    if (text.isBlank())
      return false;

    JsonElement element = JsonParser.parseString(text);
    if (element == null || !element.isJsonObject())
      return false;

    JsonObject obj = element.getAsJsonObject();
    if (!obj.has("payload") || !obj.get("payload").isJsonObject())
      return false;

    JsonObject payload = obj.get("payload").getAsJsonObject();
    if (!payload.has("password") || !payload.has("passwordRefreshToken"))
      return false;

    password = payload.get("password").getAsString();
    passwordRefreshToken = payload.get("passwordRefreshToken").getAsString();

    writeUser();
    return true;
  }

  /**
   * POSTリクエストを送信し、レスポンスを取得する
   * 
   * @param client HttpClient
   * @param link 接続先URL
   * @param json 送信するJSONデータ
   * @return HTTPレスポンス
   * @throws Exception
   */
  private static CloseableHttpResponse getPostResponse(CloseableHttpClient client, String link,
      String json) throws Exception {
    long currentTime = System.currentTimeMillis();

    HttpPost post = new HttpPost();
    post.setURI(new URI(link));

    // ヘッダーの設定
    prepareHeader(post, json, currentTime);

    // エンティティを設定
    StringEntity entity = new StringEntity(json);
    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    post.setEntity(entity);

    return client.execute(post);
  }

  /**
   * HTTPヘッダーを設定する
   * 
   * @param connection HttpPostオブジェクト
   * @param jsonText 送信するJSONデータ
   * @param timeStamp タイムスタンプ
   * @throws Exception
   */
  private static void prepareHeader(HttpPost connection, String jsonText, long timeStamp)
      throws Exception {
    connection.setHeader("Nyanko-Signature", getNyankoSignature(jsonText));
    connection.setHeader("Nyanko-Signature-Version", String.valueOf(1));
    connection.setHeader("Nyanko-Signature-Algorithm", "HMACSHA256");
    connection.setHeader("Content-Type", "application/json");
    connection.setHeader("Nyanko-Timestamp", String.valueOf(timeStamp));
    connection.setHeader("User-Agent",
        "Dalvik/2.1.0 (Linux; U; Android 13; XQ-BC52 Build/61.2.A.0.447)");
    connection.setHeader("Connection", "Keep-Alive");
    connection.setHeader("Accept-Encoding", "gzip");
  }

  /**
   * Nyanko-Signatureヘッダーの値を生成する
   * 
   * @param jsonText JSONデータ
   * @return Nyanko-Signatureの値
   * @throws Exception
   */
  private static String getNyankoSignature(String jsonText) throws Exception {
    String randomData = generateRandomHex(64);
    return randomData + Hex.encodeHexString(
        hmacSha256((accountId + randomData).getBytes(StandardCharsets.UTF_8), jsonText));
  }

  /**
   * 指定された長さのランダムな16進数文字列を生成する
   * 
   * @param length 長さ
   * @return ランダムな16進数文字列
   */
  private static String generateRandomHex(int length) {
    Random r = new Random();
    StringBuilder hex = new StringBuilder();
    while (hex.length() < length) {
      hex.append(String.format("%08x", r.nextInt()));
    }
    return hex.toString();
  }

  /**
   * HMAC-SHA256ハッシュを計算する
   * 
   * @param key キー
   * @param content ハッシュ化する文字列
   * @return ハッシュ値のバイト配列
   * @throws Exception
   */
  private static byte[] hmacSha256(byte[] key, String content) throws Exception {
    SecretKeySpec secret = new SecretKeySpec(key, "HmacSHA256");
    Mac hasher = Mac.getInstance("HmacSHA256");

    hasher.init(secret);

    return hasher.doFinal(content.getBytes(StandardCharsets.UTF_8));
  }

  private static void toStringUser() {
    System.out.println("accountId: " + accountId + "\npassword: " + password
        + "\npasswordRefreshToken: " + passwordRefreshToken);
  }

  private static void toStringToken() {
    System.out.println("timestamp: " + timestamp + "\ntoken: " + token);
  }

  private static void writeUser() {
    try {
      String userText = "accountId " + accountId + LINE_SEPARATOR + "password " + password
          + LINE_SEPARATOR + "passwordRefreshToken " + passwordRefreshToken;
      Files.writeString(USER_FILE_PATH, userText);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void writeToken() {
    try {
      String tokenText = "timestamp " + timestamp + LINE_SEPARATOR + "token " + token;
      Files.writeString(TOKEN_FILE_PATH, tokenText);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param url urL
   * @param output tsvパス
   */
  public static void writeTsv(String url, Path output) {
    try {
      HttpClient client = HttpClient.newHttpClient();

      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

      // html, imgの両方に対応できるよう、ofInputStreamを使用する
      HttpResponse<InputStream> response =
          client.send(request, HttpResponse.BodyHandlers.ofInputStream());

      if (response.statusCode() != 200) {
        System.err.println("ステータスコード " + response.statusCode() + ": " + url);
      }

      try (InputStream is = response.body()) {
        Files.copy(is, output, StandardCopyOption.REPLACE_EXISTING);
      }

    } catch (IOException e) {
      System.err.println("入出力エラーが発生しました: " + e.getMessage());
      e.printStackTrace();
    } catch (InterruptedException e) {
      System.err.println("HTTPクライアントのリクエストが中断されました: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println("予期せぬエラーが発生しました: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
