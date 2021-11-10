package no.goodtech.vaadin.search;

public class OwnerClassOwnerPkDTO {
	private String ownerClass;
	private Long ownerPk;

	public OwnerClassOwnerPkDTO(String ownerClass, Long ownerPk) {
		this.ownerClass = ownerClass;
		this.ownerPk = ownerPk;
	}

	public String getOwnerClass() {
		return ownerClass;
	}

	public void setOwnerClass(String ownerClass) {
		this.ownerClass = ownerClass;
	}

	public Long getOwnerPk() {
		return ownerPk;
	}

	public void setOwnerPk(Long ownerPk) {
		this.ownerPk = ownerPk;
	}
}
