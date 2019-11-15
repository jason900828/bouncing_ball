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
	
	private boolean ballRun = true;                       //�y�O�_�b�]
	private boolean gameStart = false;                    //�C���O�_�}�l
	private boolean gameWin = false;
	int mouseX = 270;                                   //�ƹ���m
	int[][][] map;                                       //���������m
	                                                     //map[][][0] = 1��ܤ�����_�l�y��
	                                                     //map[][][1] != 0��ܦP�ӼƦr���ݦP�˪����
	
	Canvas(Frame frm, Container ct, gameEnd end){        //�غc��k
		this.frm = frm;
		this.ct = ct;
		this.end = end;
		map =  new int [frm.getSize().width][frm.getSize().height][2];
		createMap();
	}
	class Ball{
		
		double ballX = 310, ballY = 339;              //�y�̪�y��
		double yDir = -1,xDir = -0.5;                 //���Ȫ�ܲy�b���U�B���k����
		static final double dy = 1, dx = 1;           //���ʤj�p
		static final int ballSize = 20;               //�y�j�p
		
		void reBound(Graphics g, Canvas c) {                     //�ϼu
			if(ballY > (c.getSize().height - ballSize)) {        //�yĲ��gameOver
				end.gameOver(g, false);
				ballRun = false;
			}
			if(gameWin) {                                        //�C���ӧQ
				end.gameOver(g, true);
			}
		//�IĲ�����ϼu+�������
			if((map[(int)ballX+ballSize/2][(int)ballY][1] != 0)) {          //�y���̤W��I����
				block.deleteBlock((int)ballX+ballSize/2,(int)ballY, g);     //�������
				yDir = -yDir;
				if(!block.checkHasBlock()) {                        //�S������F�N�ӧQ
					gameWin = true;
				    ballRun = false;
			    }
			}
		    if(map[(int)ballX+ballSize/2][(int)ballY+ballSize][1] != 0 ) {        //�y���̤U��I����
		    	block.deleteBlock((int)ballX+ballSize/2,(int)ballY+ballSize, g);  //�������
				yDir = -yDir;
				if(!block.checkHasBlock()) {                        //�S������F�N�ӧQ
					gameWin = true;
				    ballRun = false;
			    }
			}
			if((map[(int)ballX+ballSize][(int)ballY+ballSize/2][1] != 0)) {       //�y���̥k��I����
				block.deleteBlock((int)ballX+ballSize,(int)ballY+ballSize/2, g);  //�������
				xDir = -xDir;
				if(!block.checkHasBlock()) {                        //�S������F�N�ӧQ
					gameWin = true;
				    ballRun = false;
			    }
			}
			if( map[(int)ballX][(int)ballY+ballSize/2][1] != 0 ) {          //�y���̥���I����
				block.deleteBlock((int)ballX,(int)ballY+10, g);     //�������
				xDir = -xDir;
				if(!block.checkHasBlock()) {                        //�S������F�N�ӧQ
					gameWin = true;
				    ballRun = false;
			    }
			}
			
			if(ballY <= 0) {                                     //�yĲ���ϼu
				yDir = -yDir;
			}
			
			if(ballX <= 0) {                                     //�yĲ���ϼu
				xDir = -xDir;
			}
			if(ballX > (c.getSize().width - ballSize)) {         //�yĲ�k�ϼu
				xDir = -xDir;
				
			}
			if((ballX+ballSize/2)>=(board.boardX)&&(ballX+ballSize/2)<=(board.boardX+board.boardWidth))
				if((ballY+ballSize)==(board.boardY)) {
					//�y�I��board�ϼu
					yDir = -yDir;
				}
				
			
			ballY += dy * yDir;                               //�ϼu�y��y��m
			ballX += dx * xDir;                               //�ϼu�y��x��m
		}
		void paintBall(Graphics g, Canvas c) {                              //�e�y
			reBound(g, c);                                                  //�˴��O�_�ݭn�ϼu
			g.setColor(Color.blue);                                         //�C��  
			g.fillOval((int)ballX, (int)ballY, ballSize, ballSize);         //ø�s�ϼu�y
			g.setColor(Color.black);                                         //�C��  
			g.drawOval((int)ballX, (int)ballY, ballSize, ballSize);         //ø�s���
		}
		void paintBallNotMoved(Graphics g) {                                 //�C���}�l�e�A�e�@�өT�w���y
			g.setColor(Color.blue);
			ballX = mouseX + (board.boardWidth / 2  -ballSize / 2);
			g.fillOval((int)ballX, (int)ballY, ballSize, ballSize);         //ø�s�ϼu�y
			g.setColor(Color.black);                                         //�C��  
			g.drawOval((int)ballX, (int)ballY, ballSize, ballSize);         //ø�s���
		}
	}
	
	class Board{
		
		int boardX = 270, boardY = 360 ;
		final int boardWidth = 100,boardHeight = 10;
		                                                    //�y����B�e�B�y��      
		void paintBoard(int mouseX, Graphics g) {                        //�e�y��
			boardX = mouseX;                                             //�l�ܷƹ���x�y��
			g.setColor(Color.orange);                                    //�C��  
			g.fillRect(boardX, boardY, boardWidth, boardHeight);         //ø�s�y��
			g.setColor(Color.black);
			g.drawRect(boardX, boardY, boardWidth, boardHeight);
		}
	}
	class Block{
		static final int blockWidth = 15,blockHeight = 15;               //�]�w����j�p
		
		void paintBlock(Graphics g, int blockX, int blockY){             //ø�s���
			g.setColor(Color.red);
			g.fillRect(blockX, blockY, blockWidth, blockHeight); 
			g.setColor(Color.black);
			g.drawRect(blockX, blockY, blockWidth, blockHeight);
		}
		void deleteBlock(int blockX, int blockY, Graphics g) {                       //�R���Y�@�Ӥ��
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
	private void createMap() {                                          //�Ыئa�ϡA�H���Ы�
		
		Random ran = new Random();        
		int blockLimit = 80;                                             //�s�@70�Ӥ��
		int blockX, blockY;
		outloop : for(int i = 0; i < blockLimit; i++) {                  //�ˬd����y�ФW�O�_�����|���
			
			blockX = ran.nextInt(frm.getSize().width - 32);              //���N���srandom�@��
		    blockY = ran.nextInt(frm.getSize().height/3 - 1);
		    if(map[blockX][blockY][1] == 0) {
		    	for (int x = 1; x < 15; x++) {
		    		for (int y = 1; y < 15; y++) {
		    			if(map[blockX + x][blockY + y][1] != 0) {
		    				i--;
		    				continue outloop;                               //���srandom�@��
		    			}	
		    		}
		    	}
		    	map[blockX][blockY][0] = 1;                                 //��������_�l�y��
		    	map[blockX][blockY][1] = i+1;
		    	for (int x = 1; x < 15; x++) {                              //����������ݽs��
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
	public void mouseMoved(MouseEvent e) {                                //�ƹ����ʨ���̡A�N�l�ܨ����
		mouseX = e.getX();
		if(mouseX > 529)
			mouseX = 529;
		if(ballRun)                                                       //�p�GballRun == false�A�N��y�w����A�C������
		    repaint();
	}
	public void mouseClicked(MouseEvent e) {                              //�I���ƹ���ܶ}�l
		gameStart = true;
		t = new Thread(this);
		t.start();
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void run() {                                                    //Thread�A�C�ʤ@���A��3�@��
		while(ballRun) {  
			repaint();                                                     //����paint()
			try {
				Thread.sleep(1);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void paint(Graphics g) {
		if(gameStart)                                                     //�C���}�l�A�e�|���ʪ��y
		    ball.paintBall(g, this);
		else {                                                            //�C���|���}�l�A�y����
			ball.paintBallNotMoved(g);
		}
		for(int x = 0;x < ct.getWidth(); x++) {
			for(int y = 0;y < ct.getHeight(); y++) {                      //����map[][][0]�_�l�y�СA�e���
				if(map[x][y][0] == 1)
				    block.paintBlock(g, x, y);
			}
		}
		board.paintBoard(mouseX, g);	                                  //�e�y��
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
	static MenuBar mb = new MenuBar();                    //�Ы�MenuBar�P��U��������
	static Menu menu = new Menu("�\��");
	static MenuItem mI1 = new MenuItem("���s�}�l");
	static MenuItem mI2 = new MenuItem("����");
	static JFrame frm;
	static Container ct;
	
	static class myListener implements ActionListener{     //���s�}�l�B��������k
		public void actionPerformed(ActionEvent e) {
			MenuItem mI = (MenuItem)e.getSource();
			if(mI == mI1) {
				frm.dispose();                             //������frm
				createNewFrame();
				new GameStart(frm, ct);
			}
			if(mI == mI2) {
				frm.dispose();
			}
		}
	}
	static void createNewFrame() {                       //�Ыطs��Frame
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
