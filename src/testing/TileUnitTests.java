/**
 * @file TileUnitTests.java
 * @author William Hughes
 * @Date 13/03/2016
 * @see Tile.java
 * @brief Tests Tile class
 * 
 * Tests methods in Computer, and whether the computer AI works correctly
 */

package testing;

import static org.junit.Assert.*;


import org.junit.Test;


import game.*;

public class TileUnitTests {
	IntegrationTests interactingClass = new IntegrationTests();

	
	/**
	 * Hidden class referenced because Tile is abstract
	 */
	
	@Test
	public void test() {
		Tile tester = new Hidden(false, false, false); 
		Board testBoard = interactingClass.createBoard();
		
		/**
		 * Testing the adjacent tiles that the array has up to 8 surrounding squares
		 */

		assertEquals("test if the length of the array list created is always"
				+ " 9 for a tile in the middle of the board", 9,
				(tester.getTileArround(testBoard.getBoard(),
						testBoard.getColumns() / 2,testBoard.getRows() / 2)
						).size());
		
		
		
		assertEquals("test if the length of the array list created is always"
				+ " 4 for a tile in the corner of the board", 4,
				(tester.getTileArround(testBoard.getBoard(),0,0)).size());
	}
}
