import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LlocalizableSort {

  public static void main(String[] args) throws IOException {
    // sort();
    check();
  }

  private static void sort() throws IOException {
    List<String> lines = Files.readAllLines(Paths.get("localizable_all.csv"));

    for (int i = 0; i < 100; i++) {
      for (String line : lines) {
        String match = line.split("\t")[0];

        String num = String.valueOf(i);

        String param = "scheme_item_param_" + num;
        if (param.equals(match)) {
          System.out.println(line);
        }

        String popup = "scheme_popup_" + num;
        if (popup.equals(match)) {
          System.out.println(line);
        }
      }
    }
  }

  private static void check() throws IOException {
    List<String> lines = Files.readAllLines(Paths.get("localizable_sort.csv"));

    int count = 0;

    for (String line : lines) {
      String match = line.split("\t")[0];

      String num = null;

      if (match.startsWith("scheme_item_param_")) {
        num = match.replace("scheme_item_param_", "");
      }

      if (match.startsWith("scheme_popup_")) {
        num = match.replace("scheme_popup_", "");
      }

      if (num.equals(String.valueOf(count))) {
        System.out.println(num + " = " + line);
      } else {
        System.err.println(num + " != " + line);
      }

      if (match.startsWith("scheme_popup_")) {
        count++;
      }
    }
  }

}
