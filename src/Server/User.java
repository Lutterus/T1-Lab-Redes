package Server;

import java.util.ArrayList;

public class User {
	private String name;
	private ArrayList<Answer> answers;
	private Question lastQuestion;

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
		return answers.size() + 1;
	}

	public void addAnswer(String answer) {
		answers.add(new Answer(answer, lastQuestion.isCorrect(answer)));
	}

	public Question getLastQuestion() {
		return lastQuestion;
	}

	public String getLastQuestionText() {
		return lastQuestion.getText();
	}

	public void setLastQuestion(Question lastQuestion) {
		this.lastQuestion = lastQuestion;
	}

	public String getAnswers() {
		int index = 1;
		String text = "";
		for (Answer answer : answers) {
			System.out.println("aaa");
			if (answer.isCorrect()) {
				text += "(V)";
			} else {
				text += "(X)";
			}
			text += Integer.toString(index) + "- ";
			text += " " + answer.getText() + "\n";
			index++;

		}
		return text;
	}
}
