import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DatFileCrypter {

  private static final String KEY = "battlecats";
  private static final String USAGE =
      "Usage: java DatFileCrypter.java { decrypt | encrypt } { jp | kr | en | tw } FILE1 [ FILE2 ... ]";

  private static String locale;

  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.println(USAGE);
      return;
    }

    String command = args[0].toLowerCase();
    if (!"decrypt".equals(command) && !"encrypt".equals(command)) {
      System.err.println(USAGE);
      return;
    }

    locale = args[1].toLowerCase();

    for (int i = 0; i < args.length; i++) {
      // 0: command
      // 1: locale
      if (i < 2) {
        continue;
      }

      Path input = Paths.get(args[i]);
      if (!Files.isRegularFile(input)) {
        System.err.println(input + " は通常ファイルではありません。");
        continue;
      }

      // "decrypt".equals(command) && !"encrypt".equals(command)) {
      if ("decrypt".equals(command)) {
        decrypt(input);
      } else if ("encrypt".equals(command)) {
        encrypt(input);
      } else {
        System.err.println("commandがdecrypt、encryptのどちらでもありません。");
      }
    }
  }

  private static void decrypt(Path input) throws Exception {
    byte[] encrypted = Files.readAllBytes(input);

    // MD5ハッシュ値を取り除く
    byte[] data = Arrays.copyOfRange(encrypted, 0, encrypted.length - 32);

    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    byte[] secretKey = generateSecretKey();
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
    byte[] decrypted = cipher.doFinal(data);

    String inputName = input.getFileName().toString();
    String outputName = getOutputName(inputName);

    if (outputName == null) {
      System.err.println(outputName + " は未解析です。");
      return;
    }

    Path output = Paths.get(outputName);
    Files.write(output, decrypted);

    System.out.println("復号: " + inputName);
  }

  private static void encrypt(Path input) throws Exception {
    byte[] decrypted = Files.readAllBytes(input);

    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    byte[] secretKey = generateSecretKey();
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
    byte[] encrypted = cipher.doFinal(decrypted);

    String inputName = input.getFileName().toString();
    String outputName = getOutputName(inputName);

    if (outputName == null) {
      System.err.println(outputName + " は未解析です。");
      return;
    }

    Path output = Paths.get(outputName);
    Files.write(output, encrypted);

    // 署名 = (salt + 復号したデータ)のMD5ハッシュ値
    String salt = null;
    switch (locale) {
      case "jp":
        salt = "battlecats";
        break;
      case "kr":
        salt = "battlecatskr";
        break;
      case "en":
        salt = "battlecatsen";
        break;
      case "tw":
        salt = "battlecatstw";
        break;
      default:
        salt = "battlecats";
        break;
    }
    byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);

    byte[] data = new byte[saltBytes.length + encrypted.length];;
    System.arraycopy(saltBytes, 0, data, 0, saltBytes.length);

    System.arraycopy(encrypted, 0, data, saltBytes.length, encrypted.length);

    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] hash = md.digest(data);

    StringBuilder sb = new StringBuilder();
    for (byte b : hash) {
      // 各バイトを2桁の16進数に変換して結合
      sb.append(String.format("%02x", b));
    }

    // 32文字の16進数文字列をバイト配列（32バイト）に変換
    byte[] newHash = sb.toString().getBytes(StandardCharsets.UTF_8);

    System.out.println(Arrays.toString(hash) + "\n" + Arrays.toString(newHash));

    // 上書き
    Files.write(output, newHash, StandardOpenOption.APPEND);

    System.out.println("暗号化: " + inputName);
  }

  private static String getOutputName(String inputName) {
    String outputName = null;

    switch (inputName) {
      case "002a4b18244f32d7833fd81bc833b97f.dat":
        outputName = "sale.tsv";
        break;
      case "09b1058188348630d98a08e0f731f6bd.dat":
        outputName = "gatya.tsv";
        break;
      case "408f66def075926baea9466e70504a3b.dat":
        outputName = "item.tsv";
        break;
      case "523af537946b79c4f8369ed39ba78605.dat":
        outputName = "ad.tsv";
        break;
      case "sale.tsv":
        outputName = "002a4b18244f32d7833fd81bc833b97f.dat";
        break;
      case "gatya.tsv":
        outputName = "09b1058188348630d98a08e0f731f6bd.dat";
        break;
      case "item.tsv":
        outputName = "408f66def075926baea9466e70504a3b.dat";
        break;
      case "ad.tsv":
        outputName = "523af537946b79c4f8369ed39ba78605.dat";
        break;
    }

    return outputName;
  }

  private static byte[] generateSecretKey() throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");

    // ハッシュ値に変換する
    // battlecats: -119 -96 -7 -112 120 65 -100 40 -108 74 116 44 -71 -40 -73 -68
    byte[] byteList = md.digest(KEY.getBytes(StandardCharsets.UTF_8));

    // 16進数に変換する
    // battlecats: 89 a0 f9 90 78 41 9c 28 94 4a 74 2c b9 d8 b7 bc
    StringBuilder sb = new StringBuilder();
    for (byte b : byteList) {
      sb.append(String.format("%02x", b));
    }

    // 最初の16文字返す
    // battlecats: 89 a0 f9 90 78 41 9c 28
    String md5HashString = sb.toString().substring(0, 16);

    // バイト配列に変換する
    // battlecats: 56 57 97 48 102 57 57 48 55 56 52 49 57 99 50 56
    return md5HashString.getBytes(StandardCharsets.UTF_8);
  }

}
