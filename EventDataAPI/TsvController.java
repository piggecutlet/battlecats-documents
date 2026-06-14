package com.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TsvController {

  @Value("${file.data.path}")
  private String dataPath;

  @GetMapping("/{filename:.+\\.tsv}")
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {

    Path filePath = Paths.get(dataPath).resolve(filename).normalize();
    Resource file = new FileSystemResource(filePath);

    if (!file.exists() || !file.isReadable()) {
      // ファイルが存在しない、または読み込み不可の場合
      return ResponseEntity.notFound().build();
    }

    // ファイルの内容をレスポンスとして返す
    return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN) // TSVはテキストとして扱う
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }
}
