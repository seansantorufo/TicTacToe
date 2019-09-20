import java.io.*;
import java.net.*;
import java.util.Scanner;
public class TTTClient {
    public static void main(String[] args) throws Exception {
        //create port and host
        String hostName = "localhost";
        int portNumber = 4444;

        //Initiate a connection request to server's IPaddress, port 
        Socket clientSocket = new Socket(hostName, portNumber);
        //Object Streams needed to send interpret and send messages 
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(
                clientSocket.getInputStream());

        //these will be the objects
        Message fromServerThread;
        ClientMessage fromUser;
        String symbol = "X";

        Scanner scan = new Scanner(System.in);

        //if message is recieved
        while((fromServerThread = (Message)in.readObject()) != null){ 
            //interpret the incoming message
            String results = TTTProtocol.interpretMessage(fromServerThread);

            //display results of interpretation
            System.out.println(results);

            if(fromServerThread.getMessage().equals("You must wait")){
                continue;
            }
            
            if(fromServerThread.getMessage().equals("Other Person Won!") || fromServerThread.getMessage().equals("You Won!") ||
            fromServerThread.getMessage().equals("Draw!")){
                System.out.println(fromServerThread.getMessage());
                break;                
            }

            //intialize variables needed for loop 
            int state = 10;
            Vector2 theMove = null;
            do{
                //get input
                String move = scan.nextLine();
                //if not vaild, prompt and continue
                if(move == null || !move.matches("[1-3],[1-3]")){
                    System.out.print("Bad input, please try again: ");
                    continue;
                }else{
                    //make a temp board, this is to check internally if move will
                    //cause any sort of issue 
                    GameBoard temp = fromServerThread.getBoard();
                    //make vector2
                    int positionOfComma = move.indexOf (',');
                    String x = move.substring(0, positionOfComma);
                    String y = move.substring(positionOfComma + 1, move.length());
                    int row = Integer.parseInt(x) - 1;// Casting string of x
                    int col = Integer.parseInt(y) - 1;// Casting string of y
                    theMove =  new Vector2(row,col);
                    //apply vector2 to board and store the state in an int to check
                    state = temp.applyMove(theMove, symbol);
                    if(state == GameBoard.BAD_INPUT){
                        System.out.println("Bad input, try again");
                    }
                }            
            }while(state == GameBoard.BAD_INPUT);
            //send object 
            fromUser = new ClientMessage("",theMove);
            out.writeObject(fromUser);
            out.flush();
        }

    }
}
