package com.appsdeveloperblog.app.ws.mobileappws.shared;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class) // necessary to make it an integration test
@SpringBootTest // necessary to make it an integration test
public class UtilsTest {

	@Autowired
	Utils utils;
	
	@BeforeEach
	void setUp() throws Exception {

	}

	@Test
	final void testGenerateUserId() {
		String userId = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);

		assertNotNull(userId);
		assertNotNull(userId2);
		assertTrue(userId.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userId2));
	}

	@Test
	final void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("23789seiaie");
		assertNotNull(token);

		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);
	}

	@Test
	final void testHasTokenExpired() {
		String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJoZWxsbyIsImV4cCI6MTU5MjY1MDMxMn0.YZHK7lPlpS_c-Ksq0Dq7xjN7MW80G0ULbVXcfW20IqIsFHeRWi_NlViMOo-y9nbV9SsEn-HxL5TxCYFK0VVA5A";
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);
		assertTrue(hasTokenExpired);
	}
}