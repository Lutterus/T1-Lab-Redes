package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ComunicationThread implements Runnable {
	private DatagramSocket serverSocket;
	private GameState gameState;
	private User currentUser;
	private QuestionsList questions;

	public ComunicationThread(DatagramSocket serverSocket, User currentUser, GameState gameState,
			QuestionsList questions) {
		this.serverSocket = serverSocket;
		this.currentUser = currentUser;
		this.gameState = gameState;
		this.questions = questions;
	}

	@Override
	public void run() {
		// Avisa que vai começar
		sendMessage("Aguardando outros jogadores antes de iniciar o jogo", false);
		// Se nao pode começar, avisa
		boolean canStart = false;
		while (!canStart) {
			if (gameState.canGameStart()) {
				canStart = true;
			} else {
				sendMessage("WAIT", false);
			}
		}
		// Começando o jogo
		sendMessage("O jogo irá começar, prepare-se!", false);
		// Fluxo de perguntas e respostas
		boolean stop = false;
		while (!stop) {
			Question question = sendQuestion();
			getAnswer(question);
			if (currentUser.getAnswersSize() >= (gameState.getQuantidadeDePerguntasDeUmaSessao() + 1)) {
				stop = true;
			}
		}
		// Informa o resultado do jogo
		endGame();
		System.out.println("Encerrando conexao");
	}

	private void endGame() {
		sendMessage("STOP", true);
		while (!gameState.canGameFinish()) {
			// Esperando os demais jogares terminarem
		}
		System.out.println("Enviando resultado");
		byte[] sendData = new byte[1024];
		sendData = currentUser.getAnswers().getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, currentUser.getiPAddress(),
				currentUser.getPort());

		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("erro durante envio do pacote ao cliente");
			e.printStackTrace();
		}
	}

	private void getAnswer(Question question) {
		byte[] receiveData = new byte[1024];
		// declara o pacote a ser recebido
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		// recebe o pacote do cliente
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.println("erro no recebimento do socket");
			e.printStackTrace();
		}
		// mensagem do cliente
		String sentence = new String(receivePacket.getData());
		currentUser.addAnswer(sentence, question.isCorrect(sentence));
		System.out.println("Mensagem recebida: " + sentence);

	}

	private Question sendQuestion() {
		Question question = questions.getQuestion();
		String questionText = question.getText();
		questionText = currentUser.getAnswersSize() + "-" + questionText;
		sendMessage(questionText, true);
		return question;
	}

	// Metodo para responder a um cliente
	public void sendMessage(String messageToBeSent, boolean isQuestion) {
		if (isQuestion) {
			messageToBeSent = "TRUE;" + messageToBeSent;
		} else {
			messageToBeSent = "FALSE;" + messageToBeSent;
		}
		byte[] sendData = new byte[1024];
		sendData = messageToBeSent.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, currentUser.getiPAddress(),
				currentUser.getPort());

		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("erro durante envio do pacote ao cliente");
			e.printStackTrace();
		}
	}

}
