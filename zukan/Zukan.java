import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Zukan {

  private static Path dir;

  private static Path unitbuyPath;

  private static Path nyankoPictureBookDataPath;

  private static List<String> unitbuyList;

  private static List<String> nyankoPictureBookDataList;

  private static List<Path> explanationPathList;

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.out.println("第一引数：復号したファイルがあるディレクトリ");
      return;
    }

    dir = Paths.get(args[0]);
    unitbuyPath = dir.resolve("DataLocal").resolve("unitbuy.csv");
    nyankoPictureBookDataPath = dir.resolve("DataLocal").resolve("nyankoPictureBookData.csv");

    explanationPathList = sortExplanationPathList(getExplanationPathList());
    unitbuyList = Files.readAllLines(unitbuyPath);
    nyankoPictureBookDataList = Files.readAllLines(nyankoPictureBookDataPath);

    for (int i = 0; i < explanationPathList.size(); i++) {
      String isDisplay = getDisplay(i);
      String order = getOrder(i);
      String rarity = getRarity(i);
      String name = getName(i);
      System.out.println(i + "," + isDisplay + "," + order + "," + rarity + "," + name);
    }
  }

  private static String getDisplay(int index) {
    String line = nyankoPictureBookDataList.get(index);
    return line.split(",")[0];
  }

  private static String getOrder(int index) {
    String line = unitbuyList.get(index);
    return line.split(",")[14];
  }

  private static String getRarity(int index) {
    String line = unitbuyList.get(index);
    return line.split(",")[13];
  }

  private static String getName(int index) throws IOException {
    Path path = explanationPathList.get(index);
    List<String> list = Files.readAllLines(path);
    String line = list.get(0);
    return line.split(",")[0];
  }

  // Unit_Explanation1_ja.csv
  private static List<Path> getExplanationPathList() throws IOException {
    return Files.walk(dir).filter(Files::isRegularFile)
        .filter(path -> path.getFileName().toString().startsWith("Unit_Explanation"))
        .filter(path -> path.getFileName().toString().endsWith("_ja.csv"))
        .collect(Collectors.toList());
  }

  private static List<Path> sortExplanationPathList(List<Path> pathList) {
    return pathList.stream().sorted(Comparator.comparingInt(path -> {
      String fileName = path.getFileName().toString();
      String numStr = fileName.replace("Unit_Explanation", "").replace("_ja.csv", "");
      return Integer.parseInt(numStr);
    })).collect(Collectors.toList());
  }

}