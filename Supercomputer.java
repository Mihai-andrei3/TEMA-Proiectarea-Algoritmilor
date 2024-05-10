import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Supercomputer {

	public static class Task {


		public static final String INPUT_FILE = "supercomputer.in";
		public static final String OUTPUT_FILE = "supercomputer.out";


		public static final int NMAX = (int) 1e5 + 5; // 10^5 + 5 = 100.005
		//n = number of nodes(tasks)
		//m = number of edges
		static int n, m;

		int[] a; //array that stores the dataset needed for each task

	
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] adj = new ArrayList[NMAX];

		public void solve() {
			readInput(); 
			writeOutput(getResult());
		}

		private static class MyScanner { 

			private BufferedReader br; 
			private StringTokenizer st;

			public MyScanner(Reader reader) { 
				br = new BufferedReader(reader); 
			} 

			public String next() { 
				while (st == null || !st.hasMoreElements()) { 
					try { 
						st = new StringTokenizer(br.readLine()); 
					} catch (IOException e) { 
						e.printStackTrace(); 
					} 
				} 
				return st.nextToken(); 
			} 

			public int nextInt() { 
				return Integer.parseInt(next()); 
			} 
		}

		private void readInput() {
			try {
				MyScanner sc = new MyScanner(new BufferedReader(new FileReader(INPUT_FILE)));

				n = sc.nextInt();
				m = sc.nextInt();

				a = new int[n + 1];
				for (var i = 1; i <= n; i += 1) {
					a[i] = sc.nextInt();
				}

				for (int i = 1; i <= n; i++) {
					adj[i] = new ArrayList<>();
				}

				for (int i = 1; i <= m; i++) {
					int u, v;
					u = sc.nextInt();
					v = sc.nextInt();
					adj[v].add(u); // (v -> u)
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}


		private void writeOutput(int nr) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));
				bw.write(Integer.toString(nr));
				bw.close();

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private int getResult() {
			//array that stores the intern degree of each node
			int[] in_deg = new int[n + 1]; 

			//the number of context switches if we start a task that require the first dataset 
			int c_switch = 0;

			//we are computing the in_degree for each node
			for (int i = 1; i <= n ; i++) {
				ArrayList<Integer> temp  = adj[i]; 
				for (int node : temp) {
					in_deg[node]++;
				}
			}

			//q1 = queue for nodes that use the first dataset
			//q2 = queue for nodes that use the second dataset

			Queue<Integer> q1 = new LinkedList<Integer>(); 
			Queue<Integer> q2 = new LinkedList<Integer>();

			for (int i = 1; i <= n; i++) {
				//add the nodes that have the in_degree = 0 in the q1 if the dataset is 1
				if (in_deg[i] == 0 && a[i] == 1) {
					q1.add(i);
				}
				//add the nodes that have the in_degree = 0 in the q2 if the dataset is 2
				if (in_deg[i] == 0 && a[i] == 2) {
					q2.add(i);
				}
			}
			//while at least a queue is not empty, we make a topological sort
			while (!q1.isEmpty() || !q2.isEmpty()) {
				//for this case we start with q1
				while (!q1.isEmpty()) {
					int u = q1.poll(); //we pop the queue

					//while we solve the tasks before the node task, we are decreasing the in_degree
					for (int node : adj[u]) {
						--in_deg[node];
						//if the in_degree = 0 it means that we can solve the task
						if (in_deg[node] == 0) {
							//add it to the q1 if dataset used is 1, else in q2
							if (a[node] == 1) {
								q1.add(node);
							} else {
								q2.add(node);
							}
						}
					}
				}

				//after we finish all the tasks from q1, we have to make a context switch
				//and finish the ones from q2 if q2 is not empty
				if (!q2.isEmpty()) {
					c_switch++;
				}
				//the same process used in q1 happens here too
				while (!q2.isEmpty()) {

					int u = q2.poll();

					for (int node : adj[u]) {
						--in_deg[node];

						if (in_deg[node] == 0) {
							if (a[node] == 2) {
								q2.add(node);
							} else {
								q1.add(node);
							}
						}
					}
				}
				
				//after we finish all the tasks from q2, we have to make a context switch
				//and finish the ones from q1 if q1 is not empty
				if (!q1.isEmpty()) {	
					c_switch++;
				}
			}

			//Now we have to reset the in_degree and redo the same thing as above
			//but this time we will start with the q2 the topological sort

			for (int i = 1; i <= n ; i++) {
				ArrayList<Integer> temp  = adj[i];
				for (int node : temp) {
					in_deg[node]++;
				}
			}

			for (int i = 1; i <= n; i++) {
				if (in_deg[i] == 0 && a[i] == 1) {
					q1.add(i);
				}

				if (in_deg[i] == 0 && a[i] == 2) {
					q2.add(i);
				}
			}
		
			int c_switch2 = 0;

			while (!q1.isEmpty() || !q2.isEmpty()) {

				while (!q2.isEmpty()) {

					int u = q2.poll();
					
					for (int node : adj[u]) {
						// If in-degree becomes zero,
						// add it to queue
						--in_deg[node];

						if (in_deg[node] == 0) {
							if (a[node] == 2) {
								q2.add(node);
							} else {
								q1.add(node);
							}
						}
					}
				}

				if (!q1.isEmpty()) {
					c_switch2++;
				}

				while (!q1.isEmpty()) {
					int u = q1.poll();

					for (int node : adj[u]) {
						--in_deg[node];

						if (in_deg[node] == 0) {
							if (a[node] == 1) {
								q1.add(node);
							} else {
								q2.add(node);
							}
						}
					}
				}

				if (!q2.isEmpty()) {
					c_switch2++;
				}

			}
			//In the end we choose the minimum number of context swithces from the 2 cases
			return Math.min(c_switch, c_switch2); 
			
		}
		
	}

	public static void main(String[] args) {
		new Task().solve();
	}
}
