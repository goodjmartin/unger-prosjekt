package no.goodtech.vaadin.security.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import no.cronus.common.utils.CollectionFactory;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.vaadin.global.Globals;

import no.goodtech.vaadin.utils.Bundle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.util.StringUtils;

import com.thoughtworks.xstream.XStream;

/**
 * En bruker av systemet
 */
@Entity
@Table(name = "Person")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Bundle(resourceBundle = "vaadin-security")
public class User extends AbstractEntityImpl<User> implements UserStub, Comparable<UserStub> {

	private static final long serialVersionUID = 1L;

	private static XStream xstream = new XStream();

	// User identity (i.e. login)
	@NotNull
	@Column(name = "id", nullable = false, unique = true)
	private String id;

	// User name
	private String name;

	// User password
	@NotNull
	private String password;
	
	private String email;

	// User loginFailures
	private int loginFailures;

	// Access roles assigned to user security
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "Person_AccessRole",
			joinColumns = @JoinColumn(name = "person_pk", referencedColumnName = "pk"),
			inverseJoinColumns = @JoinColumn(name = "accessRole_pk", referencedColumnName = "pk")
	)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<AccessRole> accessRoles = new HashSet<AccessRole>();

	private String roles;

	@ManyToMany
	@JoinTable(
			name = "Person_PersonnelClass",
			joinColumns = @JoinColumn(name = "person_pk", referencedColumnName = "pk"),
			inverseJoinColumns = @JoinColumn(name = "personnelClass_pk", referencedColumnName = "pk")
	)
	private Set<PersonnelClass> personnelClasses = CollectionFactory.getHashSet();

	public Set<PersonnelClass> getPersonnelClasses() {
		return personnelClasses;
	}

	public void setPersonnelClasses(Set<PersonnelClass> personnelClasses) {
		this.personnelClasses = personnelClasses;
	}

	public User() {
	}

	public User(final String id, final String password, final String[] roles) {
		this.id = id;
		this.password = password;
		setRoles(roles);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLoginFailures() {
		return loginFailures;
	}

	public void setLoginFailures(final int loginFailures) {
		this.loginFailures = loginFailures;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<AccessRole> getAccessRoles() {
		return accessRoles;
	}

	public void setAccessRoles(Set<AccessRole> accessRoles) {
		this.accessRoles = accessRoles;
	}

	public String[] getRoles() {
		return (String[]) xstream.fromXML(roles);
	}

	public void setRoles(String[] roles) {
		this.roles = xstream.toXML(roles);
		getRoles(); // sjekker
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
	public void lazyLoad() {
		accessRoles.size();
		personnelClasses.size();
	}

	@Override
	public String getNiceClassName() {
		return "[Bruker " + id + "]";
	}


	@Override
	public int compareTo(UserStub o) {
		if (id == null || o == null || o.getId() == null)
			return 0;
		return id.compareTo(o.getId());
	}

	@Override
	public String getNiceName(final boolean shortForm) {
		return shortForm ? getId() : getName();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString(){
		return getNiceName(false);
	}
	
	public Boolean isBlocked() {
		return getBlocked();
	}

	public Boolean getBlocked() {
		return loginFailures >= Globals.getMaxLoginFailureAttempts();
	}
	
	public void setBlocked(Boolean blocked) {
		if (blocked != null && blocked) {
			loginFailures = Globals.getMaxLoginFailureAttempts();
		} else {
			loginFailures = 0;
		}
	}
	
	/**
	 * @return a comma separated list of role IDs the user has 
	 */
	public String getAccessRolesAsString() {
        List<String> displayAccessRoles = new ArrayList<String>();
        for (AccessRole accessRole : accessRoles)  {
            displayAccessRoles.add(accessRole.getId());
        }
        return StringUtils.collectionToCommaDelimitedString(displayAccessRoles);
	}

	/**
	 * @return a login object that represents this user
	 */
	public no.goodtech.vaadin.login.User getLogin() {
		Set<String> accessFunctionIds = new HashSet<>();
		for (AccessRole accessRole : accessRoles)
			accessFunctionIds.addAll(accessRole.getAccessFunctionIds());
		no.goodtech.vaadin.login.User loginUser = new no.goodtech.vaadin.login.User(getPk(), id, name, accessFunctionIds);
		return loginUser;
	}
}
