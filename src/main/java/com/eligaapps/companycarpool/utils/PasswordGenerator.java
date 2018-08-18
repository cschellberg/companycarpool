package com.eligaapps.companycarpool.utils;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {
	private int lowerCaseBegin = 97;
	private int upperCaseBegin = 65;
	private int numberBegin = 48;
	private char[] specialCharacters = { '!', '@', '#', '$', '%', '&', '?' };
	private int lowerCaseSize = 26;
	private int upperCaseSize = 26;
	private int numberSize = 10;

	public String generatePassword() {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int ii = 0; ii < 4; ii++) {
			if (ii == 2) {
				char ch = (char) (numberBegin + random.nextInt(numberSize));
				sb.append(ch);
			}
			if (ii == 3) {
				char ch = specialCharacters[random.nextInt(specialCharacters.length)];
				sb.append(ch);
			}
			char ch = (char) (lowerCaseBegin + random.nextInt(lowerCaseSize));
			sb.append(ch);
			ch = (char) (upperCaseBegin + random.nextInt(upperCaseSize));
			sb.append(ch);

		}
		return sb.toString();
	}

}
