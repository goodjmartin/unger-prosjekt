package no.goodtech.vaadin.category;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.vaadin.lists.ICopyable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Customer specific status
 * @see CategoryFinder to find statuses in the database
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category extends AbstractEntityImpl<Category> implements Comparable<Category>, ICopyable<Category> {

	public static class Fields {
		public static final String PK = "pk";
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String INDEX_NO = "indexNo";
		public static final String DESCRIPTION = "description";
		public static final String OWNER = "owner";
		public static final String COLOR = "color";
		public static final String ICON_NAME = "iconName";
	}
	
	@NotNull
	@Size(max=64)
	private String id;

	@NotNull
	@Size(max=64)
	private String name;
	
	@NotNull
	@Min(0)
	@Max(255)
	private int indexNo;
	
	@Size(max=8000)
	private String description;
	
	@NotNull
	@Size(max=255)
	private String owner;
	
	private Integer color;
	
	@Size(max=255)
	private String iconName;

	protected Category() {}
	
	/**
	 * Create a status with name = id 
	 * @param idAndName ID and name
	 */
	public Category(String idAndName) {
		this(idAndName, idAndName, (Class<?>) null, 1);
	}

	public Category(String id, Class<?> ownerClass) {
		this(id, id, ownerClass, 1);
	}

	public Category(String id, String ownerClass) {
		this(id, id, ownerClass, 1);
	}

	public Category(String id, String name, String ownerClass, int index) {
		super();
		this.id = id;
		this.name = name;
		this.indexNo = index;
		this.owner = ownerClass;
	}

	public Category(String id, String name, Class<?> ownerClass, int index) {
		this(id, name, (ownerClass != null) ? ownerClass.getSimpleName() : null, index);
	}

	/**
	 * @return a copy of this
	 */
	public Category copy() {
		Category copy = new Category();
		copy.id = id;
		copy.name = name;
		copy.indexNo = indexNo;
		copy.description = description;
		copy.owner = owner;
		copy.color = color;
		copy.iconName = iconName;
		return copy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return text shown on screen
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name text shown on screen
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @param ownerClass the type that this status is made for
	 */
	public void setOwnerClass(Class<?> ownerClass) {
		this.owner = (ownerClass != null) ? ownerClass.getSimpleName() : null;
	}

	/**
	 * @return RGB value of the color you should use when you show this status. Null = color is not relevant. 
	 */
	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	/**
	 * @return sequence number 
	 */
	public int getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(int indexNo) {
		this.indexNo = indexNo;
	}

	public int compareTo(Category other) {
		return indexNo - other.indexNo;
	}

	/**
	 * 
	 * @return name of 
	 */
	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}


	public String getStyleName(){
		return id + "_" + owner;
	}
	
}
