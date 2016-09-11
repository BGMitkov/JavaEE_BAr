package bar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

@Entity
@XmlRootElement
@Table(name = "ITEMS")
@NamedQueries({
		@NamedQuery(name = "findByPriceAndName", query = "SELECT i FROM Item i WHERE i.itemName = :name AND i.price = :price"),
		@NamedQuery(name = "getAllItems", query = "SELECT i FROM Item i")})
public class Item implements Serializable {

	private static final long serialVersionUID = 4654489222889729922L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "itemId")
	private Long itemId;

	@Column(name = "itemName")
	private String itemName;

	@Column(name = "price")
	private String price;
	
	@Column(name = "type")
	private String type;

	@Column(name = "description")
	private String description;
	
	@ManyToMany(mappedBy = "itemsInOrder")
	private List<Order> ordersForItems;

	public Item() {
	}

	public Item(String itemName, String price, String type, String description) {
		super();
		this.itemName = itemName;
		this.price = price;
		this.type = type;
		this.description = description;
	}

	public Long getItemId() {
		return this.itemId;
	}

	public void setItemId(final Long id) {
		this.itemId = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (itemName != null && !itemName.trim().isEmpty())
			result += "name: " + itemName;
		if (itemId != null)
			result += ", id: " + itemId;
		if (itemName != null && !itemName.trim().isEmpty())
			result += "name: " + itemName;
		if (price != null && !price.trim().isEmpty())
			result += ", price: " + price;
		if (type != null && !type.trim().isEmpty())
			result += ", type: " + type;
		result += ", description: " + description;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Item)) {
			return false;
		}
		Item other = (Item) obj;
		if (itemId != null) {
			if (!itemId.equals(other.itemId)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		return result;
	}
}