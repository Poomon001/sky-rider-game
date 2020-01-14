/* Game.java
 * Space Invaders Main Program
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import javax.swing.*;
import java.net.URL;

public class Game extends Canvas {

 
      	private BufferStrategy strategy;   // take advantage of accelerated graphics
        private boolean waitingForKeyPress = true;  // true if game held up until
                                                    // a key is pressed
        private boolean upPressed = false;    // true if up arrow key currently pressed
        private boolean delPressed = false;// true if game stopped 
        private boolean pause = false;// pause the enemies to generate
        
        private boolean gameRunning = true;
        private ArrayList entities = new ArrayList(); // list of entities
                                                      // in game
        private ArrayList removeEntities = new ArrayList(); // list of entities
                                                            // to remove this loop
        private Entity ship;  // the ship
        private double moveSpeed = 600; // hor. vel. of ship (px/s)
        private long lastFire = 0; // time last shot fired
        private long lastJump = 0; // time last jump
        private long firingInterval = 1000; // interval between shots (ms)
        private long jumpInterval = 300; // interval between jumps (ms)
        private int alienCount; // # of aliens left on screen
        private int d = 0; // displacement for scrolling bg
        private long frameInterval = 16; // Frame intervalin ms (16 ~= 60fps)
        private double time = 0;
        protected int speed = 0;
		private boolean stop = false;
        private long lastCoin = 0;
		private long lastMissile = 0;
		private long lastVS = 0;
		private long lastVM = 0;
		private long lastVL = 0;
		private long lastHS = 0;
		private long lastHM = 0;
		private long lastHL = 0;
		private int score;
        
        // gravity by poom
        static Image bg1;
        
        //public room room;//addeddd new var called room
        public double gravity = 30;
        public double gravitySpeed = 0;
        public double gravityResistance = 0;
        
        static int gameStage = 0;
        static Image bg2;        // image displayed while play occurs for part 3
        static Image bg3;        // image displayed while play occurs for part 1
    	static Image bg4;        // image displayed while play occurs for part 2
    	static Image bg5;     // image displayed while play occurs for part 3
        
         // waiting for 'any' key press
        
        
        private String message = ""; // message to display while waiting
                                     // for a key press

        private boolean logicRequiredThisLoop = false; // true if logic
                                                       // needs to be 
                                                       // applied this loop
        private int width = 1280;        // width of background photo
		private int height = 720;        // height of background photo
		
		private int manuMode = 0;        // height of background photo
		private int loseMode = 1;        // height of background photo
		private int pauseMode = 2;        // height of background photo
		private int gameMode = 3;        // height of background photo
		private int instructionMode = 4;        // height of background photo
		private Font font1 = new Font( "Small Fonts", Font.BOLD, 20 );
		private ArrayList<Integer> distances = new ArrayList<Integer>();
		
    	/*
    	 * Construct our game and set it running.
    	 */
    	public Game() {
    		// create a frame to contain game
    		JFrame container = new JFrame("Sky Rider");
    
    		// get hold the content of the frame
    		JPanel panel = (JPanel) container.getContentPane();
    
    		// set up the resolution of the game
    		panel.setPreferredSize(new Dimension(width, height));
    		panel.setLayout(null);
    
    		// set up canvas size (this) and add to frame
    		setBounds(0,0,width,height);
    		panel.add(this);
    
    		// Tell AWT not to bother repainting canvas since that will
            // be done using graphics acceleration
    		setIgnoreRepaint(true);
    
    		// make the window visible
    		container.pack();
    		container.setResizable(false);
    		container.setVisible(true);
    
    
            // if user closes window, shutdown game and jre
    		container.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {
    				System.exit(0);
    			} // windowClosing
    		});
    
    		// add key listener to this canvas
    		addKeyListener(new KeyInputHandler());
    
    		// request focus so key events are handled by this canvas
    		requestFocus();

    		// create buffer strategy to take advantage of accelerated graphics
    		createBufferStrategy(2);
    		strategy = getBufferStrategy();
    		
    		distances.add(0);
    		// initialize entities
    		initEntities();
    		
    		// start the game
    		gameLoop();
        } // constructor
    
    
        /* initEntities
         * input: none
         * output: none
         * purpose: Initialise the starting state of the ship and alien entities.
         *          Each entity will be added to the array of entities in the game.
    	 */
    	private void initEntities() {
              // create the ship and put in center of screen
              ship = new ShipEntity(this, "sprites/jetoff.png", 300, 520);
              entities.add(ship);

    	} // initEntities

        /* Notification from a game entity that the logic of the game
         * should be run at the next opportunity 
         */
         public void updateLogic() {
           logicRequiredThisLoop = true;
         } // updateLogic

         /* Remove an entity from the game.  It will no longer be
          * moved or drawn.
          */
         public void removeEntity(Entity entity) {
           removeEntities.add(entity);
         } // removeEntity

         /* Notification that the player has died.
          */
         public void notifyDeath() {
           reset();
           waitingForKeyPress = true;
           distances.add(score);
           Collections.sort(distances, Collections.reverseOrder());
           gameStage = loseMode;
           //redraw ship at the base when it dies
         } // notifyDeath
         
         public void pause() {
        	 if(delPressed == true) {
        	 waitingForKeyPress = true; 
        	 pause = true;
        	 velocityUp();
        	 } // if
         } // pause
         
         public void run() {
        	 if (delPressed == false) {
        		 pause = false;
        	     waitingForKeyPress = false; 
        	     gameStage = gameMode;
        	 }
         } // run
         
         public void reset() {
        	 time = 0;
             moveSpeed = 0;//reset speed
             gravity = 30;
             ShipEntity.coins = 0;
         }
         

         
         /* Notification that the play has killed all aliens
          */
         public void notifyWin(){
             message = "Yay!  You win!";
             waitingForKeyPress = true;
         } // notifyWin

        /* Notification than an alien has been killed
         */
         public void notifyAlienKilled() {
             alienCount--;
           
             if (alienCount == 0) {
                 notifyWin();
             } // if
             
             // speed up existing aliens
             for (int i=0; i < entities.size(); i++) {
                 Entity entity = (Entity) entities.get(i);
                 if (entity instanceof AlienEntity) {
                     // speed up by 2%
                     entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.04);
                 } // if
             } // for
         } // notifyAlienKilled
         
         /* Notification than an alien has been killed
          */
          public void notifyCoinRemover(Entity entity){
        	  removeEntities.add(entity);
          } //notifyCinRemover
        
        /* Attempt to jump.*/
        public void tryToJump() {
        	if (upPressed && ship.getY() >= 50) {    		
        		ship.setVerticalMovement(velocityUp());
        	} // if
        } // tryToJump
        
        // gravity
        public double gravity() {
        	//when press pause button the gravity stop
        	if(delPressed == false) {
        	//add gravity to the area below 500
        	if(ship.getY() <= 520) {
        	gravity = 30;
        	gravitySpeed += gravity / 2;
        	} else {
        	gravitySpeed = 0; //when the y is lower than 500 remove gravity	
        	}//if
        	}//if
        	return gravitySpeed; // return to tryJump
        } //gravity
        
        // upward acceleration
        public double velocityUp() {
        	//when press pause button the upspeed stop
        	if(delPressed == false) {
        	if(ship.getY() >= 0) {
        		gravityResistance = 10;
        		moveSpeed += gravityResistance;
        	}else {
        		moveSpeed = 0;
        	} // if
        	} else {
            upPressed = false;
        	moveSpeed = 0;
        	gravityResistance = 0;
        	}
        	return -moveSpeed;
        } // velocityUp
        
	/*
	 * gameLoop
         * input: none
         * output: none
         * purpose: Main game loop. Runs throughout game play.
         *          Responsible for the following activities:
	 *           - calculates speed of the game loop to update moves
	 *           - moves the game entities
	 *           - draws the screen contents (entities, text)
	 *           - updates game events
	 *           - checks input
	 */
	public void gameLoop() {
          long lastLoopTime = System.currentTimeMillis();
          
          
		  // redraws screen multiple times per second
          while (gameRunning) {
        	double rate = (Math.pow(time, 1.5)); // the rate of which the game is speeding up
            // calc. time since last update, will be used to calculate
            // entities movement
            long delta = System.currentTimeMillis() - lastLoopTime;
            lastLoopTime = System.currentTimeMillis();

            // get graphics context for the accelerated surface and make it black
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            
    		// Create Image Object
    		Toolkit tk = Toolkit.getDefaultToolkit();
    		speed = (int)(rate);	// speed increase exponentially with time
    		
    		// Scrolling background
    		//stop background from moving when the game paused
            if (pause == false) {
    		    g.drawImage(bg1, d, 0, this);
    		    g.drawImage(bg1, d + width, 0, this);
    		  if (d <= -width) {
    			    d = speed * -1;
    		    } else {
    		        d -= 5 + speed;
    		    } // else
            } // if
    		
    		// Load background images
    		URL url = Game.class.getResource("sprites/background.jpg");
    		bg1 = tk.getImage(url);
    		url = Game.class.getResource("sprites/menu.png");
    		bg2 = tk.getImage(url);
    		url = Game.class.getResource("sprites/lose.png");
    		bg3 = tk.getImage(url);
    		url = Game.class.getResource("sprites/pause.png");
    		bg4 = tk.getImage(url);
    		url = Game.class.getResource("sprites/instruction.png");
    		bg5 = tk.getImage(url);

            // move each entity
            if (!waitingForKeyPress) {
              for (int i = 0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);
                entity.move(delta);
              } // for
            } // if

            // draw all entities
            for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               entity.draw(g);
            } // for
            
            // Generate zappers on random frequency at random height
            if ((System.currentTimeMillis() - lastVM) < firingInterval * 4) {
            } else if (pause == false) {
                int yRandom = (int)(Math.random() * 520) + 50;
                lastVM = System.currentTimeMillis();
                Entity alien = new AlienEntity(this, "sprites/vm.png", width, yRandom, (speed + 5) * 58);
                entities.add(alien);
            } // else
            
            // Generate zappers on random frequency at random height
            if ((System.currentTimeMillis() - lastVS) < firingInterval * 3) {
            } else if (pause == false) {
                int yRandom = (int)(Math.random() * 520) + 50;
                lastVS = System.currentTimeMillis();
                Entity alien = new AlienEntity(this, "sprites/vs.png", width, yRandom, (speed + 5) * 58);
                entities.add(alien);
            } // else
            
            // Generate zappers on random frequency at random height
            if ((System.currentTimeMillis() - lastVL) < firingInterval * 6) {
            } else if (pause == false) {
                int yRandom = (int)(Math.random() * 520) + 50;
                lastVL = System.currentTimeMillis();
                Entity alien = new AlienEntity(this, "sprites/vs.png", width, yRandom, (speed + 5) * 58);
                entities.add(alien);
            } // else
            
            // Generate zappers on random frequency at random height
            if ((System.currentTimeMillis() - lastHS) < firingInterval * 5) {
            } else if (pause == false) {
                int yRandom = (int)(Math.random() * 520) + 50;
                lastHS = System.currentTimeMillis();
                Entity alien = new AlienEntity(this, "sprites/hs.png", width, yRandom, (speed + 5) * 58);
                entities.add(alien);
            } // else
            
            // Generate zappers on random frequency at random height
            if ((System.currentTimeMillis() - lastHM) < firingInterval * 7) {
            } else if (pause == false) {
                int yRandom = (int)(Math.random() * 520) + 50;
                lastHM = System.currentTimeMillis();
                Entity alien = new AlienEntity(this, "sprites/hm.png", width, yRandom, (speed + 5) * 58);
                entities.add(alien);
            } // else
            
            // Generate zappers on random frequency at random height
            if ((System.currentTimeMillis() - lastHL) < firingInterval * 9) {
            } else if (pause == false) {
                int yRandom = (int)(Math.random() * 520) + 50;
                lastHL = System.currentTimeMillis();
                Entity alien = new AlienEntity(this, "sprites/hl.png", width, yRandom, (speed + 5) * 58);
                entities.add(alien);
            } // else
            
			// Generate missiles on random frequency at random height
            if ((System.currentTimeMillis() - lastMissile) < firingInterval * 10) {
            } else if (pause == false) {
                int yRandom = (int)(Math.random() * 520) + 50;
                lastMissile = System.currentTimeMillis();
                Entity missile = new MissileEntity(this, "sprites/missile.png", 1280, yRandom, (speed + 5) * 29);
                entities.add(missile);
            } // else
            
            // Generate coins on random frequency at random height
            if ((System.currentTimeMillis() - lastCoin) < firingInterval * 5) {
            } else if (pause == false) {
                int yMax = (int)(Math.random() * 520) + 50;
                lastCoin = System.currentTimeMillis();
                Entity coin = new CoinEntity(this, "sprites/coin.png", width, yMax, (speed + 5) * 58);
                entities.add(coin);
            } // else
				
			// speed up missiles
            for (int i=0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);
                if (entity instanceof MissileEntity) {
                    // speed up by 4% per frame
                    entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
                } // if
            } // for
            
            // Display scores
            if (waitingForKeyPress == false) {
                score = (int)(rate * 100);
                time += 0.001;
                // distance
                g.setColor(Color.black);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("00" + score + "m", 30, 50);
                // best score
                g.setFont(new Font("Arial", Font.BOLD, 15));
                g.drawString("BEST:" + distances.get(0), 30, 67);
                // coin count
                g.setFont(new Font("Arial", Font.BOLD, 25));
                g.drawString("Coins: " + ShipEntity.coins, 30, 90);
            } // if
            
            

            // brute force collisions, compare every entity
            // against every other entity.  If any collisions
            // are detected notify both entities that it has
            // occurred
           for (int i = 0; i < entities.size(); i++) {
             for (int j = i + 1; j < entities.size(); j++) {
                Entity me = (Entity)entities.get(i);
                Entity him = (Entity)entities.get(j);

                if (me.collidesWith(him)) {
                  me.collidedWith(him);
                  him.collidedWith(me);
                } // if
             } // inner for
           } // outer for

           // remove dead entities
           entities.removeAll(removeEntities);
           removeEntities.clear();

           // run logic if required
           if (logicRequiredThisLoop) {
             for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               entity.doLogic();
             } // for
             logicRequiredThisLoop = false;
           } // if

           // if waiting for "any key press", draw message
           if (waitingForKeyPress) {
            
             
            /*added*/// draw background
            switch(gameStage) {
            //homepage
            case 0:
            pause = false;//fix bug
            g.setColor(Color.black);
            g.drawImage(bg2, 0, 0, this);
     		g.drawImage(bg2, 0 + width, 0, this);
 			g.setFont( font1 );
            g.drawString("1) Start Now", (width/2 - g.getFontMetrics().stringWidth("1) Start Now")/2), height/2+100);
            g.drawString("2) Instruction", (width/2 - g.getFontMetrics().stringWidth("1) Start Now")/2), height/2 + 150);
            g.drawString("3) Exit", (width/2 - g.getFontMetrics().stringWidth("1) Start Now")/2), height/2 + 200);
            break;
            
            //lose 
            case 1:
            pause = false;
            g.setColor(Color.white);
            g.drawImage(bg3, 0, 0, this);
     		g.drawImage(bg3, 0 + width, 0, this);
     		g.setFont( font1 );
     		g.drawString("You traveled: " + score + "m", 300, 500);
     		g.drawString("You collected: " + ShipEntity.coins + " coins", 800, 500);
     		g.drawString("1) Start Now", (width/2 - g.getFontMetrics().stringWidth("1) Start Now")/2), height/2+100);
            g.drawString("2) Return to manu", (width/2 - g.getFontMetrics().stringWidth("1) Start Now")/2), height/2 + 150);
            g.drawString("3) Exit", (width/2 - g.getFontMetrics().stringWidth("1) Start Now")/2), height/2 + 200);
            break;
            
            //pause
            case 2:
            pause = false;
            g.setColor(Color.black);
            g.drawImage(bg4, 0, 0, this);
         	g.drawImage(bg4, 0 + width, 0, this);
         	g.setFont( font1 );
         	g.drawString("1) Resume", (width/2 - g.getFontMetrics().stringWidth("1) Resume")/2), height/2+100);
            g.drawString("2) Return to manu", (width/2 - g.getFontMetrics().stringWidth("1) Resume")/2), height/2 + 150);
            g.drawString("3) Exit", (width/2 - g.getFontMetrics().stringWidth("1) Resume")/2), height/2 + 200);
            break;
                
            //instruction    
            case 4:
            pause = false;
            g.setColor(Color.white);
            g.setFont( font1 );
            g.drawImage(bg5, 0, 0, this);
            g.drawImage(bg5, 0 + width, 0, this);
            g.drawString("1) Return to manu", (width/2 - g.getFontMetrics().stringWidth("1) Return to manu")/2), height-100);                
            break;
            }
           }//if
            
            
            // clear graphics and flip buffer
            g.dispose();
            strategy.show();

            // ship should not move without user input
            ship.setVerticalMovement(0);

            // if up is pressed, try to jump
            if (upPressed) {
              tryToJump();
            } else {
              ship.setVerticalMovement(gravity());
              
              //fix a bug
              if (ship.getY() < 0) {
            	  //redraw the ship at y = 0
              }//if 
            }//if
            
            // if up is pressed, try to jump
            if (upPressed) {
              tryToJump();
            } // if


            // refresh rate
            try { Thread.sleep(frameInterval); } catch (Exception e) {}

          } // while

	} // gameLoop


        /* startGame
         * input: none
         * output: none
         * purpose: start a fresh game, clear old data
         */
         private void startGame() {
            // clear out any existing entities and initalize a new set
            entities.clear();
            initEntities();
            // blank out any keyboard settings that might exist
            delPressed = false;
            upPressed = false;
            pause();
            reset();
         } // startGame


        /* inner class KeyInputHandler
         * handles keyboard input from the user
         */
	private class KeyInputHandler extends KeyAdapter {
                 
                

                /* The following methods are required
                 * for any class that extends the abstract
                 * class KeyAdapter.  They handle keyPressed,
                 * keyReleased and keyTyped events.
                 */
		public void keyPressed(KeyEvent e) {

                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if
                  
                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                	gravitySpeed = 0;//reset gravity when the char is floating  
                    upPressed = true;
                  } // if

		} // keyPressed

		public void keyReleased(KeyEvent e) {
                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if
                  
                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                	moveSpeed = 0;
                  	gravitySpeed = 0;//reset gravity 
                    upPressed = false;
                  } // if

		} // keyReleased

 	        public void keyTyped(KeyEvent e) {

 	           /*added*/
			   // if escape is pressed, end game
			   if(gameStage == gameMode) { //esc
				   if(e.getKeyChar() == 27) {
				       if(delPressed == false) {
				          delPressed = true;
			              pause();
			              gameStage = pauseMode;
				       }//if
				   }//if
	
			   } else if(gameStage == manuMode){//maun
				   if(e.getKeyChar() == 49) {// Key "1" pressed
						waitingForKeyPress = false; 
						delPressed = false;
						startGame(); 
					    run();//run the game
				   }else if(e.getKeyChar() == 50) {
					   waitingForKeyPress = true; 
					   gameStage = 4; // Key "2" pressed
				   }else if(e.getKeyChar() == 51) {
						System.exit(0); // Key "3" pressed
					} //if
				   
			   }else if(gameStage == loseMode){
				   if(e.getKeyChar() == 49) {
					   delPressed = false;
				       run();//run the game
				       startGame(); 
				       gameStage = gameMode;
				   } if(e.getKeyChar() == 50) {
						waitingForKeyPress = true; 
						startGame();
						gameStage = manuMode;//"go to homepage"
				}else if(e.getKeyChar() == 51) { 
					System.exit(0);//"to exit"
				}//if   
			   }else if(gameStage == pauseMode){
				    delPressed = true;
					if(e.getKeyChar() == 49) {
						delPressed = false;
						run();//"to play"
					}else if(e.getKeyChar() == 51) { 
						System.exit(0);//"to exit"
					}else if(e.getKeyChar() == 50) {
						waitingForKeyPress = true; 
						startGame();
						gameStage = manuMode;//"go to homepage"
					}//if
					
					}else if(gameStage == instructionMode){
					    delPressed = true;
					    if(e.getKeyChar() == 49) {
							waitingForKeyPress = true; 
							startGame();
							gameStage = manuMode;//"go to homepage"
					}//if
			   }//if else
			   
		} // keyTyped
 	       
 	       
	} // class KeyInputHandler

	/**
	 * Main Program
	 */
	public static void main(String [] args) {
        // instantiate this object
		new Game();
	} // main
	
} // Game
