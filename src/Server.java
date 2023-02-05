import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Server {
	private static ServerSocket Listener; 

	public static void main(String[] args) throws Exception {
	@SuppressWarnings("unused")
	int clientNumber = 0;
//	String IP = "127.0.0.1";
//	int port = 5000;
	


	int port = getPort();
	String IP = getIP();
	

	
	Listener = new ServerSocket();
	Listener.setReuseAddress(true);
	InetAddress serverIP = InetAddress.getByName(IP);
	Listener.bind(new InetSocketAddress(serverIP, port));	
	System.out.format("The server is running on %s:%d%n", IP, port);
	
	
	try 
	{
	// À chaque fois qu'un nouveau client se, connecte, on exécute la fonction
	// run() de l'objet ClientHandler
		while (true) 
		{
	// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
	// Une nouvelle connection : on incémente le compteur clientNumber 
			
			//System.out.println(Thread.currentThread().getName() + " -> BEFORE LISTEN");
			Socket socket = Listener.accept();
			//System.out.println(Thread.currentThread().getName() + " -> AFTER LISTEN");
			new Thread(new ClientHandler(socket, clientNumber++)).start();
			
		}
	} 
	finally 
	{
	// Fermeture de la connexion
	//Listener.close();
	} 
}
	
	// code en commun avec Client... (creer une classe commune ?)
	
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
