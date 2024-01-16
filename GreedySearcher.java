/**
 * Construct a random regular graph and
 * tries greedily to minimize the number of vertex pairs with equal triangle degrees
 * by switching all edge pairs...
 *
 * @author DS
 */
public class GreedySearcher {
    private int n;
    private int d;
    private RRGCreator rrg;
    
    private int[][] A;
    private int[] td;
    private int neqtd;
    
    public GreedySearcher(int n, int d) {
        this.n = n;
        this.d = d;
        rrg = new RRGCreator();
    }
    
    public void refreshTD() {
        int[][] A3 = new int[n][n];
        // A3[i,j] = sum_k A[i,k]*A2[k,j] = sum_k sum_l A[i,k]*A[k,l]*A[l,j]
        for (int i=0; i<n; i++)
            for (int k=0; k<n; k++)
                if (A[i][k]==1)
                    for (int l=0; l<n; l++)
                        if (A[k][l]==1)
                            for (int j=0; j<n; j++)
                                A3[i][j] += A[l][j]; // A[i][k]*A[k][l]*A[l][j], but A[i][k]==A[k][l]==1
        
        // triangle-degrees are just the diagonal entries of A3, divided by 2
        td = new int[n];
        for (int i=0; i<n; i++)
            td[i] = A3[i][i]/2;
            
        // how many pairs of vertices with equal triangle degrees?
        neqtd = 0;
        for (int i=0; i<n; i++)
            for (int j=i+1; j<n; j++)
                if (td[i]==td[j])
                    neqtd++;
    }
    
    public int computeTD(int[][] X) {
        int[][] X3 = new int[n][n];
        // A3[i,j] = sum_k A[i,k]*A2[k,j] = sum_k sum_l A[i,k]*A[k,l]*A[l,j]
        for (int i=0; i<n; i++)
            for (int k=0; k<n; k++)
                if (X[i][k]==1)
                    for (int l=0; l<n; l++)
                        if (X[k][l]==1)
                            for (int j=0; j<n; j++)
                                X3[i][j] += X[l][j]; // A[i][k]*A[k][l]*A[l][j], but A[i][k]==A[k][l]==1
        
        // triangle-degrees are just the diagonal entries of A3, divided by 2
        int[] xtd = new int[n];
        for (int i=0; i<n; i++)
            xtd[i] = X3[i][i]/2;
            
        // how many pairs of vertices with equal triangle degrees?
        int newneqtd = 0;
        for (int i=0; i<n; i++)
            for (int j=i+1; j<n; j++)
                if (xtd[i]==xtd[j])
                    newneqtd++;
                    
        return newneqtd;
    }
    
    public void printOutDetails() {
        System.out.println();
        System.out.println("Adjacency matrix A:");
        for (int i=0; i<n; i++) {
            for (int j=0; j<n; j++)
                System.out.print(A[i][j]+" ");
            System.out.println();
        }
        
        // System.out.println("Third degree of A:");
        // for (int i=0; i<n; i++) {
        //     for (int j=0; j<n; j++)
        //         System.out.print(A3[i][j]+" ");
        //     System.out.println();
        // }
        
        System.out.println("Triangle degrees:");
        for (int i=0; i<n; i++) 
            System.out.print(td[i]+" ");
        System.out.println();
        
        System.out.println("Number of vertex pairs with equal triangle degrees: " + neqtd);
        System.out.println();
    }
    
    public void nextTry() {
        // construct next random (n,d)-regular graph
        A = rrg.randomRegularGraph(n, d);
        
        // compute triangle degrees and number of vertex pairs with equal triangle degrees
        refreshTD();
        
        // what is the best neqtd we have seen so far?
        int minneqtdspotted = neqtd;
        
        // printOutDetails();
        
        // if neqtd is not yet zero, check if we can decrease this number by any edge switching?
        while (neqtd>0) {
            // System.out.println("Current graph has neqtd=" + neqtd);
            
            // placeholder for the best performing edge switching
            int bestneqtd = neqtd;
            int bestu=0, bestv=0, bests=0, bestt=0;

            // placeholder for new triangle degrees after switchings
            int[] newtd = new int[n];
            
            // how do we identify possible edge switchings?
            // we go through all pairs of distinct vertices u,v (with td[u]==td[v]?)
            for (int u=0; u<n; u++) {
                for (int v=u+1; v<n; v++) {
                    if (true) {              // for all vertex pairs u,v 
                    // if (td[u]==td[v]) {         // for those with equal triangle degrees
                        // then we go through all possible neighbors s,t
                        // such that us,vt in E, but vs,ut not in E
                        for (int s=0; s<n; s++) {
                            if ((s==u)||(s==v))
                                continue;
                            
                            if ((A[u][s]==1)&&(A[v][s]==0)) {
                                for (int t=0; t<n; t++) {
                                    if ((t==u)||(t==v)||(t==s))
                                        continue;
                                        
                                    if ((A[u][t]==0)&&(A[v][t]==1)) {
                                        // new adjacency matrix
                                        int[][] newA = new int[n][n];
                                        for (int i=0; i<n; i++)
                                            for (int j=0; j<n; j++)
                                                newA[i][j] = A[i][j];
                                        
                                        // perform switching
                                        newA[u][s]=0; newA[s][u]=0;
                                        newA[v][t]=0; newA[t][v]=0;
                                        newA[v][s]=1; newA[s][v]=1;
                                        newA[u][t]=1; newA[t][u]=1;
                                        
                                        // obtain new triangle numbers and new value of neqtd
                                        int newneqtd = computeTD(newA);
                                        
                                        // is this the smallest new neqtd that we have seen so far?
                                        if (newneqtd<bestneqtd) {
                                            bestneqtd = newneqtd;
                                            bestu = u;
                                            bestv = v;
                                            bests = s;
                                            bestt = t;
                                        }
                                    }
                                }
                            }
                        } // end for s
                    } // end if td[u]==td[v]
                } // end for v
            } // end for u

            if (bestneqtd==neqtd) {
                // no switching among vertices with equal triangle degrees
                // will reduce the number of vertex pairs with equal triangle degrees
                // System.out.println("No edge switching further reduces neqtd, starting with a new graph...");
                A = rrg.randomRegularGraph(n, d);
                refreshTD();
            }
            else {
                // the best edge switching decreases the number of vertex pairs with equal triangle degrees,
                // so perform it: delete us,vt and add vs,ut
                A[bestu][bests] = 0; A[bests][bestu] = 0;
                A[bestv][bestt] = 0; A[bestt][bestv] = 0;
                A[bestv][bests] = 1; A[bests][bestv] = 1;
                A[bestu][bestt] = 1; A[bestt][bestu] = 1;
                
                refreshTD();
                
                // System.out.println("...seen bestneqtd="+bestneqtd+", obtained new neqtd="+neqtd);
                // for (int i=0; i<n; i++)
                //     td[i] = newtd[i]; 
                // neqtd = bestneqtd;
                
                // have we reached a new global minimum for neqtd?
                if (neqtd<minneqtdspotted) {
                    minneqtdspotted = neqtd;
                    System.out.println("Minimum neqtd spotted so far: " + neqtd);
                    printOutDetails();
                }
            }
        } // end while neqtd>0
        
        // it seems that we have found an example with neqtd=0!
        refreshTD();
        printOutDetails();
    }
    
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int d = Integer.parseInt(args[1]);
        
        GreedySearcher gs = new GreedySearcher(n,d);
        gs.nextTry();
    }
}
