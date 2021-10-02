package Server;

import java.io.*;
import java.net.*;

public class Main {
	private static int quantidadeDePerguntasDeUmaSessao = 4;
	private static int quantidadeDeJogadoresMinimos = 2;
	private static int serverPort = 9876;

	public static void main(String[] args) {
		// Carrega questoes
		QuestionsList questions = new QuestionsList();
		Users users = new Users();
		GameState gameState = new GameState(quantidadeDePerguntasDeUmaSessao, quantidadeDeJogadoresMinimos, users);
		System.out.println("Aguardando conexão");

		while (true) {
			System.out.println("começou");
			DatagramSocket serverSocket = null;
			try {
				serverSocket = new DatagramSocket(serverPort);
			} catch (SocketException e1) {
				System.out.println("erro na criacao do server socket");
				e1.printStackTrace();
			}
			System.out.println("vai receber");
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
			System.out.println("Mensagem recebida: " + sentence);
			// Ip do cliente
			InetAddress IPAddress = receivePacket.getAddress();
			// Porta
			int port = receivePacket.getPort();
			// Verifica se ja existe (nao pode existir)
			User currentUser = users.getUser(port);
			// Cria uma thread de comunicação especifica para aquele usuario
			if (currentUser == null) {
				// Cria o usuario
				users.addUser(sentence, IPAddress, port);
				Thread userThread = new Thread(
						new ComunicationThread(serverSocket, users.getUser(port), gameState, questions));
				userThread.start();
				System.out.println("criou um novo usuario, nome: " + sentence);
			}
			serverPort--;
		}
	}

}
