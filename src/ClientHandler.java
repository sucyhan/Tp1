import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientHandler implements Runnable //extends Thread { // pour traiter la demande de chaque client sur un socket particulier
{	
	private Socket socket;
	private int clientNumber;

	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}

	public void run() { 
		try {
			
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String command = in.readUTF();
			System.out.println(Thread.currentThread().getName() + " -> " + command);
			
			String directory = "";
			if (command.contains(" ")) {
				String[] parts = command.split(" ");
				command = parts[0];
				directory = parts[1];
			}
			
			DataOutputStream out;
			
			switch (command) {
			case "ls": 
				List<String> files =  Stream.of(new File(".").listFiles())
			    //.filter(file -> !file.isDirectory())
			    .map(File::getName)
			    .collect(Collectors.toList());
				
				out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF(files.toString());
				System.out.println(Thread.currentThread().getName() + " -> " + files.toString());
				break;
			case "mkdir":
				boolean success = new File("./"+directory).mkdirs();
				out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF("directory " + directory + (success ? " created": " creation failed"));
				break;
				
			
			default:
				System.err.println("Unexpected value: " + command);
			}
			
			
			
			//DataOutputStream out = new DataOutputStream(socket.getOutputStream());  
			//out.writeUTF("Hello from server - you are client#" + clientNumber);
		}
		catch (IOException e) 
		{
			System.out.println("Error handling client# " + clientNumber + ": " + e);
		} 
		finally
		{
			try 
			{
				socket.close();
			} 
			catch (IOException e) 
			{
				System.out.println("Couldn't close a socket, what's going on?");
			}
		System.out.println("Connection with client# " + clientNumber+ " closed");
		}
	}
}