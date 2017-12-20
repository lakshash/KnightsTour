import java.util.*;

public class KnightsTour {
	
	long state_total = 1000000;
	long state = 0; 
	long closed_path = 0;
	long open_path = 0;
	long states_solutions[] = new long[3];

	int step = 0; 
	int step_max = 63; 
	int x = 1;
	int y = 2;	
	
	public static boolean OUTPUT_RUN = true;
	public static boolean ANALYSIS_RUN = false;
	
	Random random = new Random(System.nanoTime());
	
	long[] computeStrategyZero(Node[][] nodes) {
		Node start_node = nodes[x][y];
		
		//Find list of unvisited, non-failure neighbours
		Node parent = nodes[x][y]; //parent
		
		 for (Node n : parent.neighbours) {
			 if (n.beenVisited == false && !parent.failureNodes.contains(n)) {
				 parent.unvisited.add(n);
			 }
		 }
		 parent.beenVisited = true;	 
		 
		 //Forward : Choose and increment in a single block
		 Node child = parent.unvisited.get(random.nextInt(parent.unvisited.size())); //Strategy 0 - random from minimum degree unvisited
		 step += 1;
		 state += 1;
		 
		 child.beenVisited = true;
		 child.parent = parent; 
		 
		//move forward
		parent = child;
		int loop = 1;
			
		while(state <= state_total) {
			parent.unvisited.clear(); 

			int fl = 0;
			 for (Node n : parent.neighbours) {
				 int x_n = n.x;
				 int y_n = n.y;
				 if (n.beenVisited == true) {
					 continue;
				 }
				 for (Node f : parent.failureNodes) {
					 if ((f.x == x_n) && (f.y == y_n)) {
						 fl = 1;
					 }
				 }
				 if (fl==0) {
					 parent.unvisited.add(n);
				 } else {
					 fl = 0;
				 }
			 }
			 
			 if(parent.unvisited.size() > 0) {
				 child = parent.unvisited.get(random.nextInt(parent.unvisited.size())); 
				 child.parent = parent;
					
				 step += 1;
				 state += 1;
				 	
				 if (step == step_max) {
					 if (child.neighbours.contains(start_node)) {
						 //CLOSED TOUR
						 closed_path += 1;
						 print(child, closed_path, 0);
						 parent.failureNodes.add(child);
						 
						 //Backtrack
						 child.parent = null;
						 child.beenVisited = false;
						 child.unvisited.clear();
						 child.failureNodes.clear();
						 step -= 1;
						 break;
					 } else {
						 //OPEN TOUR
						 open_path += 1;						 
						 parent.failureNodes.add(child);
						 
						 //Backtrack

//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
						 child.parent = null;
						 child.beenVisited = false;
						 child.unvisited.clear();
						 child.failureNodes.clear();
						 
						 step -= 1;
						 System.out.println("OPEN TOUR FOR LOOP : " +loop);
						 System.out.println(step);
						 break ; 
					 }
				 } else {
					 //Forward
					 parent = child;
					 parent.beenVisited = true;
//					 System.out.println("Moving forward to " + (parent.x+1) +"," + (parent.y+1));
				 }	
			 } else if (parent.unvisited.size() == 0) {
//				 System.out.println("Backtracking from " + (parent.x+1) +"," + (parent.y+1));

				 parent.unvisited.clear();
				 parent.beenVisited = false;
				 parent.parent.failureNodes.add(parent);
				 parent.failureNodes.clear();
				 
				 Node temp = parent.parent;
				 parent.parent = null;
				 parent = temp;
				 
				 step --;
			 } 
			 loop++;
		}
		
		states_solutions[0] = state-1;
		states_solutions[1] = closed_path;
		states_solutions[2] = open_path;
		
		return states_solutions;
	}
	
	long[] computeStrategyOne(Node[][] nodes) {
		Node start_node = nodes[x][y];
		int min_degree = 8;
		
		Node parent = nodes[x][y]; //parent
		
		 for (Node n : parent.neighbours) {
			 if (n.beenVisited == false && !parent.failureNodes.contains(n)) {
				 parent.unvisited.add(n);
				 if (n.fixed_degree <= min_degree) {
					 min_degree = n.fixed_degree;
				 }
			 }
		 }

		 parent.beenVisited = true;	 
		 
		 //Find list of unvisited neighbours having min degree
		 for (Node l : parent.unvisited) {
			 if (l.fixed_degree == min_degree) {
				 parent.min_degree_unvisited.add(l);
			 }
		 }
		 
		 //Forward : Choose and increment in a single block
		 Node child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); //Strategy 1 - random from minimum degree unvisited
		 step += 1;
		 state += 1;
		 
		 child.beenVisited = true;
		 child.parent = parent; 
		 
		//move forward
		parent = child;
		int loop = 1;
			
