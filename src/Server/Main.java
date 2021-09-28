package Server;

import java.io.*;
import java.net.*;

import Client.User;

public class Main {
	private QuestionsList questions;
	private static int quantidadeDePerguntasDeUmaSessao = 10;

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
			System.out.println("sentence: " + sentence);

			// Identifica o usuario e mensagem
			String[] parts = sentence.split(";");
			// Nome do usuario enviando a mensagem
			String userName = parts[0];
			// Mensagem enviada pelo usuario
			String message = parts[1];
			// Se o usuario ja existe, mas ja respondeu as N perguntas
			if (users.getUser(userName) != null
					&& users.getUser(userName).getAnswersSize() == quantidadeDePerguntasDeUmaSessao) {
				sendMessage(IPAddress, serverSocket, "STOP");
				break;
			}
			// Se e um novo usuario
			if (users.getUser(userName) == null) {
				users.addUser(userName);
				System.out.println("criou um novo usuario, nome: " + userName);
			}
			// Apenas envia uma mensagem
			String introduction = "Pergunta Numero " + users.getUser(userName).getAnswersSize() + 1 + ": ";
			String Question = introduction + questions.getQuestion();
			sendMessage(IPAddress, serverSocket, Question);
		}
	}

	// Metodo para receber mensagens dos clientes
	public static String receiveMessage(DatagramSocket serverSocket) {
		return "";
	}

	// Metodo para responder a um cliente
	public static void sendMessage(InetAddress IPAddress, DatagramSocket clientSocket, String messageToBeSent) {
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
