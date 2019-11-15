import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;


class GameStart {
	gameEnd end = new gameEnd();
	Canvas canvas ;
	GameStart(JFrame frm, Container ct){
		frm.setSize(640,480);
	    canvas = new Canvas(frm, ct, end);
	    ct.add(canvas);
	    ct.addMouseMotionListener(canvas);
	    ct.addMouseListener(canvas);
	    
	    frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frm.setVisible(true);
	}
}


class Canvas extends JComponent implements Runnable, MouseListener, MouseMotionListener{
	
	Thread t;
	Frame frm;
	Container ct;
	gameEnd end;
	 
	Ball ball = new Ball();
	Board board = new Board();
	Block block = new Block();
	
	private boolean ballRun = true;                       //球是否在跑
	private boolean gameStart = false;                    //遊戲是否開始
	private boolean gameWin = false;
	int mouseX = 270;                                   //滑鼠位置
	int[][][] map;                                       //紀錄方塊位置
	                                                     //map[][][0] = 1表示方塊的起始座標
	                                                     //map[][][1] != 0表示同個數字所屬同樣的方塊
	
	Canvas(Frame frm, Container ct, gameEnd end){        //建構方法
		this.frm = frm;
		this.ct = ct;
		this.end = end;
		map =  new int [frm.getSize().width][frm.getSize().height][2];
		createMap();
	}
	class Ball{
		
		double ballX = 310, ballY = 339;              //球最初座標
		double yDir = -1,xDir = -0.5;                 //正值表示球在往下、往右移動
		static final double dy = 1, dx = 1;           //移動大小
		static final int ballSize = 20;               //球大小
		
		void reBound(Graphics g, Canvas c) {                     //反彈
			if(ballY > (c.getSize().height - ballSize)) {        //球觸底gameOver
				end.gameOver(g, false);
				ballRun = false;
			}
			if(gameWin) {                                        //遊戲勝利
				end.gameOver(g, true);
			}
		//碰觸到方塊反彈+消除方塊
			if((map[(int)ballX+ballSize/2][(int)ballY][1] != 0)) {          //球的最上方碰到方塊
				block.deleteBlock((int)ballX+ballSize/2,(int)ballY, g);     //消除方塊
				yDir = -yDir;
				if(!block.checkHasBlock()) {                        //沒有方塊了就勝利
					gameWin = true;
				    ballRun = false;
			    }
			}
		    if(map[(int)ballX+ballSize/2][(int)ballY+ballSize][1] != 0 ) {        //球的最下方碰到方塊
		    	block.deleteBlock((int)ballX+ballSize/2,(int)ballY+ballSize, g);  //消除方塊
				yDir = -yDir;
				if(!block.checkHasBlock()) {                        //沒有方塊了就勝利
					gameWin = true;
				    ballRun = false;
			    }
			}
			if((map[(int)ballX+ballSize][(int)ballY+ballSize/2][1] != 0)) {       //球的最右方碰到方塊
				block.deleteBlock((int)ballX+ballSize,(int)ballY+ballSize/2, g);  //消除方塊
				xDir = -xDir;
				if(!block.checkHasBlock()) {                        //沒有方塊了就勝利
					gameWin = true;
				    ballRun = false;
			    }
			}
			if( map[(int)ballX][(int)ballY+ballSize/2][1] != 0 ) {          //球的最左方碰到方塊
				block.deleteBlock((int)ballX,(int)ballY+10, g);     //消除方塊
				xDir = -xDir;
				if(!block.checkHasBlock()) {                        //沒有方塊了就勝利
					gameWin = true;
				    ballRun = false;
			    }
			}
			
			if(ballY <= 0) {                                     //球觸頂反彈
				yDir = -yDir;
			}
			
			if(ballX <= 0) {                                     //球觸左反彈
				xDir = -xDir;
			}
			if(ballX > (c.getSize().width - ballSize)) {         //球觸右反彈
				xDir = -xDir;
				
			}
			if((ballX+ballSize/2)>=(board.boardX)&&(ballX+ballSize/2)<=(board.boardX+board.boardWidth))
				if((ballY+ballSize)==(board.boardY)) {
					//球碰到board反彈
					yDir = -yDir;
				}
				
			
			ballY += dy * yDir;                               //反彈球的y位置
			ballX += dx * xDir;                               //反彈球的x位置
		}
		void paintBall(Graphics g, Canvas c) {                              //畫球
			reBound(g, c);                                                  //檢測是否需要反彈
			g.setColor(Color.blue);                                         //顏色  
			g.fillOval((int)ballX, (int)ballY, ballSize, ballSize);         //繪製反彈球
			g.setColor(Color.black);                                         //顏色  
			g.drawOval((int)ballX, (int)ballY, ballSize, ballSize);         //繪製邊框
		}
		void paintBallNotMoved(Graphics g) {                                 //遊戲開始前，畫一個固定的球
			g.setColor(Color.blue);
			ballX = mouseX + (board.boardWidth / 2  -ballSize / 2);
			g.fillOval((int)ballX, (int)ballY, ballSize, ballSize);         //繪製反彈球
			g.setColor(Color.black);                                         //顏色  
			g.drawOval((int)ballX, (int)ballY, ballSize, ballSize);         //繪製邊框
		}
	}
	
	class Board{
		
