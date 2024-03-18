import java.util.*;

public class Sudoku {
    /**
     * Returns the filled in sudoku grid.
     *
     * @param grid the partially filled in grid. unfilled positions are -1.
     * @return the fully filled sudoku grid.
     */
    public static int[][] solve(int[][] grid) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        HashMap<Integer, int[]> map = new HashMap<>();
        HashMap<Integer, Integer> subnetMap = new HashMap<>();
        int n = (int) Math.sqrt(grid.length);
        for (int i = 1; i <= grid.length * grid.length; i++) {
            int row = (i - 1) / grid.length;
            int col = (i - 1) % grid.length;
            int subnet = ((row / n) * n + (col / n)) + 1; // Calculate subnet for 3x3 grid
            map.put(i, new int[]{row, col});
            subnetMap.put(i, subnet);
        }

        // TODO: add your variables
        Solver.Variable[] variablesArray = new Solver.Variable[grid.length * grid[0].length];
        List<Integer> domain = new ArrayList<>();
        for (int i = 1; i <= grid.length; i++) {
            domain.add(i);
        }
        for (int i = 0; i < grid.length * grid[0].length; i++) {
            if (grid[map.get(i + 1)[0]][map.get(i + 1)[1]] != -1) {
                variablesArray[i] = new Solver.Variable(List.of(grid[map.get(i + 1)[0]][map.get(i + 1)[1]]));
            } else {
                variablesArray[i] = new Solver.Variable(domain);
            }
        }

        // TODO: add your constraints
        constraints.add(new Solver.SudokuNotCollideConstraint(map, subnetMap));


        // Convert to arrays
//        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
//        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        int[] result = solver.findOneSolution();
        // Change result into 2D array
        int[][] result2D = new int[grid.length][grid[0].length];
        for (int i = 0; i < result.length; i++) {
            result2D[map.get(i + 1)[0]][map.get(i + 1)[1]] = result[i];
        }

        // TODO: use result to construct answer
        return result2D;
    }
}
