/**
 * @file GameController.java
 * @author A4 Peter Jenkins, A5 Thomas Fisher, Victoria Charvis,Ibrahim Shehu, Eromosele Gideon
 * @date 29 February 2016
 * @see Kablewie.java
 * @brief Controls the game
 *
 * Controls the flow of the game takes the
 * click of the user and passes the position
 * of the clicked tile to the Board
 */

package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import main.MainMenu;
import main.Kablewie;

/* 
 * Suppress serial ID warning as ID would not
 * match coding conventions.
 */
@SuppressWarnings("serial")
/**
 * 
 * @class GameController
 * @brief Controls the game
 *
 * Controls the flow of the game takes the
 * click of the user and passes the position
 * of the clicked tile to the Board
 */
public class GameController implements MouseListener, ActionListener {
	
	/**
	 * Constructor
	 * 
	 * @param board a Board object for containing the tiles.
	 * @param player a Player object
	 * @param frame a JFrame to add the JPanel to
	 * @param menu a mainmenu object
	 */
	public GameController(Board board, Player player, 
							JFrame frame, MainMenu menu) {
		// Set Class variables
		this.m_board = board;
		this.m_player = player;
		this.m_frame = frame;
		this.m_menu = menu;
		
		m_frame.setMinimumSize(new Dimension(BOARD_WIDTH,BOARD_HEIGHT));
		
		m_timePassed = "00:00:00";
		m_loaded = false;
		
		setInfo();
		startGame();
		setSound();

		m_time = new Timer(1000, this);
		m_time.start();
		
		if (!m_test) {
			m_tick.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param board a Board object for containing the tiles.
	 * @param player a Player object
	 * @param time the loaded time passed
	 * @param frame a JFrame to add the JPanel to
	 */
	public GameController(Board board, Player player, String time, 
			JFrame frame) {
		// Set Class variables
		this.m_board = board;
		this.m_player = player;
		this.m_timePassed = time;
		this.m_frame = frame;
		
		m_frame.setSize((m_board.getBoard().size() * 30) +
				SPACING, m_board.getBoard().size() * 30 + 105);
		
		m_loaded = true;
		
		setInfo();
		startGame();
		setSound();
		
		m_time = new Timer(TIMER_DELAY, this);
		m_time.start();
		
		if (!m_test) {
			m_tick.loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		
		m_panelGame.repaint();
		m_panelInfo.repaint();
	}
	
	/**
	 * Checks if the current game is for testing purposes
	 * 
	 * @return boolean the current setting of m_test
	 */
	public boolean isTesting() {
		return m_test;
	}
	
	/**
	 * Builds a String of the instructions
	 * 
	 * @return a String of the instructions
	 */
	private String getInstructions() {
		return "Information:\n" 
				+ "The goal of the game is to defuse all the mines on the "
				+ "board without revealing\n " 
				+ "a mine, if a mine is revealed by the player then the game "
				+ "will be over and the player will deemed to " 
				+ "have lost the game.\n" 
				+ "How to play:\n"
				+ "The user can left click to reveal a tile on the board.\n"
				+ "If the user wishes to defuse a tile then the user would " 
				+ "right click in\n"
				+ "order to place a flag on the board and defuse a possible "
				+ "mine. If the flag is placed on a tile\n" 
				+ "that is deemed to be a mine then the number of mines "
				+ "defused is increased.\n";
	}
	
	/**
	 * Show the animation when game is lost
	 */
	public void setGameLost() {
		if (!m_test) {
			m_bomb.loop(1);
		}
		
		m_time.stop();
		m_tick.stop();
		m_GameFinshed.setVisible(true);
		URL url = getClass().getResource("/images/gameLost.jpg");
		m_GameFinshed.setIcon(new ImageIcon(url));
	}
	
	/**
	 * Show the animation when game is won
	 */
	public void setGameWin() {
		if (!m_test) {
			m_won.loop(1);
		}
		
		m_time.stop();
		m_tick.stop();
		m_GameFinshed.setVisible(true);
		URL url = getClass().getResource("/images/GameWon.jpg");
		m_GameFinshed.setIcon(new ImageIcon(url));
		
		String v = "You Have won\n Time taken- " + m_timePassed;
		
		if (!m_test) {
			JOptionPane.showMessageDialog(m_frame, v, "Congratulations", 
					JOptionPane.YES_NO_CANCEL_OPTION);
		}
		
	}
	
	/**
	 * Display game UI info
	 */
	private void setInfo() {
		m_panelInfo = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				m_board.renderInfo(g, m_player, m_timePassed);
			}
		};

		m_panelInfo.setBounds(0, 0, 640, 50);
		m_frame.getContentPane().add(m_panelInfo);
		m_panelInfo.setLayout(null);

		m_GameFinshed = new JButton();
		m_GameFinshed.setVisible(false);
		m_GameFinshed.setBounds(130, 12, 38, 38);

		m_panelInfo.add(m_GameFinshed);
		m_GameFinshed.addActionListener(this);

		m_panelInfo.repaint();
		m_frame.validate();
		m_frame.repaint();
	}
	
	/**
	 * Set the sounds up
	 */
	public void setSound() {
		try {
			AudioInputStream audioInputStream = 
					AudioSystem.getAudioInputStream(
							getClass().getResourceAsStream("/sound/tick.wav"));
			m_tick = AudioSystem.getClip();
			m_tick.open(audioInputStream);
			
			audioInputStream =
					AudioSystem.getAudioInputStream(
							getClass().getResourceAsStream("/sound/bomb.wav"));
			m_bomb = AudioSystem.getClip();
			m_bomb.open(audioInputStream);
			
			audioInputStream =
					AudioSystem.getAudioInputStream(
							getClass().getResourceAsStream("/sound/won.wav"));
			m_won = AudioSystem.getClip();
			m_won.open(audioInputStream);
			
		} catch (Exception x) {
		}
	}
	
	/**
	 * Sets the time between computer player turns for this game
	 */
	public void setTime() {
		boolean validFlag = true;
		
		do {
			double time;

			try {
				time = Double.parseDouble(JOptionPane.showInputDialog(
						"Enter time (0 - 10 seconds) for between computer"
						+ " turns: "));
			} catch (NumberFormatException e) {
				time = Computer.DEFAULT_SLEEP_TIME;
				validFlag = false;
			} catch (NullPointerException e) {
				// JOptionPane was probably closed.
				time = Computer.DEFAULT_SLEEP_TIME;
			}
			
			if (time >= MIN_TIME && time <=MAX_TIME) {
				m_computerPlayer.setTime(time);
				validFlag = false;
			} else if (!(time >= MIN_TIME && time <=MAX_TIME)) {
				JOptionPane.showMessageDialog(null,
						"Time values must be between 0 and 10 seconds",
						"Value Error",
						JOptionPane.WARNING_MESSAGE);
			}
		} while (validFlag);
	}
	
	/**
	 * Called by Time or JMenuBar
	 * 
	 * @param event an ActionEvent describing what happened
	 */
	public void actionPerformed(ActionEvent event) {
		m_savedFile = new SavedFile(this);
		if (event.getSource() == m_time) {
			m_panelInfo.repaint();
			
			if (m_loaded) {
				String prevTime = m_timePassed.replaceAll("[^0-9]","");
				int prev = Integer.parseInt(prevTime);						
				m_secondsPlayed += prev + m_time.getDelay() / TIMER_DELAY;
				m_loaded = false;
			} else {
				m_secondsPlayed += m_time.getDelay() / TIMER_DELAY;
			}

			
			if (m_secondsPlayed >= 60) {
				
				m_minutesPlayed = m_minutesPlayed + 1;
				m_secondsPlayed = 0;
				
				if (m_minutesPlayed >= 60) {
					m_hoursPlayed = m_hoursPlayed + 1;
					m_minutesPlayed = 0;
				}
			}
			
			String hours = "" + m_hoursPlayed;
			String minutes = ""+ m_minutesPlayed;
			String seconds = "" + m_secondsPlayed;
			
			if (m_hoursPlayed < DOUBLE_DIGITS) {
				hours = "0" + hours;
			}
			
			if (m_minutesPlayed < DOUBLE_DIGITS) {
				minutes = "0" + minutes;
			}
			
			if (m_secondsPlayed < DOUBLE_DIGITS) {
				seconds = "0" + seconds;
			}
			
			m_timePassed = hours
							+ ":" 
							+ minutes
							+ ":" 
							+ seconds;
			
		} else if (event.getSource() == m_newGame) {
			
			reset();
			
		} else if (event.getSource() == m_settings) {
			
			m_frame.getContentPane().removeAll();
			m_frame.getJMenuBar().setVisible(false);
			try {
				m_tick.close();
				m_won.close();
				m_bomb.close();
			} 
			catch (Exception e){}
			
			if (m_menu == null) {
				new Kablewie();
				m_frame.dispose();
			} else {
				m_menu.display();
			}
			
		} else if (event.getSource() == m_stopAi) {
			if (m_computerPlayer != null) {
				if (m_computerPlayer.isRunning()) {
					m_computerPlayer.toggleAi();
				}
				m_computerPlayer = null;
				m_aiThread.interrupt();
				try {
					m_aiThread.join();
				} catch (InterruptedException e) {
					System.err.println("Error occured when waiting for ai"
							+ " thread to finish");
				}
			}
	 	} else if (event.getSource() == m_manyMistakes) {
	 		if (m_computerPlayer != null) {
				if (m_computerPlayer.isRunning()) {
					m_computerPlayer.toggleAi();
				}
			}
	 		
			m_computerPlayer = new Computer("AI", m_board, this,
					Computer.LOW_PROBABILITY);
			setTime();
			
			m_aiThread = new Thread(m_computerPlayer);
			m_aiThread.start();
			m_computerPlayer.toggleAi();
		} else if (event.getSource() == m_someMistakes) {
			if (m_computerPlayer != null) {
				if (m_computerPlayer.isRunning()) {
					m_computerPlayer.toggleAi();
				}
			}
			
			m_computerPlayer = new Computer("AI", m_board, this,
					Computer.NORMAL_PROBABILITY);
			setTime();
			
			m_aiThread = new Thread(m_computerPlayer);
			m_aiThread.start();
			m_computerPlayer.toggleAi();
		} else if (event.getSource() == m_noMistakes) {
			if (m_computerPlayer != null) {
				if (m_computerPlayer.isRunning()) {
					m_computerPlayer.toggleAi();
				}
			}
			
			m_computerPlayer = new Computer("AI", m_board, this,
					Computer.PERFECT_PROBABILITY);
			setTime();
			
			m_aiThread = new Thread(m_computerPlayer);
			m_aiThread.start();
			m_computerPlayer.toggleAi();
		} else if (event.getSource() == m_cannotLose) {
			if (m_computerPlayer != null) {
				if (m_computerPlayer.isRunning()) {
					m_computerPlayer.toggleAi();
				}
			}
			
			m_computerPlayer = new Computer("AI", m_board, this,
					Computer.CANNOT_LOSE);
			setTime();
			
			m_aiThread = new Thread(m_computerPlayer);
			m_aiThread.start();
			m_computerPlayer.toggleAi();
		} else if (event.getSource() == m_customIntelligence) {
			if (m_computerPlayer != null) {
				if (m_computerPlayer.isRunning()) {
					m_computerPlayer.toggleAi();
				}
			}
			
			int intelligence;
			
			try {
			intelligence = Integer.parseInt(JOptionPane.showInputDialog(
						"Enter AI intelligence, (Unintelligent) 1 to "
						+ "(Perfect) 100: "));
			} catch (NumberFormatException e) {
				intelligence = Computer.NORMAL_PROBABILITY;
			}
			
			m_computerPlayer = new Computer("AI", m_board, this, intelligence);
			setTime();
			
			m_aiThread = new Thread(m_computerPlayer);
			m_aiThread.start();
			m_computerPlayer.toggleAi();
		} else if (event.getSource() == m_exit) {
			
			System.exit(0);
			
		} else if (event.getSource() == m_about) {
			
			String author = "Author: Software Engineering Group 14\n";
			author += "Date created : 06/12/2015 \n";
			author += "Version : 2.0\n";
			author += "The game was part of an assignment and ";
			author += "is based on the famous game Minesweeper";
			JOptionPane.showMessageDialog(m_about,
											author,
											"About",
											JOptionPane.PLAIN_MESSAGE);
			
		} else if (event.getSource() == m_instructions) {
			
			JOptionPane.showMessageDialog(m_instructions,
											getInstructions(),
											"About",
											JOptionPane.PLAIN_MESSAGE);

		} else if (event.getSource() == m_GameFinshed) {
			reset();
		} else if (event.getSource() == m_loadSlot1) {
			//checks if file exists
			if (new File("SaveFile1.csv").isFile()) {
				//pass in slot number
				m_savedFile.loadFile(1);
				m_tick.stop();
				m_frame.dispose();
			}
		} else if (event.getSource() == m_loadSlot2) {
			//checks if file exists
			if (new File("SaveFile2.csv").isFile()) {
				//pass in slot number
				m_savedFile.loadFile(2);
				m_tick.stop();
				m_frame.dispose();
			}
		} else if (event.getSource() == m_loadSlot3) {
			//checks if file exists
			if (new File("SaveFile3.csv").isFile()) {
				//pass in slot number
				m_savedFile.loadFile(3);
				m_tick.stop();
				m_frame.dispose();
			}
		} else if (event.getSource() == m_saveSlot1) {
			//pass in slot number, board and player
			m_savedFile.saveFile(1,m_board,m_player);
		} else if (event.getSource() == m_saveSlot2) {
			//pass in slot number, board and player
			m_savedFile.saveFile(2,m_board,m_player);
		} else if (event.getSource() == m_saveSlot3) {
			//pass in slot number, board and player
			m_savedFile.saveFile(3,m_board,m_player);
		} else if (event.getSource() == m_revealMines) {
            if(!m_minesRevealed){
                m_board.showBombTile();
                m_panelGame.repaint();
                m_panelInfo.repaint();
                m_minesRevealed = !m_minesRevealed;
            }else if (m_minesRevealed){
                m_board.hideBombTile();    
                m_panelGame.repaint();
                m_panelInfo.repaint();
                m_minesRevealed = !m_minesRevealed;
            }
            
        }
	}
	
	/**
	 * Mutator method to set current instance as part of a test
	 */
	public void asTest() {
		m_test = true;
	}

	/**
	 * Called on mouse event
	 */
	public void mouseClicked(MouseEvent e) {
		if (!(m_board.getGameLost())) {
			int xPos = (int) Math.floor(e.getX() / Tile.WIDTH);
			int yPos = (int) Math.floor(e.getY() / Tile.HEIGHT);
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				// Work out the positions in the Array of the mouse click
				
				m_board.revealTile(xPos, yPos, this);
				m_panelGame.repaint();
				m_panelInfo.repaint();
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				
				m_board.defusedTile(xPos, yPos);
				m_panelGame.repaint();
				m_panelInfo.repaint();
			}
		}
		if (m_board.getGameLost()) {
			setGameLost();
		}
		if (m_board.getGameWon()) {
			setGameWin();
		}
	}
	
