import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
	private static Socket socket;
	public static void main(String[] args) throws Exception {
			//String IP = "127.0.0.1";
			//int port = 5000;
			
			int port = getPort();
			String IP = getIP();
			
			System.out.format("Serveur lancé sur [%s:%d]", IP, port);
			
			
			sendCommand(IP, port);
			
	}
	
	
	// code en commun avec Serveur... (creer une classe commune ?)
	
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

	 
	
	// switch case des commandes
	
	
	public static void sendCommand(String IP, int port)
	{

		Scanner commandScan = new Scanner(System.in);
		System.out.println("\nVeuillez écrire une commande: ");
		
		
		String command = commandScan.nextLine();
		
		Socket socket;
		try 
		{
			socket = new Socket(IP, port);
		}
		catch (Exception e) 
		{		
			e.printStackTrace();
			return;
		} 
		
		String directory = "";
		if (command.contains(" ")) {
			String[] parts = command.split(" ");
			command = parts[0];
			directory = parts[1];
		}
		
		
		DataOutputStream out;
		
		//while(Connection) {
		switch (command) {
		case "cd":
			System.out.println("cd");
			break;
		case "ls":
			
			try {
				out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF("ls");
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("erreur a l'envoie");
				e.printStackTrace();
				return;
			}
			
			//out.close();
			break;
		case "mkdir":
			
			try {
				out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF("mkdir " + directory);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("erreur a l'envoie");
				e.printStackTrace();
				return;
			}
			
			break;
		case "upload":
			System.out.println("cd");
			break;
		case "download":
			System.out.println("cd");
			break;
		case "exit":
			
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Vous avez été déconnecté.");
			return;
		default:
			System.out.println("La commande est invalide. Saisir une nouvelle commande: ");
			

			sendCommand(IP, port);
			break;
		} 
		
		
		
		try(DataInputStream in = new DataInputStream(socket.getInputStream()))
		{			
			String rsp = in.readUTF();
			System.out.println(rsp);
		}
		catch (IOException e) {
			System.err.println("erreur a la reception");
			e.printStackTrace();
		}
		sendCommand(IP, port);
	
		//in.close();
		//System.out.println("in closed");
		//sendCommand(command, socket);
		//}
	}
}
