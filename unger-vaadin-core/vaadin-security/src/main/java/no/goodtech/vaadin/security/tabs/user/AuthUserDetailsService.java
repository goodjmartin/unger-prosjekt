package no.goodtech.vaadin.security.tabs.user;


import no.goodtech.vaadin.security.model.UserFinder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User service for Spring Security
 */
public class AuthUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		no.goodtech.vaadin.security.model.User user = new UserFinder().setId(s).find();

		if (user != null) {
			// NB: currently, the authorities/roles does not matter. They are not handled by spring security yet.
			List<SimpleGrantedAuthority> authorities = user.getAccessRoles().stream().map(role -> new SimpleGrantedAuthority(role.getId())).collect(Collectors.toList());
			return new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(), authorities);
		}
		throw new UsernameNotFoundException(s + " is not a valid username");
	}
}
