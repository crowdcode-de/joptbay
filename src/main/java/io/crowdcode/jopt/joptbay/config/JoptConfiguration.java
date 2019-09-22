package io.crowdcode.jopt.joptbay.config;

import io.crowdcode.jopt.joptbay.effects.DeadLock;
import io.crowdcode.jopt.joptbay.effects.MemoryGuzzler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
@Slf4j
@Configuration
public class JoptConfiguration {

	@Bean
	public MemoryGuzzler speicherfresser() throws IOException {
		return new MemoryGuzzler();
	}

	@Bean
	@Profile("deadlock")
	public DeadLock deadLock() {
		return new DeadLock().buildDeadlock();
	}
}
