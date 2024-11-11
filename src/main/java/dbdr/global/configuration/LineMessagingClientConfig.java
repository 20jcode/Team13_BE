package dbdr.global.configuration;

import com.linecorp.bot.client.LineMessagingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class LineMessagingClientConfig {

	@Value("${line.bot.channel-token}")
	private String channelToken;

	@Bean
	@Profile("!test")
	public LineMessagingClient lineMessagingClient() {
		return LineMessagingClient.builder(channelToken).build();
	}
}
