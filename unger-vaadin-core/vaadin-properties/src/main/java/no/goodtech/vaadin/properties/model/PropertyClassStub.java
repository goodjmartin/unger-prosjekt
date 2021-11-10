package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.entity.EntityStub;

//TODO: JavaDoc
public interface PropertyClassStub extends EntityStub<PropertyClass>
{
	//TODO: Funksjoner i et public interface trenger ikke deklareres som public 
	public void setDescription(String description);

	public void setId(String id);
	public String getId();
	public String getDescription();
}