package testing;

import static org.junit.Assert.*;
import main.Kablewie;

import org.junit.Test;

public class KablewieUnitTests {
	IntegrationTests interactingClass = new IntegrationTests();

	@Test
	public void testStartGame() {
		Kablewie tester = new Kablewie();
		assertEquals("Test whether the kablewie class displays",true,tester.startGame(interactingClass.createBoard(), interactingClass.createPlayer(), interactingClass.createMainMenu()));	
	}
	@Test
	public void testStartLoadedGame() {
		Kablewie tester = new Kablewie();	
		assertEquals("Test whether the kablewie class displays",true,tester.startGame(interactingClass.createBoard(), interactingClass.createPlayer(), interactingClass.createMainMenu()));	

	}
		
}