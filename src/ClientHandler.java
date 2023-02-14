import java.awt.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket; 
	private int clientNumber; 
	private String path = ".\\";
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		System.out.println("New connection with client#" + clientNumber + " at" + socket);}


	public void run() { // Création de thread qui envoi un message à un client
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi out.writeUTF("Hello from server - you are client#" + clientNumber); // envoi de message} catch (IOException e) {
			out.writeUTF("Hello from server - you are client#" + clientNumber);
			System.out.println("Veuillez entrer une commande: ");
			

			while(true) {
				String command = in.readUTF();
				System.out.println(command);
				System.out.println(Thread.currentThread().getName() + " -> " + command);

				String directory = "";
				if (command.contains(" ")) {
					String[] parts = command.split(" ");
					command = parts[0];
					directory = parts[1];
				}

				switch (command) {
				case "cd": {
					System.out.println("haha");
					break;
				}
				case "ls": 
//					System.out.println(startDirectory);
//					List<String> files =  Stream.of(this.startDirectory.listFiles())
//							//.filter(file -> !file.isDirectory())
//							.map(File::getName)
//							.collect(Collectors.toList());
//
//					out = new DataOutputStream(socket.getOutputStream());
//					out.writeUTF(files.toString());
//					System.out.println(Thread.currentThread().getName() + " -> " + files.toString());
					break;
				case "mkdir":
//					boolean success = new File("./"+directory).mkdirs();
//					out = new DataOutputStream(socket.getOutputStream());
//					out.writeUTF("Le dossier " + directory + (success ? " a bien été créé": " creation failed"));
					break;


				default:
					System.err.println("Unexpected value: " + command);
				}

			}
		} catch (IOException e) {
			System.out.println("Error handling client# " + clientNumber + ": " + e);
		}
		finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?");}
			System.out.println("Connection with client# " + clientNumber+ " closed");}
	}
//	private void commandCd(String directory) {
//		out = new DataOutputStream(socket.getOutputStream());
//		String newPath = this.path + "\\" + directory;
//		File file = new File(newPath);
//		if(file.isDirectory()) {
//			this.path = newPath;
//			out.writeUTF("Vous êtes dans le dossier " + directory);
//		}
//		else {
//			out.writeUTF("Le dossier " + directory + " n'existe pas");
//
//		}
//	}
}



