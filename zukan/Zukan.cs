// コンパイル方法 Zukan.exeを作成
// "C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe" Zukan.cs

// 実行方法
// Zukan.exe ファイルがあるディレクトリ

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

public class Zukan {
  private static string dir;
  private static string unitbuyPath;
  private static string nyankoPictureBookDataPath;
  private static List<string> unitbuyList;
  private static List<string> nyankoPictureBookDataList;
  private static List<string> explanationPathList;

  public static void Main(string[] args) {
    if (args.Length < 1) {
      Console.WriteLine("第一引数：復号したファイルがあるディレクトリ");
      return;
    }

    dir = args[0];
    unitbuyPath = Path.Combine(dir, "DataLocal", "unitbuy.csv");
    nyankoPictureBookDataPath = Path.Combine(dir, "DataLocal", "nyankoPictureBookData.csv");

    try {
      explanationPathList = SortExplanationPathList(GetExplanationPathList());
      unitbuyList = File.ReadAllLines(unitbuyPath, Encoding.UTF8).ToList();
      nyankoPictureBookDataList = File.ReadAllLines(nyankoPictureBookDataPath, Encoding.UTF8).ToList();

      for (int i = 0; i < explanationPathList.Count; i++) {
        string isDisplay = GetDisplay(i);
        string order = GetOrder(i);
        string rarity = GetRarity(i);
        string name = GetName(i);
        Console.WriteLine(i + "," + isDisplay + "," + order + "," + rarity + "," + name);
      }
    }
    catch (Exception ex) {
      Console.WriteLine("エラーが発生しました: " + ex.Message);
    }
  }

  private static string GetDisplay(int index) {
    string line = nyankoPictureBookDataList[index];
    return line.Split(',')[0];
  }

  private static string GetOrder(int index) {
    string line = unitbuyList[index];
    return line.Split(',')[14];
  }

  private static string GetRarity(int index) {
    string line = unitbuyList[index];
    return line.Split(',')[13];
  }

  private static string GetName(int index) {
    string path = explanationPathList[index];
    // File.ReadAllLinesはファイル全体を読み込むため、最初の1行だけ取得
    string firstLine = File.ReadLines(path, Encoding.UTF8).FirstOrDefault();
    if (firstLine == null) return "";
    return firstLine.Split(',')[0];
  }

  // Unit_Explanation1_ja.csv のリストを取得
  private static List<string> GetExplanationPathList() {
    // Directory.GetFiles で再帰的に検索 (SearchOption.AllDirectories)
    return Directory.GetFiles(dir, "Unit_Explanation*_ja.csv", SearchOption.AllDirectories).ToList();
  }

  private static List<string> SortExplanationPathList(List<string> pathList) {
    return pathList.OrderBy(path => {
      string fileName = Path.GetFileName(path);
      // "Unit_Explanation" と "_ja.csv" を除外して数値化
      string numStr = fileName.Replace("Unit_Explanation", "").Replace("_ja.csv", "");
      int result;
      if (int.TryParse(numStr, out result)) {
        return result;
      }
      return 0;
    }).ToList();
  }
}
