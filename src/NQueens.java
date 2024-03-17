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
        for (int i = 1; i <= n * n; i++) {
            int row = (i - 1) / n;
            int col = (i - 1) % n;
            map.put(i, new int[]{row, col});
        }

        // TODO: add your variables
        ArrayList<Integer> domain = new ArrayList<>();
        for (int i = 1; i <= n*n; i++) {
            domain.add(i);
        }

        for (int i = 0; i < n; i++) {
            variables.add(new Solver.Variable(domain));
        }

        // TODO: add your constraints
        constraints.add(new Solver.AscendingConstraint());
        constraints.add(new Solver.NotCollideConstraint(map));

        // Convert to arrays
        Solver.Variable[] variablesArray = new Solver.Variable[variables.size()];
        variablesArray = variables.toArray(variablesArray);
        Solver.Constraint[] constraintsArray = new Solver.Constraint[constraints.size()];
        constraintsArray = constraints.toArray(constraintsArray);

        // Use solver
        Solver solver = new Solver(variablesArray, constraintsArray);
        List<int[]> result = solver.findAllSolutions();

        // TODO: use result to construct answer
        return result.size();
    }
}
