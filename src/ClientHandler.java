import java.util.List;
import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket; 
	private int clientNumber; 
	private DataInputStream in;
	private DataOutputStream out;
	private String path;
	private File startDirectory = new File ("Start");
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 

		System.out.println("New connection with client#" + clientNumber + " at" + socket);}


	public void run() {
		try {
			out = new DataOutputStream(socket.getOutputStream()); 
			out.writeUTF("Hello from server - you are client#" + clientNumber);
			in = new DataInputStream(socket.getInputStream());
			path = ".";
			boolean exit = false;


			while(!exit) {
				String command = in.readUTF();
				
				String localAddress = this.socket.getLocalAddress().toString();
				StringBuilder stringBuilder = new StringBuilder(localAddress);
				stringBuilder.deleteCharAt(0);
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");  
				LocalDateTime now = LocalDateTime.now();
				System.out.println("[" + 
					stringBuilder.toString() + ":" + 
					this.socket.getPort() + " - " + 
					dateFormatter.format(now) + "] : " + 
					command);
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
				case "upload":
					try {
						if(path == "") {
							saveFile(directory);
						}else {
							saveFile(path+"/"+directory);
						}
						out.writeUTF("Le fichier " + directory + " a bien été téléversé.");
						}
					catch(IOException e) {
						e.printStackTrace();
					}
					break;

				case "download":
					try {
						if(path == "") {
							sendFile(directory);
						}else {
							sendFile(path+"/"+directory);
						}
						out.writeUTF("Le fichier " + directory + " a bien été téléchargé.");
					}
					catch(IOException e) {
						e.printStackTrace();
					}
					break;

				case "exit":
					exit = true;
					out.writeUTF("Fin de la connection...");
					return;

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
	private String commandCd(String presentPath, String directory) throws IOException {
		String newPath = "";
		File destinationFile = new File(presentPath + "/" + directory);

		if(directory.equals("..")) {
			String[] splitPath = presentPath.split("/");
			if (splitPath.length == 1) {
				newPath = presentPath;
			} else {
				for(int i = 0; i < splitPath.length - 1; i++) {
					newPath += splitPath[i];
					if(i < splitPath.length - 2) {
						newPath += "/";
					}
				}
				out.writeUTF("Votre position est " + newPath);
				return newPath;
			}
		}
		else {
			if(destinationFile.exists()) {
				out.writeUTF("Votre position est " + presentPath + "/" + directory);
				return presentPath + "/" + directory;
			}
			out.writeUTF("Le dossier " + directory + " n'existe pas.");
		}
		return presentPath;
	}
	private void commandLs(String presentPath) throws IOException{
		File[] files =  new File(presentPath).listFiles();
		String listFiles ="";
		for (File file: files) {
				if (file.isDirectory()) {
					listFiles += "[Folder]" + file.getName() + "\n";
				} else if(file.isFile()) {
					listFiles += "[File]" + file.getName() + "\n";
				} else {
					listFiles += "L'élément n'est pas un dossier ou un fichier\n";
				}
		}
		if (files.length == 0) {
			out.writeUTF("Le répertoire ne contient pas de dossier ou de fichier.");
		}

		out.writeUTF(listFiles);
		System.out.println(Thread.currentThread().getName() + " -> " + files.toString());
	}
	private void commandMkdir(String presentPath, String directory) throws IOException {
		System.out.println(presentPath + "/" + directory);
		File destinationFile = new File(presentPath + "/" + directory);
		destinationFile.mkdir();
		if(destinationFile.exists()) {
			out.writeUTF("Le dossier " + directory +  " a bien été créé");
		} else {
			out.writeUTF("Le dossier " + directory + "n'a pas pu être créé");
		}
	}

	private void saveFile(String path)throws IOException {
		int bytes = 0;

		FileOutputStream output = new FileOutputStream(path);

		long size = this.in.readLong();
		byte[] buffer = new byte[4*1024];
		while (size > 0 && (bytes = this.in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
			output.write(buffer,0,bytes);
			size -= bytes;
		}
		output.close();
	}

	private void sendFile(String path)throws IOException{
		int bufferSize = 0;
		File newFile = new File(path);
		FileInputStream input = new FileInputStream(newFile.toString());

		byte[] buffer = new byte[4*1024];
		out.writeLong(newFile.length());

		while((bufferSize = input.read(buffer)) !=-1) {
			out.write(buffer, 0, bufferSize);
			out.flush();
		}
		input.close();
	}
}


