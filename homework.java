/*
* Author: Michelle Becerra
* Date: 9/21/16
* Foundations of Artificial Intelligence Homework 1 Search algorithms for car navigation
* Professor Dr. Itti
*/

import java.util.*;
import java.io.*;

class homework{
	public static Hashtable<String,LinkedList<Node>> graph;
	public static Hashtable<String, Integer> heuristic;

	public static void main(String[] args) throws FileNotFoundException, IOException{
	
		Node node = new Node();
		Scanner br = new Scanner(new File("input.txt"));
		String line;
		int real, liveInt;
		String algo = "";
		String startState = "";
		String goalState = "";
		//Create graph of Parent to Children.
		//Create heuristic table of no traffic data.
		graph = new Hashtable<String,LinkedList<Node>>();
		heuristic = new Hashtable<String, Integer>();
		//Tested with multiples spaces after input and with  none. Both work.
		while(br.hasNextLine()){
			algo = br.next();
			br.nextLine();
			startState = br.next();
			goalState = br.next();
			break;
		}
		//Parse the live traffic information first
		liveInt = Integer.parseInt(br.next());
		br.nextLine();
		while(br.hasNextLine()){
			//If theres more than one child append to children linked list 
			while(liveInt > 0){
				line = br.nextLine();

				String[] tokens = line.split(" ");

				if(graph.get(tokens[0]) == null){

					graph.put(tokens[0], new LinkedList<Node>());

				}
				Node child = new Node();
				child.setState(tokens[1]);
				child.setPathCost(Integer.parseInt(tokens[2]));
				graph.get(tokens[0]).add(child);
				liveInt--;
			}

			break; //Ignore no traffic info for now
		}
		//Parse no traffic info for our heuristic in A*
		real = Integer.parseInt(br.next());
		br.nextLine();
		while(br.hasNextLine()){
			//Create heuristc table of ints
			while(real > 0){
				line = br.nextLine();
				String[] tokens = line.split(" ");
				heuristic.put(tokens[0], Integer.parseInt(tokens[1]));
				real--;
			}
			break; //In case there are extra lines in test case
		}
		//Process the search algorithms
		switch (algo){
			case "BFS" : BFS(startState, goalState);
					    break;
			case "DFS" : DFS(startState, goalState);
						break;
			case "UCS" : UCS(startState, goalState);
						break;
			case "A*" : ASTAR(startState, goalState);
						break;
			default:
						break;

		}
		
	}

