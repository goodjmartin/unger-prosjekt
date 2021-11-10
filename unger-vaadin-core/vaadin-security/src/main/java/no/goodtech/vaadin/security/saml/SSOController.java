package no.goodtech.vaadin.security.saml;

import com.vaadin.annotations.Widgetset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Widgetset("vaadinWidgetSet")
@RequestMapping("/saml")
@Profile(value = {"saml"})
public class SSOController {

	// Logger
	private static final Logger LOG = LoggerFactory.getLogger(SSOController.class);

	@Autowired
	private MetadataManager metadata;

	/*
	@RequestMapping(value = "/discovery", method = RequestMethod.GET)
	public String idpSelection(HttpServletRequest request, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			LOG.info("Current authentication instance from security context is null");
		else
			LOG.info("Current authentication instance from security context: " + this.getClass().getSimpleName());
		if (auth == null || (auth instanceof AnonymousAuthenticationToken)) {
			Set<String> idps = metadata.getIDPEntityNames();
			for (String idp : idps)
				LOG.info("Configured Identity Provider for SSO: " + idp);
			model.addAttribute("idps", idps);
			return "/idpselection";
		} else {
			LOG.warn("The current user is already logged.");
			return "redirect:/gmi";
		}
	}*/

}
