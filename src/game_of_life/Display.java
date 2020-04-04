package game_of_life;
/**
 * the face of the simulation, uses bits of Java2D and mostly Swing
 * it is a frame that uses a border layout and puts a grid of rectangles
 * representing the universe and the cells living in it, and adds a panel
 * to the left for controlling various aspects of the simulation
 * (well, you can pause it or reset it and change colors i guess).
 * listens for changes in the pace slider and the color button pressing
 * */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

public class Display extends JFrame implements GameOfLifeConstants, ActionListener, ChangeListener {
    /**
     * method to be called by the main object whenever it wants to display a change
     * in the universe
     * */
    public void updateDisplay(int currentGeneration, int aliveCells, char[][] displayMatrix) {
        /*
         * picasso is a "drawing machine" that actually does the work of painting
         * the new arrangement of cells in universe
         * */
        picassoThePainter.updateData(displayMatrix);
        GenerationLabel.setText("Generation #" + currentGeneration);
        AliveLabel.setText("Alive: " + aliveCells);
        this.update(getGraphics()); //i honestly don't understand what's a "graphics context", i hope this is bug free
    }
    /**@@param master: the Main object that is running the simulation which also handles some GUI events
     * */
    public Display(ActionListener master) {
        super("Game Of Life");
        //i want to translate this program eventually so most of these parameters should've been constants, oh well...
        GenerationLabel.setText("Generation #0");
        AliveLabel.setText("Alive: 0");
        PlayToggleButton.setLabel("Pause");
        ResetButton.setLabel("Reset");
        ColorButton.setLabel("Pick color...");

        PlayToggleButton.setPreferredSize(new Dimension(75,25));
        ResetButton.setPreferredSize(new Dimension(75,25));
        ColorButton.setPreferredSize(new Dimension(75,25));
        speedSlider.setPreferredSize(new Dimension(20,150));
        speedSlider.setMinorTickSpacing(10);
        colorPicker.setPreferredSize(new Dimension(200,100));

        //bad software architecture led to these elements being handled in different objects for reasons
        PlayToggleButton.addActionListener(master);
        ResetButton.addActionListener(master);
        ColorButton.addActionListener(this);
        speedSlider.addChangeListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        //dont care about the height so it just sets the panel height to be the same as the frame
        controlPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH, this.getHeight()));

        //panels use FlowLayout by default
        controlPanel.add(GenerationLabel);
        controlPanel.add(AliveLabel);
        controlPanel.add(PlayToggleButton);
        controlPanel.add(ResetButton);
        controlPanel.add(ColorButton);
        controlPanel.add(new JLabel("Pace:  "));
        controlPanel.add(speedSlider);


        add(controlPanel, BorderLayout.WEST);

        //picasso the drawing machine is also a JComponent
        picassoThePainter = new DrawingMachine();
        add(picassoThePainter, BorderLayout.CENTER);

        //this never gets me the size that i want and i don't understand why
        setSize((CELL_LENGTH + 1) * UNIVERSE_SIZE + CONTROL_PANEL_WIDTH, (CELL_LENGTH+1) * UNIVERSE_SIZE + 30);
        setVisible(true);

    }

    public void updatePauseButton() {
        if ("Pause".equals(PlayToggleButton.getLabel())) {
            PlayToggleButton.setLabel("Resume");
        } else {
            PlayToggleButton.setLabel("Pause");
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        //sets the speed of the animation of the universe living between generations
        if (speedSlider == e.getSource()) {
            picassoThePainter.evolution_delay = speedSlider.getValue();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Pick color...".equals(e.getActionCommand())) {
            /*
            the color chooser is an object that only appears when you press the button
            i'm using the useful method that automatically creates a JDialog for you
            and eventually returns a color to be used to color living cells
            */
            Color c = colorPicker.showDialog(this,"Color Picker", Color.DARK_GRAY);
            if (c != null) {
                picassoThePainter.currentFillColor = c;
            }
        }
    }

    private DrawingMachine picassoThePainter;
    private Button PlayToggleButton = new Button();
    private Button ResetButton = new Button();
    private Button ColorButton = new Button();
    private JLabel GenerationLabel = new JLabel();
    private JLabel AliveLabel = new JLabel();
    private JSlider speedSlider = new JSlider(SwingConstants.VERTICAL,1, 500, EVOLUTION_DELAY);
    private JColorChooser colorPicker = new JColorChooser(Color.DARK_GRAY);

}
/**
 * DrawingMachine is a component that can be added to a frame/panel, it
 * handles the drawing of the grid representing the universe with Java2D
 * */
class DrawingMachine extends JComponent implements GameOfLifeConstants {

        public void updateData(char[][] newMatrix) {
            cellsMatrix = newMatrix;

            try {
                java.lang.Thread.sleep(evolution_delay);
            } catch (InterruptedException e) {
                //is it bad practice to leave this empty?
            }
        }
        /**
         * draws squares to make up a grid. draws an extra, one pixel smaller but
         * colored square inside aforementioned squares to represent each live cell
         * */
        @Override
        public void paint(Graphics g) {
                Graphics2D graph = (Graphics2D) g;

                if (cellsMatrix != null) {
                        int squareX = 0;
                        int squareY = 0;
                        for (int i = 0; i < cellsMatrix.length; i++) {
                                for (int j = 0; j < cellsMatrix[0].length; j++) {
                                        Shape drawRect = new Rectangle2D.Float(squareX, squareY, CELL_LENGTH, CELL_LENGTH);
                                        graph.setColor(Color.BLACK);
                                        graph.draw(drawRect);
                                    //what if I decide to use a char other than 'O'?
                                        if (cellsMatrix[i][j] == 'O') {
                                                Shape filling = new Rectangle2D.Float(squareX + 1, squareY + 1, CELL_LENGTH - 1, CELL_LENGTH - 1);
                                                graph.setPaint(currentFillColor);
                                                graph.fill(filling);
                                        }
                                        squareX += CELL_LENGTH;
                                }
                                squareY += CELL_LENGTH;
                                squareX = 0;
                        }
                }
        }

        Color currentFillColor = Color.DARK_GRAY;
        private char[][] cellsMatrix;
        int evolution_delay = EVOLUTION_DELAY;
}