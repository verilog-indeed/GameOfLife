package game_of_life;
/**
 * made following the Game Of Life project on Hyperskill (JetBrains Academy)
 *
 * this is the brains of the simulation, so to speak. I made this early on my journey of learning java
 * so it contains a lot of design problems, mainly because i didn't understand how threads worked at
 * the time. the class uses the Evolution tools to run a Conway's Game Of Life simulation, storing the data
 * in a two dimensional (square) char array where alive cells are denoted 'O' and dead cells are
 * denoted ' ', it creates a Swing frame and functions as a listener for some GUI events (button presses
 * etc...)
 * */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Main implements GameOfLifeConstants, ActionListener {

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        display = new Display(this);
        runGameOfLife();
    }

    public void runGameOfLife() {
        universe = Evolution.createNewUniverse(UNIVERSE_SIZE);
        int counter = 0;

        /* this program used to let you directly pick which generation of the universe
             you want to look at, now it's just handling the technicality of the universe
            created above being "generation #0".
         */
        char[][] desiredGeneration = Evolution.progressUniverse(1, universe);

        do {
            int aliveCells = 0;
            //this part just goes through every cell there is in the current generation and counts the live ones
            for (int i = 0; i < desiredGeneration.length; i++) {
                for (int j = 0; j < desiredGeneration[0].length; j++) {
                    if (desiredGeneration[i][j] == 'O') aliveCells++;
                }
            }

            //gives the necessary data to the gui to refresh itself
            display.updateDisplay(counter + 1, aliveCells, desiredGeneration);

            desiredGeneration = Evolution.progressUniverse(1, desiredGeneration);
            counter++;

            while(isPaused) {
                // this has the effect of "pausing" the simulation if the flag is raised until it is changed again
                // Lord forgive me for what I have done, the hackiest part in this humble program, i hope...
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }

        } while (!resetFlag);
        /***/
        if (resetFlag) {
            //I really hope this isn't too horrible of a practice
            resetFlag = false;
            Thread t = new Thread(() -> runGameOfLife()); //overrides run()
            t.start();

        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        //comes with major lag because of asinine method to break the simulation and start it over
        //tbh i don't understand exactly why it does it, i'm just not surprised
        if ("Reset".equals(actionEvent.getActionCommand())) {
            resetFlag = true;
        }

        if ("Pause".equals(actionEvent.getActionCommand())) {
           isPaused = true;
           display.updatePauseButton();
        }

        if ("Resume".equals(actionEvent.getActionCommand())) {
            isPaused = false;
            display.updatePauseButton();
        }
    }

    private boolean resetFlag = false;
    private boolean isPaused = false;
    private char[][] universe;
    private Display display;
}
