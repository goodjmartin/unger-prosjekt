package no.goodtech.vaadin.lists;


public class Bean {

	Long pk;
	int version;
	
	public Bean(int pk, int version) {
		this.pk = Integer.valueOf(pk).longValue();
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		Bean otherBean = (Bean) obj;
		return this.pk.equals(otherBean.pk);
	}
	
	@Override
	public int hashCode() {
		return pk.intValue(); 
	}
	
	public String toString() {
		return "pk=" + pk + ", version=" + version;
	}
}
