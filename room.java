/*create rectangular obstacles
 *assign a random location for each obstacle
 * 
 * */

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
public class room{
	public ArrayList<Rectangle> obstacle;//arraylist of rectangular obstacles
	
	private int time; //initialize time
	private int currentTime = 0;//store currentTime
	private int speed = 20;//store speed
	private final int SPACE = 80;//store a space between top and bottom obstacles
	private final int width = 20;//specific width of the obstacle
	private Random random;//randomly picking up a number 
	
	//constructor
	public room(int time) {
		obstacle = new ArrayList();
		this.time = time;
		random = new Random();
	}//room
	
	//create new obstacle according to gameloop (time interval)
	public void update() {
		//each update (from gameloop) will increase 1 current time
		currentTime++;
		
		//if currentTime = time create new 2 obstacles(top&bottom)
		if (currentTime == time) {
			currentTime=0;//reset after the objects created
			
			int x1 = 1280;//max width of the screen
			int y1 = 0;//min width
			
			int height1 = random.nextInt(800/2);//height of the first is a random num from 1-400
			
			int y2 = height1 + SPACE;
			int height2 = 800 - y2;//the rest of the screen excluding for height1 and SPACE
			obstacle.add(new Rectangle(x1,y1,width,height1));//add bottom obstacle to the obstacle arraylist
			obstacle.add(new Rectangle(x1,y2,width,height2));//add top obstacle to the obstacle arraylist
		}//if
		
		//relocation each obstacle 
		for(int i = 0; i < obstacle.size(); i++) {
    		Rectangle object = obstacle.get(i);//specify to the [i] obstacle 
    		object.x -= speed;//the obstacles moving backward = 'speed' 
    		
    		//remove the obstacle when it get to the end of the screen
    		if(object.x + object.width < 0) {
    		    obstacle.remove(i--);   
    		}//if
    		
    		
    	}//for
	}//update
	
	//draw obstacle 
    public void obstacle(Graphics g) {
    	g.setColor(Color.green);//color of the obstacles
    	
    	//draw every obstacle in the arraylist
    	for(int i = 0; i < obstacle.size(); i++) {
    		Rectangle object = obstacle.get(i);//specify to the [i] obstacle 
    		g.fillRect(object.x, object.y, object.width, object.height); //draw(position x, position y, width, hight)
    	}//for
    	
    }//obstacle
}//room
