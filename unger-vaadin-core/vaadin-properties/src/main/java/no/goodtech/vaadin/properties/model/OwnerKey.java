package no.goodtech.vaadin.properties.model;

import no.goodtech.persistence.entity.EntityStub;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OwnerKey {
	String key;
	
	OwnerKey(Class<?> ownerClass, Long ownerPk) {
		key = ownerClass.toString() + "-" + String.valueOf(ownerPk);
	}
	
	OwnerKey(EntityStub<?> owner) {
		this(owner.getClass(), owner.getPk());
	}
	
	OwnerKey(PropertyValueStub value) {
		this(value.getOwnerClass(), value.getOwnerPk());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!obj.getClass().equals(OwnerKey.class))
			return false;
		OwnerKey other = (OwnerKey) obj;
        return key != null && other.key != null && key.equals(other.key);
    }
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(key)
		.toHashCode();
	}		
}
