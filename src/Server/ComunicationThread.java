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
	private String lastReceivedMessage = "";
	private String lastAnswer = "";

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
		// Começando o jogo
		startGame();
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

	// Logica de inicio de game, cnfirmando cadastro do usuario
	private void startGame() {
		// Avisa que vai começar
		sendMessage("Aguardando outros jogadores antes de iniciar o jogo", true);
		// Espera a confirmação de cadastro do usuario
		receiveMessage();
		// Se nao pode começar, avisa
		while (true) {
			if (gameState.canGameStart()) {
				break;
			} else {
				sendMessage("WAIT", false);
			}
		}
		sendMessage("O jogo irá começar, prepare-se!", true);
		receiveMessage();
	}

	// Logica de fim de jogo
	private void endGame() {
		// Avisa sobre o fim do jogo
		sendMessage("STOP", true);
		receiveMessage();
		sendMessage("Aguardando demais jogadores concluirem suas partidas", true);
		receiveMessage();
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
		receiveMessage();
		// Remove o jogador
		users.endGame(currentUser);
		System.out.println(users.getUser(currentUser.getPort()));
	}

	// Obtenção de uma mensagem qualquer

	// Logica para obter uma resposta para uma pergunta
	private boolean getAnswer(Question question) {
		receiveMessage();
		String[] parts = lastReceivedMessage.split(";");
		if (parts.length == 2 && currentUser.getName().contains(parts[0])) {
			if (!lastAnswer.contains(parts[1])) {
				lastAnswer = parts[1];
				currentUser.addAnswer(parts[1], question.isCorrect(parts[1]));
			}
			return true;
		}
		return false;

	}

	// Logica para enviar uma pergunta
	private Question sendQuestion() {
		Question question = questions.getQuestion(currentUser.getAnswersSize());
		String questionText = question.getText();
		questionText = currentUser.getAnswersSize() + 1 + "-" + questionText;
		sendMessage(questionText, true);
		return question;
	}

	// Envio de mensagem
	public void sendMessage(String messageToBeSent, boolean print) {
		if (print) {
			messageToBeSent = messageToBeSent.replace("TRUE;", "");
			messageToBeSent = "TRUE;" + messageToBeSent;
		} else {
			messageToBeSent = messageToBeSent.replace("FALSE;", "");
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

	// Recebimento de mensagem
	public void receiveMessage() {
		byte[] receiveData = new byte[1024];
		// declara o pacote a ser recebido
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		// recebe o pacote do cliente
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			// Looping de tentar receber a mensagem
			boolean stop = false;
			while (!stop) {
				System.out.println("Cliente não respondeu, enviando mensagem novamente");
				sendMessage(lastSentMessage, lastSentMessagePrint);
				try {
					serverSocket.receive(receivePacket);
					stop = true;
				} catch (IOException e1) {
					// Ainda nao recebeu
				}
			}
		}
		lastReceivedMessage = new String(receivePacket.getData());
	}

}
