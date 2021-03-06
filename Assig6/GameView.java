import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.text.SimpleDateFormat;
import java.util.Date;

//View class for a card game.
class View
{
   private static final int MAX_IMAGES_IN_DECK = 56 / 5;
   private static final int HUMAN_PLAYER = Model.HUMAN_PLAYER;
   private static final int COMPUTER_PLAYER = Model.COMPUTER_PLAYER;
   private static int NUM_CARD_STACKS = Model.NUM_CARD_STACKS;
   private static int NUM_CARDS_PER_HAND = Model.NUM_CARDS_PER_HAND;
   private static int NUM_PLAYERS = Model.NUM_PLAYERS;
   private JLabel[] addToStackLabel = new JLabel[NUM_CARD_STACKS];
   private JButton cantPlayButton;
   private static JButton startTimerButton;
   private JLabel[] computerHandLabels = new JLabel[NUM_CARDS_PER_HAND];
   private JButton[] playerHandButtons = new JButton[NUM_CARDS_PER_HAND];
   private JLabel[] playerScoreLabels = new JLabel[NUM_PLAYERS];
   private JLabel[] deckLabel = new JLabel[MAX_IMAGES_IN_DECK];
   private JLabel deckCountLabel;
   private CardTable myCardTable;
   private Thread t = new Thread(new View.Timer());
   private static boolean clockRun = false;
   private static final SimpleDateFormat date = new SimpleDateFormat("mm:ss");
   private static long clockCounter = 0;
   private static JLabel clock = new JLabel();

   // Constructor sets up the table.
   public View()
   {
      // Create the table.
      myCardTable = new CardTable("CardGame", NUM_CARDS_PER_HAND, NUM_PLAYERS);
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      Insets insets = myCardTable.pnlPlayArea.getInsets();
      int offset = -30;

      //Stacks
      addToStackLabel[0] = new JLabel();
      addToStackLabel[0].setName("Play Area Card Stack");
      myCardTable.pnlPlayArea.add(addToStackLabel[0]);
      addToStackLabel[0].setBounds(160 + offset + insets.left, 50 + insets.top, 100, 120);

      addToStackLabel[1] = new JLabel();
      addToStackLabel[1].setName("Play Area Card Stack");
      myCardTable.pnlPlayArea.add(addToStackLabel[1]);
      addToStackLabel[1].setBounds(330 + offset + insets.left, 50 + insets.top, 100, 120);

      //Deck images
      for(int i = 0; i < MAX_IMAGES_IN_DECK; i++)
      {
         deckLabel[i] = new JLabel();
         deckLabel[i].setName("Playing Deck " + i);
         myCardTable.pnlPlayArea.add(deckLabel[i]);
         deckLabel[i].setBounds(245 + offset + insets.left - (i / 2), 50 + insets.top - i, 100, 120);
      }
      
      //Deck count
      deckCountLabel = new JLabel("0");
      deckCountLabel.setName("Deck Count");
      myCardTable.pnlPlayArea.add(deckCountLabel);
      deckCountLabel.setBounds(277 + offset + insets.left, 155 + insets.top, 150, 20);
      
      //Buttons
      cantPlayButton = new JButton("I Cannot Play");
      Dimension size = cantPlayButton.getPreferredSize();
      cantPlayButton.setBounds(200 + offset + insets.left, 200 + insets.top, size.width + 56, size.height);
      cantPlayButton.setActionCommand("I Cannot Play");
      cantPlayButton.setVisible(true);
      myCardTable.pnlPlayArea.add(cantPlayButton);

      startTimerButton = new JButton("Click to Start Timer");
      size = startTimerButton.getPreferredSize();
      startTimerButton.setBounds(450 + offset + insets.left, 200 + insets.top, size.width + 56, size.height);
      startTimerButton.setActionCommand("Start Timer");
      startTimerButton.setVisible(true);
      myCardTable.pnlPlayArea.add(startTimerButton);

      // Player Hands
      for (int i = 0; i < NUM_CARDS_PER_HAND; i++)
      {
         playerHandButtons[i] = new JButton();
         playerHandButtons[i].setVisible(true);
         playerHandButtons[i].setActionCommand("Human Player Card");
         playerHandButtons[i].setBorderPainted(false);
         myCardTable.pnlHumanHand.add(playerHandButtons[i]);

         computerHandLabels[i] = new JLabel();
         computerHandLabels[i].setVisible(true);
         myCardTable.pnlComputerHand.add(computerHandLabels[i]);
      }

      // Player Scores
      playerScoreLabels[0] = new JLabel("Score:");
      playerScoreLabels[0].setBounds(160 + offset + insets.left, 10 + insets.top, 200, 20);
      myCardTable.pnlPlayArea.add(playerScoreLabels[0]);

      playerScoreLabels[1] = new JLabel("Score:");
      playerScoreLabels[1].setBounds(300 + offset + insets.left, 10 + insets.top, 200, 20);
      myCardTable.pnlPlayArea.add(playerScoreLabels[1]);

      Date elapsed = new Date(clockCounter * 1000);
      clock.setFont(clock.getFont().deriveFont(40.0f));
      clock.setText(" " + date.format(elapsed));
      clock.setBounds(480 + offset + insets.left, 110 + insets.top, 130, 50);
      Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
      clock.setBorder(border);
      clock.setBackground(Color.WHITE);
      clock.setOpaque(true);

      myCardTable.pnlPlayArea.add(clock);
   }//End Constructor

