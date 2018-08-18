package com.eligaapps.companycarpool.utils;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;

public class MailUtils {
	
	public static SimpleMailMessage getActivationMessage(String carpoolHost, int carpoolPort,String email, String activationKey){
		SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
		simpleMailMessage.setTo(email);
		StringBuilder sb=new StringBuilder();
		sb.append("To activate your account please click on the link below:\n\n\n").
		append("http://").append(carpoolHost).append(":").append(carpoolPort).append("/activate?activationKey=")
		.append(activationKey).append("&email=").append(email);
		simpleMailMessage.setSubject("Carpool Activation");
		simpleMailMessage.setText(sb.toString());
		return simpleMailMessage;
	}

	public static SimpleMailMessage getForgotPasswordMessage(String email, String generatedPassword) {
		SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
		simpleMailMessage.setTo(email);
		StringBuilder sb=new StringBuilder();
		sb.append("You have requested a new password.  Your new password is:\n\n"+generatedPassword);
		simpleMailMessage.setSubject("Forgot Password Request");
		simpleMailMessage.setText(sb.toString());
		return simpleMailMessage;
	}

}
