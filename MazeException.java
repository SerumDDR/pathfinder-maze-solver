/*
    Student Name: Donnie Ranjel
    File Name: MazeException.java
    Date: 11/11/2025
    Class: CSC 1060-503
 
*/

//  A custom exception for handling all maze errors built in Maze.java
public class MazeException extends Exception {

    /*
        Constructor that takes a specific error message.
        @param message The error message
    */
    public MazeException(String message) {
        // super passes the message up to the parent Exception class
        super(message);
    }
}