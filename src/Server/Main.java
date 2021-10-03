package Server;

import java.io.*;
import java.net.*;

public class Main {
	private static int timeoutSeconds = 4;
	private static int quantidadeDePerguntasDeUmaSessao = 10;
	private static int quantidadeDeJogadoresMinimos = 1;
	private static int serverPort = 9876;

	public static void main(String[] args) {
		// Carrega questoes
		QuestionsList questions = new QuestionsList();
		Users users = new Users();
		GameState gameState = new GameState(quantidadeDePerguntasDeUmaSessao, quantidadeDeJogadoresMinimos, users);
		System.out.println("Aguardando conexão");

		while (true) {
			DatagramSocket serverSocket = null;
			try {
				serverSocket = new DatagramSocket(serverPort);
				serverSocket.setSoTimeout(timeoutSeconds * 1000);
				byte[] receiveData = new byte[1024];
				// declara o pacote a ser recebido
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				// recebe o pacote do cliente
				try {
					serverSocket.receive(receivePacket);
					// mensagem do cliente
					String userName = new String(receivePacket.getData());
					// Ip do cliente
					InetAddress IPAddress = receivePacket.getAddress();
					// Porta
					int port = receivePacket.getPort();
					// Verifica se ja existe (nao pode existir)
					User currentUser = users.getUser(port);
					// Cria uma thread de comunicação especifica para aquele usuario
					if (currentUser == null) {
						// Cria o usuario
						users.addUser(userName, IPAddress, port);
						Thread userThread = new Thread(
								new ComunicationThread(serverSocket, users.getUser(port), gameState, questions, users));
						userThread.start();
						System.out.println("criou um novo usuario, nome: " + userName);
						serverPort--;
					}
				} catch (IOException e) {
					// Ignorar
				}
			} catch (SocketException e1) {
				// ignorar
			}

		}
	}

}
