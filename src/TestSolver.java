import java.util.ArrayList;

/**
 * Jan 12, 2013
 * TestSolver.java
 * Daniel Pok
 * AP Java 6th
 */

/**
 * @author poler_000
 *
 */
public class TestSolver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean allVerbose = true;
		String fileName = "";
		if(args.length < 1){
			fileName = "puzzle.txt";
		} else if(args.length < 2) {
			fileName = args[0];
			allVerbose = false;
		} else {
			fileName = args[0];
			allVerbose = true;
		}
		Puzzle puzzle = getPuzzle(fileName);
		System.out.println("Puzzle loaded from file \"" + fileName + "\".");

		Solver solver = new Solver(puzzle, true);
		solver.allVerbose = allVerbose;
		solver.setOffset("  ");
		
		if(solver.solve()){
			System.out.println("Solution in " + solver.passes() + " passes: ");
			solver.result().printPuzzle();
		} else {
			solver.setAllPos();
			solver.result().printPuzzle();
			puzzle.printPossibilities();
		}
	}

	private static Puzzle getPuzzle(String filename) {
		FileHandler file = new FileHandler(filename);
		if(file.ready()) {
			int[][] cells = new int[9][9];
			int rows = 0;
			while(file.ready() && rows < 9) {
				ArrayList<Integer> temp = getNumbers(file.readLine());
				if(temp.size() > 0){
					for(int i = 0; i < temp.size(); i++){
						cells[rows][i] = temp.get(i);
					}
					rows++;
				}
			}
			file.close();
			return new Puzzle(cells);
		} else {
			file.close();
			//if it fails, return a blank one
			return new Puzzle();
		}
	}

	private static ArrayList<Integer> getNumbers(String line){
		//make a temporary array list to load values as they come so I can avoid regular expressions
		ArrayList<Integer> vals = new ArrayList<Integer>();
		int i = 0;
		String temp = "";
		while (i < line.length()) {
			if(line.charAt(i) >= 48 && line.charAt(i) <= 57){
				temp += line.charAt(i);
			} else if(temp.length() > 0) {
				try{
					vals.add(Integer.parseInt(temp));
				} catch (Exception ex) {
					vals.add(0);
				} finally {
					temp = "";
				}
			}
			i++;
		}
		if(!temp.isEmpty()){
			try{
				vals.add(Integer.parseInt(temp));
			} catch (Exception ex) {
				vals.add(0);
			} finally {
				temp = "";
			}
		}
		return vals;
	}

}
