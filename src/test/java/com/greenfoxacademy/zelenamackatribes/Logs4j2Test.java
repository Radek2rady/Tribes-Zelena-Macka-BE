package com.greenfoxacademy.zelenamackatribes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class Logs4j2Test {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void compareLogFileAfterAccessingSecuredEndpointOk() throws Exception {
    long len1 = countBytes();
    mockMvc
        .perform(post("/kingdom/buildings"))
        .andExpect(status().is(400));
    long len2 = countBytes();
    Assertions.assertNotEquals(len1, len2);
  }

  private long countBytes() throws IOException {
    return Files.readAllBytes(Paths.get("logs.log")).length;
  }
}
