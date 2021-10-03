package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class QuestionsList {
	// Lista de perguntas faceis
	private ArrayList<Question> eazyQuestions;
	// Lista de perguntas medias
	private ArrayList<Question> mediumQuestions;
	// Lista de perguntas dificeis
	private ArrayList<Question> hardQuestions;

	public QuestionsList() {
		eazyQuestions = new ArrayList<Question>();
		mediumQuestions = new ArrayList<Question>();
		hardQuestions = new ArrayList<Question>();
		loadQuestions();
	}

	// Logica para escolha e uma pergunta
	public Question getQuestion(int answered) {
		ArrayList<Question> currentQuestions;

		if (answered < 5) {
			// As perguntas 1, 2, 3 e 4 sao faceis
			currentQuestions = eazyQuestions;
		} else if (answered < 9) {
			// As perguntas 5, 6, 7 e 8 sao medias
			currentQuestions = mediumQuestions;
		} else {
			// As perguntas 9 e 10 sao dificeis
			currentQuestions = hardQuestions;
		}
		int random = 0 + (int) (Math.random() * currentQuestions.size());
		return currentQuestions.get(random);
	}

	// Le todos os arquivos da pasta para carregar as questoes
	private void loadQuestions() {
		System.out.println("Iniciando carregamento das questões...");
		String pathToFolder = "src/Server/Questions";
		File folder = new File(pathToFolder);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				Scanner input = null;
				try {
					input = new Scanner(file);
				} catch (FileNotFoundException e) {
					System.out.println("Erro !!! Ocorreu um erro ao abrir o arquivo: " + file.getName());
					e.printStackTrace();
				}
				try {
					// Formato esperado para cada .txt (separado por linha):
					// -> dificuldade (easy / medium / hard
					// -> Questão
					// -> Resposta
					String difficulty = input.nextLine();
					String question = input.nextLine();
					String answer = input.nextLine();
					Question q = new Question(difficulty, question, answer);
					if (q.getDifficulty().contains("hard")) {
						hardQuestions.add(q);
					} else if (q.getDifficulty().contains("medium")) {
						mediumQuestions.add(q);
					} else {
						eazyQuestions.add(q);
					}
					// System.out.println(q.toString());

				} catch (Exception e) {
					System.out.println("Erro !!! Não foi possível ler as linhas do arquivo: " + file.getName());
					e.printStackTrace();
				}

			}
		}
		System.out.println("Questões carregadas com sucesso !");
	}
}