		while(state <= state_total) {
			parent.min_degree_unvisited.clear();
			parent.unvisited.clear();
			min_degree = 8;

			for (Node n: parent.neighbours) {
				if(n!= start_node && !n.beenVisited && !parent.failureNodes.contains(n)) {
					parent.unvisited.add(n);
					 if (n.fixed_degree <= min_degree) {
						 min_degree = n.fixed_degree;
					 }
				}
			}
			 
			 for (Node m : parent.unvisited) {
				 if (m.fixed_degree == min_degree) {
					 parent.min_degree_unvisited.add(m);
				 }
			 }

			 if(parent.unvisited.size() > 0) {
				 child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); 
				 child.parent = parent;
					
				 step += 1;
				 state += 1;
				 	
				 if (step == step_max) {
					 if (child.neighbours.contains(start_node)) {
						 //CLOSED TOUR
						 closed_path += 1;
						 
						 print(child, closed_path, 1);
						 parent.failureNodes.add(child);
						 
						 //Backtrack
//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
						 child.parent = null;
						 child.beenVisited = false;
						 child.unvisited.clear();
						 child.failureNodes.clear();
						 
						 //ADDED
						 parent.beenVisited = false;
						 parent.unvisited.clear();
						 parent.failureNodes.clear();
						 
						 child = parent;
						 parent = parent.parent;
						 parent.failureNodes.add(child);
						 
						 step -=2;
					 } else {
						 //OPEN TOUR
						 open_path += 1;
						 
						 parent.failureNodes.add(child);
						 
						 //Backtrack

//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
						 child.parent = null;
						 child.beenVisited = false;
						 child.unvisited.clear();
						 child.failureNodes.clear();
						 
						 parent.beenVisited = false;
						 parent.unvisited.clear();
						 parent.failureNodes.clear();
						 
						 child = parent;
						 parent = parent.parent;
						 parent.failureNodes.add(child);
						 
						 step -=2;
					 }
				 } else {
					 //Forward
					 parent = child;
					 parent.beenVisited = true;
//					 System.out.println("Moving forward to " + (parent.x+1) +"," + (parent.y+1));
				 }		
			 } else if (parent.unvisited.size() == 0) {
//				 System.out.println("Backtracking from " + (parent.x+1) +"," + (parent.y+1));
				 parent.unvisited.clear();
				 parent.beenVisited = false;
				 parent.parent.failureNodes.add(parent);
				 parent.failureNodes.clear();
				 parent.min_degree_unvisited.clear();
				 
				 Node temp = parent.parent;
				 parent.parent = null;
				 parent = temp;
				 
				 step --;
			 } 			 
			 loop++;
		}
		
		states_solutions[0] = state-1;
		states_solutions[1] = closed_path;
		states_solutions[2] = open_path;
		
		return states_solutions;
	}
	
	long[] computeStrategyTwo(Node[][] nodes) {
		Node start_node = nodes[x][y];
		int min_degree = 8;
		
		//Find list of unvisited, non-failure neighbours
		Node parent = nodes[x][y]; //parent
		
		 for (Node n : parent.neighbours) {
			 if (n.beenVisited == false && !parent.failureNodes.contains(n)) {
				 parent.unvisited.add(n);
				 if (n.dynamic_degree <= min_degree) {
					 min_degree = n.dynamic_degree;
				 }
			 }
		 }
		
		 parent.beenVisited = true;	 
		 
		 //Find list of unvisited neighbours having min dynamic degree
		 for (Node l : parent.unvisited) {
			 if (l.dynamic_degree == min_degree) {
				 parent.min_degree_unvisited.add(l);
			 }
		 }
		 
		 //Forward : Choose and increment in a single block
		 Node child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); //Strategy 2
		 step += 1;
		 state += 1;
		 forwardAndDecrement (child);
		 
		 child.beenVisited = true;
		 child.parent = parent; 
		 
		//move forward
		parent = child;
		int loop = 1;
			
		while(state <= state_total) {
			parent.min_degree_unvisited.clear();
			parent.unvisited.clear(); 
			min_degree = 8;
			
			for (Node n: parent.neighbours) {
				if(n!= start_node && !n.beenVisited && !parent.failureNodes.contains(n)) {
					parent.unvisited.add(n);
					 if (n.dynamic_degree <= min_degree) {
						 min_degree = n.dynamic_degree;
					 }
				}
			}
			 
			 for (Node m : parent.unvisited) {
				 if (m.dynamic_degree == min_degree) {
					 parent.min_degree_unvisited.add(m);
				 }
			 }

			 if(parent.unvisited.size() > 0) {
				 child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); 
				 child.parent = parent;
					
				 step += 1;
				 state += 1;
				 	
				 if (step == step_max) {
					 if (child.neighbours.contains(start_node)) {
						 //CLOSED TOUR
						 closed_path += 1;
						 
						 print(child, closed_path, 2);
						 parent.failureNodes.add(child);
						 
						 //Backtrack
//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
						 child.parent = null;
						 child.beenVisited = false;
						 child.unvisited.clear();
						 child.failureNodes.clear();
						 step -= 1;
					 } else {
						 //OPEN TOUR
						 open_path += 1;
						 
						 parent.failureNodes.add(child);
						 
						 //Backtrack

//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
						 child.parent = null;
						 child.beenVisited = false;
						 child.unvisited.clear();
						 child.failureNodes.clear();
						 step -= 1;
					 }
				 } else {
					 //Forward
					 forwardAndDecrement (child);
					 parent = child;
					 parent.beenVisited = true;
//					 System.out.println("Moving forward to " + (parent.x+1) +"," + (parent.y+1));
				 }		
			 } else if (parent.unvisited.size() == 0) {
//				 System.out.println("Backtracking from " + (parent.x+1) +"," + (parent.y+1));
				 parent.unvisited.clear();
				 parent.beenVisited = false;
				 parent.parent.failureNodes.add(parent);
				 parent.failureNodes.clear();
				 parent.min_degree_unvisited.clear();
				 
				 backtrackAndIncrement(parent);
				 
				 Node temp = parent.parent;
				 parent.parent = null;
				 parent = temp;
				 
				 step --;
			 } 			 
			 loop++;
		}
		
		states_solutions[0] = state-1;
		states_solutions[1] = closed_path;
		states_solutions[2] = open_path;
		
		return states_solutions;
	}
	
	long[] computeStrategyThree(Node[][] nodes) {
		
		Node start_node = nodes[x][y];
		int min_degree = 8;
		
		//Find list of unvisited, non-failure neighbours
		Node parent = nodes[x][y]; //parent
		
		 for (Node n : parent.neighbours) {
			 if (n.beenVisited == false && !parent.failureNodes.contains(n)) {
				 parent.unvisited.add(n);
				 if (n.fixed_degree <= min_degree) {
					 min_degree = n.fixed_degree;
				 }
			 }
		 }
		
		 parent.beenVisited = true;	 
		 
		 //Find list of unvisited neighbours having min fixed degree
		 for (Node l : parent.unvisited) {
			 if (l.fixed_degree == min_degree) {
				 parent.min_degree_unvisited.add(l);
			 }
		 }
		 
		 //Forward : Choose and increment in a single block
		 Node child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); //Strategy 2
		 step += 1;
		 state += 1;
		 forwardAndDecrement (child);
		 
		 child.beenVisited = true;
		 child.parent = parent; 
		 
		//move forward
		parent = child;
		int loop = 1;
			
		while(state <= state_total) {
			parent.min_degree_unvisited.clear();
			parent.unvisited.clear(); //ADDED
			min_degree = 8;
			int dynamic_degree_check = 0;
			
			for (Node n: parent.neighbours) {
				if(n!= start_node && !n.beenVisited && !parent.failureNodes.contains(n)) {
					 parent.unvisited.add(n);
					 if (n.fixed_degree <= min_degree) {
						 min_degree = n.fixed_degree;
					 }
					 if (n.dynamic_degree == 1) { //Strategy 3
						 dynamic_degree_check ++;
					 }
				}
			}
			 
			 for (Node m : parent.unvisited) {
				 if (m.fixed_degree == min_degree) {
					 parent.min_degree_unvisited.add(m);
				 }
			 }
			 
			 if (dynamic_degree_check > 1) {
//				 Backtrack
//				 System.out.println("Backtracking from " + (parent.x+1) +"," + (parent.y+1));
				 parent.unvisited.clear();
				 parent.beenVisited = false;
				 parent.parent.failureNodes.add(parent);
				 parent.failureNodes.clear();
				 parent.min_degree_unvisited.clear();
				 
				 backtrackAndIncrement(parent);
				 
				 Node temp = parent.parent;
				 parent.parent = null;
				 parent = temp;
				 
				 step --;
			 } else {
				 if(parent.unvisited.size() > 0) {
					 child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); 
					 child.parent = parent;
						
					 step += 1;
					 state += 1;
					 	
					 if (step == step_max) {
						 if (child.neighbours.contains(start_node)) {
							 //CLOSED TOUR
							 closed_path += 1;
							 
							 print(child, closed_path, 3);
							 parent.failureNodes.add(child);
							 
							 //Backtrack
	//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
							 child.parent = null;
							 child.beenVisited = false;
							 child.unvisited.clear();
							 child.failureNodes.clear();
							 step -= 1;
						 } else {
							 //OPEN TOUR
							 open_path += 1;
							 
							 parent.failureNodes.add(child);
							 
							 //Backtrack
	
	//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
							 child.parent = null;
							 child.beenVisited = false;
							 child.unvisited.clear();
							 child.failureNodes.clear();
							 step -= 1;
						 }
					 } else {
						 //Forward
						 forwardAndDecrement (child);
						 parent = child;
						 parent.beenVisited = true;
	//					 System.out.println("Moving forward to " + (parent.x+1) +"," + (parent.y+1));
					 }		
				 } else if (parent.unvisited.size() == 0) {
	//				 System.out.println("Backtracking from " + (parent.x+1) +"," + (parent.y+1));
					 parent.unvisited.clear();
					 parent.beenVisited = false;
					 parent.parent.failureNodes.add(parent);
					 parent.failureNodes.clear();
					 parent.min_degree_unvisited.clear();
					 
					 backtrackAndIncrement(parent);
					 
					 Node temp = parent.parent;
					 parent.parent = null;
					 parent = temp;
					 
					 step --;
				 } 			 
			 }
			 loop++;
		}
		
		states_solutions[0] = state-1;
		states_solutions[1] = closed_path;
		states_solutions[2] = open_path;
		
		return states_solutions;
	}
	
	long[] computeStrategyFour(Node[][] nodes) {
		
		Node start_node = nodes[x][y];
		int min_degree = 8;
		int dynamic_degree_check = 0;
		Node single_dd1 = new Node();
		
		//Find list of unvisited, non-failure neighbours
		Node parent = nodes[x][y]; //parent
		
		 for (Node n : parent.neighbours) {
			 if (n.beenVisited == false && !parent.failureNodes.contains(n)) {
				 parent.unvisited.add(n);
				 if (n.fixed_degree <= min_degree) {
					 min_degree = n.fixed_degree;
				 }
				 if (n.dynamic_degree == 1) {
					 dynamic_degree_check ++;
					 single_dd1 = n;
				 }
			 }
		 }

		 parent.beenVisited = true;	 
		 
		 //Find list of unvisited neighbours having min degree
		 for (Node l : parent.unvisited) {
			 if (l.fixed_degree == min_degree) {
				 parent.min_degree_unvisited.add(l);
			 } 
		 }

		 //Forward : Choose and increment in a single block
		 Node child = new Node();
		 if (dynamic_degree_check == 1) {
			 child = single_dd1;
		 }else {
			 child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); //Strategy 1 - random from minimum degree unvisited
		 }
		 step += 1;
		 state += 1;
		 forwardAndDecrement (child);
		 
		 child.beenVisited = true;
		 child.parent = parent; 
		 
		//move forward
		parent = child;
		int loop = 1;
			
		while(state <= state_total) {
			parent.min_degree_unvisited.clear();
			parent.unvisited.clear(); //ADDED
			min_degree = 8;
			dynamic_degree_check = 0;
			
			for (Node n: parent.neighbours) {
				if(n!= start_node && !n.beenVisited && !parent.failureNodes.contains(n)) {
					 parent.unvisited.add(n);
					 if (n.fixed_degree <= min_degree) {
						 min_degree = n.fixed_degree;
					 }
					 if (n.dynamic_degree == 1) {
						 dynamic_degree_check ++;
						 single_dd1 = n;
					 }
				}
			}
			 
			 for (Node m : parent.unvisited) {
				 if (m.fixed_degree == min_degree) {
					 parent.min_degree_unvisited.add(m);
				 }
			 }

			 if(parent.unvisited.size() > 0) {
				 if (dynamic_degree_check == 1) {
					 child = single_dd1;
				 } else {
					 child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); 
				 }
				 child.parent = parent;
					
				 step += 1;
				 state += 1;
				 	
				 if (step == step_max) {
					 if (child.neighbours.contains(start_node)) {
						 //CLOSED TOUR
						 closed_path += 1;
						 
						 print(child, closed_path, 4);
						 parent.failureNodes.add(child);
						 
						 //Backtrack
//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
						 child.parent = null;
						 child.beenVisited = false;
						 child.unvisited.clear();
						 child.failureNodes.clear();
						 step -= 1;
					 } else {
						 //OPEN TOUR
						 open_path += 1;
						 
						 parent.failureNodes.add(child);
						 
//						 Backtrack
//						 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1));
						 child.parent = null;
						 child.beenVisited = false;
						 child.unvisited.clear();
						 child.failureNodes.clear();
						 
						 step -= 1;
					 }
				 } else {
					 //Forward
					 forwardAndDecrement (child);
					 parent = child;
					 parent.beenVisited = true;
//					 System.out.println("Moving forward to " + (parent.x+1) +"," + (parent.y+1));
				 }		
			 } else if (parent.unvisited.size() == 0) {
//				 System.out.println("Backtracking from " + (parent.x+1) +"," + (parent.y+1));
				 int flag = 0;
				 
				 if (parent.dynamic_degree == 1) {
					 flag = 1;
				 }
				 
				 parent.unvisited.clear();
				 parent.beenVisited = false;
				 parent.parent.failureNodes.add(parent);
				 parent.failureNodes.clear();
				 parent.min_degree_unvisited.clear();
				 backtrackAndIncrement(parent);
				 
				 Node temp = parent.parent;
				 parent.parent = null;
				 parent = temp;
				 
				 step --;
				 
				 if (flag == 1) {
					 parent.failureNodes.clear();
					 for (Node f : parent.neighbours) {
						 parent.failureNodes.add(f);
					 }
				 }
			 } 			 
			 loop++;
		}
		
		states_solutions[0] = state-1;
		states_solutions[1] = closed_path;
		states_solutions[2] = open_path;
		
		return states_solutions;
	}
	
	long[] computeStrategyFive(Node[][] nodes) {
	
		Node start_node = nodes[x][y];
		int min_degree = 8;
		Node single_dd1 = new Node();
		
		//Find list of unvisited, non-failure neighbours
		Node parent = nodes[x][y]; //parent
		
		 for (Node n : parent.neighbours) {
			 if (n.beenVisited == false && !parent.failureNodes.contains(n)) {
				 parent.unvisited.add(n);
				 if (n.dynamic_degree <= min_degree) {
					 min_degree = n.dynamic_degree;
				 }
			 }
		 }

		 parent.beenVisited = true;	 
		 
		 //Find list of unvisited neighbours having min degree
		 for (Node l : parent.unvisited) {
			 if (l.dynamic_degree == min_degree) {
				 parent.min_degree_unvisited.add(l);
			 } 
		 }
		 
		 //Forward : Choose and increment in a single block
		 Node child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); //Strategy 1 - random from minimum dynamic degree unvisited
		 step += 1;
		 state += 1;
		 forwardAndDecrement (child);
		 
