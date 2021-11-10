package no.goodtech.vaadin.security.tabs.user;

import no.goodtech.vaadin.security.MD5Hash;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @deprecated use ${@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder} instead
 */
public class MD5PasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence charSequence) {
		return MD5Hash.convert(charSequence.toString());
	}

	@Override
	public boolean matches(CharSequence charSequence, String s) {
		return encode(charSequence).equals(s);
	}
}
