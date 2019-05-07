package io.crowdcode.jopt.joptbay.config;

import io.crowdcode.jopt.joptbay.effects.MemoryGuzzler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JoptConfiguration {


    @Bean
    public MemoryGuzzler speicherfresser() throws IOException {
        return new MemoryGuzzler();
    }
}
