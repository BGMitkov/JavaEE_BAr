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

	public boolean isUserInRole(Role... roles) {
		if (currentUser != null) {
			for (Role role : roles) {
				if (currentUser.getRole() == role) {
					return true;
				}
			}
		}
		return false;
	}
}
