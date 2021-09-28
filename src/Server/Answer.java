package Server;

public class Answer {
	private String text;
	private boolean isCorrect;

	public Answer(String text, boolean isCorrect) {
		System.out.println("adicionou uam nova resposta: " + text);
		System.out.println("correta: " + isCorrect);
		setText(text);
		setCorrect(isCorrect);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
}
