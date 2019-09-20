import java.net.*;
import java.io.*;
public class TTTMultiServer
{
    public static void main (String[] args) throws IOException{        
        boolean listening = true;
        try(ServerSocket serverSocket = new ServerSocket(4444)){
            while(listening){
                Game game = new Game();
                new TTTMultiServerThread(serverSocket.accept(),game).start();
                System.out.println("First player connected");
                new TTTMultiServerThread(serverSocket.accept(),game).start();
            }
        } catch(IOException e) {
            System.err.println("Could not listen on port 4444");
            System.exit(-1);
        }
    }
}
