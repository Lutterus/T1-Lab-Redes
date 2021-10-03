package Server;

public class Question {
	private String difficulty;
	private String text;
	private String answer;

	public Question(String difficulty, String text, String answer) {
		this.setDifficulty(difficulty);
		this.setText(text);
		this.setAnswer(answer);
	}

	public String toString() {
		return "Dificuldade: " + getDifficulty() + ", Questão: " + getText() + ", Resposta: " + getAnswer();
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		// Remove caracteres especiais
		difficulty = normalizeString(difficulty);
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

	public String getText() {
		return text.toLowerCase();
	}

	public void setText(String question) {
		this.text = question;
	}

	public String getAnswer() {
		return normalizeString(answer.toLowerCase());
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isCorrect(String userAnswer) {
		// Se a pergunta esta correta
		if (normalizeString(userAnswer).contains(getAnswer())) {
			return true;
		}
		// Se a pergutna esta errada
		return false;
	}

	public String normalizeString(String text) {
		return text.replaceAll("[^a-zA-Z0-9]", " ");
	}
}
