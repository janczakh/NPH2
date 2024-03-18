import java.util.*;

import static java.lang.Math.sqrt;

public class NQueens {
    /**
     * Returns the number of N-Queen solutions
     */
    public static int getNQueenSolutions(int n) {
        // Initialize lists for variables and constraints
        List<Solver.Variable> variables = new ArrayList<>();
        List<Solver.Constraint> constraints = new ArrayList<>();

        HashMap<Integer, int[]> map = new HashMap<>();
        for (int i = 0; i < n * n; i++) {
            int row = (i) / n;
            int col = (i) % n;
            map.put(i, new int[]{row, col});
        }

        // TODO: add your variables
        ArrayList<Integer> domain = new ArrayList<>();
        for (int i = 0; i < n*n; i++) {
            domain.add(i);
        }

        for (int i = 0; i < n; i++) {
            variables.add(new Solver.Variable(domain));
        }

        // TODO: add your constraints
        constraints.add(new Solver.AscendingConstraint());
//        constraints.add(new Solver.NotOtherConstraint());
        constraints.add(new Solver.NotCollideConstraint(map));
        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();

        // Print an nxn board with coordinates from result marked on each solution
//        System.out.println("\n");
////        for (int[] solution : List.of(result.get(10))) {
//        for (int[] solution : result) {
//            char[][] board = new char[n][n];
//            for (int i = 0; i < n; i++) {
//                Arrays.fill(board[i], '.');
//            }
//            for (int i = 0; i < n; i++) {
//                int[] coord = map.get(solution[i]);
//                board[coord[0]][coord[1]] = 'Q';
//            }
//            for (char[] row : board) {
//                System.out.println(Arrays.toString(row));
//            }
//            System.out.println();
//        }
//        // Print the coordinates for each queen in each solution
////        for (int[] solution : List.of(result.get(10))) {
//        for (int[] solution : result) {
//            for (int i = 0; i < n; i++) {
//                int[] coord = map.get(solution[i]);
//                System.out.print(Arrays.toString(coord));
//                System.out.println(solution[i]);
//            }
//            System.out.println();
//        }
        // TODO: use result to construct answer
//        for (int[] res : result) {
//            System.out.println(Arrays.toString(res));
//        }
//        System.out.println(result);
        return result.size();
    }
}
