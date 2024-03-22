import java.util.*;

public class Sudoku {

    static boolean sudokuCollides(int a, int b, HashMap<Integer, int[]> map, HashMap<Integer, Integer> subnetMap) {
        return map.get(a)[0] == map.get(b)[0] || map.get(a)[1] == map.get(b)[1] || subnetMap.get(a).equals(subnetMap.get(b));
    }
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
        HashMap<Integer, List<Integer>> collisions = new HashMap<>();
        int n = (int) Math.sqrt(grid.length);
        for (int i = 0; i < grid.length * grid.length; i++) {
            int row = (i) / grid.length;
            int col = (i) % grid.length;
            int subnet = ((row / n) * n + (col / n)); // Calculate subnet for 3x3 grid
            map.put(i, new int[]{row, col});
            subnetMap.put(i, subnet);
        }

        for (int i = 0; i < grid.length * grid.length; i++) {
            for (int j = 0; j < grid.length * grid.length; j++) {
                if (!collisions.containsKey(i)) {
                    collisions.put(i, new ArrayList<>());
                }
                if (i != j && sudokuCollides(i, j, map, subnetMap)) {
                    collisions.get(i).add(j);
                }
            }
        }

        // TODO: add your variables
        Solver.Variable[] variablesArray = new Solver.Variable[grid.length * grid[0].length];
        List<Integer> domain = new ArrayList<>();
        for (int i = 1; i <= grid.length; i++) {
            domain.add(i);
        }
        for (int i = 0; i < grid.length * grid[0].length; i++) {
            if (grid[map.get(i)[0]][map.get(i)[1]] != -1) {
                variablesArray[i] = new Solver.Variable(List.of(grid[map.get(i)[0]][map.get(i)[1]]));
            } else {
                variablesArray[i] = new Solver.Variable(domain);
            }
        }

        // TODO: add your constraints
        constraints.add(new Solver.SudokuNotCollideConstraint(collisions));


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
            result2D[map.get(i)[0]][map.get(i)[1]] = result[i];
        }
        //Print result
//        for (int i = 0; i < result2D.length; i++) {
//            for (int j = 0; j < result2D[0].length; j++) {
//                System.out.print(result2D[i][j] + " ");
//            }
//            System.out.println();
//        }
        // TODO: use result to construct answer
        return result2D;
    }
}
