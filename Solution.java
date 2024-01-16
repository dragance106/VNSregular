/**
 * This class contains information about the current "solution",
 * i.e., the adjacency matrix of a regular graph and
 * the sequence of its triangle-degrees.
 * It knows how to initialize itself to a random regular graph,
 * how to shake itself through a sequence of random edge switchings and
 * how to perform the steepest descent over edge switchings.
 *
 * @author Dragan Stevanovic
 * @version January 11, 2024
 */
import java.util.Random;
import java.util.Arrays;

public class Solution
{
    public int n;           // number of vertices
    public int d;           // common vertex degree
    public int[][] A;       // adjacency matrix
    public int[] td;        // triangle-degree array
    public int neqtd;       // number of vertex pairs with equal triangle-degrees
    public double value;    // objective function that favors distinct triangle-degrees
    
    Random rnd;             // for performing random edge switchings
    
    /**
     * Constructors for objects of class Solution
     */
    public Solution() {
        // empty constructor for solution placeholders
    }

    public void copySolution(Solution other) {
        this.n = other.n;
        this.d = other.d;

        this.A = new int[n][n];
        for (int i=0; i<other.n; i++)
            for (int j=0; j<other.n; j++)
                this.A[i][j] = other.A[i][j];

        // checkRegularity("copySolution");                
                
        this.td = new int[n];
        for (int i=0; i<other.n; i++)
            this.td[i] = other.td[i];

        this.neqtd = other.neqtd;
        this.value = other.value;
        
        if (rnd==null)
            rnd = new Random();
    }
    
    public Solution(int n, int d, int[][] A) {
        this.n = n;
        this.d = d;

        this.A = new int[n][n];
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                this.A[i][j] = A[i][j];
        
        // checkRegularity("Solution constructor");                
                
        this.td = new int[n];
        refreshTD();
        
        if (rnd==null)
            rnd = new Random();
    }
    
    public void checkRegularity(String message) {
        System.out.println(message);
        
        int[][] A2 = new int[n][n];
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                for (int k=0; k<n; k++)
                    A2[i][j] += A[i][k]*A[k][j];
                    
        boolean regular = true;
        int expected = A2[0][0];
        for (int i=1; i<n; i++)
            if (A2[i][i]!=expected) {
                regular=false;
                break;
            }
            
        if (!regular) {
            System.out.println("GRAPH IS NO LONGER REGULAR!");
            reportFull();
        }
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
        for (int i=0; i<n; i++)
            td[i] = A3[i][i]/2;
            
        // how many pairs of vertices with equal triangle degrees?
        neqtd = 0;
        for (int i=0; i<n; i++)
            for (int j=i+1; j<n; j++)
                if (td[i]==td[j])
                    neqtd++;
                    
        // the value of the objective function
        value = 0.0;
        Arrays.sort(td);
        for (int i=1; i<n; i++)
            value += 1.0/(td[i]-td[i-1] + 1.0/n);        
    }
    
    public double computeTD(int[][] X) {
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

        // the value of the objective function
        double newvalue = 0.0;
        Arrays.sort(xtd);
        for (int i=1; i<n; i++)
            newvalue += 1.0/(xtd[i]-xtd[i-1] + 1.0/n);        
                    
        return newvalue;
    }
    
    /**
     * Report all the important things about the solution.
     */
    public void reportFull() {
        System.out.println();
        System.out.println("Adjacency matrix A:");
        for (int i=0; i<n; i++) {
            System.out.print("!");
            for (int j=0; j<n; j++)
                System.out.print(A[i][j]+" ");
            System.out.println();
        }
        
        System.out.println("Triangle degrees:");
        for (int i=0; i<n; i++) 
            System.out.print(td[i]+" ");
        System.out.println();
        
        System.out.println("!!The value of the objective function: " + value);
        System.out.println("!!!Number of vertex pairs with equal triangle degrees: " + neqtd);
        System.out.println();
    }
    
    /**
     * Report only the basic information about the solution.
     */
    public void report() {
        System.out.println("!!The value of the objective function: " + value);
        System.out.println("!!!Number of vertex pairs with equal triangle degrees: " + neqtd);
    }
    