	public static void BFS(String start, String goal) throws IOException{

		LinkedList<Node> explored = new LinkedList<Node>();
		LinkedList<Node> frontier = new LinkedList<Node>();
		int timestamp = 0;
		//Create start node and add it to frontier
		Node node = new Node();
		node.setState(start);
		node.setPathCost(0);

		frontier.add(node);
		timestamp++;

		while(!frontier.isEmpty()){

			Node curr =  frontier.pop();
			explored.add(curr);
			
			//If we have popped off the goal state then print the result in output.txt
			if(curr.state.equals(goal)){
				printPath(explored, start);
				return;
			}
			//If the current node doesn't have children then don't process its' children
			if(graph.get(curr.getState()) == null){
				continue;
			}
			//Process children and add to frontier if not explored
			LinkedList<Node> children = graph.get(curr.getState());
			for(Node ch: children){
				Node c = new Node(ch);

				if(!explored.contains(c) && !frontier.contains(c)){
					c.setParent(curr.getState());
					c.setPathCost(curr.getPathCost() + 1);
					frontier.add(c);
					timestamp++;
				}
			}


		}

		return;
	}
	public static void DFS(String start, String goal) throws IOException{


		LinkedList<Node> explored = new LinkedList<Node>();
		LinkedList<Node> frontier = new LinkedList<Node>();
		int timestamp = 0;
		//Create start node and add it to frontier
		Node node = new Node();
		node.setState(start);
		node.setPathCost(0);
		//Enqueue to front like a stack
		frontier.push(node);
		timestamp++;
		
		while(!frontier.isEmpty()){
			
			Node curr = frontier.pop();
			explored.add(curr);
			//If we have popped off the goal state then print the result in output.txt
			if(curr.state.equals(goal)){
				printPath(explored, start);
				return;
			}
			//If the current node doesn't have children then don't process it's children
			if(graph.get(curr.getState()) == null){
				continue;
			}
			//Process children from last added to first added and enqueue to the stack
			LinkedList<Node> children = graph.get(curr.getState());
			Iterator<Node> itr = (Iterator<Node>)children.descendingIterator();

			while(itr.hasNext()){
				Node ch = itr.next();
				Node n = new Node(ch);
				if(!explored.contains(n) && !frontier.contains(n)){

					n.setParent(curr.getState());
					n.setPathCost(curr.getPathCost() + 1);
					frontier.push(n);
					timestamp++;

				}			
			}
		}

		return;
	}
	public static void UCS(String start, String goal) throws IOException{
		//Will be using a priority queue based on comperator method
		PriorityQueue<Node> q = new PriorityQueue<Node>();
		LinkedList<Node> explored = new LinkedList<Node>();
		int timestamp = 0;
		//Create and add first node
		Node node = new Node();
		node.setState(start);
		node.setPathCost(0);
		node.setTotalPathCost(0);
		q.add(node);
		timestamp++;

		while(!q.isEmpty()){
			Node curr = q.poll();
			//If we have popped off the goal state then print the result in output.txt
			if(curr.state.equals(goal)){
				explored.add(curr);
				printPath(explored, start);
				return;
			}
			//If the current node doesn't have children then don't process it's children
			if(graph.get(curr.getState()) == null){
				explored.add(curr);
				continue;
			}
			//Process children and follow algorithm shown in class
			LinkedList<Node> children = graph.get(curr.getState());
			for(Node ch: children){
				//Create local copy of node in graph to not override any fields
				Node c = new Node(ch);
				if(!explored.contains(c) && !q.contains(c)){
					
					c.setParent(curr.getState());
					int updatedCost = c.getPathCost() + curr.getPathCost();
					c.setPathCost(updatedCost);
					c.setTotalPathCost(updatedCost);
					c.setTimeStamp(timestamp);
					q.offer(c);
					timestamp++;
				}
				else if(q.contains(c)){	
					Node old = new Node();
					Iterator<Node> iter = q.iterator();

					while(iter.hasNext()){
						Node check = iter.next();
						if(check.equals(c)){
							old = check;
						}
					}
					
					if(c.getPathCost() + curr.getPathCost() < old.getPathCost())
					{
						q.remove(c);
						c.setParent(curr.getState());
						int updatedCost = c.getPathCost() + curr.getPathCost();
						c.setPathCost(updatedCost);
						c.setTotalPathCost(updatedCost);
						c.setTimeStamp(timestamp);	
						q.offer(c);
						timestamp++;
					}
				}
				else if(explored.contains(c)){
					Node old = new Node();
					Iterator<Node> iter = explored.iterator();

					while(iter.hasNext()){
						Node check = iter.next();
						if(check.equals(c)){
							old = check;
						}
					}

					if(c.getPathCost() + curr.getPathCost() < old.getPathCost()){
						explored.remove(c);
						c.setParent(curr.getState());
						int updatedCost = c.getPathCost() + curr.getPathCost();
						c.setPathCost(updatedCost);
						c.setTotalPathCost(updatedCost);
						c.setTimeStamp(timestamp);
						q.offer(c);
						timestamp++;

					}
				}
			}
			explored.add(curr);
		}
		return;
	}
	public static void ASTAR(String start, String goal) throws IOException{
		
		PriorityQueue<Node> q = new PriorityQueue<Node>();
		LinkedList<Node> explored = new LinkedList<Node>();
		int timestamp = 0;

		Node node = new Node();
		node.setState(start);
		node.setPathCost(0);
		node.setTotalPathCost(0);
		q.add(node);

		while(!q.isEmpty()){
			Node curr = q.poll(); 
			//If we have popped off the goal state then print the result in output.txt
			if(curr.state.equals(goal)){	
				explored.add(curr);
				printPath(explored, start);
				return;
			}
			//If the current node doesn't have children then don't process it's children
			if(graph.get(curr.getState()) == null){	
				explored.add(curr);
				continue;
			}
			//Process children while updating the f funciton g + h
			LinkedList<Node> children = graph.get(curr.getState());
			//System.out.println(children);

			for(Node ch: children){
				//Create local copy of node in graph to not override any fields
				Node c = new Node(ch);

				if(!explored.contains(c) && !q.contains(c)){
						
					c.setParent(curr.getState());
					int updatedCost = c.getPathCost() + curr.getPathCost();
					c.setPathCost(updatedCost);
					c.setTotalPathCost(updatedCost + heuristic.get(c.state));
					c.setTimeStamp(timestamp);
					q.offer(c);
					timestamp++;
				}
				else if(q.contains(c)){

					Node old = new Node();
					Iterator<Node> iter = q.iterator();

					while(iter.hasNext()){
						Node check = iter.next();
						if(check.equals(c)){
							old = check;
						}
					}
					
					if(c.getPathCost() + curr.getPathCost() + heuristic.get(c.state) < old.getTotalPathCost())
					{
						q.remove(old);
						c.setParent(curr.getState());
						int updatedCost = c.getPathCost() + curr.getPathCost();
						c.setPathCost(updatedCost);
						c.setTotalPathCost(updatedCost + heuristic.get(c.state));
						c.setTimeStamp(timestamp);
						q.offer(c);
						timestamp++;
					}
				}
				else if(explored.contains(c)){
					Node old = new Node();
					Iterator<Node> iter = explored.iterator();

					while(iter.hasNext()){
						Node check = iter.next();
						if(check.equals(c)){
							old = check;
						}
					}

					if(c.getPathCost() + curr.getPathCost() + heuristic.get(c.state) < old.getTotalPathCost()){
						
						explored.remove(old);
						c.setParent(curr.getState());
						int updatedCost = c.getPathCost() + curr.getPathCost();
						c.setPathCost(updatedCost);
						c.setTotalPathCost(updatedCost + heuristic.get(c.state));
						c.setTimeStamp(timestamp);
						q.offer(c);
						timestamp++;

					}

				}
			}
			explored.add(curr);
		}
		return;

	}
	public static void printPath(LinkedList explored, String start) throws IOException{

		//Create output.txt
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

		//Iterate through explored list backwards to print the answer as specified
		Stack<Node> nodePaths = new Stack<Node>();
		Node n = (Node) explored.remove(explored.size() - 1);
		nodePaths.push(n);

		Node node;
		while(!nodePaths.peek().getState().equals(start)){
			String parent = n.getParent();
			Iterator<Node> iter = explored.descendingIterator();	
			while(iter.hasNext()){
				node = iter.next();		
				if(node.getState().equals(parent)){
					nodePaths.push(node);
					n = node;
					break;
				}
				
			}
		}

		// //Pop answer in correct order
		while(!nodePaths.empty()){
			Node path = nodePaths.pop();
				writer.write(path.getState() + " " + path.getPathCost() + "\n");
		}
		writer.close();

	}
}