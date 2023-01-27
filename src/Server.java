import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.regex.Pattern;
public class Server {
	private static ServerSocket Listener; 

	public static void main(String[] args) throws Exception {
	@SuppressWarnings("unused")
	int clientNumber = 0;
//	String serverAddress = "127.0.0.1";
//	int serverPort = 5000;
	
	Scanner portScan = new Scanner(System.in);
	System.out.println("Saisir un numéro de port: ");
	int port = portScan.nextInt();

	validatePort(port);
	
	Scanner IPScan = new Scanner(System.in);
	System.out.println("Saisir une adresse IP: ");
	String IP = IPScan.nextLine();
	validateIp(IP);
	

	
	Listener = new ServerSocket();
	Listener.setReuseAddress(true);
	InetAddress serverIP = InetAddress.getByName(IP);
	Listener.bind(new InetSocketAddress(serverIP, port));
	System.out.format("The server is running on %s:%d%n", IP, port);
	try {
	// À chaque fois qu'un nouveau client se, connecte, on exécute la fonstion
	// run() de l'objet ClientHandler
	while (true) {
	// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
	// Une nouvetle connection : on incémente le compteur clientNumber 
		new ClientHandler(Listener.accept(), clientNumber++).start();
	}
	} finally {
	// Fermeture de la connexion
	Listener.close();
	} }
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

}
