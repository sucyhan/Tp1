import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
	private static Socket socket;
	public static void main(String[] args) throws Exception {
		int port = Server.getPort();
		String serverAddress = Server.getIP();

		socket = new Socket(serverAddress, port);
		System.out.format("Serveur lancé sur [%s:%d]", serverAddress, port);

		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		Thread inputThread = new Thread(new Runnable() {
			String message;

			public void run() {
				try {
					while (true) {
						System.out.println("\nVeuillez entrer une commande: ");
						message = in.readUTF();
						System.out.print(message);
						if(message.equals("Telechargement...")) {
							String fileName = in.readUTF();
							downloadFile(fileName);
						}
						if(message.equals("Fin de la connection...")) {
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Thread outputThread = new Thread(new Runnable() {
		String message;
			public void run() {
				try {
					while(true) {
						BufferedReader reader = new BufferedReader (new InputStreamReader(System.in));
						message = reader.readLine();
						if(message.startsWith("upload")) {
							String fileName = message.substring(7);
							uploadFile(fileName);
							out.writeUTF(message);
						}
						else if(message.startsWith("exit")) {
							out.writeUTF(message);
							break;
						} else {
							out.writeUTF(message);
						}
						
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});

		inputThread.start();
		outputThread.start();

		while (outputThread.isAlive()) {
		}
		while (inputThread.isAlive()) {
		}
		socket.close();
	}
		public static int getPort()
		{

			int port;
			System.out.println();
			do {
				System.out.print("Saisir un numéro de port: ");
				Scanner portScan = new Scanner(System.in);
				port = portScan.nextInt();
			}
			while(! validatePort(port));
			return port;

		}

		public static  boolean validatePort(int portVar) {
			if ( portVar >= 5000 && portVar <= 5050) 
			{
				System.out.println("Le numéro de port est: " + portVar);
				return true;
			}
			else
			{
				System.out.println("Numéro de port invalide");
				return false;
			}

		}

		public static String getIP()
		{

			String IP;
			System.out.println();
			do {
				System.out.print("Saisir une adresse IP: ");
				Scanner IPScan = new Scanner(System.in);
				IP = IPScan.nextLine();
			}
			while(! validateIp(IP));
			return IP;

		}


		private static final Pattern PATTERN = Pattern.compile(
				"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

		public static boolean validateIp(final String ip) {
			if (PATTERN.matcher(ip).matches()) {
				System.out.println("L'adresse IP est: " + ip);
				return true;
			}
			else {
				System.out.println("L'adressse IP est invalide. Saisir une nouvelle adresse IP: ");
				return false;
			}

		}
		
		private static void uploadFile(String fileName)throws IOException {
		    int bufferSize;
		    File newFile = new File(fileName);
		    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		    FileInputStream input = new FileInputStream(newFile.toString());

		    byte[] buffer = new byte[(int)newFile.length()];
		    output.writeLong(newFile.length());
		    while((bufferSize = input.read(buffer)) > 0) {
		        output.write(buffer, 0, bufferSize);
		        output.flush();
		    }
		    input.close();
		    System.out.println("Le fichier a bien ete televerse");
		}

		private static void downloadFile(String fileName)throws Exception {
		    int bytes = 0;
		    DataInputStream input = new DataInputStream(socket.getInputStream());
		    FileOutputStream output = new FileOutputStream(fileName);
		    long size = input.readLong();
		    byte[] buffer = new byte[(int)size];
		    while (size > 0 && (bytes = input.read(buffer, 0, (int)Math.min(buffer.length, size))) > 0) {
		        output.write(buffer,0,bytes);
		        size -= bytes;
		    }
		    output.close();
		}
		

}