package Client;

import java.io.*;
import java.net.*;

public class Main {
	private static int timeoutSeconds = 1;
	private static int serverPort = 9876;
	private static int clientPort = 9877;
	private static InetAddress IPAddress;
	private static DatagramSocket clientSocket;
	private static String myName = "";
	private static String message = "";
	private static String question = "";

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
			// Tenta receber a primeira pergunta
			getMessage();
			// Se recebeu, o jogador foi registrado
			if (!question.contentEquals("")) {
				showMessage();
				break;
			} else {
				// Se nao, tenta outra porta
				serverPort--;
			}
		}
	}

	// Espera o jog começar, esperando a chegada da mensagem especificaF
	private static void waitForGametoStart() {
		while (true) {
			if (question.contains("O jogo irá começar, prepare-se")) {
				showMessage();
				break;
			}
			getMessage();
		}

	}

	private static void getMessage() {
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			clientSocket.receive(receivePacket);
			question = new String(receivePacket.getData());
		} catch (IOException e) {
			System.out.println("erro ao enviar mensagem");
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
			getMessage();
			// Se for o aviso de fim de jogo, para
			if (question.contains("STOP")) {
				break;
			}
			// Se nao
			// Mostra pergunta em tela
			showMessage();
			// Pega resposta do cliente
			getUserInput();
			// Envia para o servidor
			postMessage();
		}
	}

	// Apreseta a mensagem em tela, formatada
	private static void showMessage() {
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
			if (print.contains("TRUE")) {
				System.out.println("Servidor: " + message);
				break;
			}
			getMessage();
		}
	}

	// Procedimento de encerrar o game
	private static void endGame() {
		// Mensagem de fim aviso de espera
		getMessage();
		showMessage();
		// Mensagem de resultado
		getMessage();
		showMessage();
	}
}
