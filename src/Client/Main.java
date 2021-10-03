package Client;

import java.io.*;
import java.net.*;

public class Main {
	private static int timeoutSeconds = 2;
	private static int serverPort = 9876;
	private static int clientPort = 9877;
	private static InetAddress IPAddress;
	private static DatagramSocket clientSocket;
	private static String myName = "";
	private static String message = "";
	private static String question = "";
	private static String lastReceivedMessage = "";
	private static int maxAttempsToRegister = 5;

	public static void main(String[] args) {
		startGame();
		while (true) {
			message = "";
			question = "";
			System.out.println("Deseja jogar novamente? S/N");
			getUserInput();
			if (message.toLowerCase().contains("n")) {
				break;
			}
			startGame();
		}
		// fecha o cliente
		clientSocket.close();

	}

	// Controlador do jogo
	private static void startGame() {
		System.out.println("Iniciando...");
		// Configura socket para o cliente
		setClientSocket();
		// obtem endereco ip do servidor com o DNS
		setIp();
		// Registra jogador
		registerPlayer();
		System.out.println("registou");
		// Espera o jogo estar pronto para começar
		waitForGametoStart();
		// Inicia jogo
		gameFlow();
		System.out.println("");
		endGame();
	}

	// Leitura do teclado
	private static void getUserInput() {
		// cria o stream do teclado
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// le uma linha do teclado
		String sentence = null;
		try {
			sentence = inFromUser.readLine();
		} catch (IOException e) {
			System.out.println("erro durante leitura do input do cliente");
			e.printStackTrace();
		}
		message = sentence;
	}

	// Configuração de socket
	private static void setClientSocket() {
		boolean foundAvailablePort = false;
		while (!foundAvailablePort) {
			System.out.println("Tentando a porta: " + clientPort);
			try {
				clientSocket = new DatagramSocket(clientPort);
				foundAvailablePort = true;
				clientSocket.setSoTimeout(timeoutSeconds * 1000);
			} catch (SocketException e) {
				System.out.println("Erro! porta ja em uso");
				clientPort++;
			}
		}
		System.out.println("Porta livre encontrada");

	}

	// Configuração de ip
	private static void setIp() {
		try {
			IPAddress = InetAddress.getByName("localhost");
			System.out.println("Conectado ao servidor com sucesso");
		} catch (UnknownHostException e) {
			System.out.println("");
			System.out.println("erro durante obtencao do ip do servidor");
			e.printStackTrace();
		}

	}

	// Registro do jogador
	private static void registerPlayer() {
		System.out.println("Olá! Nos informe seu nome");
		getUserInput();
		myName = message;
		while (true) {
			// Envia nome
			postMessage();
			System.out.println("enviou");
			// Tenta receber a confirmação de cadastro
			getMessage(true);
			System.out.println("recebeu");
			// Se recebeu, o jogador foi registrado
			if (!question.contentEquals("")) {
				showMessage(true);
				System.out.println("mostrou");
				break;
			} else {
				// Se nao, tenta outra porta
				System.out.println("Tentando com uma porta diferente");
				serverPort--;
			}
		}
	}

	// Espera o jog começar, esperando a chegada da mensagem especificaF
	private static void waitForGametoStart() {
		System.out.println("esperando");
		postMessage();
		getMessage(false);
		showMessage(false);

	}

	private static void getMessage(boolean isRegistration) {
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			clientSocket.receive(receivePacket);
			question = new String(receivePacket.getData());
		} catch (IOException e) {
			question = "";
			// Looping de tentar receber a mensagem
			System.out.println("Servidor nao respondeu");
			if (isRegistration) {
				handleLagRegistration(receivePacket);
			} else {
				handleLagGameFlow(receivePacket);
			}
		}
	}

	public static void handleLagRegistration(DatagramPacket receivePacket) {
		int attemp = maxAttempsToRegister;
		while (question.contentEquals("") && attemp > 0) {
			System.out.println("Servidor nao respondeu, enviando mensagem novamente");
			postMessage();
			try {
				clientSocket.receive(receivePacket);
				question = new String(receivePacket.getData());
			} catch (IOException e1) {
				// Ainda nao recebeu
			}
			attemp--;
		}
	}

	public static void handleLagGameFlow(DatagramPacket receivePacket) {
		while (question.contentEquals("")) {
			System.out.println("Servidor nao respondeu, enviando mensagem novamente");
			postMessage();
			try {
				clientSocket.receive(receivePacket);
				question = new String(receivePacket.getData());
			} catch (IOException e1) {
				// Ainda nao recebeu
			}
		}
	}

	// Envio de mensagem
	private static void postMessage() {
		String sentence = myName + ";" + message;
		byte[] sendData = new byte[1024];
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("erro durante envio do pacote ao servidor");
			e.printStackTrace();
		}
	}

	// Fluxo de perguntas e respostas do jogo
	private static void gameFlow() {
		while (true) {
			getMessage(false);
			// Se for o aviso de fim de jogo, para
			if (question.contains("STOP")) {
				break;
			}
			// Se nao
			// Mostra pergunta em tela
			showMessage(false);
			// Pega resposta do cliente
			getUserInput();
			// Envia para o servidor
			postMessage();
		}
	}

	// Apreseta a mensagem em tela, formatada
	private static void showMessage(boolean isRegistration) {
		// Importante!
		// Para evitar timeout, foi feita a gambi de que
		// Quando é necessario esperar, continuamente é enviado mensagens
		// Para tanto, só mostra em tela quando chegar uma mensagem com a tag TRUE
		while (true) {
			String[] parts = question.split(";");
			// Tipo da mensagem recebida
			String print = parts[0];
			// Mensagem
			String message = parts[1];
			if (print.contains("TRUE") && !lastReceivedMessage.contains(message)) {
				System.out.println("Servidor: " + message);
				lastReceivedMessage = message;
				break;
			}
			getMessage(isRegistration);
		}
	}

	// Procedimento de encerrar o game
	private static void endGame() {
		// Mensagem de fim aviso de espera
		getMessage(false);
		showMessage(false);
		// Mensagem de resultado
		getMessage(false);
		showMessage(false);
	}
}
