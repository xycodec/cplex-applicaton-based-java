package modified_proj_1;

public class edge{
	public int src,dest;
	public double capacity;
	
	public edge(int src,int dest,double capacity) {
		this.src=src;
		this.dest=dest;
		this.capacity=capacity;
	}

	public edge() {
		super();
	}
}