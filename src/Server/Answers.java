package Server;

import java.util.ArrayList;

public class Answers {

	// Usuario a quem pertence as respostas
	private User user;
	// Lista de respostas
	private ArrayList<Answer> answers;

	public Answers(User user) {
		this.setUser(user);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
