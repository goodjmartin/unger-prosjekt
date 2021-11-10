package no.goodtech.vaadin.frontpage.model;

import no.goodtech.persistence.server.AbstractRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FrontPageCardRepositoryImpl extends AbstractRepositoryImpl<FrontPageCard, FrontPageCard, FrontPageCardFinder>{
}
