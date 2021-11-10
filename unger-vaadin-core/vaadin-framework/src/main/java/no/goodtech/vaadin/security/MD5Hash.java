package no.goodtech.vaadin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {

	private static final Logger logger = LoggerFactory.getLogger(MD5Hash.class);

	// Initier digest objektet
	private static final MessageDigest digest;
	static {
		MessageDigest tmpDigest;
		try {
			tmpDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			tmpDigest = null;
		}
		digest = tmpDigest;
	}

	/**
	 * Denne metoden kan kalles for å generere en MD5 hash for angitt streng.
	 *
	 * @param clearText Klar tekst streng
	 * @return MD5 hash for angitt streng
	 */
	public static String convert(final String clearText) {
		StringBuffer hexString = null;

		if (digest != null) {
			try {
				digest.update(clearText.getBytes("UTF-8"));

				byte[] hash = digest.digest();

				hexString = new StringBuffer();
				for (byte aHash : hash) {
					String digit = Integer.toHexString(0xFF & aHash);

					if (digit.length() == 1) {
						digit = "0" + digit;
					}
					hexString.append(digit);
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("UnsupportedEncodingException - " + e.getMessage());
			}
		}

		return (hexString != null) ? hexString.toString() : null;
	}

}
