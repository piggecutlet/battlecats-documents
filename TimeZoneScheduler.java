import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TimeZoneScheduler {

  private static final String USAGE =
      "Usage: java TimeZoneScheduler.java { 開始タイムゾーン: GMT-12:00 ~ GMT+14:00 }";

  private static final int COUNT = 9;
  private static final int INTERVAL = 1000 * 60 * 30;

  public static void main(String[] args) throws InterruptedException {
    if (args.length < 1) {
      System.err.println(USAGE);
      return;
    }

    if (args[0].length() != COUNT) {
      System.err.println(USAGE);
      return;
    }

    List<String> timeZones = getTimeZones();
    boolean isLoop = false;

    for (String timeZone : timeZones) {
      if (timeZone.equals(args[0])) {
        isLoop = true;
      }

      if (isLoop) {
        changeTimeZone(timeZone);
        Thread.sleep(INTERVAL);
      }
    }
  }

  private static void changeTimeZone(String timeZone) {
    List<String> command = new ArrayList<>();
    command.add("adb");
    command.add("shell");
    command.add("service");
    command.add("call");
    command.add("alarm");
    command.add("3");
    command.add("s16");
    command.add(timeZone);

    System.out.println(String.join(" ", command));

    processHelper(command);

    System.out.println();
  }

  private static List<String> getTimeZones() {
    List<String> timeZones = new ArrayList<>();
    timeZones.add("GMT+14:00");
    timeZones.add("GMT+13:30");
    timeZones.add("GMT+13:00");
    timeZones.add("GMT+12:30");
    timeZones.add("GMT+12:00");
    timeZones.add("GMT+11:30");
    timeZones.add("GMT+11:00");
    timeZones.add("GMT+10:30");
    timeZones.add("GMT+10:00");
    timeZones.add("GMT+09:30");
    timeZones.add("GMT+09:00");
    timeZones.add("GMT+08:30");
    timeZones.add("GMT+08:00");
    timeZones.add("GMT+07:30");
    timeZones.add("GMT+07:00");
    timeZones.add("GMT+06:30");
    timeZones.add("GMT+06:00");
    timeZones.add("GMT+05:30");
    timeZones.add("GMT+05:00");
    timeZones.add("GMT+04:30");
    timeZones.add("GMT+04:00");
    timeZones.add("GMT+03:30");
    timeZones.add("GMT+03:00");
    timeZones.add("GMT+02:30");
    timeZones.add("GMT+02:00");
    timeZones.add("GMT+01:30");
    timeZones.add("GMT+01:00");
    timeZones.add("GMT+00:30");
    timeZones.add("GMT+00:00");
    timeZones.add("GMT-00:30");
    timeZones.add("GMT-01:00");
    timeZones.add("GMT-01:30");
    timeZones.add("GMT-02:00");
    timeZones.add("GMT-02:30");
    timeZones.add("GMT-03:00");
    timeZones.add("GMT-03:30");
    timeZones.add("GMT-04:00");
    timeZones.add("GMT-04:30");
    timeZones.add("GMT-05:00");
    timeZones.add("GMT-05:30");
    timeZones.add("GMT-06:00");
    timeZones.add("GMT-06:30");
    timeZones.add("GMT-07:00");
    timeZones.add("GMT-07:30");
    timeZones.add("GMT-08:00");
    timeZones.add("GMT-08:30");
    timeZones.add("GMT-09:00");
    timeZones.add("GMT-09:30");
    timeZones.add("GMT-10:00");
    timeZones.add("GMT-10:30");
    timeZones.add("GMT-11:00");
    timeZones.add("GMT-11:30");
    timeZones.add("GMT-12:00");
    return timeZones;
  }

  private static boolean processHelper(List<String> command) {
    ProcessBuilder processBuilder = new ProcessBuilder(command);

    processBuilder.redirectErrorStream(true);

    Process process = null;
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = br.readLine()) != null) {
        if ("Result: Parcel(00000000    '....')".equals(line)) {
          continue;
        }
        System.out.println(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    int exitValue = 1;
    try {
      exitValue = process.waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return false;
    }

    if (0 == exitValue) {
      return true;
    } else {
      return false;
    }
  }

}
