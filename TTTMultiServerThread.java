import java.net.*;
import java.io.*;
public class TTTMultiServerThread extends Thread
{
    // instance variables - replace the example below with your own
    private Socket socket = null;
    private Game game; 
    private int playerNum;
    private String symbol;
    public TTTMultiServerThread(Socket socket, Game game){
        super("TTTMultiServer");
        this.socket = socket;
        this.game = game;
        this.playerNum = this.game.getNum();
        this.symbol = playerNum == 1 ? GameBoard.X : GameBoard.O;
    }

    public void run(){
        //Object Streams needed to send interpret and send messages 
        try(
        ObjectInputStream in = new ObjectInputStream(
                socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(
                socket.getOutputStream());

        ){
            //message objects 
            Message fromServerThread;
            ClientMessage fromUser;
            Vector2 move;

            //initial turn 
            if(playerNum == 1){
                //if we go first, send prompt and board
                System.out.println("about to send first message");
                fromServerThread = new Message("Select a move: ", game.getBoard());
                out.writeObject(fromServerThread);
                out.flush();
                System.out.println("about to get back message");                
            }else{
                //else send message telling to wait 
                fromServerThread = new Message("You must wait",game.getBoard());
                out.writeObject(fromServerThread);
                out.flush();

                //else go to sleep 
                game.goToSleep();

                //send prompt and board 
                System.out.println("made it past first set");
                fromServerThread = new Message("Select a move: ",game.getBoard());
                out.writeObject(fromServerThread);
                out.flush();
                System.out.println("wrote over the object");
            }

            //if message is recieved 
            while((fromUser = (ClientMessage)in.readObject()) != null) { 
                System.out.println("got the message back from the user");
                //send message telling to wait 
                fromServerThread = new Message("You must wait",game.getBoard());
                out.writeObject(fromServerThread);
                out.flush();
                //apply to game
                int state = game.applyMove(fromUser.getMove(),symbol);

                if(game.getGameState() != GameBoard.CONTINUE){
                    //check for wins, losses, or draws
                    if(game.getGameState() == GameBoard.WIN){
                        if(state == GameBoard.WIN){
                            //we won
                            fromServerThread = new Message("Other Person Won!", game.getBoard());
                            out.writeObject(fromServerThread);
                            break;
                        }else{
                            //the other player won
                            fromServerThread = new Message("You Won!", game.getBoard());
                            out.writeObject(fromServerThread);
                            break;
                        }
                    }else{
                        if(game.getGameState() == GameBoard.DRAW){
                            //the game was a draw
                            fromServerThread = new Message("Draw!", game.getBoard());
                            out.writeObject(fromServerThread);
                            break;
                        }
                    }
                }
                System.out.println(game.getGameState());

                //if the game is won, 
                //send out object
                fromServerThread = new Message("Select a move: ",game.getBoard());
                out.writeObject(fromServerThread);
                out.flush();
            }
        }catch(IOException e) {
            System.out.println("Exception caught when trying to listen on port  4444 or listening for a connection");
            System.out.println(e.getMessage());
        }catch(Exception e){
        }
    }
}
