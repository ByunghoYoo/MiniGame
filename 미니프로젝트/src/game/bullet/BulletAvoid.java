package game.bullet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//import mainMenuCreating.GameSelectView;

public class BulletAvoid extends JFrame implements Runnable, KeyListener {
	// JFrame : GUI를 만들기 위한 클래스
	// Runnable : 쓰레드를 다루기 위한 인터페이스, 총알의 객체를 동시에 처리하기 위해서는 쓰레드가 필수적이다.
	// KeyListener : 키보드에 대한 이벤트를 다루는 클래스
	
	// 필드
	private BufferedImage bi = null; // BufferedImage : 이미지 데이터를 조작하고 처리하는 클래스
	private ArrayList enList = null;  // 총알에 대한 ArrayList 생성
	private boolean left = false, right = false, up = false, down = false; // 키보드 입력을 true, false로 받음 
	private boolean start = false, end = false; // 게임 진행 상태와 게임 대기 상태 구현읠 위한 변수
	private int w = 700, h = 700, x = 350, y = 350, xw = 30, xh = 30; // 좌표 및 크기 설정 wx와 wh는 우주선에 대한 크기
	private int initX; // 총알들의 초기 위치
	private int initY;
	private long time;
	
	//JPanel panel = new JPanel();
	
	// 생성자
	public BulletAvoid() {
		
		bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); // 크기와 넓이, RGB에 색성분에 의한 이미지 구현
		enList = new ArrayList(); // 떨어지는 총알에 대한 리스트 생성
		this.addKeyListener(this);
		this.setSize(w, h);
		this.setTitle("총알 피하기");
		this.setResizable(false); 
		this.setVisible(true);  
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		}
	
	// 게임 시작 시 실행 되는 메소드
	@Override
	public void run() {
		
		try {
			
			int enCnt = 0; // 게임 실행시 시간마다 총알을 생성하게 하는 총알 시간 변수 선언
			while(true) {
				
				Thread.sleep(10);
		
				if(start) { // 게임 시작
					
					time++; // 시작시 게임 시간 카운팅
					
					if(enCnt > 2000) { // 만약 2000이라는 총알 시간에 도달한다면...
						enCreate(); // 총알 초기 위치 세팅 및 생성
						enCnt = 0; // 다시 시간을 0으로
					}
					enCnt += 30; // 0이 되었다면 다시 2000까지 카운팅함
					keyControl(); // 우주선을 x, y좌표로 움직이게 하는 메소드
					crashChk(); // 충돌 확인 메소드
				}
				
				draw(); // GUI를 구성하는 메소드
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 총알 생성 및 초기 위치 세팅 메소드
	public void enCreate() {
		
		for(int i = 0; i < 15; i++) {
			
			int initPos = 0; // (0 ~ 7까지 게임의 좌상변부터 시계방향으로 8개 섹터로 구분 짓는 섹터 변수 선언
			
			switch( (int)(7 * Math.random()) ){ // switch문에 0~7까지의 난수를 생성하여 랜덤으로 총알이 만들어지게 함
			case 0:
				initX = ((int)(Math.random() * w/2)); // 0 ~ 250
				initY = (int)(Math.random() * -50); // -50 ~ 0
				initPos = 0;
				break;		
			case 1:
				initX = (int)(Math.random() * w/2) + w/2; // 250 ~ 500
				initY = (int)(Math.random() * -50); // -50 ~ 0
				initPos = 1;
				break;
			case 2:
				initX = (int)(Math.random() * 50) + w; // 500 ~ 550
				initY = (int)(Math.random() * h/2); // 0 ~ 250
				initPos = 2;
				break;
			case 3:
				initX = (int)(Math.random() * 50) + w; // 500 ~ 550
				initY = (int)(Math.random() * h/2) + h/2; // 250 ~ 500
				initPos = 3;
				break;
			case 4:
				initX = (int)(Math.random() * w/2) + w/2; // 250 ~ 500
				initY = (int)(Math.random() * 50) + h; // 500 ~ 550
				initPos = 4;
				break;
			case 5:
				initX = (int)(Math.random() * w/2); // 0 ~ 250
				initY = (int)(Math.random() * 50) + h; // 500 ~ 550
				initPos = 5;
				break;
			case 6:
				initX = (int)(Math.random() * -50); // -50 ~ 0
				initY = (int)(Math.random() * h/2) + h/2; // 250 ~ 500
				initPos = 6;
				break;
			case 7:
				initX = (int)(Math.random() * -50); // -50 ~ 0
				initY = (int)(Math.random() * h/2); // 0 ~ 250
				initPos = 7;
				break;
			}
			
			Enemy en = new Enemy(initX, initY, initPos); // 초기 포지션 및 섹터의 정보를 Enemy 클래스로 넣어준다.
			enList.add(en); // 생성한 총알을 리스트에 넣어줌
		}
	}
	
	// 충돌 확인 메소드
	public void crashChk() {
		
		//Graphics g = this.getGraphics(); // Graphics 클래스는 도형을 그릴 수 있는 다양한 메소드를 제공함
		Polygon p = null; // 다각형을 그리기 위한 변수 선언
		
		for(int i = 0; i < enList.size(); i++) {
			
			Enemy e = (Enemy)enList.get(i); // 총알의 정보를 e변수로 받아옴
			// 초기 게임에서는 우주선 이미지가 아닌 아닌 사각형 이미지였기 때문에 wx, wh변수를 이용하여 다각형을 구성하였지만, 우주선은 크기가 제각각이라 좌표 값을 테스트하며 임의 값 설정 
			int[] xpoints = {(x + 25), (x + 10), (x + 25), x}; // 다각형(우주선)의 x 좌표값 
			int[] ypoints = {(y + 7), (y + 7), (y + 18), (y + 18) }; // 다각형(우주선)의 y 좌표값 
			p = new Polygon(xpoints, ypoints, 4); // 위의 x, y좌표를 통해 다각형 생성
			
			if(p.intersects((double)e.x, (double)e.y, (double)e.w, (double)e.h)) { // 생성된 다각형과 총알과 교차하는지 판정하여 boolean값으로 반환
				
				enList.remove(i);
				start = false;
				end = true;
				
				try {
					Thread.sleep(100);
					enList.removeAll(enList);
					x = 350;
					y = 350;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	// 색 입히기와 총알 움직이기를 담당하는 메소드
	public void draw() {
		
		Graphics gs = bi.getGraphics(); 
		Toolkit toolkit = null;
		ImageObserver observer = null;
		
		gs.setColor(Color.black); 
		gs.fillRect(0, 0, w, h); // 배경을 흰색으로 다 채운다.
		
		gs.setColor(Color.white);
		// gs.drawString("총알 갯수 : " + enList.size(), 530, 90);
		gs.drawString("게임 시작 : Enter", 530, 50); 
		gs.drawString("게임 선택 화면 : Back Space", 530, 70);
		String str;
		gs.drawString("시간 : ", 640, 680);
		gs.drawString(str = String.valueOf(time/80), 675, 680);// 글씨 및 위치 지정
		
		Image img1 = toolkit.getDefaultToolkit().getImage("img/name.png");
		gs.drawImage(img1, 110, 140, 500, 200, observer);
		
		if(start) {
			gs.setColor(Color.black); 
			gs.fillRect(50,100,600,500);
		}
		
		if(end) {
			gs.setColor(Color.black); 
			gs.fillRect(50,100,600,500);
			
			Image gameover = toolkit.getDefaultToolkit().getImage("img/gameover.png");
			gs.drawImage(gameover, 25, 140, 650, 100, observer);
		}
		
		// 나만의 ship만들어보기
		Image img2 = toolkit.getDefaultToolkit().getImage("img/ship2.png");
		gs.drawImage(img2, x, y, xw, xh, observer);
		
		gs.setColor(Color.YELLOW); // 총알 색 지정
		
		// 일일히 총알 객체에 색 및 크기 설정
		for(int i = 0; i < enList.size(); i++) {
			
			Enemy e = (Enemy)enList.get(i);
			gs.fillRect(e.x, e.y, e.w, e.h);

		}
		
		// 총알 움직이게 하기
		for(int i = 0; i < enList.size(); i++) {
			
			Enemy e = (Enemy)enList.get(i);
			
			e.moveEn();
			
			if(e.y > h+70) enList.remove(i); 
			if(e.y < -70) enList.remove(i); // 화면 밖을 벗어나면 총알을 없앤다.
			
		}
			
		Graphics ge = this.getGraphics();
		ge.drawImage(bi, 0, 0, w, h, this);
		
	}
	
	// 우주선 모션
	public void keyControl() {
		if(0 < x) {
			if(left) x -= 3;
		}
		if(w > x + xw) {
			if(right) x += 3;
		}
		if(25 < y) {
			if(up) y -= 3;
		}
		if(h > y + xh) {
			if(down) y += 3;
		}
	}
	
	// 키보드 누를때 마다 나타나는 반응
	public void keyPressed(KeyEvent ke) {
		switch(ke.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_UP:
			up = true;
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		case KeyEvent.VK_ENTER:
			start = true;
			end = false;
			time = 0;
			break;
		}
	}
 
	public void keyReleased(KeyEvent ke) {
		switch(ke.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
			
		//게임 셀렉 화면으로 돌아가기 위한 키 입력
		case KeyEvent.VK_BACK_SPACE:
			/*	
			JOptionPane.showMessageDialog(panel, "게임 선택 화면으로 돌아갑니다. :)");
			System.out.println("돌아가기");
			new GameSelectView();
			dispose();
			*/
		}
	}
 
	public void keyTyped(KeyEvent ke) { }

}

// 총알 클래스
class Enemy {
	int x;
	int y;
	int w = 5;
	int h = 5; //총알 크기는 작게
	int bulletSpeed = 1; // 난이도 조절용 스피드
	int initPos;
		
	public Enemy() { }
	
	public Enemy(int x, int y, int initPos) { // 총알 생성 메소드에서 이니셜 값을 가지고와서 넣어줌.
		this.x = x;
		this.y = y;
		this.initPos = initPos;
	}
		
	// 좀 더 세분화 (8개의 영역으로 나누어 진행)
	public void moveEn() {
		
		if(initPos == 0) {
			x+=bulletSpeed;
			y+=bulletSpeed;
		} else if(initPos == 1) {
			x-=bulletSpeed;
			y+=bulletSpeed;
		} else if(initPos == 2) {
			x-=bulletSpeed;
			y+=bulletSpeed;
		} else if(initPos == 3) {
			x-=bulletSpeed;
			y-=bulletSpeed;
		} else if(initPos == 4) {
			x-=bulletSpeed;
			y-=bulletSpeed;
		} else if(initPos == 5) {
			x+=bulletSpeed;
			y-=bulletSpeed;
		} else if(initPos == 6) {
			x+=bulletSpeed;
			y-=bulletSpeed;
		} else if(initPos == 7) {
			x+=bulletSpeed;
			y+=bulletSpeed;
		}
		
	}

}