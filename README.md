This is an implementation of both the greedy search and the variable neighborhood search 
aimed to perform searches among regular graphs with given number of vertices and vertex degree.

In both cases the search starts with a random regular graph, 
which follows the implementation from Section 1.4 of 
H.T. Lau's "A Java Library of Graph Algorithms and Optimization", Chapman & Hall/CRC, Boca Raton, 2007.

In this particular case, the code was specifically used to find several examples of regular, triangle-distinct graphs
(graphs in which each vertex belongs to a distinct number of triangles),
but it can be relatively simply adapted to other objective functions that you might have in mind.
