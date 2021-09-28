package Server;

import java.util.ArrayList;

public class Users {
	// Lista de perguntas faceis
	private ArrayList<User> users;

	public Users() {
		users = new ArrayList<User>();

	}

	public void addUser(String name) {
		users.add(new User(name));
	}

	public User getUser(String name) {
		for (User user : users) {
			if (user.getName().toLowerCase().contentEquals(name)) {
				return user;
			}
		}
		return null;
	}
}