	/**
	 * Unused but included due to implements.
	 * @param e unused MouseEvent
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Unused but included due to implements.
	 * @param e unused MouseEvent
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Unused but included due to implements.
	 * @param e unused MouseEvent
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Unused but included due to implements.
	 * @param arg0 unused MouseEvent
	 */
	public void mouseReleased(MouseEvent arg0) {
	}
	
	
	/**
	 * Repaints Panels
	 */
	public void repaintAll() {
		m_panelGame.revalidate();
		m_panelGame.repaint();
		m_panelInfo.revalidate();
		m_panelInfo.repaint();
		m_frame.revalidate();
		m_frame.repaint();
		m_panelGame.updateUI();
	}
	
	/**
	 * Builds a JMenuBar with options 
	 * 
	 * @return - a JMenuBar Object
	 * @see Tony Gaddis and Godfrey Muganda, chapter 13.8,page 813 
	 * from "Starting out with Java from control structures 
	 * through data structures, 1st edition
	 */
	private JMenuBar myMenu() {
		JMenuBar menu = new JMenuBar();
		JMenu game = new JMenu("Game");
		
		m_newGame = new JMenuItem("New Game");
		m_newGame.addActionListener(this);
		m_settings = new JMenuItem("Settings");
		m_settings.addActionListener(this);
		m_revealMines = new JMenuItem("Reveal Mines");
		m_revealMines.addActionListener(this);
		m_exit = new JMenuItem("Exit");
		m_exit.addActionListener(this);
		
		m_playAutomatically = new JMenu("Play Automatically");
		m_stopAi = new JMenuItem("Stop");
		m_stopAi.addActionListener(this);
		m_manyMistakes = new JMenuItem("Low-Intelligence");
		m_manyMistakes.addActionListener(this);
		m_someMistakes = new JMenuItem("Normal Intelligence");
		m_someMistakes.addActionListener(this);
		m_noMistakes = new JMenuItem("High-Intelligence");
		m_noMistakes.addActionListener(this);
		m_cannotLose = new JMenuItem("Can't Lose (Cheating AI)");
		m_cannotLose.addActionListener(this);
		m_customIntelligence = new JMenuItem("Custom Intelligence");
		m_customIntelligence.addActionListener(this);
		
		m_playAutomatically.add(m_stopAi);
		m_playAutomatically.add(m_manyMistakes);
		m_playAutomatically.add(m_someMistakes);
		m_playAutomatically.add(m_noMistakes);
		m_playAutomatically.add(m_cannotLose);
		m_playAutomatically.add(m_customIntelligence);
		
		m_loadGame = new JMenu("Load Game");
		m_loadSlot1 = new JMenuItem("Slot 1");
		m_loadSlot1.addActionListener(this);
		m_loadSlot2 = new JMenuItem("Slot 2");
		m_loadSlot2.addActionListener(this);
		m_loadSlot3 = new JMenuItem("Slot 3");
		m_loadSlot3.addActionListener(this);
		
		m_saveGame = new JMenu("Save Game");
		m_saveGame.addActionListener(this);
		m_saveSlot1 = new JMenuItem("Slot 1");
		m_saveSlot1.addActionListener(this);
		m_saveSlot2 = new JMenuItem("Slot 2");
		m_saveSlot2.addActionListener(this);
		m_saveSlot3 = new JMenuItem("Slot 3");
		m_saveSlot3.addActionListener(this);
		
		m_loadGame.add(m_loadSlot1);
		m_loadGame.add(m_loadSlot2);
		m_loadGame.add(m_loadSlot3);
		
		m_saveGame.add(m_saveSlot1);
		m_saveGame.add(m_saveSlot2);
		m_saveGame.add(m_saveSlot3);
		
		game.add(m_newGame);
		game.add(m_saveGame);
		game.add(m_loadGame);
		game.add(m_settings);
		game.add(m_revealMines);
		game.add(m_playAutomatically);
		game.add(m_exit);
				
		JMenu help = new JMenu("Help");
		
		m_about = new JMenuItem("About");
		m_about.addActionListener(this);
		m_instructions = new JMenuItem("Instructions");
		m_instructions.addActionListener(this);
		
		help.add(m_about);
		help.add(m_instructions);
		menu.add(game);
		menu.add(help);
		
		return menu;

	}
	