//		 System.out.println("Moving forward to " + (child.x+1) +"," + (child.y+1)+ "degree :" + child.dynamic_degree);
		 
		 child.beenVisited = true;
		 child.parent = parent; 
		 
		//move forward
		parent = child;
		int loop = 1;
			
		while(state <= state_total) {
			parent.min_degree_unvisited.clear();
			parent.unvisited.clear();
			min_degree = 8;
			int dynamic_degree_check = 0;
			
			for (Node n: parent.neighbours) {
				if(n!= start_node && !n.beenVisited && !parent.failureNodes.contains(n)) {
					 parent.unvisited.add(n);
					 if (n.dynamic_degree <= min_degree) {
						 min_degree = n.dynamic_degree;
					 }
					 if (n.dynamic_degree == 1) { //Strategy 3
						 dynamic_degree_check ++;
						 single_dd1 = n;
					 }
				}
			}
			 
			 for (Node m : parent.unvisited) {
				 if (m.dynamic_degree == min_degree) {
					 parent.min_degree_unvisited.add(m);
				 }
			 }
			 
			 if (dynamic_degree_check > 1) { //Strategy 3
//				 Backtrack
//				 System.out.println("Backtracking from " + (parent.x+1) +"," + (parent.y+1) + "degree :" + parent.dynamic_degree);
				 parent.unvisited.clear();
				 parent.beenVisited = false;
				 parent.parent.failureNodes.add(parent);
				 parent.failureNodes.clear();
				 parent.min_degree_unvisited.clear();
				 
				 backtrackAndIncrement(parent);
				 
				 Node temp = parent.parent;
				 parent.parent = null;
				 parent = temp;
				 
				 step --;
			 } 
			 else { 
				 if(parent.unvisited.size() > 0) {
					 if (dynamic_degree_check == 1) {
						 child = single_dd1;
					 } else {
//						 random = new Random(System.currentTimeMillis());
						 child = parent.min_degree_unvisited.get(random.nextInt(parent.min_degree_unvisited.size())); 
					 }
					 child.parent = parent;
						
					 step += 1;
					 state += 1;
					 	
					 if (step == step_max) {
						 if (child.neighbours.contains(start_node)) {
							 //CLOSED TOUR
							 closed_path += 1;
							 
							 print(child, closed_path, 5);
							 parent.failureNodes.add(child);
							 
							 //Backtrack
//							 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1)+ "degree :" + child.dynamic_degree);
							 child.parent = null;
							 child.beenVisited = false;
							 child.unvisited.clear();
							 child.failureNodes.clear();
							 
							 step -= 1;
						 } else {
							 //OPEN TOUR
							 open_path += 1;
							 
							 parent.failureNodes.add(child);
							 
							 //Backtrack
	
//							 System.out.println("Backtracking from " + (child.x+1) +"," + (child.y+1)+ "degree :" + child.dynamic_degree);
							 child.parent = null;
							 child.beenVisited = false;
							 child.unvisited.clear();
							 child.failureNodes.clear();
							 
							 step -= 1;
						 }
					 } else {
						 //Forward
						 forwardAndDecrement (child);
						 parent = child;
						 parent.beenVisited = true;
//						 System.out.println("Moving forward to " + (parent.x+1) +"," + (parent.y+1)+ "degree :" + parent.dynamic_degree);
					 }		
				 } else if (parent.unvisited.size() == 0) {
//					 System.out.println("Backtracking from " + (parent.x+1) +"," + (parent.y+1));
					 int flag = 0;
					 
					 if (parent.dynamic_degree == 1) { //Strategy 4
						 flag = 1;
					 }
					 
					 parent.unvisited.clear();
					 parent.beenVisited = false;
					 parent.parent.failureNodes.add(parent);
					 parent.failureNodes.clear();
					 parent.min_degree_unvisited.clear();
					 backtrackAndIncrement(parent);
					 
					 Node temp = parent.parent;
					 parent.parent = null;
					 parent = temp;
					 
					 step --;
					 
					 if (flag == 1) {
						 parent.failureNodes.clear();
						 for (Node f : parent.neighbours) {
							 parent.failureNodes.add(f);
						 }
					 }
				 } 
			 }
			 loop++;
		}
		
		states_solutions[0] = state-1;
		states_solutions[1] = closed_path;
		states_solutions[2] = open_path;
		
		return states_solutions;
	}
	
	//Helper methods
	
	void forwardAndDecrement (Node current) {
		for (Node n : current.neighbours) {
			n.dynamic_degree -= 1;
		}
	}
	
	void backtrackAndIncrement (Node current) {
		for (Node n : current.neighbours) {
			n.dynamic_degree += 1;
			if (n.dynamic_degree > 8) {
				System.out.println("ERROR : " + n.x +"," + n.y + "   " + current.x + "," + current.y);
			}
		}
	}
	
	void print(Node current, long num, int strategy)
	{
		if (OUTPUT_RUN == true) {
			String number;
			
			if (num > 100) {	return; }   
		
			Node temp_child = current;
			Node temp_parent = temp_child.parent;
	
			
			if (num >=1 && num <= 99) {
				number = String.format("%03d", num);
			} else {
				number = String.format("%d", num);
			}
			
			int count = 0;
			if (num == 1) {
				System.out.println("KT: 8x8, strategy = " + strategy + ", start = 2,3");
			}
			
			System.out.print(number + ": 2,3 ");
			while (count < 63)
			{
				System.out.print((temp_child.x + 1) + "," + (temp_child.y + 1) + " ");
				temp_child = temp_parent;
				temp_parent = temp_child.parent;
				count++;
			}
			
			System.out.println((temp_child.x + 1) + "," + (temp_child.y + 1));
		}
	}
	
	public void printStats (long[] states_solutions, long start, long stop) {

//		System.out.println("Number of states : " + states_solutions[0]);
//		System.out.println("Number of solutions : " + states_solutions[1]);
//		System.out.println("Number of open paths : " + states_solutions[2]);
//		System.out.println("Time taken to complete : " + (stop-start)/1000000000.0 + " seconds");
		System.out.println();
	}
	
	public static void main(String[] args) {
		
		long start;
		long stop;
		long[] states_solutions;
		
		if (OUTPUT_RUN == true) {
			//Strategy #0
			start = System.nanoTime(); 
			ChessBoard n1 = new ChessBoard();
			n1.initializeModel();
			
			KnightsTour k1 = new KnightsTour();
			states_solutions = k1.computeStrategyZero(n1.nodes);
			stop = System.nanoTime(); 
			
			k1.printStats(states_solutions, start, stop);
			
			//Strategy #1 
			start = System.nanoTime(); 
			ChessBoard n2 = new ChessBoard();
			n2.initializeModel();
			
			KnightsTour k2 = new KnightsTour();
			states_solutions = k2.computeStrategyOne(n2.nodes);
			stop = System.nanoTime(); 
			
			k2.printStats(states_solutions, start, stop);
			
			//Strategy #2 
			start = System.nanoTime(); 
			ChessBoard n3 = new ChessBoard();
			n3.initializeModel();
			
			KnightsTour k3 = new KnightsTour();
			states_solutions = k3.computeStrategyTwo(n3.nodes);
			stop = System.nanoTime(); 
			
			k3.printStats(states_solutions, start, stop);
			
			//Strategy #3
			start = System.nanoTime(); 
			ChessBoard n4 = new ChessBoard();
			n4.initializeModel();
			
			KnightsTour k4 = new KnightsTour();
			states_solutions = k4.computeStrategyThree(n4.nodes);
			stop = System.nanoTime(); 
			
			k4.printStats(states_solutions, start, stop);
			
			//Strategy #4
			start = System.nanoTime(); 
			ChessBoard n5 = new ChessBoard();
			n5.initializeModel();
			
			KnightsTour k5 = new KnightsTour();
			states_solutions = k5.computeStrategyFour(n5.nodes);
			stop = System.nanoTime(); 
			
			k5.printStats(states_solutions, start, stop);
			
			//Strategy #5
			start = System.nanoTime();
			ChessBoard n6 = new ChessBoard();
			n6.initializeModel();
			
			KnightsTour k6 = new KnightsTour();
			states_solutions = k6.computeStrategyFive(n6.nodes);
			stop = System.nanoTime();
			
			k6.printStats(states_solutions, start, stop);
		}
		
		if (ANALYSIS_RUN == true) {
			
			List<Double> timeTaken = new ArrayList<Double>();			
			List<Long> solutions = new ArrayList<Long>();
			double time_total = 0;
			double sol_total = 0;
			
			System.out.println("\nStrategy 0");
			for (int i = 0; i < 30; i++) {
				ChessBoard n = new ChessBoard();
				n.initializeModel();
				KnightsTour k = new KnightsTour();
				
				start = System.nanoTime();
				states_solutions = k.computeStrategyZero(n.nodes);
				stop = System.nanoTime();
				
				timeTaken.add((stop-start)/1000000000.0);
				solutions.add(states_solutions[1]);
				
				time_total += (stop-start)/1000000000.0;
				sol_total +=  states_solutions[1];
				
				System.out.println(states_solutions[1] + "\t" + (stop-start)/1000000000.0);
				}
			
			System.out.println("TOTAL TIME : " + time_total);
			System.out.println("TOTAL SOLUTIONS : " +sol_total);
			System.out.println("SOLS/SEC : " + sol_total/time_total);
			
			time_total = 0;
			sol_total = 0;
			solutions.clear();
			timeTaken.clear();
			
			System.out.println("\nStrategy 1");
			for (int i = 0; i < 30; i++) {
				start = System.nanoTime();
				ChessBoard n = new ChessBoard();
				n.initializeModel();
				
				KnightsTour k = new KnightsTour();
				states_solutions = k.computeStrategyOne(n.nodes);
				stop = System.nanoTime();
				
				timeTaken.add((stop-start)/1000000000.0);
				solutions.add(states_solutions[1]);
				
				time_total += (stop-start)/1000000000.0;
				sol_total +=  states_solutions[1];
				
				System.out.println(states_solutions[1] + "\t" + (stop-start)/1000000000.0);
				}
			
			System.out.println("TOTAL TIME : " + time_total);
			System.out.println("TOTAL SOLUTIONS : " +sol_total);
			System.out.println("SOLS/SEC : " + sol_total/time_total);
			
			time_total = 0;
			sol_total = 0;
			solutions.clear();
			timeTaken.clear();
			
			System.out.println("\nStrategy 2");
			for (int i = 0; i < 30; i++) {
				start = System.nanoTime();
				ChessBoard n = new ChessBoard();
				n.initializeModel();
				
				KnightsTour k = new KnightsTour();
				states_solutions = k.computeStrategyTwo(n.nodes);
				stop = System.nanoTime();
				
				timeTaken.add((stop-start)/1000000000.0);
				solutions.add(states_solutions[1]);
				
				time_total += (stop-start)/1000000000.0;
				sol_total +=  states_solutions[1];
				
				System.out.println(states_solutions[1] + "\t" + (stop-start)/1000000000.0);
				}
			
			System.out.println("TOTAL TIME : " + time_total);
			System.out.println("TOTAL SOLUTIONS : " +sol_total);
			System.out.println("SOLS/SEC : " + sol_total/time_total);
			
			time_total = 0;
			sol_total = 0;
			solutions.clear();
			timeTaken.clear();
			
			System.out.println("\nStrategy 3");
			for (int i = 0; i < 30; i++) {
				start = System.nanoTime();
				ChessBoard n = new ChessBoard();
				n.initializeModel();
				
				KnightsTour k = new KnightsTour();
				states_solutions = k.computeStrategyThree(n.nodes);
				stop = System.nanoTime();
				
				timeTaken.add((stop-start)/1000000000.0);
				solutions.add(states_solutions[1]);
				
				time_total += (stop-start)/1000000000.0;
				sol_total +=  states_solutions[1];
				
				System.out.println(states_solutions[1] + "\t" + (stop-start)/1000000000.0);
				}
			
			System.out.println("TOTAL TIME : " + time_total);
			System.out.println("TOTAL SOLUTIONS : " +sol_total);
			System.out.println("SOLS/SEC : " + sol_total/time_total);
			
			time_total = 0;
			sol_total = 0;
			solutions.clear();
			timeTaken.clear();
			
			System.out.println("\nStrategy 4");
			for (int i = 0; i < 30; i++) {
				start = System.nanoTime();
				ChessBoard n = new ChessBoard();
				n.initializeModel();
				
				KnightsTour k = new KnightsTour();
				states_solutions = k.computeStrategyFour(n.nodes);
				stop = System.nanoTime();
				
				timeTaken.add((stop-start)/1000000000.0);
				solutions.add(states_solutions[1]);
				
				time_total += (stop-start)/1000000000.0;
				sol_total +=  states_solutions[1];
				
				System.out.println(states_solutions[1] + "\t" + (stop-start)/1000000000.0);
				}
			
			System.out.println("TOTAL TIME : " + time_total);
			System.out.println("TOTAL SOLUTIONS : " +sol_total);
			System.out.println("SOLS/SEC : " + sol_total/time_total);
			
			time_total = 0;
			sol_total = 0;
			solutions.clear();
			timeTaken.clear();
			
			System.out.println("\nStrategy 5");
			for (int i = 0; i < 30; i++) {
				start = System.nanoTime();
				ChessBoard n = new ChessBoard();
				n.initializeModel();
				
				KnightsTour k = new KnightsTour();
				states_solutions = k.computeStrategyFive(n.nodes);
				stop = System.nanoTime();
				
				timeTaken.add((stop-start)/1000000000.0);
				solutions.add(states_solutions[1]);
				
				time_total += (stop-start)/1000000000.0;
				sol_total +=  states_solutions[1];
				
				System.out.println(states_solutions[1] + "\t" + (stop-start)/1000000000.0);
				}
			
			System.out.println("TOTAL TIME : " + time_total);
			System.out.println("TOTAL SOLUTIONS : " +sol_total);
			System.out.println("SOLS/SEC : " + sol_total/time_total);
			
			time_total = 0;
			sol_total = 0;
			solutions.clear();
			timeTaken.clear();
			}
		}
	}

