package bar.dao;

import java.util.Collection;
import java.util.Date;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import bar.model.Order;
import bar.model.Status;
import bar.model.User;

@Singleton
public class OrderDAO {

	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private UserDAO userDAO;

	public void addOrder(Order order) {
		em.persist(order);
	}

	public Collection<Order> getAllWaitingOrders() {
		TypedQuery<Order> query = em.createNamedQuery("findByStatus", Order.class)
				.setParameter("status1", Status.WAITING).setParameter("status2", Status.OVERDUE)
				.setParameter("status3", Status.ACCEPTED);
		Collection<Order> orders;
		try {
			orders = query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
		for (Order order : orders) {
			order.setItemsInOrder(order.getItemsInOrder());
			if(order.getExecutor() == null)
			order.setExecutor(order.getExecutor());
		}
		return orders;
	}

	public Collection<Order> getCurrentUserOrders(User user) {
		TypedQuery<Order> query = em.createNamedQuery("getAcceptedAndOverdue", Order.class)
				.setParameter("executor", user.getUserName()).setParameter("status1", Status.ACCEPTED)
				.setParameter("status2", Status.OVERDUE);
		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Order findById(long key) {
		return em.find(Order.class, key);
	}

	public void setOrderAsAccepted(Order orderToAccept, User executor) {
		Order foundOrder = findById(orderToAccept.getOrderId());
		User foundExecutor = userDAO.findUserByName(executor.getUserName());
		foundOrder.setStatus(Status.ACCEPTED);
		foundOrder.setDateOfAcceptance(new Date());
		foundOrder.setExecutor(foundExecutor);
	}

	public void setOrderAsOverdue(Order orderOverdue) {
		Order foundOrder = findById(orderOverdue.getOrderId());
		foundOrder.setStatus(Status.OVERDUE);
	}
	
	public void setOrderAsCompleted(Order orderCompleted) {
		Order foundOrder = findById(orderCompleted.getOrderId());
		if(foundOrder.getStatus() == Status.ACCEPTED) {
			foundOrder.setStatus(Status.COMPLETE);
		}
		else if (foundOrder.getStatus() == Status.OVERDUE) {
			foundOrder.setStatus(Status.OVERDUE_COMPLETED);
		}
	}

	public float estimateProfitBetweenTwoDates(Date begDate, Date endDate) {
		Query q = em.createQuery(
				"SELECT SUM(o.totalPrice) FROM Order o WHERE o.dateOfAcceptance BETWEEN :begDate AND :endDate ");
		q.setParameter("begDate", begDate);
		q.setParameter("endDate", endDate);
		try {
			return (float) q.getSingleResult();
		} 
		catch (Exception e) {
			return 0f;
		}
	}

	public float estimateDailyProfit() {
		Date today = new Date();
		Query q = em.createQuery("SELECT SUM(o.totalPrice) FROM Order o WHERE o.dateOfAcceptance=:today ");
		q.setParameter("today", today);
		try {
			return (float) q.getSingleResult();
		} catch (Exception e) {
			return (Float) null;
		}
	}

}