		int boardX = 270, boardY = 360 ;
		final int boardWidth = 100,boardHeight = 10;
		                                                    //球拍長、寬、座標      
		void paintBoard(int mouseX, Graphics g) {                        //畫球拍
			boardX = mouseX;                                             //追蹤滑鼠的x座標
			g.setColor(Color.orange);                                    //顏色  
			g.fillRect(boardX, boardY, boardWidth, boardHeight);         //繪製球拍
			g.setColor(Color.black);
			g.drawRect(boardX, boardY, boardWidth, boardHeight);
		}
	}
	class Block{
		static final int blockWidth = 15,blockHeight = 15;               //設定方塊大小
		
		void paintBlock(Graphics g, int blockX, int blockY){             //繪製方塊
			g.setColor(Color.red);
			g.fillRect(blockX, blockY, blockWidth, blockHeight); 
			g.setColor(Color.black);
			g.drawRect(blockX, blockY, blockWidth, blockHeight);
		}
		void deleteBlock(int blockX, int blockY, Graphics g) {                       //刪除某一個方塊
			int blocknum = map[blockX][blockY][1];
			for(int x = 0;x < ct.getWidth(); x++) {
			    for(int y = 0;y < ct.getHeight(); y++) { 
			    	if(map[x][y][1] == blocknum) {
			    		map[x][y][1] = 0;
			    		map[x][y][0] = 0;
			    	}
			    }	
			}
		}
		boolean checkHasBlock() {
			
			for(int x = 0;x < ct.getWidth(); x++) {
			    for(int y = 0;y < ct.getHeight(); y++) { 
			    	if (map[x][y][0] == 1)
			    		return true;
			    }
			}
			return false;
		}
	}
	private void createMap() {                                          //創建地圖，隨機創建
		
		Random ran = new Random();        
		int blockLimit = 80;                                             //製作70個方塊
		int blockX, blockY;
		outloop : for(int i = 0; i < blockLimit; i++) {                  //檢查方塊座標上是否有重疊方塊
			
			blockX = ran.nextInt(frm.getSize().width - 32);              //有就重新random一個
		    blockY = ran.nextInt(frm.getSize().height/3 - 1);
		    if(map[blockX][blockY][1] == 0) {
		    	for (int x = 1; x < 15; x++) {
		    		for (int y = 1; y < 15; y++) {
		    			if(map[blockX + x][blockY + y][1] != 0) {
		    				i--;
		    				continue outloop;                               //重新random一個
		    			}	
		    		}
		    	}
		    	map[blockX][blockY][0] = 1;                                 //紀錄方塊起始座標
		    	map[blockX][blockY][1] = i+1;
		    	for (int x = 1; x < 15; x++) {                              //紀錄方塊所屬編號
		    		for (int y = 1; y < 15; y++) {
		    			map[blockX + x][blockY + y][1] = i+1;
		    		}
		    	}
		    }
		    else {
		    	i--;
		    	continue;
		    }
		}	
	}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {                                //滑鼠移動到哪裡，就追蹤到哪裡
		mouseX = e.getX();
		if(mouseX > 529)
			mouseX = 529;
		if(ballRun)                                                       //如果ballRun == false，代表球已停止，遊戲結束
		    repaint();
	}
	public void mouseClicked(MouseEvent e) {                              //點擊滑鼠表示開始
		gameStart = true;
		t = new Thread(this);
		t.start();
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void run() {                                                    //Thread，每動一次，睡3毫秒
		while(ballRun) {  
			repaint();                                                     //重做paint()
			try {
				Thread.sleep(1);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void paint(Graphics g) {
		if(gameStart)                                                     //遊戲開始，畫會移動的球
		    ball.paintBall(g, this);
		else {                                                            //遊戲尚未開始，球不動
			ball.paintBallNotMoved(g);
		}
		for(int x = 0;x < ct.getWidth(); x++) {
			for(int y = 0;y < ct.getHeight(); y++) {                      //偵測map[][][0]起始座標，畫方塊
				if(map[x][y][0] == 1)
				    block.paintBlock(g, x, y);
			}
		}
		board.paintBoard(mouseX, g);	                                  //畫球拍
	}
}
class gameEnd{
	
	void gameOver(Graphics g, boolean win) {
		//System.out.println(win);
		if(win) {
			g.setColor(Color.blue);
		    g.setFont(new Font("Arial", Font.BOLD, 40));
		    g.drawString("Victory", 250,200);
		}
		else {
			g.setColor(Color.red);
		    g.setFont(new Font("Arial", Font.BOLD, 40));
		    g.drawString("Fail", 250,200);
		}
	}
}
//main class

public class bouncing_ball {
	static MenuBar mb = new MenuBar();                    //創建MenuBar與其下面的物件
	static Menu menu = new Menu("功能");
	static MenuItem mI1 = new MenuItem("重新開始");
	static MenuItem mI2 = new MenuItem("結束");
	static JFrame frm;
	static Container ct;
	
	static class myListener implements ActionListener{     //重新開始、結束的方法
		public void actionPerformed(ActionEvent e) {
			MenuItem mI = (MenuItem)e.getSource();
			if(mI == mI1) {
				frm.dispose();                             //關閉舊frm
				createNewFrame();
				new GameStart(frm, ct);
			}
			if(mI == mI2) {
				frm.dispose();
			}
		}
	}
	static void createNewFrame() {                       //創建新的Frame
		frm = new JFrame("Bouncing Ball");
		ct = frm.getContentPane();
		mb.add(menu);
        menu.add(mI1);
		menu.add(mI2);
		mI1.addActionListener(new myListener());
		mI2.addActionListener(new myListener());
		frm.setMenuBar(mb);
	}
	public static void main(String[] args) {
		
		createNewFrame();
	    new GameStart(frm, ct);
	    
	}

}
