
/**
 * Jan 12, 2013
 * Solver.java
 * Daniel Pok
 * AP Java 6th
 */

/**
 * @author poler_000
 *
 */

public class Solver {
	//Class variables
	private Puzzle puzzle; //the puzzle that this solver is solving
	private boolean solved; //tracks whether or not the puzzle was solved successfully or not
	private int passes; //tracks how hard the puzzle was to solve for the computer
	private int hints; //the number of hints that the puzzle had before the solver started working
	public boolean allVerbose = false; //if this is set to true, diagnostic info will be posted to console
	private String offset = ""; //controls how much the output should be offset

	//creates a solver to solve the given puzzle, creating a copy of the puzzle
	public Solver(Puzzle input){
		puzzle = new Puzzle(input);
		solved = false;
		passes = 0;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(puzzle.get(i, j) > 0){
					hints++;
				}
			}
		}
	}

	//creates a solver that references the given puzzle if the boolean is true instead of copying it
	public Solver(Puzzle input, boolean byRef){
		if(byRef){
			puzzle = input;
		} else {
			puzzle = new Puzzle(input);
		}
		solved = false;
		passes = 0;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(puzzle.get(i, j) > 0){
					hints++;
				}
			}
		}
	}

	//set offset
	public void setOffset(String str){
		puzzle.setOffset(str);
		offset = str;
	}
	
	//gets hints
	public int hints(){
		return hints;
	}

	//gets passes
	public int passes(){
		return passes;
	}

	//gets a copy of the puzzle in its current state (the solution, if solved is true)
	public Puzzle result(){
		return new Puzzle(puzzle);
	}

	//solves the puzzle using the other exposed methods. returns whether the puzzle was solved after its attempts
	public boolean solve(){
		return solve(false);
	}

	public boolean solve(boolean verbose){
		
		//print starting matrix
		if(allVerbose){
			System.out.println(offset + "Attempting to solve(" + hints + " hints):");
			System.out.println(puzzle);
			setAllPos();
			puzzle.printPossibilitiesSpaces();
			verbose = true;
		}
		//find possibilities
		if(verbose){
			System.out.println(offset + "Finding possibilities...");
		}
		if(!setAllPos()){
			if(verbose) {
				System.out.println(offset + "Puzzle unsolveable.");
			}
			return false;
		}

		//1st level
		if(verbose){
			System.out.println(offset + "Solving for squares with one possibility...");
		}
		if(!fillDepthOne()){
			if(verbose) {
				System.out.println(offset + "Puzzle unsolveable.");
			}
			return false;
		}
		if(solved()){
			if(verbose){
				System.out.println(offset + "Puzzle solved in " + passes + " passes.");
			}
			return true;
		} else {
			if(verbose){
				System.out.printf(offset + "Solved %d cells.%n", passes);
			}
		}

		//2nd level
		if(verbose){
			System.out.println(offset + "Solving for squares with multiple possibilities...");
		}
		if(!fillDepthTwo()){
			if(verbose) {
				System.out.println(offset + "Puzzle unsolveable.");
			}
			return false;
		}
		if(solved()){
			if(verbose){
				System.out.println(offset + "Puzzle solved in " + passes + " passes.");
			}
			return true;
		}

		//failure
		return false;
	}

	//checks every indefinite cell and sets possibilities
	public boolean setAllPos(){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(puzzle.getPos(i, j) != 1){
					if(puzzle.findPos(i, j)==0){
						return false;
					}
				}
			}
		}
		return true;
	}

	//checks cells that have one possibility but are not filled and fills all of them
	public boolean fillDepthOne(){ //returns true is the stack ends with no more 1 possibles, false if the puzzle becomes invalid instead
		int count = 0;

		if(!setAllPos()){
			return false;
		}
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(puzzle.getPos(i,j) == 1 && puzzle.get(i, j) == 0){
					if (setOne(i,j) == -1){
						return false;
					}
					count++;
					passes++;
				}
			}
		}

		//recursion logic: keep checking after each level until no changes are made
		if(count == 0){
			return true;
		} else {
			return fillDepthOne();
		}
	}

	//cells with multiple possibilities
	public boolean fillDepthTwo(){

		if(!setAllPos()){
			return false;
		}

		//find the least possibilities cell
		int min = Integer.MAX_VALUE, row = -1, col = -1;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(puzzle.getPos(i,j) < min && puzzle.getPos(i, j) > 1){
					min = puzzle.getPos(i, j);
					row = i;
					col = j;
				}
			}
		}
		
		if(row == -1 || col == -1 || min == Integer.MAX_VALUE){
			System.err.println(offset + "Failed to find solvable portion.");
			return false;
		}
		
		//try every possibility there is to try
		for(int i = 0; i < puzzle.getPos(row, col); i++){
			if(setTwo(row,col, i)){
				return true;
			}
		}
		
		//failure
		return false;

	}



	//sets a cell to its correct value if it has only one possibility
	public int setOne(int row, int col){
		if((puzzle.getPos(row, col) == 0 && puzzle.findPos(row, col) == 1) || puzzle.getPos(row, col) == 1){
			boolean[] allNot = puzzle.getNotArray(row, col);
			int val = -1;
			for(int i = 0; i < allNot.length; i++){
				if(!allNot[i]){
					if(val == -1){
						val = i;
					} else {
						//there was more than one possibility (which should not be possible at this point...)
						return -1;
					}
				}
			}
			if(val == -1){
				return -1;
			} else{
				if(puzzle.set(row,col, val + 1)){
					if(puzzle.findPos(row, col) == 0){
						return -1;
					}
					for(int i = 0; i < 9; i ++){
						if(puzzle.findPos(row, i) == 0){
							return -1;
						}
					}
					for(int i = 0; i < 9; i++){
						if(puzzle.findPos(i, col) == 0) {
							return -1;
						}
					}
					int sRow = ((int) (row/3)) * 3;
					int sCol = ((int) (col/3)) * 3;
					for(int i = sRow; i < sRow + 3; i++){
						for(int j = sCol; j < sCol + 3; j++){
							if(puzzle.findPos(sRow, sCol) == 0){
								return -1;
							}
						}
					}
					return val + 1;
				} else {
					return -1;
				}
			}
		} else {
			//if it can't set the cell, it will return -1
			return -1;
		}
	}

	//code to solve for 2nd level stuff
	public boolean setTwo(int row, int col, int n){
		Puzzle copy = new Puzzle(puzzle);
		copy.set(row, col, getNPos(row, col, n));
		Solver solver = new Solver(copy, true);
		solver.setOffset(offset + "  ");
		
		if(allVerbose){
			System.out.print(offset);
			System.out.printf("Trying with [%d,%d] set to %d%n", row, col, getNPos(row, col, n));
			solver.allVerbose = true;

			if(solver.solve(true)){
				passes += solver.passes();
				puzzle = solver.result();
				solver = null;
				copy = null;
				return true;
			} else {
				passes += solver.passes();
				solver = null;
				copy = null;
				return false;
			}	
		} else {
			if(solver.solve()){
				passes += solver.passes();
				puzzle = solver.result();
				solver = null;
				copy = null;
				return true;
			} else {
				passes += solver.passes();
				solver = null;
				copy = null;
				return false;
			}
		}
	}

	//gets the nth 0 indexed possibility for a cell
	public int getNPos(int row, int col, int n){
		boolean[] allNot = puzzle.getNotArray(row, col);
		for(int i = 0; i <9 ; i++){
			if(!allNot[i]){
				if(--n < 0){
					return i + 1;
				}
			}
		}
		System.err.println("n = " + n);
		return -1;
	}

	//checks to see if the whole puzzle is solved
	public boolean solved(){
		solved = puzzle.solved();
		return solved;
	}

}
