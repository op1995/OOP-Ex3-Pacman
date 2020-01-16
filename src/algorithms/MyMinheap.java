package algorithms;
import dataStructure.Node;

public class MyMinheap {
//	public class HeapNode{
//		double weight;
//		Node relevantNode;
//		
//		public HeapNode(Node n) {
//			this.relevantNode = n;
//			this.weight = n.getWeight();
//		}
//	}//end inner class HeapNode

	int size;
	int maxSize;
	Node[] heapNodeArray;
	
	
	public MyMinheap(int maxSize) {
		this.maxSize = maxSize;
		heapNodeArray = new Node[maxSize];
		size=0;
		
	}
	
	public void add(Node newNode) {
//		HeapNode newHeapNode = new HeapNode(newNode);
		this.heapNodeArray[size] = newNode;
		size++;
		int now = size-1;
		
		if(size==1) {return;} //if there is only 1 Node in the heap, no need to do anymore.
		
		while(heapNodeArray[now].getWeight() < heapNodeArray[parent(now)].getWeight()) {
			swap(now, parent(now));
			now = parent(now);
		}
	}
	
	public int parent(int child) {
		return (child-1)/2;
	}
	
	public int leftChild(int parentInt) {
		return 2*parentInt+1;
	}
	
	public int rightChild(int parentInt) {
		return (2*parentInt) +2;
	}
	
    public boolean isLeaf(int pos) 
    { 
        if (pos >= (size / 2) && pos <= size) { 
            return true; 
        } 
        return false; 
    }
    
// // Function to heapify the node at pos 
//    public void minHeapify(int pos) 
//    { 
//  
//        // If the node is a non-leaf node and greater 
//        // than any of its child 
//        if (!isLeaf(pos)) {
//        	double leftChildWeight = heapNodeArray[leftChild(pos)].getWeight();
//        	double rightChildWeight = heapNodeArray[rightChild(pos)].getWeight();
//        	double myWeight = heapNodeArray[pos].getWeight(); //too much
//            if (myWeight > leftChildWeight || myWeight > rightChildWeight) { 
//  
//                // Swap with the left child and heapify 
//                // the left child 
//                if (leftChildWeight < myWeight) { 
//                    swap(pos, leftChild(pos)); 
//                    minHeapify(leftChild(pos));
//                } 
//  
//                // Swap with the right child and heapify 
//                // the right child 
//                else { 
//                    swap(pos, rightChild(pos)); 
//                    minHeapify(rightChild(pos)); 
//                } 
//            } 
//        }
//        
//    }//end minHeapify
    
    public void minHeapify(int v){
		int smallest;
		int left = leftChild(v);
		int right = rightChild(v);
		if (left<size && heapNodeArray[left].getWeight()<heapNodeArray[v].getWeight()){
			smallest = left;
		}
		else{
			smallest = v;
		}
		if (right<size && heapNodeArray[right].getWeight()<heapNodeArray[smallest].getWeight()){
			smallest = right;
		}
		if (smallest!=v){
			swap(v, smallest);
			minHeapify(smallest); //minHeapify the "new" child, as in - check that the new place for the node that was degraded , that it isn't bigger then it's children
			minHeapify(parent(v));
		}		
	}
    
    public Node heapExtractMin(){
		Node v=null;
		if (!(size==0)){
			v = heapNodeArray[0];
			heapNodeArray[0]=heapNodeArray[size-1];
			size--;
			minHeapify(0);
		}
		return v;
	}
    
	
	public void swap(int src, int dst) {
		Node temp = heapNodeArray[src];		
		heapNodeArray[src] = heapNodeArray[dst];
		heapNodeArray[dst] = temp;
	}
	
	/** returns the heap minimum */
	public Node heapMinimum(){return heapNodeArray[0];}
	public boolean isEmpty() {return (this.size==0);}
	
}
