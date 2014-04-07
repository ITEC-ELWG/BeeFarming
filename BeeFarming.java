import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Toolkit;
import java.util.*;
import java.io.*;
import java.sql.*;
public class BeeFarming extends JFrame{
	private Image imgBee;//背景、蜜蜂的图片
	private Image imgBee1,imgFlw;
	//private Image imgFlw[3];
	private Image imgBee2;
	private static Image imgBG;
	private Bee bee;
	private int angle_damp = 0;
	private boolean flag = false;
	private boolean begin = true;
    //2013年将属性改为private
	private static int time = 0;
    private static int count = 3;	//蜜蜂数量
	private static int countflower = 20;//花的数量，包括未开发的
	private static int Beegoal = 0;//存活蜜蜂可以兑换的积分总数 
	private static String game = "";
	//private final static int[] SPEED = {6,6,6,6,6,6,6,15,15,12};
	private final static int[] RANGE = {50,50,50,50,50,50,50,50,50,100};
	//private final static int[] SIGHT = {120,120,120,120,120,120,120,120,120,150}; //都是180，所以不设了
	private final static int BG_WIDTH=800;
	private final static int BG_HEIGHT=600;
	private static Bee[] bees = new Bee[10];//蜜蜂对象数组，存放每只蜜蜂对象的句柄
	private static Hornet hornet;
	private static FlyingStatus[] status = new FlyingStatus[10];//每只蜜蜂当前飞行状态数据
	//private static String[] messages = new String[10];//蜜蜂的消息信箱，每个蜜蜂一个，用于相互通信
	protected static int totalHoney = 0;
	private static ArrayList<Flower> flowers = new ArrayList<Flower>();
    Music music = new Music("flourish.mid");
	
