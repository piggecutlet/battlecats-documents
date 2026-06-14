package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

public class EventDataDownloader {

  private String accountId;

  private String password;

  private String passwordRefreshToken;

  private String token;

  // private String timestamp;

  private Path tokenFilePath = Paths.get("src/main/resources/token.txt");

  private Path userFilePath = Paths.get("src/main/resources/user.txt");

  private final String[] localArray = {"JP", "KR", "EN", "TW"};

  private final String[] typeArray = {"gatya.tsv", "item.tsv", "sale.tsv"};

  public static void main(String[] args) {
    EventDataDownloader eventDataDownloader = new EventDataDownloader();

    // tokenが有効か確認する
  }

  private void getToken() {
    List<String> lineList = null;

    try {
      if (Files.notExists(tokenFilePath)) {
        System.out.println(tokenFilePath + " が存在しません。");
        this.token = getNewToken();
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      lineList = Files.readAllLines(tokenFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    }


    if (lineList.size() < 2) {
      System.out.println(tokenFilePath + " の情報が不足しています。");
      this.token = getNewToken();
      return;
    }

    if (isValidTimestamp(lineList)) {
      this.token = lineList.get(1).split(" ")[1];
    } else {
      this.token = getNewToken();
    }
  }


  private boolean isValidTimestamp(List<String> lineList) {
    String timestampStr = lineList.get(0).split(" ")[1];

    long fileTimestamp = Long.parseLong(timestampStr);

    long nowTimestamp = Instant.now().getEpochSecond();

    long timestampDiff = nowTimestamp - fileTimestamp;

    // トークンは24時間で無効になるので1時間余裕を持たせる
    long maxTimestamp = 60 * 60 * 23;

    // 82800秒(23時間)未満の場合はトークン有効
    if (timestampDiff < maxTimestamp) {
      return true;
    } else {
      return false;
    }
  }

  private String getNewToken() {
    System.out.println("新しいトークンを返します。");
    return null;
  }

}
