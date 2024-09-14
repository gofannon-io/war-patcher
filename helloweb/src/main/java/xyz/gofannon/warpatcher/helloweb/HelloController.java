package xyz.gofannon.warpatcher.helloweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.gofannon.warpatcher.sayhello.SayHelloService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RestController
public class HelloController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);
    private final SayHelloService helloService = new SayHelloService();


    @GetMapping("/hello")
    public String sayHello() {
        return helloService.getDefaultHello();
    }



}
