package Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class Main {
	private static int quantidadeDePerguntasDeUmaSessao = 1;

	public static void main(String[] args) {
		// Carrega questoes
		QuestionsList questions = new QuestionsList();
		Users users = new Users();
		System.out.println("Aguardando conexão");
		// cria socket do servidor com a porta 9876
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(9876);
		} catch (SocketException e1) {
			System.out.println("erro na criacao do server socket");
			e1.printStackTrace();
		}

		while (true) {
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
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			// mensagem do cliente
			String sentence = new String(receivePacket.getData());
			System.out.println("Mensagem recebida: " + sentence);

			// Identifica o usuario e mensagem
			String[] parts = sentence.split(";");
			// Nome do usuario enviando a mensagem
			String userName = parts[0];
			// Mensagem enviada pelo usuario
			String message = parts[1];
			// Usuario identificado
			User currentUser = users.getUser(userName);
			// Se o usuario ja existe, mas ja respondeu as N perguntas
			if (currentUser != null && currentUser.getAnswersSize() >= quantidadeDePerguntasDeUmaSessao) {
				// Registra a ultima questao
				currentUser.addAnswer(message);
				System.out.println("Encerrando conexao");
				// Avisa que chegou ao fim
				sendMessage(IPAddress, serverSocket, "STOP");
				// Envia o resultado ao cliente
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("currentUser.getAnswers(): " + currentUser.getAnswers());
				sendMessage(IPAddress, serverSocket, currentUser.getAnswers());
				break;
			}
			// Se e um novo usuario
			if (currentUser == null) {
				users.addUser(userName);
				currentUser = users.getUser(userName);
				System.out.println("criou um novo usuario, nome: " + userName);
			} else {
				// Se nao for
				// Adiciona resposta dada pelo usuario
				currentUser.addAnswer(message);
			}
			// Cria uma nova questao
			// Registra a questao atual
			currentUser.setLastQuestion(questions.getQuestion());
			// Apenas envia uma mensagem
			String introduction = "Pergunta Numero " + users.getUser(userName).getAnswersSize() + ": ";
			String QuestionText = introduction + currentUser.getLastQuestionText();
			System.out.println("Pergunta enviada: " + QuestionText);
			sendMessage(IPAddress, serverSocket, QuestionText);
		}
	}

	// Metodo para receber mensagens dos clientes
	public static String receiveMessage(DatagramSocket serverSocket) {
		return "";
	}

	// Metodo para responder a um cliente
	public static void sendMessage(InetAddress IPAddress, DatagramSocket clientSocket, String messageToBeSent) {
		System.out.println("Mensagem sendo enviada: " + messageToBeSent);
		byte[] sendData = new byte[1024];
		sendData = messageToBeSent.getBytes();

		// cria pacote com o dado, o endereco e porta do servidor
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9877);

		// envia o pacote
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("erro durante envio do pacote ao cliente");
			e.printStackTrace();
		}
	}
}
