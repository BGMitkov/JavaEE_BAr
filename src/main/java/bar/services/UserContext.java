package bar.services;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

import bar.model.Role;
import bar.model.User;

@SessionScoped
public class UserContext implements Serializable {

	private static final long serialVersionUID = -5185469629320384569L;

	private User currentUser;

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
	public boolean isManager(){
		return currentUser.getRole() == Role.Manager;
	}
	
	public boolean isBarman(){
		return currentUser.getRole() == Role.Barman;
	}
	
	public boolean isWaiter(){
		return currentUser.getRole() == Role.Waiter;
	}
}
