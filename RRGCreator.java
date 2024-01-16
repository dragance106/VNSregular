import java.util.Random;

public class RRGCreator {
    Random rnd;

    public RRGCreator() {
        rnd = new Random();
    }
    
    public RRGCreator(long seed) {
        rnd = new Random(seed);
    }
    
    public int[] randomPermutation(int n) {
        int[] perm = new int[n];
        
        for (int i=0; i<n; i++) 
            perm[i] = i;
            
        for (int i=0; i<n; i++) {
            int j = i + rnd.nextInt(n - i);
            int k = perm[i];
            perm[i] = perm[j];
            perm[j] = k;
        } 
        
        return perm;
    }
    
    public int[][] randomRegularGraph(int n, int degree) throws IllegalArgumentException {
        // check input data consistency 
        if ((degree % 2) != 0)
            if ((n % 2) != 0)
                throw new IllegalArgumentException("Either the number of vertices or the degree must be even - no such regular graph...");
                
        if (n <= degree)
            throw new IllegalArgumentException("Number of vertices must be larger than the degree - no such regular graph...");
        
        int numedges,p,q,r=0,s=0,u,v=0;
        // initialize the adjacency matrix 
        int[][] adj = new int[n][n];
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++) 
                adj[i][j] = 0;
        // initialize the degree of each node 
        int deg[] = new int[n];
        for (int i=0; i<n; i++)
            deg[i] = 0;
            
        // generate the regular graph 
        iterate:
        while (true) {
            int[] permute = randomPermutation(n);
            boolean more = false;
            
            // find two non-adjacent nodes each has less than required degree 
            u = -1;
            for (int i=0; i<n; i++)
                if (deg[permute[i]] < degree) { 
                    v = permute[i];
                    more = true;
                    for (int j=i+1; j<n; j++) {
                        if (deg[permute[j]] < degree) { 
                            u = permute[j];
                            if (adj[v][u]==0) {
                                // add edge (u,v) to the random graph 
                                adj[v][u] = 1;
                                adj[u][v] = 1; 
                                deg[v]++;
                                deg[u]++;
                                continue iterate; 
                            }
                            else {
                                // both r & s are less than the required degree 
                                r = v;
                                s = u;
                            }
                        }
                    }
                }
            
            if (!more) 
                break; 
                
            if (u == -1) {
                r = v;
                // node r has less than the required degree,
                // find two adjacent nodes p and q non-adjacent to r. 
                for (int i=0; i<n-1; i++) {
                    p = permute[i]; 
                    if (r != p)
                        if (adj[r][p]==0) 
                            for (int j=i+1; j<n; j++) {
                                q = permute[j];
                                if (q != r)
                                    if ((adj[p][q]==1) && (adj[r][q]==0)) {
                                        // add edges (r,p) & (r,q), delete edge (p,q)
                                        adj[r][p] = 1; adj[p][r] = 1;
                                        adj[r][q] = 1; adj[q][r] = 1;
                                        adj[p][q] = 0; adj[q][p] = 0;
                                        deg[r]+=2; 
                                        continue iterate;
                                    }
                            }
                } 
            }
            else {
                // nodes r and s of less than required degree, find two
                // adjacent nodes p & q such that (p,r) & (q,s) are not edges. 
                for (int i=0; i<n; i++) {
                    p = permute[i];
                    if ((p != r) && (p != s))
                        if (adj[r][p]==0)
                            for (int j=0; j<n; j++) {
                                q = permute[j];
                                if ((q != r) && (q != s))
                                    if ((adj[p][q]==1) && (adj[s][q]==0)) {
                                        // remove edge (p,q), add edges (p,r) & (q,s)
                                        adj[p][q] = 0; adj[q][p] = 0;
                                        adj[r][p] = 1; adj[p][r] = 1;
                                        adj[s][q] = 1; adj[q][s] = 1;
                                        deg[r]++; 
                                        deg[s]++;
                                        continue iterate;
                                    }
                            }
                }
            }
        }
        
        return adj;
    }
    
    public void generate(int n, int degree) {
        try {
            int[][] A = randomRegularGraph(n,degree);
            System.out.println("Adjacency matrix of a random ("+n+", "+degree+")-regular graph:");
            for (int i=0; i<n; i++) {
                for (int j=0; j<n; j++)
                    System.out.print(A[i][j]+" ");
                System.out.println();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Argument error: " + e.getMessage());
        }
    }
}
