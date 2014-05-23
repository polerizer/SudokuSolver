

/**
 * Jan 12, 2013
 * Puzzle.java
 * Daniel Pok
 * AP Java 6th
 */

/**
 * @author poler_000
 *
 */


public class Puzzle {

	private String offset = ""; //how much the output should be offset

	//you can name the puzzle and a field for comments or maybe difficulty
	protected String puzzleName; //i'm too lazy to write getters/setters for these since they don't affect function
	protected String comment;

	//this is a 2D array of integers containing the puzzle's current values. valid values are 1-9, 0 means blank cell
	private int[][] cells; //size 9x9

	////this is a 3D array of which values are still possible for a given cell.
	////address the cell in 2D then the 3rd dimension array is whether each value is possible (i.e. [0,0,0] = true means that the top left cell could be one)
	//boolean[][][] pos; //size 9x9x9

	//not sure whether i'll need the whole boolean array in the end so this is taking its place for now
	private int[][] numPos; //number of possibilities for a given cell

	//constructs and instance of Puzzle that is blank/empty
	public Puzzle() {
		cells = new int[9][9];
		//poss = new boolean[9][9][9];
		numPos = new int[9][9];
		puzzleName = "";
		comment = "";
	}

	//constructs an instance of Puzzle from a 9x9 int matrix
	public Puzzle(int[][] copyMatrix){
		this();
		if(copyMatrix.length == 9 && copyMatrix[0].length == 9) {
			//validate each entry before loading. If a number is System.out of range (not 0-9), set it to 0
			for(int i = 0; i < copyMatrix.length; i++) {
				for(int j = 0; j < copyMatrix[0].length; j++) {
					if(copyMatrix[i][j] < 0 || copyMatrix[i][j] > 9) {
						cells[i][j] = 0;
					} else {
						cells[i][j] = copyMatrix[i][j];
					}
				}
			}
			setPossibilities();
		} else {
			System.err.println(offset + "Invalid matrix size. Blank Puzzle was created instead.");
		}
	}

	//makes a new puzzle by copying another one
	public Puzzle(Puzzle puzzle){
		cells = puzzle.getCells();
		numPos = puzzle.getNumPos();
		puzzleName = puzzle.puzzleName;
		comment = puzzle.comment;
	}

	//set offset
	public void setOffset(String str){
		offset = str;
	}
	
	//getters and setters are 0 indexed

	//get value: note, this function is unsafe and will return garbage if call is System.out of range
	public int get(int row, int col){
		if(row > 8 || col > 8 || row < 0 || col < 0) {
			//instead of throwing an exception, it just gives a sentinel value of -1
			return -1;
		} else {
			return cells[row][col];		
		}
	}

	//set value: this operation is safe and will throw errors if the call is System.out of physical size
	public boolean set(int row, int col, int val) {
		if(row > 8 || col > 8 || row < 0 || col < 0 || val < 0 || val > 9){
			return false;
		} else {
			cells[row] [col] = val;
			return true;
		}
	}

