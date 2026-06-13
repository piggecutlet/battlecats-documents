import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileUpdater {

    public static void main(String[] args) {
        Path allFilePath = Paths.get("all.txt");
        Path resultFilePath = Paths.get("result.txt");

        try {
            // result.txtが存在しない場合は作成する
            if (Files.exists(resultFilePath)) {
                Files.delete(resultFilePath);
            }

            Files.createFile(resultFilePath);

            // all.txtの各行を読み込む
            List<String> allLines = Files.readAllLines(allFilePath);

            for (String line : allLines) {
                // result.txtの内容をすべて読み込む
                String resultContent = Files.readString(resultFilePath);

                if (line.equals("")) {
                    System.out.println("空白: " + line);
                    continue;
                }

                if (line.contains(".html")) {
                    System.out.println("HTML: " + line);
                    continue;
                }

                // result.txtにその行が含まれているかチェック
                if (!resultContent.contains(line)) {
                    // 含まれていなければ追記する
                    Files.writeString(resultFilePath, line + System.lineSeparator(), StandardOpenOption.APPEND);
                    System.out.println("追記: " + line);
                } else {
                    System.out.println("スルー: " + line);
                }
            }

            System.out.println("処理が完了しました。");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}