package Client;

import java.io.*;
import java.net.*;

public class Main {
	private static int serverPort = 9876;
	private static int clientPort = serverPort + 1;
	private static String lastMessage = "";
	private static InetAddress IPAddress;
	private static DatagramSocket clientSocket;

	public static void main(String[] args) {
		startGame();
		System.out.println("Deseja jogar novamente? S/N");
		String sentence = getUserInput();
		while (!sentence.toLowerCase().contains("n")) {
			startGame();
			sentence = getUserInput();
		}
		// fecha o cliente
		clientSocket.close();

	}

	private static void startGame() {
		System.out.println("Iniciando...");
		// Configura socket para o cliente
		setClientSocket();
		// obtem endereco ip do servidor com o DNS
		setIp();

		System.out.println("Olá! Nos informe seu nome");
		gameFlow();

		System.out.println("Parabens, voce chegou ao fim! Aguardando os demais players terminarem");
		endGame();
	}

	private static void endGame() {
		byte[] receiveData = new byte[1024];
		// declara o pacote a ser recebido
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		// recebe o pacote do cliente
		try {
			clientSocket.setSoTimeout(0);
			clientSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.println("erro no recebimento do socket");
			e.printStackTrace();
		}
		// Enquanto nao for uma pergunta, continua recebendo
		System.out.println("Seu resultado:");
		String sentence = new String(receivePacket.getData());
		System.out.println(sentence);
		String[] parts = sentence.split(";");
		for (String string : parts) {
			System.out.println(string);
		}
	}

	private static void gameFlow() {
		String receivedMessage = "";
		while (!receivedMessage.contains("STOP")) {
			// Envia conteudo do cliente ao servidor
			sendMessage();
			// Espera mensagem do servidor e apresenta em tela
			receivedMessage = receiveMessage();
		}
	}

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

	private static void setClientSocket() {
		boolean foundAvailablePort = false;
		while (!foundAvailablePort) {
			System.out.println("Tentando a porta: " + clientPort);
			try {
				clientSocket = new DatagramSocket(clientPort);
				foundAvailablePort = true;
				clientSocket.setSoTimeout(1000);
			} catch (SocketException e) {
				System.out.println("Erro! porta ja em uso");
				clientPort++;
			}
		}
		System.out.println("Porta livre encontrada");

	}

	// Metodo para que o cliente possa receber uma mensagem do servidor
	public static String receiveMessage() {
		String isQuestion = "";
		String message = "";
		String sentence = "";
		// Enquanto nao for uma pergunta, continua recebendo
		while (!isQuestion.contains("TRUE")) {
			byte[] receiveData = new byte[1024];
			// declara o pacote a ser recebido
			boolean conection = false;
			while (!conection) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				// recebe o pacote do cliente
				try {
					clientSocket.receive(receivePacket);
					conection = true;
					sentence = new String(receivePacket.getData());
				} catch (IOException e) {
					System.out.println("erro no recebimento do socket");
					System.out.println("reestabelecendo conexao com o servidor");
					byte[] sendData = new byte[1024];
					sendData = lastMessage.getBytes();
					serverPort--;
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
					// envia o pacote
					try {
						clientSocket.send(sendPacket);
					} catch (IOException e2) {
						System.out.println("erro durante envio do pacote ao servidor");
						e2.printStackTrace();
					}
				}
			}

			String[] parts = sentence.split(";");
			// Nome do usuario enviando a mensagem
			isQuestion = parts[0];
			// Mensagem enviada pelo usuario
			message = parts[1];
			// Enquanto nao for uma pergunta, continua recebendo
			if (!message.contains("STOP") && !message.contains("WAIT")) {
				System.out.println("Servidor: " + message);
			}
		}
		return message;
	}

	// Metodo para que o cliente possa enviar uma mensagem ao servidor
	public static void sendMessage() {
		String sentence = getUserInput();
		byte[] sendData = new byte[1024];
		sendData = sentence.getBytes();

		// cria pacote com o dado, o endereco e porta do servidor
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);

		// envia o pacote
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("erro durante envio do pacote ao servidor");
			e.printStackTrace();
		}
		lastMessage = sentence;
	}

	// Leitura do teclado
	public static String getUserInput() {
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
		return sentence;
	}
}
