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
				case "upload":
                    try {
                        saveFile(path+"/"+directory);
                        out.writeUTF("Uploaded " + directory);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "download":
                    try {
                        sendFile(path+"/"+directory);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    break;
                    
                case "exit":
                	out.writeUTF("Fin de la connection...");
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
		String newPath = "";
		File destinationFile = new File(presentPath + "/" + directory);
		String returnDirectory ="";
		DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 

		if(directory.equals("..")) {
			String[] splitPath = presentPath.split("/");
			if (splitPath.length == 1) {
				newPath = presentPath;
				out.writeUTF("Vous êtes au dossier de départ.");
			} else {
				for(int i = 0; i < splitPath.length - 1; i++) {
					newPath += splitPath[i];
					if(i < splitPath.length - 2) {
						newPath += "/";
					}
				}
				out.writeUTF("Vous êtes retournés au dossier précédent.");
				return newPath;
			}
		}
		else {
			if(destinationFile.exists()) {
			newPath = presentPath + "/" + directory;
			presentPath = newPath;
			out.writeUTF("Vous êtes dans le dossier " + directory);
			return presentPath;
		}
		out.writeUTF("Le dossier " + directory + " n'existe pas.");
		}
		return presentPath;
	}
	private static void commandLs(String presentPath) throws IOException{
		DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 
		File[] files =  new File(presentPath).listFiles();
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
		if (files.length == 0) {
			out.writeUTF("Le répertoire ne contient pas de dossier ou de fichier.");
		}

		out = new DataOutputStream(socket.getOutputStream());
		out.writeUTF(files.toString());
		System.out.println(Thread.currentThread().getName() + " -> " + files.toString());
	}
	private static void commandMkdir(String presentPath, String directory) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		File destinationFile = new File(presentPath + "/" + directory);
		out.writeUTF(presentPath);
		destinationFile.mkdir();
		if(destinationFile.exists()) {
			out.writeUTF("Le dossier " + directory +  " a bien été créé");
		} else {
			out.writeUTF("Le dossier " + directory + "n'a pas pu être créé");
		}
	}
	
	private static void saveFile(String path)throws IOException {
        int bytes = 0;

        DataInputStream input = new DataInputStream(socket.getInputStream());
        FileOutputStream output = new FileOutputStream(path);

        long size = input.readLong();
        byte[] buffer = new byte[(int)size];
        while (size > 0 && (bytes = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            output.write(buffer,0,bytes);
            size -= bytes;
        }
        output.close();
        System.out.println("Le fichier a bien ete televerse");
    }

    private static void sendFile(String path)throws IOException{
        int bufferSize;
        File newFile = new File(path);
        FileInputStream input = new FileInputStream(newFile.toString());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        byte[] buffer = new byte[(int)newFile.length()];
        output.writeLong(newFile.length());

        while((bufferSize = input.read(buffer)) > 0) {
            output.write(buffer, 0, bufferSize);
            output.flush();
        }
        input.close();
        output.writeUTF("Telechargement...");
    }
}


