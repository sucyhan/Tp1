import java.io.DataInputStream;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.net.Socket;
public class Client {
	private static Socket socket;
	public static void main(String[] args) throws Exception {
//			String serverAddress = "127.0.0.1";
//			int port = 5000;
			
			Scanner portScan = new Scanner(System.in);
			System.out.println("Saisir un numéro de port: ");
			int port = portScan.nextInt();

			validatePort(port);
			
			Scanner IPScan = new Scanner(System.in);
			System.out.println("Saisir une adresse IP: ");
			String IP = IPScan.nextLine();
			validateIp(IP);
			
			System.out.format("Serveur lancé sur [%s:%d]", IP, port);
			
			Scanner commandScan = new Scanner(System.in);
			System.out.println("Veuillez écrire une commande: ");
			String command = commandScan.nextLine();
			getCommand(command);
			
			
			socket = new Socket(IP, port);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String helloMessageFromServer = in.readUTF();
			System.out.println(helloMessageFromServer);
			socket.close();
			
	}
	public static void validatePort(int portVar) {
		if ( portVar >= 5000 && portVar <= 5050) {
			System.out.println("Le numéro de port est: " + portVar);
		}
		else {
			System.out.println("Numéro de port invalide. Saisir un nouveau numéro de port: ");
			Scanner portScan = new Scanner(System.in);
			int newPort = portScan.nextInt();
			validatePort(newPort);
		}
	}
	private static final Pattern PATTERN = Pattern.compile(
	        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static void validateIp(final String ip) {
		if (PATTERN.matcher(ip).matches()) {
			System.out.println("L'adresse IP est: " + ip);
		}
		else {
			System.out.println("L'adressse IP est invalide. Saisir une nouvelle adresse IP: ");
			Scanner IPScan = new Scanner(System.in);
			String newIP = IPScan.nextLine();
			validateIp(newIP);
		}
	}
	public static void getCommand(String comm) {
		String command = comm;
		if (comm.contains(" ")) {
			String[] parts = comm.split(" ");
			command = parts[0];
			String directory = parts[1];
		}
		switch (command) {
		case "cd":
			System.out.println("cd");
			break;
		case "ls":
			System.out.println("cd");
			break;
		case "mkdir":
			System.out.println("cd");
			break;
		case "upload":
			System.out.println("cd");
			break;
		case "download":
			System.out.println("cd");
			break;
		case "exit":
			System.out.println("cd");
			break;
		default:
			System.out.println("La commande est invalide. Saisir une nouvelle commande: ");
			Scanner commScan = new Scanner(System.in);
			String newComm = commScan.nextLine();
			getCommand(newComm);
			break;
		}
	}
}
