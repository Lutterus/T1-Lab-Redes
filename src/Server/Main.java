package Server;

import java.io.*;
import java.net.*;

public class Main {
	private QuestionsList questions;

	public static void main(String[] args) {
		// Carrega questoes
		QuestionsList questions = new QuestionsList();
		// Thread para escutar usuario
		// cria socket do servidor com a porta 9876
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(9876);
		} catch (SocketException e1) {
			System.out.println("erro na criacao do server socket");
			e1.printStackTrace();
		}

		byte[] receiveData = new byte[1024];
		while (true) {
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
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			System.out.println("Mensagem recebida: " + sentence);
		}

//		Thread serverListener = new Thread(new ServerListener(questions));
//		serverListener.start();
//
//		try {
//			serverListener.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