	private static Flower[] flws = new Flower[20];
	/**类构造函数，用于初始化整个游戏中的对象及设置*/
	public BeeFarming(){
        setTitle("BeeFarming Game"); //调用父类构造函数               
	 	imgBee = getToolkit().getImage("bee.png");
		imgBee2 = getToolkit().getImage("bee2.png");
		imgBG = this.getToolkit().getImage("green.jpg");
	    Image[] imgFlw = new Image[3];
		imgFlw[0] = this.getToolkit().getImage("flower0.png");
		imgFlw[1] = this.getToolkit().getImage("flower1.png");
		imgFlw[2] = this.getToolkit().getImage("flower2.png");
	
		MediaTracker mt = new MediaTracker(this); //实例化媒体加载器
        mt.addImage(imgBee, 0); //增加图像到加载器中
		mt.addImage(imgBee2, 1);
		mt.addImage(imgBG,2); //增加图像到加载器中
		mt.addImage(imgFlw[0],4);
		mt.addImage(imgFlw[1],5);
		mt.addImage(imgFlw[2],3);
        try {
            mt.waitForAll(); //等待图片加载
        } catch (Exception ex) {
            ex.printStackTrace();  //输出出错信息
        } 
		
		Container container=getContentPane(); //得到窗口容器
		container.setLayout(null);//不能用BorderLayout，否杂JPanel会被改变大小
	    BackGroundJP pGround = new BackGroundJP(imgBG);	
		pGround.setBounds(0,0,BG_WIDTH,BG_HEIGHT);//直接绝对布局
		container.add(pGround);
		pGround.setLayout(null);
		//2013修改了初始值，避免将黄蜂和蜜蜂放在一起
		bees[1] = new HoneyBee(0,435,210,176,true,imgBee);
		bees[2] = new HoneyBee(1,130,230,145,true,imgBee);
		bees[3] = new HoneyBee(2,730,370,40,true,imgBee);
		bees[9] = new Hornet(9,490,500,240,true,imgBee2);
		pGround.add(bees[9]);
		pGround.add(bees[1]);
		pGround.add(bees[2]);
		pGround.add(bees[3]);
		
		//将花生成文件读入内存，存在数组flw[]中
        try{
			int i = 0;
			Scanner input = new Scanner(new File("flowers2014.dat"));
			while(input.hasNext()){				
				flws[i] = new Flower(Integer.parseInt(input.next()),30+Integer.parseInt(input.next()),
									30+Integer.parseInt(input.next()),10+Integer.parseInt(input.next()),
									imgFlw[Integer.parseInt(input.next())],pGround);
				i++;
			}
		}catch(IOException e) {
			System.err.println(e);
		}
		
        setSize(BG_WIDTH+5,BG_HEIGHT+35); //设置窗口尺寸
        setVisible(true); //设置窗口可视
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     //关闭窗口时退出程序    
		//加入鼠标事件监听器
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(flag == true)
					flag = false;
				else
					flag = true;
			}
		});
		
		new Thread(new GodThread()).start();
		
    }
	

	
	/**线程内部类，用于定时触发系统调度方法*/
	private class GodThread implements Runnable {
		public void run() {
			while(true) {	
				if(flag&&begin){
					next();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void next(){
		//判断花的生成时期是否到达，生成花
		for(int i = 0;i<20;i++){
			if(flws[i].getTime() == time){
				flowers.add(flws[i]);
				flws[i].getPGround().add(flws[i]);
				repaint();
			}
		} 
		time++;
		music.play();
		//3000轮后结束程序
		if(time==4000||count==0||countflower==0){
			game="GAME OVER";
			music.stop();
			begin=false;
			Beegoal = count*50;
			try{
				File dirFile = new File("Result");
				if(dirFile.exists() != true) dirFile.mkdir();
				PrintWriter out = new PrintWriter(
								  new FileWriter("Result\\result"+System.currentTimeMillis()+".txt"));
			    
		            out.println("totalHoney: "+BeeFarming.getHoney()+" kg;"); 
					out.println("still alive Bees: "+BeeFarming.count+" bees;");
					int atime = 200-time/20;
					out.println("The time left: "+atime+" S;");
					int goals = BeeFarming.getHoney()+BeeFarming.Beegoal;
					if(count>0&&countflower==0)goals+=atime;
		            if(count==0&&countflower>0)goals-=atime;
					out.println("Final Goals: "+goals+".");
					out.close();

					}catch(FileNotFoundException e){
						System.out.println("Error:Cannot open file for writing.");
					}catch(IOException e){
						System.out.println("Error:Cannot write to file.");
					}
		}
		//稳定一段时间搜索一次，观察周边情况，设定飞行路线
		if(angle_damp==0){
			for(int i=1;i<10;i++)
				if(bees[i]!=null)//蜜蜂是否存在，死了会从数组里删除
					if(bees[i].getlive()==true)bees[i].search();					
		}
		//延既定飞行路线飞行直到下一次搜索
		for(int i=1;i<10;i++)
			if(bees[i]!=null)//蜜蜂是否存在，死了会从数组里删除
				if(bees[i].getlive()==true)bees[i].flying1(angle_damp);
		angle_damp ++;	
		if(angle_damp==9)angle_damp =0;
	}
	
	/**蜜蜂通过该类方法将自己的状态反馈给主程序
	* @param fs 记录蜜蜂状态的对象
	*/
	public static void update(FlyingStatus fs)
	{
		status[fs.id]=fs;
		//System.out.println("X"+fs.id+"="+fs.x+", Y"+fs.id+"="+fs.y+",a="+fs.angle+",isAlive="+fs.isAlive);
	}
	
	/**蜜蜂通过该类方法向主程序查询视距、视限范围内的物体
	* @param fs 记录蜜蜂状态的对象
	* @return 以逗号隔开的字符串，描述边代号“W、E、N、S”,描述蜜蜂格式id-angle
	*/
	public static String search(int id)
	{
		
		FlyingStatus fs = status[id];
		String result="";
		
		//首先判断蜜蜂与4个边界的关系，(x,y)是蜜蜂中心，RANGE是视距,
		int visionX = fs.x + (int)(Math.cos(Math.toRadians(fs.angle))*RANGE[fs.id])-18;
		int visionY = fs.y + (int)(Math.sin(Math.toRadians(fs.angle))*RANGE[fs.id])-18;
		//System.out.println("vx="+fs.x+",vy="+fs.y);
		if(visionX<0)
		{
			result += "*W~";//west
		}	
		else if(visionX>BG_WIDTH)
		{
			result += "*E~";//east
		}
		if(visionY<0)
		{
			result += "*N~";//north
		}
		else if(visionY>BG_HEIGHT)
		{
			result += "*S~";//south
		}
		//还要判断视角范围内是否有花
		Iterator<Flower> it = flowers.iterator();
		while(it.hasNext()){
			Flower f = it.next();
			if(f.getVolumn()>0){//花是否还在显示？
				int fx = (int)(f.getPosition().getX());
				int fy = (int)(f.getPosition().getY());
				int distance1 = (int)(Math.pow((fs.x+18*Math.cos(Math.toRadians(fs.angle))-fx),2)
				            +Math.pow((fs.y+18*Math.sin(Math.toRadians(fs.angle))-fy),2));
				int distance2 = (int)(Math.pow((fs.x-18*Math.cos(Math.toRadians(fs.angle))-fx),2)
				            +Math.pow((fs.y-18*Math.sin(Math.toRadians(fs.angle))-fy),2));
				int distance = (int)(Math.pow(fs.x-fx,2)+Math.pow(fs.y-fy,2));
				if(distance<=4)
					result = result+"-("+f.getVolumn()+",ON)~";//蜜蜂在花上
				else if(distance1<=RANGE[fs.id]*RANGE[fs.id])//头部与花的距离小于范围值
				{
					//System.out.println("花"+f.getVolumn()+" 在可视距离内:"+distance+" F("+fx+","+fy+")-B("+fs.x+","+fs.y+")");									
					//飞行方向朝花靠近
					if(distance1<distance2){
						//System.out.println("花"+f.getVolumn()+" 在可视距离内, 头距离="+distance1+",尾距离="+distance2);
						double a = getVectorDegree(fs.x,fs.y,fx,fy);
						result = result+"-("+f.getVolumn()+","+a+")~";//花存在的方向
					}					
				}
			}
		}
		//再判断视角范围内是否有其它蜜蜂
		
		for(int i=0;i<10;i++)
		if(fs.getisAlive()==true){
			if(i!=fs.id&&status[i]!=null&&status[i].isAlive==true)
			{
				FlyingStatus fs1 = status[i];
				int distance = (int)(Math.pow(fs.x-fs1.x,2)+Math.pow(fs.y-fs1.y,2));
				//在蜜蜂视距范围内
				if(distance <= Math.pow(RANGE[fs.id],2))
				{					
					int distance1 = (int)(Math.pow((fs.x+18*Math.cos(Math.toRadians(fs.angle))-fs1.x),2)
				            +Math.pow((fs.y+18*Math.sin(Math.toRadians(fs.angle))-fs1.y),2));
					int distance2 = (int)(Math.pow((fs.x-18*Math.cos(Math.toRadians(fs.angle))-fs1.x),2)
				            +Math.pow((fs.y-18*Math.sin(Math.toRadians(fs.angle))-fs1.y),2));
					 if(distance1<distance2){
						//System.out.println("蜂"+fs.id+" 看到了蜂"+fs1.id);
						double a = getVectorDegree(fs.x,fs.y,fs1.x,fs1.y);
						result = result+"+("+fs1.id+","+a+","+fs1.angle+")~";
						//看到的蜂的方向以及蜂的飞行方向		
					}					
				}
			}
		}			
		return result;
	} 
	
	/**根据蜜蜂id看附近（2,2）是否有花蜜可以采
	* @param id 蜜蜂id 
	* @return 采蜜状态（1-此次采完还有花蜜，0-采完没有花蜜了，-1-附近无花）
	*/
	public static int pickFlowerHoney(int id){
		//根据蜜蜂id，查询到相应的花，减去相应花的花蜜
		int bx = status[id].x;
		int by = status[id].y;
		Iterator<Flower> it = flowers.iterator();
		while(it.hasNext()){
			Flower f = it.next();
			if(f.getVolumn()>0){//花是否还在显示？
				int fx = (int)(f.getPosition().getX());
				int fy = (int)(f.getPosition().getY());
				int distance = (int)(Math.pow(bx-fx,2)+Math.pow(by-fy,2));
				if(distance <= 4){
					boolean more = f.consume(1);//花蜜减少
					totalHoney++;//总采集花蜜增
					if(more)
						return 1;//1代表还有蜜可采
					else
					    countflower--;
						return 0;//这次是最后一点了，花蜜已经采完
				}
			}
		}
		return -1;//附近没有花
	}
	public static int getHoney()
	{
	 return  totalHoney;
	}	 
	/**给定两点(A,B)坐标，计算矢量AB的角度，范围是0-360，X 正半轴上的点向 Y 正半轴旋转
	* @param x1,y1,x2,y2 两点坐标A(x1,y1),B(x2,y2) 
	* @return [0,360), 如果返回360则说明出现意外情况
	*/
	public static double getVectorDegree(int x1,int y1,int x2,int y2){
		int deltaY = y2 - y1;
		int deltaX = x2 - x1;
		if(deltaX ==0){//tan为无穷大的情况
			if(deltaY>0)
				return 90;
			if(deltaY<0)
				return 270;
		}else
		{
			double k = (double)deltaY / deltaX;
			if(deltaX>0&&deltaY>=0)
				return Math.toDegrees(Math.atan(k));
			if(deltaX>0&&deltaY<0)
				return 360+Math.toDegrees(Math.atan(k));
			if(deltaX<0)
				return 180+Math.toDegrees(Math.atan(k));
		}
		return 360;//如果返回360则说明出现意外情况
	}
	public static void killBee(int id){
	    FlyingStatus fs = status[id];
		int visionX = fs.x + (int)(Math.cos(Math.toRadians(fs.angle))*RANGE[fs.id])-18;
		int visionY = fs.y + (int)(Math.sin(Math.toRadians(fs.angle))*RANGE[fs.id])-18;	
		//判断视角范围内是否有其它蜜蜂
		for(int i=0;i<9;i++)
			if(status[i]!=null&&status[i].isAlive==true)
			{
				FlyingStatus fs1 = status[i];
				int distance = (int)(Math.pow(fs.x-fs1.x,2)+Math.pow(fs.y-fs1.y,2));
				//在黄蜂死亡圈范围内
				if(distance<=16){
					fs1.isAlive=false;
					Hornet hornet = (Hornet) bees[9];
					hornet.isCatched();
					//System.out.println("蜂"+fs1.id+"已经死亡 ！");
					count--;	
				}					
			}
	}
	public static void main(String[] args){
        new BeeFarming();
    }
	
	//2013年修正，改为内部类后，其访问BeeFarming属性可以改为private类型
	class BackGroundJP extends JPanel{
		private Image bgImg;
		int i;
	
		public BackGroundJP(Image img) {
			this.bgImg = img;
			Dimension size = new Dimension(img.getWidth(null),img.getHeight(null));
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setSize(size);
			setLayout(null);
		}
	
		public void paintComponent(Graphics g) {
			if(bgImg!=null)
				g.drawImage(bgImg,0,0,this);
				Color c = g.getColor();
				Font f = g.getFont();
				g.setFont(new Font("Arial",Font.PLAIN,30));
				
				int atime = 200-BeeFarming.time/20;
				if(atime>10){
					g.setColor(new Color(128,255,255));
					g.drawString("TIME: "+atime+" S", 500, 30);
				}
				else
				{
					g.setColor(Color.RED);
					g.drawString("TIME: "+atime+" S", 500, 30);
				}
				g.setColor(new Color(128,255,255));
				g.drawString("totalHoney: "+BeeFarming.getHoney()+" kg", 500, 65); 
				g.drawString("still alive Bees: "+BeeFarming.count, 500, 100);
				int sum= BeeFarming.getHoney()+BeeFarming.Beegoal;
				if(BeeFarming.count>0&&BeeFarming.countflower==0)sum+=atime;
				if(BeeFarming.count==0&&BeeFarming.countflower>0)sum-=atime;
				g.drawString("Goals: "+sum,500, 135);
				//1kg花蜜记一分，1只蜜蜂记50分，若花提前采完每秒时间记1分，若蜜蜂提前死光，每秒时间记-1分
				g.setFont(new Font("Arial",Font.PLAIN,60));
				g.setColor(Color.RED);
				g.drawString(BeeFarming.game,230, 310);
				repaint();
				g.setColor(c);
				g.setFont(f); 
		}
	}

}


