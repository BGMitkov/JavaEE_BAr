package bar.dao;

import java.util.Collection;
import java.util.Date;

import javax.ejb.Singleton;
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

	public void addOrder(Order order) {
		order.setDateOfOrder(new Date());
		order.setStatus(Status.WAITING);
		order.calculateTotalPrice();
		em.persist(order);
	}

	public Collection<Order> getAllWaitingOrders() {
		TypedQuery<Order> query = em.createNamedQuery("findByStatus", Order.class).setParameter("status", "WAITING");
		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
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
	
	
	public void setOrderAsAccepted(long id, User Executor) {
	
		Order orderToAccept=findById(id);
		
		orderToAccept.setStatus(Status.ACCEPTED);
		
		Executor.getCurrentOrders().add(orderToAccept);
		
	}	
	
	public void setOrderAsOverdue(long id) {
			
		Order overdueOrder=findById(id);
		overdueOrder.setStatus(Status.OVERDUE);
	}
	
	

	public float estimateProfitBetweenTwoDates(Date begDate, Date endDate){
		 Query q = em.createQuery(
	    		   "SELECT SUM(o.totalPrice) FROM Order o WHERE o.dateOfAcceptance BETWEEN :begDate AND :endDate ");
		 q.setParameter("begDate",begDate);
		 q.setParameter("endDate",endDate);
		 try {
				return (float) q.getSingleResult();
			} catch (Exception e) {
				return (Float) null;
			}	
	}

	public float estimateDailyProfit(){
		Date today=new Date();
		 Query q = em.createQuery(
	    		   "SELECT SUM(o.totalPrice) FROM Order o WHERE o.dateOfAcceptance=:today ");
		 q.setParameter("today", today);
		 try {
				return (float) q.getSingleResult();
			} catch (Exception e) {
				return (Float) null;
			}	
	}
	

}
