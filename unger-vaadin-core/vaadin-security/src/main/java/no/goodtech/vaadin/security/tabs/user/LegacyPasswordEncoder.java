package no.goodtech.vaadin.security.tabs.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * New password encoder gradually shifting users towards a much safer password encoder (BCrypt)
 */
public class LegacyPasswordEncoder implements PasswordEncoder {

	private final MD5PasswordEncoder legacyEncoder = new MD5PasswordEncoder();

	private final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@Override
	public String encode(CharSequence rawPassword) {
		return bCryptPasswordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (legacyEncoder.matches(rawPassword, encodedPassword)) {
			SecurityUtils.updateLegacyPasswords(encodedPassword, bCryptPasswordEncoder.encode(rawPassword));
			return true;
		}

		return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
	}

	public static void main(String[] args) {
		System.out.println(new LegacyPasswordEncoder().encode("Goodtech"));
	}
}