	/**
	 * Resets the game so it can be replayed
	 */
	private void reset() {
		if (m_computerPlayer != null) {
			if (m_computerPlayer.isRunning()) {
				m_computerPlayer.toggleAi();
				m_computerPlayer = null;
				m_aiThread.interrupt();
				
				try {
					m_aiThread.join();
				} catch (InterruptedException e) {
					System.err.println("Error occured when waiting "
							+ "for ai thread to finish");
				}
			}
		}
		
		m_board.reset();
		m_GameFinshed.setVisible(false);
		m_panelGame.repaint();
		m_panelInfo.repaint();
		m_frame.repaint();
		m_secondsPlayed = 0;
		m_minutesPlayed = 0;
		m_hoursPlayed = 0;
		m_timePassed = null;
		try {
			m_won.stop();
		}
		catch (Exception e) {}
		m_won.flush();
		m_won.setFramePosition(0);
		m_bomb.stop();
		m_bomb.flush();
		m_bomb.setFramePosition(0);
		m_time.start();
		
		if (!m_test) {
			m_tick.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	/**
	 * Called when the game starts. Loads the JPanel
	 * and starts the players turn.
	 */
	private void startGame() {
		
		m_panelGame = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				m_board.render(g);
			}
		};

		m_panelGame.addMouseListener(this);
		m_panelGame.setBounds(0, 50, m_frame.getWidth(), m_frame.getHeight());
		m_frame.getContentPane().add(m_panelGame);
		m_frame.setJMenuBar(myMenu());
		
		m_frame.validate();
		m_frame.repaint();

		m_panelGame.repaint();
	}

