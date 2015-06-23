/*
 * Puzzle.java
 *
 * Implementation of a class that represents a Sudoku puzzle and solves
 * it using recursive backtracking.
 *
 * Computer Science S-111, Harvard University
 *
 * skeleton code by the course staff
 *
 * Modified by:     Andy LeRoy, apleroy@gmail.com
 * Date modified:   7/23/13
 */

import java.io.*;
import java.util.Scanner;

public class Puzzle {
    // the dimension of the puzzle grid
    public static final int DIM = 9;
    
    // the dimension of the smaller subgrids within the grid
    public static final int SUBGRID_DIM = 3; 
    
    // The current contents of the cells of the puzzle. 
    // values[r][c] gives the value in the cell at row r, column c.
    // The rows and columns are numbered from 0 to DIM-1.
    private int[][] values;
    
    // Indicates whether the value in a given cell is fixed 
    // (i.e., part of the original puzzle).
    // valIsFixed[r][c] is true if the value in the cell 
    // at row r, column c is fixed, and valIsFixed[r][c] is false 
    // if the value in that cell is not fixed.
    private boolean[][] valIsFixed;

    // These matrices allow us to determine if a given
    // row or column already contains a given value.
    // For example, rowHasValue[3][4] will be true if row 3
    // already has a 4 in it.
    private boolean[][] rowHasValue;
    private boolean[][] colHasValue;

    
    // XXX: add any additional fields here. 
    // In particular, we recommend adding one or more fields
    // to keep track of whether a given subgrid
    // (i.e., a given SUBGRID_DIM x SUBGRID_DIM region of the
    // puzzle) already contains a given value.
    private boolean[][][] sectorHasValue;

    /** 
     * Constructs a new Puzzle object, which initially
     * has all empty cells.
     */
    public Puzzle() {
        values = new int[DIM][DIM];
        valIsFixed = new boolean[DIM][DIM];
        rowHasValue = new boolean[DIM][DIM + 1];
        colHasValue = new boolean[DIM][DIM + 1];
        
        // XXX: add code to initialize any
        // fields that you added.
        //This is a 3D boolean to hold the sector grid and the associated value
        //it is constructed as a row (0-2), column(0-2), and value(1-9).
        sectorHasValue = new boolean[SUBGRID_DIM][SUBGRID_DIM][DIM + 1];
             
    }
    
    /**
     * This is the key recursive-backtracking method.
     * Returns true if a solution has been found, and false otherwise.
     * 
     * Each invocation of the method is responsible for finding the
     * value of a single cell of the puzzle. The parameter n
     * is the number of the cell that a given invocation of the method
     * is responsible for. We recommend that you consider the cells
     * one row at a time, from top to bottom and left to right,
     * which means that they would be numbered as follows:
     * 
     *     0  1  2  3  4  5  6  7  8
     *     9 10 11 12 13 14 15 16 17
     *    18 ...
     */
    //n represents cells 0 - 80;
    public boolean solve(int n) {
        // XXX: replace this return statement with your implementation
        // of the method.
        //if n gets to cell 81 - we have completed the puzzle.
        if (n == (DIM * DIM)) {
            //display();
            return true;
        }
    
        //if the value is fixed (ie already placed on the board - skip it by adding one and returning to the recursive case).
        if (valIsFixed[n / 9][n % 9] == true) {
            return solve (n + 1);
        }
        
            //for each cell (0-80) - check the possible values (1-9) by looping through
            for (int i = 1; i <= DIM; i++) {
                
                //for each value check if the cell meets the constraints of the puzzle - outlined below
                if ((rowHasValue[n / 9][i] == false) &&             //does the row already contain the value?
                (colHasValue[n % 9][i] == false) &&                 //does the column already contain the value?
                (sectorHasValue[n / 27][(n % 9)/ 3][i] == false)) { //does the sector (subgrid) contain the value?  

                //**The sector method checks to see if the the current cell (1-80) has a value (1-9) in the given sector (3x3 subgrid).
                //This is a 3d array in which we pass the row, column and value.
                //The sector has rows 0,1,2 and columns 0,1,2.
                //To place the value in the appropriate sector we take the cell(n) and divide the row by 27 and take the (column % 9) / 3. 
                //Thesse "division" formulas likewise apply to the methods to place and remove invoked below.

                //if we pass the constraints, place the value (i) into the cell (n).
                    //the place value method marks the appropriate row and column as filled
                    placeVal((i), (n / 9), (n % 9)); 
                    
                    //the placeSector method marks the appropriate sector as filled.
                    placeSector((i), (n / 27), ((n % 9) / 3));
                        
                        //recursive case - continue at n + 1
                        if (solve(n + 1)) {return true;}

                    //if the above case is false, we must backtrack and replace the constraints (change back to false) at the last value of n.
                    removeSector((i), (n / 27), ((n % 9) / 3));
                    removeVal((i), (n / 9), (n % 9));

                }
            
            }
            
            return false;
        
    }
    
    /**
     * place the specified value in the cell with the
     * specified coordinates, and update the state of
     * the puzzle accordingly.
     */
    public void placeVal(int val, int row, int col) {
        values[row][col] = val;
        rowHasValue[row][val] = true;
        colHasValue[col][val] = true;
    }

    //Additional method to update the sector (3x3 subgrid) with as true
    public void placeSector(int val, int row, int col) {         
        sectorHasValue[row][col][val] = true;    
    }
    
    /**
     * remove the specified value from the cell with the
     * specified coordinates, and update the state of
     * the puzzle accordingly.
     */
    public void removeVal(int val, int row, int col) {
        values[row][col] = 0;
        rowHasValue[row][val] = false;
        colHasValue[col][val] = false;
        
    }

    //Additional method to update the sector (3x3 subgrid) as false
    public void removeSector(int val, int row, int col) {        
        sectorHasValue[row][col][val] = false;
    }
    
    /**
     * Reads in a puzzle specification from the specified Scanner,
     * and uses it to initialize the state of the puzzle.  The
     * specification should consist of one line for each row, with the
     * values in the row specified as digits separated by spaces.  A
     * value of 0 should be used to indicate an empty cell.
     */ 
    public void readFrom(Scanner input) {
        for (int r = 0; r < DIM; r++) {
            for (int c = 0; c < DIM; c++) {
                int val = input.nextInt();
                placeVal(val, r, c);

                //The below method also places the input values into the appropriate sectors (3x3 subgrids).
                placeSector(val, (r / 3), (c / 3));

                if (val != 0)
                    valIsFixed[r][c] = true; //returns true only if value other than 0 is placed in cell
            }
            input.nextLine();
        }
    }
    
    /**
     * Displays the current state of the puzzle.
     * You should not change this method.
     */
    public void display() {
        for (int r = 0; r < DIM; r++) {
            printRowSeparator();
            for (int c = 0; c < DIM; c++) {
                System.out.print("|");
                if (values[r][c] == 0)
                    System.out.print("   ");
                else
                    System.out.print(" " + values[r][c] + " ");
            }
            System.out.println("|");
        }
        printRowSeparator();
    }
    
    // A private helper method used by display() 
    // to print a line separating two rows of the puzzle.
    private static void printRowSeparator() {
        for (int i = 0; i < DIM; i++)
            System.out.print("----");
        System.out.println("-");
    }
}
