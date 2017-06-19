/*
   File Name:   XOregio.java
   Name:        Ryan Kwan and Raghav Srinivasan
   Class:       ICS3U1-02(D)
   Date:        May 30, 2016
   Description: This program plays the XO Regio game, where there is a 4 X 4 board, and if an empty
					 square is clicked, that and all empty adjacent squares are filled with the player's symbol,
					 either an X or and O. This program also includes a menu to choose whether to face another user,
					 or the computer. There is then an end screen, with additional options as well.
*/
	
	import javax.swing.*;
   import java.awt.*;
   import java.awt.event.*;

   public class XOregio implements MouseListener
   {
      // board sizes, as determined by the size of the play screen
      final int MAXX = 420;
      final int MAXY = 480;
		// special messages for whose turn it is
		final String XGOES = "X's Turn"; 
		final String OGOES = "O's Turn";
		final String FACINGCOMPUTER = "Make Your Move";
      
      Drawing draw = new Drawing();
      int [][] board = new int [4][4];       // 0 = empty; 1 = X; 2 = O
      boolean xTurn = true;
      boolean win = false;
		boolean submit = false;
		boolean turn1 = true;                  // checking if it is turn 1, primarily for the exception case
      boolean againstComputer;               // if player is facing computer or not
      int gameState = 0;                     // 0 = menu, 1 = game, 2 = end screen
      
      // buttons on menu and end screen for selecting certain choices
      Rectangle button1 = new Rectangle(MAXX/2 - 100, MAXY/2 - 40, 200, 30);
      Rectangle button2 = new Rectangle(MAXX/2 - 100, MAXY/2, 200, 30);
      
      JLabel message = new JLabel("");
      ImageIcon[] boardPictures = new ImageIcon[3];
   
      public XOregio()      // constructor
      {
         for (int i = 0; i < boardPictures.length; i++)
         boardPictures[i] = new ImageIcon(i + ".jpg"); 
         JFrame frame = new JFrame("XOregio");
         frame.add(draw);
         draw.addMouseListener(this);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setSize(420, 480);
         message.setFont(new Font("Serif",Font.BOLD,20));
         message.setForeground(Color.blue);
         message.setHorizontalAlignment(SwingConstants.CENTER);
         frame.add(message, "South");
         frame.setVisible(true);
      }
     
	   // draws menu screen and its buttons (as indicated by its parameter, which is graphics, 
      // which are used to draw things in java)
      public void menu(Graphics g) 
      {
         Graphics2D g2D = (Graphics2D)g; // getting 2D graphics, in order to draw rectangle buttons
         
         // drawing title on menu screen
         Font menuFont = new Font ("arial", Font.BOLD, 35);
         g.setFont(menuFont);
         g.setColor(Color.orange);
         g.drawString ("XORegio Game", MAXX/2 - 100, 100);
         
         // setting font details for buttons and text inside it   
         Font buttonFont = new Font ("Serif", Font.BOLD, 20);
         g.setFont(buttonFont);
         g.setColor(Color.red);
			// drawing buttons and the text inside them
         g2D.draw(button1);
         g.drawString ("Player vs Computer", button1.x + 5, button1.y + 25);
         g2D.draw(button2);
         g.drawString ("Player vs Player", button2.x + 5, button2.y + 25);
      } // menu method
           		
      // Marks chosen square (as indicated by parameters row and col, which is the position on the board), 
      // and any adjacent empty squares.
      public void markBoard(int row, int col)
      {
   		int marker = 1;   // variable for setting positions in the board array to either 1 or 2, depending on xTurn
   		
   		if (!xTurn)       // if it is player X's turn, array elements are set to 1, if it is O's, elements are set to 2
   			marker = 2;
   		
         // block marks the chosen square as well as any open adjacent ones			
   		board[row][col] = marker;
         
   		for (int r = row - 1; r <= row + 1; r += 2) // loop marks the empty positions above and below the submission
   			if (r < board.length && r >= 0 && board[r][col] == 0) // checks if position is empty and valid
   				board[r][col] = marker;
               
   		for (int c = col - 1; c <= col + 1; c += 2) // loop marks the empty positions left and right of the submission
   			if (c < board.length && c >= 0 && board[row][c] == 0) // checks if position is empty and valid
   				board[row][c] = marker;
      } // markBoard
	
   
      // Checks if the board is full.  If it is, return true; otherwise, return false.
		// No formal parameters
      public boolean fullBoard()
      {
   		boolean full = true;
   		
         // checking to see if the gameboard has been completely filled with X's and O's
   		for (int r = 0; r < board.length; r++)
   			for (int c = 0; c < board[r].length; c++)
   				if (board[r][c] == 0)
   					full = false;
                  
   		return (full); // returns true if the board is full and false if it is not
      } // fullBoard
  
  
      // Updates game board and checks for win after a player has chosen a square 
      // (as indicated by parameters row and col).
		// Method also displays whose turn it is
      // choseSquare calls methods markBoard and fullBoard.
      public void choseSquare(int row, int col)
      {
   		if (board[row][col] == 0)        // selects a coordinate if it is empty
   		{
				markBoard(row, col);
				
				submit = true;                // identifies that a valid move has been submitted
      		
				if (againstComputer)
					message.setText(FACINGCOMPUTER);
					
            // displays a message depending on the turn   
				else
				{
	            if (xTurn)
	         		message.setText(OGOES);
						
	          	else 
	      		   message.setText(XGOES);
            }
				
            // changing the turn after each position submission
   			if (!fullBoard())
   			{
      			xTurn = !xTurn;
   			}
            
            // displays a win message depending on who won
   			else if (!againstComputer)
   			{										
	   		   if (xTurn)
	               message.setText("Player X wins! Press Anywhere to Continue!");
	            else
	               message.setText("Player O wins! Press Anywhere to Continue!");
   				win = true;
   			}
            
            if (!againstComputer)
               submit = false;
   
         } // if statement that only executes if the selected space is empty
      } // choseSquare
      
      /* 
         Method that plays for the computer, no formal parameter needed 
         Using a turn system of P1's first move is turn 1, P2's first move is turn 2, P1's second move is turn 3, etc.
         Player 2 
         On turn 2 (player 2's first turn), play the highest move possible (meaning fill as many squares as possible)
         On turn 4, player 2 must play a move that divides the board into an even number of sections, 
         and then play the move that does so while filling the most squares
         Note: P2 may achieve the above by playing the move that fills the highest number of squares
      */
      public void comPlayer2()
      {
         // counts and records how many squares every possible move will fill if selected (parallel array to board)
         int[][] emptyCount = new int[4][4];       
         
         // to find and store the option that fills the most number of squares
         int largest = 0;                
         
         // checks to see if the largest option has already been found          
         boolean largestFound = false;             
   		
   		if (!xTurn) // method only goes when it is Player O's turn
   		{
            // nested for loop structure is able to find how many squares are able to be filled by the computer if 
            // position [r][c] is chosen
   	      for (int r = 0; r < board.length; r++)
   	      {
   	         for (int c = 0; c < board.length; c++)
   	         {
   	            if (board[r][c] == 0)
   	            {
                     // adds one to the number of empty squares that will be filled if a move is submitted at (r, c)
   	               emptyCount[r][c]++; 
   	                  
                     // checking the adjacent squares to see if they're empty
                     // outer if statements to ensure that the method doesn't check for an element outside the array
   	               if (c > 0)
   	                  if (board[r][c - 1] == 0)
   	                     emptyCount[r][c]++;
   	               if (c < 3)      
   	                  if (board[r][c + 1] == 0)
   	                     emptyCount[r][c]++;
   	               if (r > 0)      
   	                  if (board[r - 1][c] == 0)
   	                     emptyCount[r][c]++;
   	               if (r < 3)
   	                  if (board[r + 1][c] == 0)
   	                     emptyCount[r][c]++;
   	            } // nested if structure
   	         } // nested for loop
   	      } // for loop
   	         
            // finds the square with the highest number of squares around it (including itself) that can be filled 
   	      for (int r = 0; r < board.length; r++)   
   	         for (int c = 0; c < board.length; c++)      
   	            if (emptyCount[r][c] > largest)            
   	               largest = emptyCount[r][c];             
   	            
            // submits moves
   	      for (int r = 0; r < board.length && !largestFound; r++)        // exits loop if computer makes its move  
   	      { 
   	         for (int c = 0; c < board.length && !largestFound; c++)     // exits loop if computer makes its move
   	         {   
                  // this if structure is the exception case, where playing the first largest move found can lead to a loss
                  // turn1 is required, otherwise if position (1, 2) is taken by player X, the computer will not respond
   	            if (board[3][3] == 1 && board[3][2] == 1 && board[2][3] == 1 && turn1 == true) 
   	            {
                     // manually submitting an alternate move that counters player 1 playing (3, 3), turn 1
   	               markBoard(1, 2);        
                     // identifies that the largest move has been found
                     largestFound = true;			
   	            }
   	            
                  // submits moves (regular moves)
                  // largestFound stops it from submitting another square that also has an empty count of largest
   	            if (emptyCount[r][c] == largest && (largestFound == false)) 
   	            {
   	               markBoard(r, c);           // submitting position (r, c) to the board
   	               largestFound = true;       // identifies that largest is found		
   	            }    					
   	         }      	
   	      }    
            // resetting booleans at the end of turn if game is still going
   			if (!fullBoard())
   			{
   	      	turn1 = false;
   				xTurn = true;
               submit = false;
   			}
            
            // if there is a win, displays winner
   			else
   			{
   				if (xTurn)
                  message.setText("You Win! Press Anywhere to Continue!");
               else
                  message.setText("You Lose! Press Anywhere to Continue!");
   				win = true;
   			}
   		} // if structure that only executes body if it is Player O's turn
      } // comPlayer2 method

		// draws end screen titles and buttons 
      // (as indicated by the parameter Graphics g, which is used to draw things in Java)
   	public void endScreen(Graphics g)
		{
			Graphics2D g2D = (Graphics2D) g;
         
			// setting the details of the end screen title font
			Font endFont = new Font ("arial", Font.BOLD, 35);
         g.setFont(endFont);
			g.setColor(Color.red);
			
			// displays a different message for each different outcome of the game
			if (againstComputer)
			{
				if (xTurn)
					g.drawString ("YOU WIN!", MAXX/2 - 110, 100);	
               
				else
					g.drawString ("YOU LOSE!", MAXX/2 - 110, 100);
			}
			else
			{
				if (xTurn)
					g.drawString ("PLAYER X WINS!", MAXX/2 - 110, 100);	
               
				else
					g.drawString ("PLAYER O WINS!", MAXX/2 - 110, 100);
			}
       	
			// setting the details of the font for the end screen button and button text
			Font buttonFont = new Font ("Serif", Font.BOLD, 20);
         g.setFont(buttonFont);
			g.setColor(Color.blue);
			
			// drawing buttons and text inside buttons
		 	g2D.draw(button1);
         g.drawString ("Replay", button1.x + 10, button1.y + 25);
         g2D.draw(button2);
         g.drawString ("Main Menu", button2.x + 10, button2.y + 25);

		} // endScreen method
   
      class Drawing extends JComponent
      {
			// utilizes graphics to draw the gameboard and the objects outlined in the menu and endScreen methods
			// uses Graphics g as its formal parameter, showing that it utilizes graphics to draw certain objects
         public void paint(Graphics g)
         {
            if (gameState == 0) // draws menu
					menu(g);
               
				else if (gameState == 1)
            {
               // draw the content of the board
               for (int row = 0; row < 4; row++)
                  for (int col = 0; col < 4; col++)
                     g.drawImage(boardPictures[board[row][col]].getImage(),col * 100, row * 100,100,100,this);
               // draw grid
               g.fillRect(100,5,5,395);
               g.fillRect(200,5,5,395);
               g.fillRect(300,5,5,395);
            
               g.fillRect(5,100,395,5);
               g.fillRect(5,200,395,5);
               g.fillRect(5,300,395,5);
            }
            
            else // draws end screen
            {
              endScreen(g); 
            }
         } // paint method
      }
   
      // --> starting implementing MouseListener - it has 5 methods 
      public void mousePressed(MouseEvent e)
      {
      }
      
		// Obtains all the mouse input used in this program, in order for the user to interact with the game
		// MouseEvent e is the formal parameter, which is the release of the mouse click by the user
      public void mouseReleased(MouseEvent e)
      {
			// obtaining coordinates of input
			int mouseX = e.getX();
         int mouseY = e.getY();
			
			// mouse input for various game states
			if (gameState == 0) // obtaining mouse input for menu screen choices
			{
            if (mouseX >= MAXX/2 - 100 && mouseX <= MAXX/2 + 100) // checking x-coordinates of button
            {
               if (mouseY >= MAXY/2 - 40 && mouseY <= MAXY/2 - 10) // checking y-coordinates
               {
                  // User selects Player vs Computer mode
                  gameState = 1;
                  againstComputer = true;
                  draw.repaint();
                  message.setText(FACINGCOMPUTER);
               }
               
               else if (mouseY >= MAXY/2 && mouseY <= MAXY/2 + 30) // checking y-coordinates
               {
                  // User selects Player vs Player mode
                  gameState = 1;
                  againstComputer = false;
                  draw.repaint();
                  message.setText(XGOES);
               }
            }
	
			}
         
         else if (gameState == 1) // obtaining mouse input for game
         {
            if (!win) // mouse input while game is going
            {
               // find row and column of mouse click
               int row = mouseY/100;
               int col = mouseX/100;
               // handles the move that the player has made on the game board
               choseSquare(row, col);
               
               // call the comPlayer2 method for the computer to play as player 2 (player O) after a mouseclick is released
               if (againstComputer)
                  comPlayer2(); 

               // get paint to be called to reflect your mouse click
               draw.repaint();
            }
   			else // mouse input after game has been decided, so pressing anywhere will allow to go to the end screen
   			{
					// resetting the board for any subsequent games the user wants to play
   				for (int r = 0; r < board.length; r++)
      				for (int c = 0; c < board[r].length; c++)
      					board[r][c] = 0;	
   				turn1 = true;
               win = false;   // resetting the win condition to false so that a win can be achieved in subsequent games
					gameState = 2; // allows program to go to end screen
   				draw.repaint();
					
   				message.setText(""); // getting rid of the message at the bottom of the frame
   				
   			}
         }
         
         // obtaining mouse input on end screen, for player-selected options
         else
         {  
            if (mouseX >= MAXX/2 - 100 && mouseX <= MAXX/2 + 100)       // checking x-coordinates of button
            {
               if (mouseY >= MAXY/2 - 40 && mouseY <= MAXY/2 - 10)      // checking y-coordinates
               {
                  // Replay is selected
                  gameState = 1; // back to game
						xTurn = true;
                  draw.repaint();
						
						// custom messages depending on if user is playing vs computer or vs another player
						if (againstComputer)
							message.setText(FACINGCOMPUTER);
						else
							message.setText(XGOES);
               }
               
               else if (mouseY >= MAXY/2 && mouseY <= MAXY/2 + 30) // checking y-coordinates
               {
                  // back to Main menu
                  gameState = 0;
						xTurn = true;
                  draw.repaint();
               }
            }
         }
      } // mouseReleased method
   
      public void mouseClicked(MouseEvent e)
      {
      }
   
      public void mouseEntered(MouseEvent e)
      {
      }
   
      public void mouseExited(MouseEvent e)
      {
      }
      // finishing implementing MouseListener  <---
      public static void main(String[] args)
      {
      	new XOregio();
      }
   } // XOregio class