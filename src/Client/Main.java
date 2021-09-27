package Client;

import java.io.*; // classes para input e output streams e
import java.net.*;// DatagramaSocket,InetAddress,DatagramaPacket

public class Main {
	public static void main(String[] args) {
		// cria o stream do teclado
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// declara socket cliente
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("erro durante a declaração do socket do cliente");
			e.printStackTrace();
		}

		// obtem endereco ip do servidor com o DNS
		InetAddress IPAddress = null;
		try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			System.out.println("erro durante obtencao do ip do servidor");
			e.printStackTrace();
		}

		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		// l� uma linha do teclado
		String sentence = null;
		try {
			sentence = inFromUser.readLine();
		} catch (IOException e) {
			System.out.println("erro durante leitura do input do cliente");
			e.printStackTrace();
		}
		sendData = sentence.getBytes();

		// cria pacote com o dado, o endere�o do server e porta do servidor
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

		// envia o pacote
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("erro durante envio do pacote ao cliente");
			e.printStackTrace();
		}

		// fecha o cliente
		clientSocket.close();

	}
}
