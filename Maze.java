/*
    Student Name: Donnie Ranjel
    File Name: Maze.java
    Date: 11/11/2025
    Class: CSC 1060-503
 
*/

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

// A Maze class that loads the maze from a file, validates it, and uses recursive logic to solve it
public class Maze {

    // Maze dimensions constants
    private final int MAX_ROWS = 10;
    private final int MAX_COLS = 20;

    // Stores the maze grid (#, $, .)
    private char[][] mazeData;

    // Where the coordinates of the correct path will be stored
    private LinkedList<Point> path;

    // Stores the column index of the '$' in the first row
    private int startCol;

    // A boolean flag to track if the maze has been solved
    private boolean solved;

    // Constructor for the Maze class that initializes the maze grid and path list
    public Maze() {

        // Initializes a 2D array
        this.mazeData = new char[MAX_ROWS][MAX_COLS];
        
        // Initializes LinkedList
        this.path = new LinkedList<>();
        
        // Initialize fields to my default values
        this.startCol = -1; // Sentinel value
        this.solved = false;
    }

    /*
        Loads a maze from a text file, validates its format, and fills the 2D mazeData array
        @param file The .txt file selected by the user
        @throws IOException If there's an error reading the file
        @throws MazeException If the maze violates any of the 5 validationrules
    */
    public void loadMaze(File file) throws IOException, MazeException {
        // Using FileInputStream and Scanner to read the file
        // We use a try-with-resources block to automatically close the file
        try (FileInputStream fileStream = new FileInputStream(file);
             Scanner scanner = new Scanner(fileStream)) {

            int rowCount = 0;
            boolean startFound = false;
            
            // Reads the selected file line-by-line until the end or the row limit
            while (scanner.hasNextLine() && rowCount < MAX_ROWS) {
                String line = scanner.nextLine();

                // 1st Validation: Maze is too wide
                if (line.length() > MAX_COLS) {
                    throw new MazeException("Maze is wider than 20 columns.");
                }

                // Go through the line one character at a time
                for (int col = 0; col < line.length(); col++) {
                    char c = line.charAt(col);

                    // 2nd Validation: Invalid character
                    if (c != '#' && c != '$') {
                        throw new MazeException("Invalid character '" + c + "' found at (" + (rowCount + 1) + ", " + (col + 1) + ").");
                    }

                    // 3rd Validation: Find the start point in row 0
                    if (rowCount == 0 && c == '$') {
                        if (startFound) {
                            // 2 $ in the first row
                            throw new MazeException("Multiple start points found in first row.");
                        }
                        this.startCol = col; // The start is found
                        startFound = true;
                    }

                    // Stores the character in the 2D array
                    this.mazeData[rowCount][col] = c;
                }
                
                // Horizontal Padding Loop: Fills the rest of the column with walls if falls short
                for (int col = line.length(); col < MAX_COLS; col++) {
                    this.mazeData[rowCount][col] = '#';
                }

                rowCount++;
            }

            // 4th Validation: Maze is too long
            if (scanner.hasNextLine()) {
                throw new MazeException("Maze is longer than 10 rows.");
            }
            
            // Vertical Padding Loop: Fills the rest of the rows with walls if falls short
            while (rowCount < MAX_ROWS) {
                for (int col = 0; col < MAX_COLS; col++) {
                    this.mazeData[rowCount][col] = '#';
                }
                rowCount++;
            }

            // Post-Loop Validations
            
            // 5th Validation: No Start Point
            if (!startFound) {
                throw new MazeException("No start point '$' found in first row.");
            }

            // 6th Validation: No Exit Point
            boolean exitFound = false;
            for (int col = 0; col < MAX_COLS; col++) {
                if (this.mazeData[MAX_ROWS - 1][col] == '$') {
                    exitFound = true;
                    break; // Found one, no need to keep looking
                }
            }
            if (!exitFound) {
                throw new MazeException("No exit '$' found in last row (row 10).");
            }
            
        } // The file automatically closes here from try-with-resources
    }

    /*
        Converts the 2D mazeData array into a single String for display in the GUI's JTextArea.
        @return A String representation of the maze.
    */
    public String getMazeString() {
        StringBuilder sb = new StringBuilder();
        
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLS; col++) {
                sb.append(this.mazeData[row][col]);
            }
            sb.append("\n"); // A newline after each row
        }
        return sb.toString();
    }

    /*
        Public method to start the recursive solving process.
        It initializes the path list and calls the recursive helper.
        @return true if a path is found, false otherwise.
    */
    public boolean solve() {
        // Resets the path for re-solving
        this.path = new LinkedList<>();
        
        // Starts recursion from row 0 and the column where '$' was found
        if (this.startCol != -1) {
            this.solved = findPathRecursive(0, this.startCol);
            return this.solved;
        }
        return false;
    }

    private boolean findPathRecursive(int row, int col) {
        // 1. Bounds Check
        if (row < 0 || row >= MAX_ROWS || col < 0 || col >= MAX_COLS) {
            return false;
        }

        // 2. Wall or Visited Check
        if (this.mazeData[row][col] == '#' || this.mazeData[row][col] == '.') {
            return false;
        }

        // 3. Mark Visited
        this.mazeData[row][col] = '.'; // Mark anything visited in trial
        this.path.add(new Point(row, col)); // Add to path tentatively

        // 4. Check Exit
        if (row == MAX_ROWS - 1) { // At bottom row
             return true; // Path found
        }

        // 5. Recursive Search
        if (findPathRecursive(row + 1, col) ||  // Down
            findPathRecursive(row, col + 1) ||  // Right
            findPathRecursive(row, col - 1) ||  // Left
            findPathRecursive(row - 1, col)) {  // Up
            return true;
        }

        // 6. No direction worked, hit dead end, now backtrack
        this.path.removeLast(); // Remove this coordinate from the path
        return false;
    }

    /*
        Converts the valid path in the linked list into a string
        Prints solution
        @return The string listing all coordinates in the path
    */
    public String getPathString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Correct Path through the maze is;\n"); // Match exact wording
        
        for (Point p : this.path) {
            // Added 1 to each to convert to 1-based index
            sb.append("(").append(p.y + 1).append(",").append(p.x + 1).append(")\n");
        }
        
        return sb.toString();
    }

}