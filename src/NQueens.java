import java.util.*;

import static java.lang.Math.sqrt;

public class NQueens {
    /**
     * Returns the number of N-Queen solutions
     */
    static boolean collides(int[] a, int[] b) {
        return a[0] == b[0] || a[1] == b[1] || Math.abs(a[0] - b[0]) == Math.abs(a[1] - b[1]);
    }
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

        HashMap<List<Integer>, Boolean> collidesMap = new HashMap<>();
        for (int i = 0; i < n * n; i++) {
            for (int j = 0; j < n * n; j++) {
                if (collides(map.get(i), map.get(j))) {
                    collidesMap.put(List.of(i, j), true);
                } else {
                    collidesMap.put(List.of(i, j), false);
                }
            }
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
        constraints.add(new Solver.NotCollideConstraint(map, collidesMap));
        constraints.add(new Solver.AscendingConstraint());

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