	//gets the entire matrix
	public int[][] getCells(){
		int[][] temp = new int[9][9];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				temp[i][j] = cells[i][j];
			}
		}
		return temp;
	}

	//sets the entire matrix
	public boolean setCells(int[][] temp){
		if(temp.length != 9 || temp[0].length != 9){
			return false;
		} else {
			for(int i = 0; i < 9; i++){
				for(int j = 0; j < 9; j++){
					if(temp[i][j] >=0 && temp[i][j] <= 9){
						cells[i][j] = temp[i][j];
					} else {
						cells[i][j] = 0;
					}

				}
			}
			setPossibilities();
			return true;	
		}
	}

	//gets the number of possibilities for a cell
	public int getPos(int row, int col){
		if(row > 8 || col > 8 || row < 0 || col < 0) {
			return -1;
		} else {
			return numPos[row][col];
		}
	}

	//sets the number of possibilities for a cell
	public boolean setPos(int row, int col, int val){
		if(row > 8 || col > 8 || row < 0 || col < 0 || val < 0 || val > 9) {
			return false;
		} else {
			numPos[row][col] = val;
			return true;
		}
	}

	//gets the entire matrix
	public int[][] getNumPos(){
		int[][] temp = new int[9][9];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				temp[i][j] = numPos[i][j];
			}
		}
		return temp;
	}

	//sets the entire matrix
	public boolean setNumPos(int[][] temp){
		if(temp.length != 9 || temp[0].length != 9){
			return false;
		} else {
			for(int i = 0; i < 9; i++){
				for(int j = 0; j < 9; j++){
					if(temp[i][j] >=0 && temp[i][j] <= 9){
						numPos[i][j] = temp[i][j];
					} else {
						numPos[i][j] = 0;
					}

				}
			}
			return true;	
		}
	}

	//gets a copy of this puzzle
	public Puzzle getCopy(){
		return new Puzzle(this);
	}

	//intializes possibilities matrix from the cells matrix
	//note this will wipe an existing possibilities matrix! this is why it's private: it's for constructors and the like!
	private void setPossibilities(){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(cells[i][j] != 0){
					numPos[i][j] = 1;
				} else {
					numPos[i][j] = 0;
				}
			}
		}
	}

	//evaulates the number of possibilities for a given cell and sets the corresponding cell in numPos
	public int findPos(int row, int col){
		if(cells[row][col] != 0){
			numPos[row][col] = 1;
			return 1;
		} else {
			int num = 0;
			//printPuzzle();
			for(boolean i:getNotArray(row, col)){
				if(!i){
					num++;
				}
			}

			//now set the correct cell in numPos and return the number
			numPos[row][col] = num;
			return num;
		}
	}

	//returns a bool array with which values a cell cannot be (like findPos but returns the array instead of the count and doesn't set the numPos array)
	public boolean[] getNotArray(int row, int col){
		boolean[] notRow, notCol, notSquare, allNot; //these are boolean arrays saying which values the given cell cannot be
		//load them
		notRow = findRow(row);
		notCol = findCol(col);
		notSquare = findSquare(row, col);
		//now we'll combine the lists into one
		allNot = new boolean[9];
		for(int i = 0; i < 9; i++){
			allNot[i] = notRow[i] || notCol[i] || notSquare[i];
		}
		return allNot;
	}

	//a series of functions that get boolean arrays of which numbers are present in a row, col, square
	//these return lists of which numbers the given cell cannot be(or row, or column). TRUE = the value is already taken, FALSE = the value is possible
	private boolean[] findRow(int row){
		boolean[] not = new boolean[9];
		for(int j = 0; j < 9; j++){
			if(cells[row][j] != 0){
				not[cells[row][j]-1] = true;
			}
		}
		return not;
	}

	private boolean[] findCol(int col){
		boolean[] not = new boolean[9];
		for(int j = 0; j < 9; j++){
			if(cells[j][col] != 0){
				not[cells[j][col]-1] = true;
			}
		}
		return not;
	}

	private boolean[] findSquare(int row, int col){
		boolean[] not = new boolean[9];
		//find the top left cell of the square the thing is in by integer dividing the row/col and muliplying by 3 (i.e. (5,1) becomes (3,0)
		int sRow = ((int) (row/3)) * 3;
		int sCol = ((int) (col/3)) * 3;
		for(int i = sRow; i < sRow + 3; i++){
			for(int j = sCol; j < sCol + 3; j++){
				if(cells[i][j] != 0){
					not[cells[i][j]-1] = true;
				}
			}
		}
		return not;
	}

	//checks to see if the whole puzzle is solved
	public boolean solved(){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(cells[i][j] == 0){
					return false;
				}
			}
		}
		//check every row
		for(int i = 0; i < 9; i ++){
			//check every number that's supposed to be in that row
			for(int j = 1; j <= 9; j++){
				boolean part = false;
				//check every column against the number until one is found
				for(int k = 0; k < 9; k++){
					if(cells[i][k] == j){
						part = true;
						break;
					}
				}
				if(!part){
					return false;
				}
			}
		}

		//check every column
		for(int i = 0; i < 9; i ++){
			//check every number that's supposed to be in that column
			for(int j = 1; j <= 9; j++){
				boolean part = false;
				//check every row against the number until one is found
				for(int k = 0; k < 9; k++){
					if(cells[k][i] == j){
						part = true;
						break;
					}
				}
				if(!part){
					return false;
				}
			}
		}

		//check every square
		for(int i = 0; i < 3; i ++){
			for(int j = 0; j < 3; j++){
				//check every number that's supposed to be in that row
				for(int k = 1; k <= 9; k++){
					boolean part = false;
					//check every cell against the number until one is found
					for(int l = 0; l < 3; l++){
						for(int m = 0; m < 3; m++){
							if(cells[i*3 + l][j*3 + m] == k){
								part = true;
								break;
							}	
						}
					}
					if(!part){
						return false;
					}
				}
			}
		}

		//if every row, column, and square checks System.out, then it's solved!
		return true;

	}


	//print puzzle in a nice formatted form
	public void printPuzzle() {
		System.out.println(this.toString());
	}

	public void printSpaces() {
		String value = "";

		//get the puzzle name and comment if available
		if(!puzzleName.isEmpty()){
			value += offset + puzzleName;
		}
		if(!comment.isEmpty() && puzzleName.isEmpty()){
			value += offset + String.format("(%s)%n", comment);
		} else if (!comment.isEmpty()){
			value += String.format("(%s)%n", comment);
		}
		else if(!puzzleName.isEmpty()){
			value += String.format("%n");
		}

		//print the chart
		for(int i = 0; i < 9; i++){
			//row formatters
			if(i != 3 && i != 6){
				value += offset + String.format(" --- --- ---  --- --- ---  --- --- ---%n");
			} else {
				System.out.print(offset);
				value += offset + String.format("=======================================%n");
			}

			for(int j = 0; j < 9; j++){
				if(j == 0){
					value += offset;
				}
				//column formatters
				if(j != 3 && j != 6) {
					value += "| ";
				} else {
					value += "|| ";
				}

				//spit System.out the number, but substituting space for zero
				if(cells[i][j] != 0) {
					value += String.format("%d ", cells[i][j]);
				} else {
					value += "  ";
				}


				//end formatter
				if(j == 8){
					value += String.format("|%n");
				}
			}

			//bottom formatter formatters
			if(i == 8){
				value += offset + String.format(" --- --- ---  --- --- ---  --- --- ---%n");
			}
		}
		System.out.println(value);
	}

	//prints withSystem.out much formatting
	public void printTiny(){
		String value = "";

		//get the puzzle name and comment if available
		if(!puzzleName.isEmpty()){
			value += offset + puzzleName;
		}
		if(!comment.isEmpty() && puzzleName.isEmpty()){
			value += offset + String.format("(%s)%n", comment);
		} else if (!comment.isEmpty()){
			value += String.format("(%s)%n", comment);
		}
		else if(!puzzleName.isEmpty()){
			value += String.format("%n");
		}

		//print the chart
		for(int i = 0; i < 9; i++){
			//row formatters
			if(i != 3 && i != 6){
				//value += String.format(" --- --- ---  --- --- ---  --- --- ---%n");
			} else {
				value += offset + String.format("------------------%n");
			}

			for(int j = 0; j < 9; j++){
				if(j == 0) {
					value += offset;
				}
				//column formatters
				if(j != 3 && j != 6) {
					value += " ";
				} else {
					value += "|";
				}

				//spit System.out the number, but substituting space for zero
				value += String.format("%d", cells[i][j]);

				//end formatter
				if(j == 8){
					value += String.format("%n");
				}
			}
		}
		System.out.print(value);
	}

	//toString
	public String toString(){
		String value = "";

		//get the puzzle name and comment if available
		if(!puzzleName.isEmpty()){
			value += offset + puzzleName;
		}
		if(!comment.isEmpty() && puzzleName.isEmpty()){
			value += offset + String.format("(%s)%n", comment);
		} else if (!comment.isEmpty()){
			value += String.format("(%s)%n", comment);
		}
		else if(!puzzleName.isEmpty()){
			value += String.format("%n");
		}

		//print the chart
		for(int i = 0; i < 9; i++){
			//row formatters
			if(i != 3 && i != 6){
				value += offset + String.format(" --- --- ---  --- --- ---  --- --- ---%n");
			} else {
				value += offset + String.format("=======================================%n");
			}

			for(int j = 0; j < 9; j++){
				if(j==0){
					value += offset;
				}
				//column formatters
				if(j != 3 && j != 6) {
					value += "| ";
				} else {
					value += "|| ";
				}

				//spit System.out the number
				value += String.format("%d ", cells[i][j]);

				//end formatter
				if(j == 8){
					value += String.format("|%n");
				}
			}

			//bottom formatter formatters
			if(i == 8){
				value += offset + String.format(" --- --- ---  --- --- ---  --- --- ---%n");
			}
		}

		return value;
	}

	//prints the possibilities withSystem.out much formatting
	public void printPossibilities(){
		String value = "";

		value += offset + String.format("Possibilities:%n");

		//print the chart
		for(int i = 0; i < 9; i++){

			//row formatters
			if(i != 3 && i != 6){
				//value += String.format(" --- --- ---  --- --- ---  --- --- ---%n");
			} else {
				value += offset + String.format("------------------%n");
			}

			for(int j = 0; j < 9; j++){
				if(j == 0){
					value += offset;
				}
				//column formatters
				if(j != 3 && j != 6) {
					value += " ";
				} else {
					value += "|";
				}

				//spit System.out the number, but substituting space for zero
				value += String.format("%d", numPos[i][j]);

				//end formatter
				if(j == 8){
					value += String.format("%n");
				}
			}
		}
		System.out.print(value);
	}

	//prints the possibilities withSystem.out much formatting
	public void printPossibilitiesSpaces(){
		String value = "";

		value += offset + String.format("Possibilities:%n");

		//print the chart
		for(int i = 0; i < 9; i++){
			//row formatters
			if(i != 3 && i != 6){
				//value += String.format(" --- --- ---  --- --- ---  --- --- ---%n");
			} else {
				value += offset + String.format("------------------%n");
			}

			for(int j = 0; j < 9; j++){
				if(j == 0){
					value += offset;
				}
				//column formatters
				if(j != 3 && j != 6) {
					value += " ";
				} else {
					value += "|";
				}

				//spit System.out the number, but substituting space for zero
				if(numPos[i][j] == 1 && cells[i][j] != 0){
					value += " ";
				} else{
					value += String.format("%d", numPos[i][j]);						
				}

				//end formatter
				if(j == 8){
					value += String.format("%n");
				}
			}
		}
		System.out.print(value);
	}
}
