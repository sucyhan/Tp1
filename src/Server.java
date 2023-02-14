import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.regex.Pattern;
public class Server {
	private static ServerSocket Listener; // Application Serveur
	public static void main(String[] args) throws Exception {
		// Compteur incrémenté à chaque connexion d'un client au serveur
		int clientNumber = 0;
		// Adresse et port du serveur
		//		String serverAddress = "127.0.0.1";
		//		int serverPort = 5000;
		int serverPort = getPort();
		String serverAddress = getIP();

		// Création de la connexien pour communiquer ave les, clients
		Listener = new ServerSocket();
		Listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		// Association de l'adresse et du port à la connexien
		Listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		try {
			// À chaque fois qu'un nouveau client se, connecte, on exécute la fonstion
			// run() de l'objet ClientHandler
			while (true) {
				// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
				// Une nouvetle connection : on incémente le compteur clientNumber new ClientHandler(Listener.accept(), clientNumber++).start();
				new ClientHandler(Listener.accept(), clientNumber++).start();
			}
		} finally {
			// Fermeture de la connexion
			Listener.close();
		} 
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

}