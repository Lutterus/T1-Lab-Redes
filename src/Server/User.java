package Server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class User {
	private String name;
	private ArrayList<Answer> answers;
	private InetAddress iPAddress;
	private int port;

	public User(String name, InetAddress iPAddress, int port) {
		this.setName(name);
		this.setiPAddress(iPAddress);
		this.setPort(port);
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

	public void addAnswer(String answer, boolean iscorrect) {
		answers.add(new Answer(answer, iscorrect));
	}

	public String getAnswers() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int index = 0; index < answers.size(); index++) {
			System.out.println(answers.get(index).getText());
			String isCorrect = "";
			if (answers.get(index).isCorrect()) {
				isCorrect = "(V)";
			} else {
				isCorrect = "(X)";
			}
			String text = isCorrect + Integer.toString(index + 1) + "-" + answers.get(index).getText() + "a;";
			System.out.println("text:" + text);
			stringBuilder.append(text);
		}
		String finalString = stringBuilder.toString();
		System.out.println("final:" + finalString);
		return finalString;
	}

	public InetAddress getiPAddress() {
		return iPAddress;
	}

	public void setiPAddress(InetAddress iPAddress) {
		this.iPAddress = iPAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