   // Adds the controller events to the buttons and stacks.
   public void addController(Controller controller)
   {
      for (int i = 0; i < NUM_CARD_STACKS; i++)
         addToStackLabel[i].addMouseListener(controller);
      
      cantPlayButton.addActionListener(controller);
      startTimerButton.addActionListener(controller);

      for (int i = 0; i < NUM_CARDS_PER_HAND; i++)
         playerHandButtons[i].addActionListener(controller);
   }
   
   // Loads the end game prompt for the player.
   public int loadEndGamePrompt(int[] playerScores)
   {
      //Give player end game prompt message.
      String endMessage;
      if (playerScores[COMPUTER_PLAYER] > playerScores[HUMAN_PLAYER])
         endMessage = "Computer wins!\nYour score was: " + playerScores[HUMAN_PLAYER] + "\nComputer's Score was: "
               + playerScores[COMPUTER_PLAYER];
      else if (playerScores[COMPUTER_PLAYER] < playerScores[HUMAN_PLAYER])
         endMessage = "You win!\nYour score was: " + playerScores[HUMAN_PLAYER] + "\nComputer's Score was: "
               + playerScores[COMPUTER_PLAYER];
      else
         endMessage = "Tie!\nYour score was: " + playerScores[HUMAN_PLAYER] + "\nComputer's Score was: "
               + playerScores[COMPUTER_PLAYER];
      endMessage = endMessage + "\nDo you want to play again?";
      
      return (JOptionPane.showConfirmDialog(null, endMessage, "Game Over", JOptionPane.YES_NO_OPTION));
   }
   
   // Sets the card table's visibility.
   public void setVisible(boolean visible)
   {
      myCardTable.setVisible(visible);
   }

   //Sets the image for the deck using a supplied icon.
   public void setDeckImage(Icon icon)
   {
      for(int i = 0; i < deckLabel.length; i++)
         deckLabel[i].setIcon((ImageIcon) icon);
   }
   
   //Updates the deck count and images using a supplied count.
   public void updateDeckCount(int count)
   {
      if(count == 0) 
      {
         deckCountLabel.setVisible(false);
         for(int i = 0; i < deckLabel.length; i++)
            deckLabel[i].setVisible(false);
      }
      else
      {
         deckCountLabel.setText("" + count);
         deckCountLabel.setVisible(true);
         
         //Gives deck a diminished look as the count goes lower.
         for(int i = 0; i < deckLabel.length; i++)
         {
            if(i * 5 > count)
               deckLabel[i].setVisible(false);
            else
               deckLabel[i].setVisible(true);
         }
      }
   }
   
   // Updates the display of the hand icons for a specific player.
   public boolean updateHand(Icon[] icons, int playerIndex)
   {
      // Sets unused buttons to invisible
      if (playerIndex == HUMAN_PLAYER && icons.length < playerHandButtons.length)
         for (int i = icons.length; i < playerHandButtons.length; i++)
            playerHandButtons[i].setVisible(false);

      if (playerIndex == COMPUTER_PLAYER && icons.length < computerHandLabels.length)
         for (int i = icons.length; i < computerHandLabels.length; i++)
            computerHandLabels[i].setVisible(false);

      // Sets the icons for the hands depending on if it is a player or computer.
      for (int i = 0; i < icons.length; i++)
      {
         if (playerIndex == HUMAN_PLAYER && i < playerHandButtons.length)
         {
            if (playerHandButtons[i].getIcon() != (ImageIcon) icons[i])
               playerHandButtons[i].setIcon((ImageIcon) icons[i]);
            if (!playerHandButtons[i].isVisible())
               playerHandButtons[i].setVisible(true);
         } 
         else if (playerIndex == COMPUTER_PLAYER && i < computerHandLabels.length)
         {
            if(computerHandLabels[i].getIcon() == null) 
               computerHandLabels[i].setIcon((ImageIcon) icons[i]);
            if (!computerHandLabels[i].isVisible())
               computerHandLabels[i].setVisible(true);
         } 
         else
            return false;
      }
      update();
      return true;
   }//End UpdateHand

   // Changes the display for the stack icons.
   public boolean updateStack(Icon[] icon)
   {
      if (icon.length != NUM_CARD_STACKS)
         return false;

      for (int i = 0; i < NUM_CARD_STACKS; i++)
         addToStackLabel[i].setIcon((ImageIcon) icon[i]);
      return true;
   }

   // Updates the score on the display.
   public boolean updateScore(int score, int playerIndex)
   {
      if (playerIndex > playerScoreLabels.length)
         return false;
      else if (playerIndex == 0)
         playerScoreLabels[COMPUTER_PLAYER].setText("Computer Score: " + score);
      else
         playerScoreLabels[playerIndex].setText("Player " + playerIndex + " Score: " + score);

      return true;
   }

