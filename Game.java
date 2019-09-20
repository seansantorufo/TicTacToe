
public class Game
{
    private GameBoard board;
    private int playerCounter;
    private boolean lock;
    private int status =  GameBoard.CONTINUE;
    public Game(){
        this.board = new GameBoard();
        this.playerCounter = 1;
        this.lock = false;
    }

    public int getNum(){
        int num = this.playerCounter;
        this.playerCounter++;
        return num;
    }

    public synchronized void goToSleep(){
        notifyAll();
        try {
                lock = false;
                if(status == GameBoard.CONTINUE)
                    wait();
            } catch (InterruptedException e) {}
    }

    public GameBoard getBoard(){
        return this.board.copyBoard();
    }

    public int getGameState(){
        return this.status;
    }

    public int applyMove(Vector2 move, String symbol){
        this.status = board.applyMove(move, symbol);
        System.out.println(board);
        goToSleep();
        return this.status;
    }
}
