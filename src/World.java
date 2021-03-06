import java.util.Random;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

public class World implements GLEventListener{
	
    public static int N_PLANE_ROWS = 10;
    public static int N_PLANE_COLS = 7;
    public static int N_PASSENGERS = N_PLANE_ROWS * (N_PLANE_COLS-1);
    public static int BORDER_SIZE = 2;
    public static int NUM_VERTICES = 16;
    public static double PASSENGER_RADIUS = 0.25;
    public static double SEAT_OFFSET = 0.2;
    public static int MAX_INT = 2147483647;
    
    private static Plane state;
	
	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		//System.out.println("display called");
		GL2 gl = drawable.getGL().getGL2();
		
		// clear colour buffer
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
	    drawPlane(gl);
	    drawState(gl);
		//System.out.println("display finished");
	}
	
	// draw the location of each passenger
	private void drawState(GL2 gl) {
		Passenger p;
		for (int r = 0; r < state.nrows; r++) {
            for (int c = 0; c < state.ncols; c++) {
            	// get passenger in each cell
            	p = state.grid[r][c].getPassenger();
            	if(p == null) continue;
    			// render the passenger
            	drawPassenger(gl, p);
    			//System.out.println("Passenger is at (" + p.getLoc().getRow() + ", " + p.getLoc().getCol() + ")");

            }
        }
	}
	
	// draws the passenger as a circle
	private void drawPassenger(GL2 gl, Passenger p) {
		
		double offset = 0.5;
		double p_row = p.getLoc().getRow() + offset;
		double p_col = p.getLoc().getCol() + offset;
		
		System.out.println("Drawing passenger at "+p.getLoc().getRow()+","+p.getLoc().getCol());
		
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        
		// set the colour to red
        int r = MAX_INT/(p.getTicketRow()+1);
        int g = MAX_INT/(p.getTicketCol()+1);
        int b = MAX_INT/2;
        
        gl.glColor3i(r, g, b);

		gl.glBegin(GL2.GL_TRIANGLE_FAN);
	    // set the centre of the circle
	    gl.glVertex2d(p_row, p_col); //The centre of the circle
	    
	    // draw circle
		double angle = 0;
		double angleIncrement = 2 * Math.PI/NUM_VERTICES;
		for(int i = 0; i <= NUM_VERTICES; i++){
	        angle = i * angleIncrement;
	        double x = PASSENGER_RADIUS * Math.cos(angle);
	        double y = PASSENGER_RADIUS * Math.sin(angle);
	        gl.glVertex2d(x+p_row, y+p_col);
		}
		gl.glEnd();
	}
	
	// draw the plane seats and aisle
	private void drawPlane(GL2 gl) {
		int r, c;
		gl.glBegin(GL2.GL_QUADS);
	    // render the seats and aisle for the plane
	    for(r = 0; r < N_PLANE_ROWS; r++) {
	    	for(c = 0; c < N_PLANE_COLS; c++) {
	    		gl.glColor3f(1f, 1f, 1f);
	    		// render the aisle
	    		if(c == N_PLANE_COLS/2) gl.glColor3f(0.9f, 0.9f, 0.9f);
	    		gl.glVertex2d(r, c);
	    		gl.glVertex2d(r, c+1);
	    		gl.glVertex2d(r+1, c+1);
	    		gl.glVertex2d(r+1, c);
	    	}
	    }
	    gl.glEnd();	    
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		// set background colour
		gl.glClearColor(0.6f, 0.6f,  0.6f,  1);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		// set up coordinate system
		GLU glu = new GLU();
		glu.gluOrtho2D(-BORDER_SIZE, N_PLANE_ROWS+BORDER_SIZE, -BORDER_SIZE, N_PLANE_COLS+BORDER_SIZE);
	}
	
	World(Plane plane) {
		GLProfile profile = GLProfile.get(GLProfile.GL2);
	    GLCapabilities capabilities = new GLCapabilities(profile);
	    GLJPanel canvas = new GLJPanel(capabilities);
	    canvas.addGLEventListener(this);
	   
	    JFrame frame = new JFrame ("Plane Boarding Simulator");
	    frame.add(canvas);
	      
	    // set the default size of the window
	    frame.setSize(500,500);
	    frame.setVisible(true);
	      
	    // quit on window close
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    FPSAnimator animator = new FPSAnimator(2);
	    animator.add(canvas);
	    animator.start();
	    
	    // initialise the state, the location of passengers at a given time
	    state = plane;
	}

}