class ChessBoard {
	Node[][] nodes = new Node[8][8];
	
	void initializeModel () {
		int i,j;
		
		//Initialize objects
		for (i=0;i<8;i++) {
			for (j=0;j<8;j++) {
			Node node = new Node();
			nodes[i][j] = node;
			node.beenVisited = false;
			node.x = i;
			node.y = j;
			node.parent = null;
			}
		}
		
		for (i=0;i<8;i++) {
			for (j = 0;j<8; j++) {
//				System.out.println("X : " + i +" Y : " + j);
				
				boolean oneRight = false;
				boolean twoRight = false;
				boolean oneLeft = false;
				boolean twoLeft = false;
				boolean oneUp = false;
				boolean twoUp = false;
				boolean oneDown = false;
				boolean twoDown = false;
				
				//Validate possible moves
				if ((j+1) <= 7) {oneRight = true;}
				if ((j+2) <= 7) {twoRight = true;}
				if ((j-1) >= 0) {oneLeft = true;}
				if ((j-2) >= 0) {twoLeft = true;}
				if ((i-1) >= 0) {oneUp = true;}
				if ((i-2) >= 0) {twoUp = true;}
				if ((i+1) <= 7) {oneDown = true;}
				if ((i+2) <= 7) {twoDown = true;}
				
				//Add valid neighbours to neighbour array
				if (oneRight && twoUp) { addNeighbour(nodes[i][j], nodes[i-2][j+1]); }
				if (oneRight && twoDown) {addNeighbour(nodes[i][j], nodes[i+2][j+1]); }
				if (oneLeft && twoUp) { addNeighbour(nodes[i][j], nodes[i-2][j-1]); }
				if (oneLeft && twoDown){ addNeighbour(nodes[i][j], nodes[i+2][j-1]); }
				if (twoRight && oneUp) { addNeighbour(nodes[i][j], nodes[i-1][j+2]); }
				if (twoRight && oneDown) { addNeighbour(nodes[i][j], nodes[i+1][j+2]); }
				if (twoLeft && oneUp) { addNeighbour(nodes[i][j], nodes[i-1][j-2]); }
				if (twoLeft && oneDown){ addNeighbour(nodes[i][j], nodes[i+1][j-2]); }
			}
		}
	}
	void addNeighbour(Node currentNode, Node neighbourNode) {
		currentNode.neighbours.add(neighbourNode);
//		currentNode.unvisited.add(neighbourNode);
		currentNode.fixed_degree += 1;
		currentNode.dynamic_degree += 1;
		}	
}

class Node {
	Node parent;
	ArrayList <Node> neighbours =  new ArrayList <Node> ();
	ArrayList <Node> failureNodes =  new ArrayList <Node> ();
	ArrayList <Node> unvisited = new ArrayList <Node> ();
	ArrayList <Node> min_degree_unvisited = new ArrayList <Node> ();
	int fixed_degree = 0;
	int dynamic_degree = 0;
	int x = 0;
	int y = 0;
	boolean beenVisited = false;
}


