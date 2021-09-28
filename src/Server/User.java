package Server;

import java.util.ArrayList;

public class User {
	private String name;
	private ArrayList<Answer> answers; 

	public User(String name) {
		this.setName(name);
		answers = new ArrayList<Answer>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getAnswersSize() {
		return answers.size();
	}
}
