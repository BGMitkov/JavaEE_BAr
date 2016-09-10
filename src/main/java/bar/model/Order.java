package bar.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@XmlRootElement
@Table(name = "ORDERS")
@NamedQueries({ @NamedQuery(name = "findById", query = "SELECT o FROM Order o WHERE o.status = :status"),
		@NamedQuery(name = "findByStatus", query = "SELECT o FROM Order o WHERE o.status = :status"),
		@NamedQuery(name = "getAcceptedAndOverdue", query = "SELECT o FROM Order o WHERE o.executor = :executor AND (o.status = :status1 OR o.status = :status2)"),
		@NamedQuery(name = "setOrderAsOverdue", query = "SELECT o FROM Order o WHERE o.executor = :executor AND (o.status = :status1 OR o.status = :status2)"), })

public class Order implements Serializable {

	private static final long serialVersionUID = 735934458877201921L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToMany
	private List<Item> itemsInOrder;
	
	private String tableNumber;
	
	@ManyToOne
	private User executor;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Temporal(TemporalType.DATE)
	private Date dateOfOrder;

	@Temporal(TemporalType.DATE)
	private Date dateOfAcceptance;

	private float totalPrice;

	public Order(){
	}
	
	public Order(List<Item> itemsInOrder, String tableNumber) {
		super();
		this.itemsInOrder = itemsInOrder;
		this.tableNumber = tableNumber;
		this.status = Status.WAITING;
		this.dateOfOrder = new Date();
		this.totalPrice = 0.0f;
	}

	/*public Order() {
		this.status = Status.WAITING;
		this.dateOfAcceptance = new Date();

		this.totalPrice = 0.0f;
		this.tableNumber = -1;
	}*/

	/*public Order( List<Item> itemsInOrder) {
		super();
		this.status = Status.WAITING;
		this.dateOfOrder=new Date();
		this.totalPrice = 0.0f;
		this.itemsInOrder=itemsInOrder;

		this.tableNumber = -1;
	}*/

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public User getExecutor() {
		return executor;
	}

	public void setExecutor(User executor) {
		this.executor = executor;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}


	public Date getDateOfOrder() {
		return dateOfOrder;
	}

	public void setDateOfOrder(Date dateOfOrder) {
		this.dateOfOrder = dateOfOrder;
	}
	

	public Date getDateOfAcceptance() {
		return dateOfAcceptance;
	}

	public void setDateOfAcceptance(Date dateOfAcceptance) {
		this.dateOfAcceptance = dateOfAcceptance;
	}

	public List<Item> getItemsInOrder() {
		return itemsInOrder;
	}

	public void setItemsInOrder(List<Item> itemsInOrder) {
		this.itemsInOrder = itemsInOrder;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}

	private void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void calculateTotalPrice() {
		float sumPrice = 0.0f;
		for (Item item : itemsInOrder) {
			sumPrice += Float.parseFloat(item.getPrice());
		}
		setTotalPrice(sumPrice);
	}

	public String getTableNumber() {
		return tableNumber;
	}

	void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";

		if (id != null)
			result += ", id: " + id;
		if (executor != null)
			result += "executor: " + executor.getUserName();
		if (status != null)
			result += ", status: " + status;
		if (dateOfOrder != null)
			result += ", dateOfOrder: " + dateOfOrder.toString();
		if (itemsInOrder != null && !itemsInOrder.isEmpty()) {
			result += "\nordered items:\n";
			for (Item item : itemsInOrder) {
				result += " " + item.getItemName() + ": " + item.getPrice() + "\n";
			}
			if (getTotalPrice() != 0.0f)
				calculateTotalPrice();
			result += "Total price: " + getTotalPrice() + "\n";
		}
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Order)) {
			return false;
		}
		Order other = (Order) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
}