package no.goodtech.vaadin.security.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import no.goodtech.persistence.PersistenceFactory;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import no.cronus.common.utils.CollectionFactory;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

/**
 * En gruppe av brukere, f.eks. et skift
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PersonnelClass extends AbstractEntityImpl<PersonnelClass> implements PersonnelClassStub {

	private static final long serialVersionUID = 1L;

	@Column(name = "id", nullable = false, unique = true)
	private String id;
	
	private String name;

	private String description;

	@ManyToMany(mappedBy = "personnelClasses")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<User> users = CollectionFactory.getHashSet();

	public PersonnelClass() {
	}

	public PersonnelClass(final String id) {
		this.id = id;
	}

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

	public Set<User> getUsers() {
		return users;
	}

	/**
	 * Legg angitt bruker til denne gruppa. Om brukeren er medlem fra før, skjer det ingenting
	 */
	public void addUser(User user) {
		users.add(user);
	}

	/**
	 * Fjern angitt bruker fra denne gruppa. Brukeren vil ikke slettes. Om bruker ikke finnes i gruppa, skjer ingenting
	 */
	public void removeUser(User user) {
		users.remove(user);
	}

	@Override
	public PersonnelClass load() {
		return getPersonnelClassRepository().load(this);
	}

	public PersonnelClass save() {
		return getPersonnelClassRepository().save(this);
	}

	@Override
	public void lazyLoad() {
		users.size();
	}

	@Override
	public String getNiceName(final boolean shortForm) {
		return shortForm ? getId() : getDescription();
	}

	@Override
	public String getNiceClassName() {
		return "skift";
	}

	private PersonnelClassRepository getPersonnelClassRepository() {
		return (PersonnelClassRepository)PersistenceFactory.getPersistenceRepositoryManager().getRepository(PersonnelClass.class.getName());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
