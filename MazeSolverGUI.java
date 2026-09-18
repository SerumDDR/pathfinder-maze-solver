/*
    Student Name: Donnie Ranjel
    File Name:  MazeSolverGUI.java
    Date: 11/11/2025
    Class: CSC 1060-503

    Description: This is the main entry point for the application.
    >> RUN THIS FILE TO START THE PROGRAM <<
 
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MazeSolverGUI extends JFrame {

    // GUI Components
    private JButton openFileButton;
    private JButton solveButton;
    private JTextArea mazeTextArea;
    private JTextArea pathTextArea;
    private JFileChooser fileChooser;

    // The logic object that solves the maze in Maze.java
    private Maze maze;

    // Constructor to build the window when called
    public MazeSolverGUI() {
        // 1. Setup the main window frame
        setTitle("Maze Solver");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 2. Initialize Components
        // A file picker created
        fileChooser = new JFileChooser();
        
        // Text areas to show the maze and the solution path
        mazeTextArea = new JTextArea(15, 25);
        mazeTextArea.setEditable(false);
        mazeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));  // Keeps the grid maze aligned
        pathTextArea = new JTextArea(15, 25);
        pathTextArea.setEditable(false);
        
        // Buttons for opening a file and solving the maze
        openFileButton = new JButton("Open File");
        solveButton = new JButton("Solve Maze");
        solveButton.setEnabled(false); // Only works after a maze is loaded

        // 3. Adds all components to the window
        // Middle panel holds the maze and path displays
        JPanel textPanel = new JPanel();
        textPanel.add(new JScrollPane(mazeTextArea));
        textPanel.add(new JScrollPane(pathTextArea));
        add(textPanel, BorderLayout.CENTER);

        // Bottom panel holds the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openFileButton);
        buttonPanel.add(solveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 4. Logic to hook up button actions
        // Logic for when Open File is clicked

        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOpenButton();
            }
        }); 

        // Logic for when Solve Maze button is clicked 
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (maze != null) {
                    // 1. Attempt to solve the maze
                    boolean solved = maze.solve();
                    
                    // 2. Update the maze display 
                    // (This will show the '.' characters marking the path)
                    mazeTextArea.setText(maze.getMazeString());
                    
                    // 3. Update and show the path if solved, or a message if not

                    if (solved) {
                        pathTextArea.setText(maze.getPathString());
                    } else {
                        pathTextArea.setText("No path found through this maze.");
                    }
                }
            }
        });
    }

    // Handle the "Open File" button click
    private void handleOpenButton() {
        int fileChooserVal = fileChooser.showOpenDialog(this);

        if (fileChooserVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Create a new Maze object
            maze = new Maze();

            try {
                // Call and load the maze from the file

                maze.loadMaze(selectedFile);
                
                // Update and show the maze in the text area

                mazeTextArea.setText(maze.getMazeString());
                pathTextArea.setText("Maze loaded. Ready to solve.");

                // Enable the solve button now that a valid maze is loaded
                solveButton.setEnabled(true);
                
            } catch (Exception ex) {
                // Handle errors with MazeException or IOException
                mazeTextArea.setText("Error loading maze: " + ex.getMessage());
                pathTextArea.setText("");
                solveButton.setEnabled(false); // Disable solve if loading failed
            }
        }
    }

    // Main Method for point of entry to start the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MazeSolverGUI().setVisible(true);
        });
    }
}