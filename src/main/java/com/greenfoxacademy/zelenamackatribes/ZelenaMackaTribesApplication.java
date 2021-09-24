package com.greenfoxacademy.zelenamackatribes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com"})
public class ZelenaMackaTribesApplication {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(ZelenaMackaTribesApplication.class);
    application.run(args);
  }
}
