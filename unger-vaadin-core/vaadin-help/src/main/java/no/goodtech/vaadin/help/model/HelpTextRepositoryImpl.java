package no.goodtech.vaadin.help.model;

import no.goodtech.persistence.server.AbstractRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HelpTextRepositoryImpl extends AbstractRepositoryImpl<HelpText, HelpText, HelpTextFinder>{
}
