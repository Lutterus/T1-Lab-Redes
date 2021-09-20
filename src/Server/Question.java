package Server;

public class Question {
	private String difficulty;
	private String question;
	private String answer;

	public Question(String difficulty, String question, String answer) {
		this.setDifficulty(difficulty);
		this.setQuestion(question);
		this.setAnswer(answer);
	}

	public String toString() {
		return "Dificuldade: " + getDifficulty() + ", Questão: " + getQuestion() + ", Resposta: " + getAnswer();
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		// Remove caracteres especiais
		difficulty = difficulty.replaceAll("[^a-zA-Z0-9]", " ");
		// Normalização
		if (difficulty.toLowerCase().contains("dificil")) {
			difficulty = "hard";
		} else if (difficulty.toLowerCase().contains("medio") || difficulty.toLowerCase().contains("media")) {
			difficulty = "medium";
		} else {
			difficulty = "eazy";
		}
		this.difficulty = difficulty;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
