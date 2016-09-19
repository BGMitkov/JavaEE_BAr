package bar.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "ORDERS")
@NamedQueries({ @NamedQuery(name = "findById", query = "SELECT o FROM Order o WHERE o.status = :status"),
		@NamedQuery(name = "findByStatus", query = "SELECT o FROM Order o WHERE o.status = :status1 OR o.status = :status2 OR o.status = :status3"),
		@NamedQuery(name = "getAcceptedAndOverdue", query = "SELECT o FROM Order o WHERE o.executor = :executor AND (o.status = :status1 OR o.status = :status2)"),
		@NamedQuery(name = "setOrderAsOverdue", query = "SELECT o FROM Order o WHERE o.executor = :executor AND (o.status = :status1 OR o.status = :status2)"),
		@NamedQuery(name = "findAccepted", query = "SELECT o FROM Order o WHERE o.status = :status")})

public class Order implements Serializable {

	private static final long serialVersionUID = 735934458877201921L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "orderId")
	private Long orderId;

	@ManyToMany
	@JoinTable(name = "ORDER_HAS_ITEMS", joinColumns = {
			@JoinColumn(name = "orderId", referencedColumnName = "orderId") }, inverseJoinColumns = {
					@JoinColumn(name = "itemId", referencedColumnName = "itemId") })
	private List<Item> itemsInOrder;

	@Column(name = "tableNumber")
	private String tableNumber;

	@ManyToOne
	private User executor;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "dateOfOrder")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOfOrder;

	@Column(name = "dateOfAcceptance")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOfAcceptance;

	@Column(name = "totalPrice")
	private float totalPrice;

	public Order() {
	}

	public Order(List<Item> itemsInOrder, String tableNumber) {
		super();
		this.itemsInOrder = itemsInOrder;
		this.tableNumber = tableNumber;
		this.status = Status.WAITING;
		this.dateOfOrder = new Date();
		this.totalPrice = 0.0f;
	}

	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderId(final Long id) {
		this.orderId = id;
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
	
	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	public void calculateTotalPrice() {
		float sumPrice = 0.0f;
		for (Item item : itemsInOrder) {
			sumPrice += Float.parseFloat(item.getPrice());
		}
		setTotalPrice(sumPrice);
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";

		if (orderId != null)
			result += ", id: " + orderId;
		if (executor != null)
			result += "executor: " + executor;
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
		if (orderId != null) {
			if (!orderId.equals(other.orderId)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
		return result;
	}
}