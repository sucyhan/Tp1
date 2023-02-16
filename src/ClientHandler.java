import java.util.List;
import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.stream.Stream;
public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private static Socket socket; 
	private int clientNumber; 
	private static String path;
	private static File startDirectory = new File ("Start");
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		this.startDirectory = new File ("Start");
		this.startDirectory.mkdir();
		this.path = ".";

		System.out.println("New connection with client#" + clientNumber + " at" + socket);}


	public void run() { // Création de thread qui envoi un message à un client
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 
			out.writeUTF("Hello from server - you are client#" + clientNumber);
			//			System.out.println("Veuillez entrer une commande: ");
			//			out.writeUTF(message);


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
					path = commandCd(path,directory);
					break;
				}
				case "ls": 
					commandLs(path);
					break;
				case "mkdir":
					commandMkdir(path, directory);

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
	private static  String commandCd(String presentPath, String directory) throws IOException {
		String newPath, returnDirectory ="";
		DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 

		if(directory.equals("..")) {
			String[] splitPath = presentPath.split("/");
			if (splitPath.length == 1) {
				newPath = presentPath;
			} else {
				//				for(int i = 0; i < splitPath.length - 1; i++) {
				//					newPath += splitPath[i];
				//					if(i < splitPath.length - 2) {
				//						newPath += "/";
				//					}
				//				}
				returnDirectory = splitPath[-1];
				newPath = presentPath + "/" + returnDirectory;
			}
			//out.writeUTF("Vous êtes dans retourné dans le dossier " + directory);
		}
		File destinationFile = new File(presentPath + "/" + directory);
		newPath = presentPath + "/" + directory;
		if (destinationFile.exists()) {
			presentPath = newPath;
			out.writeUTF(presentPath);
			out.writeUTF("Vous êtes dans le dossier " + directory);
			return presentPath;
		}
		out.writeUTF("Le dossier " + directory + " n'existe pas");
		out.writeUTF(presentPath);
		return presentPath;



		//File destinationFile = new File(startDirectory.getAbsolutePath() + "\\" + directory);

		//		if(destinationFile.isDirectory()) {
		//			startDirectory = destinationFile;
		////			out.writeUTF(startDirectory);
		//			out.writeUTF("Vous êtes dans le dossier " + directory);
		//		}
		//		else {
		//			out.writeUTF("Le dossier " + directory + " n'existe pas");
		//
		//		}
	}
	private static void commandLs(String presentPath) throws IOException{
		DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 
		File[] files =  new File(presentPath).listFiles();
		//				.filter(file -> !file.isDirectory())
		//				.map(File::getName)
		//				.collect(Collectors.toList()));
		for (File file: files) {
			if(!(file.getName().contains("."))) {
				if (file.isDirectory()) {
					out.writeUTF("[Folder]" + file.getName());

				} else if(file.isFile()) {
					out.writeUTF("[File]" + file.getName());
				} else {
					out.writeUTF("L'élément n'est pas un dossier ou un fichier");
				}
			}
		}

		out = new DataOutputStream(socket.getOutputStream());
		out.writeUTF(files.toString());
		System.out.println(Thread.currentThread().getName() + " -> " + files.toString());
	}
	private static void commandMkdir(String presentPath, String directory) throws IOException {
//		boolean success = new File(presentPath).mkdir();
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//		out.writeUTF("Le dossier " + directory + (success ? " a bien été créé": " creation failed"));
		File destinationFile = new File(presentPath + "/" + directory);
		out.writeUTF(presentPath);
		destinationFile.mkdir();
		if(destinationFile.exists()) {
			out.writeUTF("Le dossier " + directory +  " a bien été créé");
		} else {
			out.writeUTF("Le dossier " + directory + "n'a pas pu être créé");
		}
		
	}
}


