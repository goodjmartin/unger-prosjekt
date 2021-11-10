package no.goodtech.vaadin.security.model;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.vaadin.lists.ICopyable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "AccessRole")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AccessRole extends AbstractEntityImpl<AccessRole> implements AccessRoleStub, Comparable<AccessRoleStub>, ICopyable<AccessRole> {

	private static final long serialVersionUID = 1L;

	// Role identity
	@Column (name="id", nullable=false, unique=true)
	private String id;

	// Role description
	@Column(name = "description")
	private String description;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name="AccessRole_AccessFunction",
		joinColumns=@JoinColumn(name="accessRole_pk")
	)
    private Set<String> accessFunctionIds = new HashSet<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public Set<String> getAccessFunctionIds() {
        return accessFunctionIds;
    }

    public void setAccessFunctionIds(Set<String> accessFunctionIds) {
        this.accessFunctionIds = accessFunctionIds;
    }

	public void removeAccessFunctionId(String id){
		accessFunctionIds.remove(id);
	}

	public void addAccessFunctionId(String id){
		accessFunctionIds.add(id);
	}

    public AccessRole() {
	}

	public AccessRole(AccessRoleRepository repository) {
		this();
		this.repository = repository;
	}

	@Transient
	private transient AccessRoleRepository repository;

	public void setRepository(AccessRoleRepository repository) {
		this.repository = repository;
	}

	@Override
	public int compareTo(AccessRoleStub o) {
		if (id == null || o == null || o.getId() == null)
			return 0;
		return id.compareTo(o.getId());
	}

	@Override
	public String toString() {
		return "[AccessRole " + id + "]";
	}

	public boolean delete() {
		boolean success = false;
		try {
			success = super.delete();
		} catch (RuntimeException ignored) {
		}

		return success;
	}

	@Override
    public String getNiceClassName() {
        return "Aksessroller";
    }

	@Override
	public AccessRole copy() {
		AccessRole copy = new AccessRole();
		copy.setId(id + "(copy)");
		copy.setDescription(description);
		copy.setAccessFunctionIds(new HashSet<>(accessFunctionIds));
		copy.setRepository(repository);
		return copy;
	}

}
