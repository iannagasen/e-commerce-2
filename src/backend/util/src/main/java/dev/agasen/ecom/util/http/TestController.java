package dev.agasen.ecom.util.http;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
  
  private final ServiceUtil serviceUtil;

  @GetMapping
  public Map<String, String> test() {
    return Map.of(
      "message", "Hello from the other side!",
      "serviceAddress", serviceUtil.getServiceAddress()
    );
  }

}