	private MainMenu m_menu;
	private Player m_player;
	private Board m_board;
	private Computer m_computerPlayer;
	private Thread m_aiThread;
	private SavedFile m_savedFile;
	
	private JFrame m_frame;
	private JPanel m_panelGame;
	private JPanel m_panelInfo;
	private JButton m_GameFinshed;

	private Timer m_time;
	private long m_hoursPlayed;
	private long m_minutesPlayed;
	private long m_secondsPlayed;
	private String m_timePassed;
	private boolean m_minesRevealed = false;

	private JMenuItem m_newGame;
	private JMenuItem m_settings;
	private JMenuItem m_revealMines;
	private JMenuItem m_exit;
	private JMenuItem m_about;
	private JMenuItem m_instructions;
	
	private JMenu m_loadGame;
	private JMenuItem m_loadSlot1;
	private JMenuItem m_loadSlot2;
	private JMenuItem m_loadSlot3;
	
	private JMenu m_saveGame;
	private JMenuItem m_saveSlot1;
	private JMenuItem m_saveSlot2;
	private JMenuItem m_saveSlot3;
	
	private JMenu m_playAutomatically;
	private JMenuItem m_stopAi;
	private JMenuItem m_manyMistakes;
	private JMenuItem m_someMistakes;
	private JMenuItem m_noMistakes;
	private JMenuItem m_cannotLose;
	private JMenuItem m_customIntelligence;
	
	private Clip m_tick;
	private Clip m_bomb;
	private Clip m_won;
	
	private boolean m_test = false;
	
	private final int MIN_TIME = 0;
	private final int MAX_TIME = 10;
	private final int DOUBLE_DIGITS = 10;
	private final int BOARD_WIDTH = 315;
	private final int BOARD_HEIGHT = 400;
	private final int TIMER_DELAY = 1000;
	private final int SPACING = 8;

	private boolean m_loaded;
	
}
