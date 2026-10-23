package com.example.banking_wallet;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BankingWalletApplicationTests {

	static {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		dotenv.entries()
				.forEach(entry ->
						System.setProperty(
								entry.getKey(),
								entry.getValue()
						)
				);
	}

	@Test
	void contextLoads() {
	}

}
