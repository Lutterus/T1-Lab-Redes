package Client;

import java.io.*;
import java.net.*;

public class Main {
	public static void main(String[] args) {
		// Identificacao do usuario
		User user = null;
		System.out.println("Nos informe seu nome");

		// declara socket cliente
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("erro durante a declaração do socket do cliente");
			e.printStackTrace();
		}
		// cria socket do servidor com a porta 9876
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(9877);
		} catch (SocketException e1) {
			System.out.println("erro na criacao do server socket");
			e1.printStackTrace();
		}
		// obtem endereco ip do servidor com o DNS
		InetAddress IPAddress = null;
		try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			System.out.println("erro durante obtencao do ip do servidor");
			e.printStackTrace();
		}

		String receivedMessage = "";
		while (!receivedMessage.contains("STOP")) {
			// Envia conteudo do cliente ao servidor
			sendMessage(IPAddress, clientSocket, user);
			// Espera mensagem do servidor e apresenta em tela
			receivedMessage = receiveMessage(serverSocket);
		}
		System.out.println("Parabens, voce chegou ao fim");
		receivedMessage = receiveMessage(serverSocket);
		System.out.println("Seu resultado foi: " + receivedMessage);

		// fecha o cliente
		clientSocket.close();

	}

	// Metodo para que o cliente possa receber uma mensagem do servidor
	public static String receiveMessage(DatagramSocket serverSocket) {
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
		// pega os dados, o endereco ip e porta do cliente
		// para poder mandar a msg de volta
		String sentence = new String(receivePacket.getData());
		System.out.println("Servidor: " + sentence);
		return sentence;
	}

	// Metodo para que o cliente possa enviar uma mensagem ao servidor
	public static void sendMessage(InetAddress IPAddress, DatagramSocket clientSocket, User user) {
		// cria o stream do teclado
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		byte[] sendData = new byte[1024];

		// le uma linha do teclado
		String sentence = null;
		try {
			sentence = inFromUser.readLine();
			// Se e a primeira mensagem
			if (user == null) {
				user = new User(sentence);
			}
			// Concatena a identificacao do usuario no inicio
			sentence = user.getName() + ";" + sentence;
		} catch (IOException e) {
			System.out.println("erro durante leitura do input do cliente");
			e.printStackTrace();
		}
		sendData = sentence.getBytes();

		// cria pacote com o dado, o endereco e porta do servidor
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

		// envia o pacote
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("erro durante envio do pacote ao cliente");
			e.printStackTrace();
		}
	}
}
