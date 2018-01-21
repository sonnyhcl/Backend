package supplychain.activiti.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import supplychain.util.HelloTest;

@Configuration
public class CustomBeans {
    @Bean
    public HelloTest helloTest() {
        return new HelloTest();
    }


}
