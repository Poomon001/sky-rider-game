/* CoinEntity.java
 * Represents one of the coins
 * 
 */

public class CoinEntity extends Entity{
	
	private Game game;

	 /* construct a new coin
	   * input: game - the game in which the coin is being created
	   *        r - the image representing the alien
	   *        x, y - initial location of alien
	   */
	public CoinEntity(Game g, String r, int newX, int newY, int moveSpeed){
		super(r, newX, newY);  // calls the constructor in Entity
		game = g;
		dx = -moveSpeed;  // start off moving left
	} // constructor
	
	/* move
	   * input: delta - time elapsed since last move (ms)
	   * purpose: move alien
	   */
	  public void move (long delta){
	    // if we reach left side of screen and are moving left
	    // request logic update
	    if ((dx < 0) && (x < 10)) {
	      game.updateLogic();   // logic deals with moving entities
	                            // in other direction and down screen
	    } // if

	    // if we reach right side of screen and are moving right
	    // request logic update
	    if ((dx > 0) && (x > 750)) {
	      game.updateLogic();
	    } // if
	    
	    // proceed with normal move
	    super.move(delta);
	  } // move
	  
	  /* doLogic
	   * Updates the game logic related to the aliens,
	   * ie. move it down the screen and change direction
	   */
	  public void doLogic() {
	    // swap horizontal direction and move down screen 10 pixels
	    //dx *= -1;
	    //y += 10;
		  
		  // if shot moves off top of screen, remove it from entity list
		   if (x < -100) {
		     game.removeEntity(this);
		   } // if
	  } // doLogic
	  
	  public void collidedWith(Entity other){
		     // collisions with aliens are handled in ShotEntity and ShipEntity
	  } // collidedWith
	  
} //CoinEntity