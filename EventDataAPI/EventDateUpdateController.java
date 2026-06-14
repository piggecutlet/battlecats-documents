package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventDateUpdateController {

  // インスタンスではなく、直接staticメソッドを呼び出すように変更
  @GetMapping("/afa83373b57f605b332053021a3248d4")
  public String updateData() {
    try {
      EventFileGrabber2.eventMain(); // クラス名から直接呼び出す
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "更新完了";
  }

}