   // Highlights a selected button green and unhighlights any other button.
   public void updateSelectedButton(int index)
   {
      for (int i = 0; i < playerHandButtons.length; i++)
      {
         if (i == index)
            playerHandButtons[i].setBackground(Color.GREEN);
         else
            playerHandButtons[i].setBackground(null);
      }
   }

   // Updates the display.
   private void update()
   {
      myCardTable.revalidate();
      myCardTable.repaint();
   }
   
   // Returns the index of a button component in the player's hand.
   public int getPlayerButtonIndex(JButton button)
   {
      for (int i = 0; i < myCardTable.pnlHumanHand.getComponentCount(); i++)
      {
         if (button == myCardTable.pnlHumanHand.getComponent(i))
            return i;
      }
      return -1;
   }
   
   // Gets the component index of a stack label from the play area.
   public int getStackIndex(JLabel stack)
   {
      for (int i = 0; i < myCardTable.pnlPlayArea.getComponentCount(); i++)
      {
         if (stack == myCardTable.pnlPlayArea.getComponent(i))
            return i;
      }
      return -1;
   }

   //Start or Stop the timer.
   public void startTimer()
   {
      if (t.isAlive())
      {
         t = new Thread(new View.Timer());
         clockRun = false;
      } 
      else
      {
         t.start();
         clockRun = true;
      }
   }

   //Resets the timer to 0.
   public void resetTimer()
   {
      clockCounter = 0;
      clock.setText(" " + date.format(clockCounter));
   }
   
   // class for clock in game
   static class Timer extends Thread
   {
      public void run()
      {
         // long clockCounter = 0;
         while (View.clockRun == true)
         {
            clockCounter++;
            Date elapsed = new Date(clockCounter * 1000);
            // add the date to a label change test to whatever the label name is
            clock.setText(" " + date.format(elapsed));
            doNothing(1000);
            // System.out.println(clockCounter);
            startTimerButton.setText("Click to Stop Timer");

         }
         clockCounter--;
         startTimerButton.setText("Click to Restart Timer");
      }

      public void doNothing(int milliseconds)
      {
         try
         {
            Thread.sleep(milliseconds);
         } catch (InterruptedException e)
         {
            System.out.println("Unexpected interrupt.");
            System.exit(0);
         }
      }
   }//End Timer Class
}

// CardTable - Class represents the GUI for a card table.
class CardTable extends JFrame
{
   static int MAX_CARDS_PER_HAND = 56;
   static int MAX_PLAYERS = 2;

   private int numCardsPerHand;
   private int numPlayers;

   public JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea;

   // Constructor
   // Takes the title of the window, number of cards per hand, and the number of
   // players.
   CardTable(String title, int numCardsPerHand, int numPlayers)
   {
      setTitle(title);

      // Validate and set the two instance variables.
      if (numCardsPerHand > 0 && numCardsPerHand <= MAX_CARDS_PER_HAND)
         this.numCardsPerHand = numCardsPerHand;
      else
         this.numCardsPerHand = 1;

      if (numPlayers > 0 && numPlayers <= MAX_PLAYERS)
         this.numPlayers = numPlayers;
      else
         this.numPlayers = 1;

      // Create the panel layout and add the 3 panels to the frame.
      setLayout(new GridBagLayout());
      GridBagConstraints gridConstraints = new GridBagConstraints();
      gridConstraints.fill = GridBagConstraints.BOTH;
      gridConstraints.weightx = 1.0;

      // Computer Hand
      pnlComputerHand = new JPanel();
      pnlComputerHand.setBorder(BorderFactory.createTitledBorder("Computer Hand"));
      gridConstraints.weighty = 0.0;
      gridConstraints.gridy = 0;
      add(pnlComputerHand, gridConstraints);

      // Playing Area
      pnlPlayArea = new JPanel();
      pnlPlayArea.setBorder(BorderFactory.createTitledBorder("Playing Area"));
      gridConstraints.weighty = 1.0;
      gridConstraints.gridy = 1;
      add(pnlPlayArea, gridConstraints);

      // Player's Hand
      pnlHumanHand = new JPanel();
      pnlHumanHand.setBorder(BorderFactory.createTitledBorder("Your Hand"));
      gridConstraints.weighty = 0.0;
      gridConstraints.gridy = 2;
      add(pnlHumanHand, gridConstraints);

      // CREATE THE PLAY AREA.
      // Create a grid layout for the play and card areas.
      // FlowLayout playAreaFlow = new FlowLayout();
      // pnlPlayArea.setLayout(playAreaFlow);
      pnlPlayArea.setLayout(null);

      GridLayout cardAreaGrid = new GridLayout(1, MAX_CARDS_PER_HAND);
      pnlComputerHand.setLayout(cardAreaGrid);
      pnlHumanHand.setLayout(cardAreaGrid);
   }// End CardTable Constructor

   // Accessors
   public int getCardsPerHand()
   {
      return numCardsPerHand;
   }

   public int getNumberOfPlayers()
   {
      return numPlayers;
   }

} // End Card Table Class.