    /**
     * General steepest descent method that 
     * iteratively and greedily switches edges in a graph to minimize neqtd.
     */
    public void performSteepestDescent() {
        // any edge switching that further reduces neqtd?
        boolean hasImproved = true;
        while (hasImproved) {
            // placeholder for the best performing edge switching
            // int bestneqtd = neqtd;
            double bestvalue = value;
            int bestu=0, bestv=0, bests=0, bestt=0;

            // placeholder for new triangle degrees after switchings
            int[] newtd = new int[n];
            
            // we go through all pairs of distinct vertices u,v (with td[u]==td[v]?)
            for (int u=0; u<n; u++) {
                for (int v=u+1; v<n; v++) {
                    if (true) {              // for all vertex pairs u,v 
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
                                        double newvalue = computeTD(newA);
                                        
                                        // is this the smallest new neqtd that we have seen so far?
                                        if (newvalue<bestvalue) {
                                            bestvalue = newvalue;
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

            if (bestvalue>value-0.000001)
                // no switching among vertices with equal triangle degrees
                // will further reduce the number of vertex pairs with equal triangle degrees
                hasImproved = false;
            else {
                // the best edge switching decreases the number of vertex pairs with equal triangle degrees,
                // so perform it: delete us,vt and add vs,ut
                performSwitching(bestu, bestv, bests, bestt);
                refreshTD();

                // checkRegularity("steepestDescent");                
            }
        } // end while hasImproved
    }
    
    /**
     * Shaking procedure to perform random edge switchings
     */
    public int shake(int howManyTimes) {        
        for (int shakeTimes=0; shakeTimes<howManyTimes; shakeTimes++) {
            boolean shakingDone = false;

            // first try a few times to randomly select appropriate u and v
            int MAXRANDOMTRIALS=10;
            for (int randomTrials=0; randomTrials<MAXRANDOMTRIALS; randomTrials++) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                if (u==v)
                    continue;       // bad random choice of u and v, try another one...
                
                // enumerate feasible s and t
                int scandnum=0;
                int[] scand = new int[n];
                int tcandnum=0;
                int[] tcand = new int[n];
                
                for (int s=0; s<n; s++) {
                    if ((s==u)||(s==v))
                        continue;           // this would ruin regularity after switching...
                    if ((A[u][s]==1)&&(A[v][s]==0)) {
                        scand[scandnum]=s;
                        scandnum++;
                    }
                }
                    
                for (int t=0; t<n; t++) {
                    if ((t==u)||(t==v))
                        continue;           // this would also ruin regularity after switching...
                    if ((A[u][t]==0)&&(A[v][t]==1)) {
                        tcand[tcandnum]=t;
                        tcandnum++;
                    }
                }
                
                if ((scandnum>0)&&(tcandnum>0)) {
                    // we have some feasible switchings available
                    int sindex = rnd.nextInt(scandnum);
                    int tindex = rnd.nextInt(tcandnum);
                    
                    // it's possible that scand[sindex]==tcand[tindex], which would also ruin regularity after switching...
                    if (scand[sindex]==tcand[tindex])
                        continue;           // bad random choice of s and t, try another random choice of u and v...
                    
                    performSwitching(u, v, scand[sindex], tcand[tindex]);
                    refreshTD();
                    
                    // checkRegularity("random switching");
                    
                    shakingDone = true;
                    break;
                }
            }
            
            if (!shakingDone) {
                // random selections did not yield appropriate choices for u and v,
                // try out complete enumeration of feasible edge switchings...
                int switchcandnum=0;
                int[] ucand = new int[n*n*d*d];
                int[] vcand = new int[n*n*d*d];
                int[] scand = new int[n*n*d*d];
                int[] tcand = new int[n*n*d*d];
                
                for (int u=0; u<n; u++)
                    for (int v=u+1; v<n; v++)
                        for (int s=0; s<n; s++) {
                            if ((s==u)||(s==v))
                                continue;
                            
                            if ((A[u][s]==1)&&(A[v][s]==0))
                                for (int t=0; t<n; t++) {
                                    if ((t==u)||(t==v)||(t==s))
                                        continue;
                                        
                                    if ((A[u][t]==0)&&(A[v][t]==1)) {
                                        ucand[switchcandnum] = u;
                                        vcand[switchcandnum] = v;
                                        scand[switchcandnum] = s;
                                        tcand[switchcandnum] = t;
                                        switchcandnum++;
                                    }
                                }
                        }
                        
                if (switchcandnum>0) {
                    // there exists a feasible switching in a graph after all...
                    int index = rnd.nextInt(switchcandnum);
                    performSwitching(ucand[index], vcand[index], scand[index], tcand[index]);
                    refreshTD();
                    
                    // checkRegularity("all switchings");
                    
                    shakingDone = true;
                }
            }
            
            if (!shakingDone)
                return -1;
        }
        
        // all the necessary shakings have been performed, so...
        return 1;        
    }
    
    /**
     * perform the switching: delete us,vt and add vs,ut
     */
    public void performSwitching(int u, int v, int s, int t) {
        A[u][s] = 0; A[s][u] = 0;
        A[v][t] = 0; A[t][v] = 0;
        A[v][s] = 1; A[s][v] = 1;
        A[u][t] = 1; A[t][u] = 1;
    }
}