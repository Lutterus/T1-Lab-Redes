package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ComunicationThread implements Runnable {
	private DatagramSocket serverSocket;
	private GameState gameState;
	private User currentUser;
	private QuestionsList questions;
	private Users users;
	private String lastSentMessage = "";
	private boolean lastSentMessagePrint = false;

	public ComunicationThread(DatagramSocket serverSocket, User currentUser, GameState gameState,
			QuestionsList questions, Users users) {
		this.serverSocket = serverSocket;
		this.currentUser = currentUser;
		this.gameState = gameState;
		this.questions = questions;
		this.users = users;
	}

	@Override
	public void run() {
		// Avisa que vai começar
		sendMessage("Aguardando outros jogadores antes de iniciar o jogo", true);
		// Se nao pode começar, avisa
		while (true) {
			if (gameState.canGameStart()) {
				break;
			} else {
				sendMessage("WAIT", false);
			}
		}
		// Começando o jogo
		sendMessage("O jogo irá começar, prepare-se!", true);
		// Fluxo de perguntas e respostas
		boolean stop = false;
		while (!stop) {
			Question question = sendQuestion();
			while (!getAnswer(question)) {
				// Continua esperando uma mensagem
				// enquanto nao for do usuario correto
			}
			if (currentUser.getAnswersSize() == (gameState.getQuantidadeDePerguntasDeUmaSessao())) {
				stop = true;
			}
		}
		// Informa o resultado do jogo
		endGame();
		System.out.println("Encerrando conexao");
	}

	private void endGame() {
		// Avisa sobre o fim do jogo
		sendMessage("STOP", false);
		sendMessage("Aguardando demais jogadores concluirem suas partidas", true);
		// Enquanto os demais jogadores nao concluirem
		while (true) {
			if (gameState.canGameFinish()) {
				break;
			} else {
				sendMessage("WAIT", false);
			}
		}
		System.out.println("Enviando resultado");
		// Envia os resultados
		sendMessage(currentUser.getAnswers(), true);
		// Remove o jogador
		users.endGame(currentUser);
		System.out.println(users.getUser(currentUser.getPort()));
	}

	private boolean getAnswer(Question question) {
		byte[] receiveData = new byte[1024];
		// declara o pacote a ser recebido
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		// recebe o pacote do cliente
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.println("cliente nao respondeu");
			// Looping de tentar receber a mensagem
//			boolean stop = false;
//			while (!stop) {
//				System.out.println("Cliente não respondeu, enviando mensagem novamente");
//				sendMessage(lastSentMessage, lastSentMessagePrint);
//				try {
//					serverSocket.receive(receivePacket);
//					stop = true;
//				} catch (IOException e1) {
//					// Ainda nao recebeu
//				}
//			}
		}
		// mensagem do cliente
		String sentence = new String(receivePacket.getData());
		String[] parts = sentence.split(";");
		if (parts.length == 2 && currentUser.getName().contains(parts[0])) {
			currentUser.addAnswer(parts[1], question.isCorrect(sentence));
			return true;
		}
		return false;

	}

	private Question sendQuestion() {
		Question question = questions.getQuestion(currentUser.getAnswersSize());
		String questionText = question.getText();
		questionText = currentUser.getAnswersSize() + 1 + "-" + questionText;
		sendMessage(questionText, true);
		return question;
	}

	// Metodo para responder a um cliente
	public void sendMessage(String messageToBeSent, boolean print) {
		if (print) {
			messageToBeSent = "TRUE;" + messageToBeSent;
		} else {
			messageToBeSent = "FALSE;" + messageToBeSent;
		}
		lastSentMessage = messageToBeSent;
		lastSentMessagePrint = print;
		System.out.println("Enviou:" + messageToBeSent);
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
