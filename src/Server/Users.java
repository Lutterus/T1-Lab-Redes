package Server;

import java.net.InetAddress;
import java.util.ArrayList;

public class Users {
	// Lista de perguntas faceis
	private ArrayList<User> users;

	public Users() {
		users = new ArrayList<User>();

	}

	public void addUser(String name, InetAddress iPAddress, int port) {
		users.add(new User(name, iPAddress, port));
	}

	public User getUser(int port) {
		for (User user : users) {
			if (user.getPort() == port) {
				return user;
			}
		}
		return null;
	}

	public int getSize() {
		return users.size();
	}

	public int haveAllPlayersResponded() {
		int smallest = 999;
		for (User user : users) {
			int quantAnswers = user.getAnswersSize();
			if (quantAnswers <= smallest) {
				smallest = quantAnswers;
			}
		}
		System.out.println("smallest:" + smallest);
		return smallest;
	}

	public void endGame(User currentUser) {
		users.removeIf(t -> t.getPort() == currentUser.getPort());

	}
}
