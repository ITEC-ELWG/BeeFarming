import java.awt.*;
import java.awt.image.*;
import java.util.*;
public class Hornet extends Bee{
	private int id;
	private boolean dead=false;
	private boolean seen=false;
	private int x,y;
	private int num=0;
	private String strVision,strVision1;
	private	String[] info;
	private	String[] beeInfos;
	private String[] flwInfos;
	private String flwInfo=new String();
	private	String beeInfo=new String();
	private int flwVolumn,beeId;
	private	double flwAngle,beeAngle,beeHeading,beeAngle1,beeHeading1;
	private int[] nextX = new int[9];
	private int[] nextY = new int[9];
	private int dx_sum = 0;
	private	int dy_sum = 0;
	private int deltaX,deltaY;
	private double newAngle;
	private int maxVolumn;
	private int chasingId=9;//记录当前目标
	private	Random ra = new Random();
	

	public Hornet(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
		this.x=x;
		this.y=y;
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	public void search(){
	   // System.out.println("My position is "+x+","+y+",actual position is"+posX+','+posY+'\n');
	    strVision = BeeFarming.search(id);
		 BeeFarming.update(new FlyingStatus (id,x,y,angle+180,true,0));
		strVision1 = BeeFarming.search(id);  
		 maxVolumn=0;
		 newAngle=0;
////////////////////////////////////////////////////////////		   
//////////优先处理蜜蜂的信息，保存beeInfo中/////////////////
////////////////////////////////////////////////////////////
		if((strVision.length()>6)||(strVision1.indexOf("+")!=-1))     //有花或蜜蜂的情况下
		 //if(strVision.length()>6)
		 { //初始化信息字符串
		   num=0;
		   beeInfo=beeInfo.substring(beeInfo.length());
		   flwInfo=flwInfo.substring(flwInfo.length());	   
          //将蜜蜂、花的信息，分别保存flwInfo和beeInfo中
		   info=strVision.split("~",0);
		   for(int i=0;i<info.length;i++)
		   {
		     if(info[i].indexOf('-')==0)
			 {
			   flwInfo=flwInfo.concat(info[i]);		 
			 }
			 if(info[i].indexOf('+')==0)
			 {
			   beeInfo=beeInfo.concat(info[i]);		    			 
			 }		 
           }	
		   
		   //将beeInfo按格式分段，提取有用信息
		   flwInfos=flwInfo.split("[()]");
		   beeInfos=beeInfo.split("[()]");
		   
		   //有密蜂的情况下，只考虑蜜蜂
		   if(beeInfos.length>1) 
		   {
		    //解析蜂的信息，追逐蜜蜂
		     for(int i=0;i<beeInfos.length;i++)
		    {
		      if((beeInfos[i].indexOf('+')!=0)&&(beeInfos[i].length()>0))
			  {		
               				
		        beeId=Integer.parseInt(beeInfos[i].substring(0,beeInfos[i].indexOf(',')));
				//没目标时，重新锁定目标
				if(seen==false)
				{
				 chasingId=beeId;
				 seen=true;	
				}
				//有目标时，只追当前目标对象
				if((seen==true)&&(beeId==chasingId))
				{
			      beeAngle=Double.parseDouble(beeInfos[i].substring(beeInfos[i].indexOf(',')+1,beeInfos[i].indexOf(',',4)));
			      beeHeading=Double.parseDouble(beeInfos[i].substring(beeInfos[i].indexOf(',',4)+1));
				  
				  //归一化观测到的蜜蜂的角度值
			      beeAngle1=90-angle+beeAngle;
			      beeHeading1=90-angle+beeHeading;	  
			     while(beeAngle1<0)
			     {
			      beeAngle1=beeAngle1+360;
			     }
			      while(beeAngle1>360)
			     {
			      beeAngle1=beeAngle1-360;
			     }		  
			      while(beeHeading1<0)
			     {
			      beeHeading1=beeHeading1+360;
			     }
			      while(beeHeading1>360)
			     {
			      beeHeading1=beeHeading1-360; 
			     }
				
				
				   if((x<80)||(y<80)||(x>740)||(y>540))
				   angle=beeAngle1+60*Math.cos(Math.toRadians(90-beeHeading1+beeAngle1))-90+angle;
                  else 
				   angle=beeAngle1+30*Math.cos(Math.toRadians(90-beeHeading1+beeAngle1))-90+angle;
				 
				}			    		   
			   }
			  
			 }
			}
			
	///////////////////////////////////////////////////////////////////////
	
			  else if(strVision1.indexOf("+")!=-1)
			{
			  beeInfo=beeInfo.substring(beeInfo.length());
			  info=strVision1.split("~",0);
			  for(int i=0;i<info.length;i++)
		      {   
			    if(info[i].indexOf('+')==0)
			    {
			     beeInfo=beeInfo.concat(info[i]);		    			 
			    }		 
              }	
			  beeInfos=beeInfo.split("[()]");
			  if(beeInfos.length>1) 
		      {
		       //解析蜂的信息，追逐蜜蜂
		       for(int i=0;i<beeInfos.length;i++)
		      {
		        if((beeInfos[i].indexOf('+')!=0)&&(beeInfos[i].length()>0))
			   {		
               				
		         beeId=Integer.parseInt(beeInfos[i].substring(0,beeInfos[i].indexOf(',')));
				  //没目标时，重新锁定目标
				 if(seen==false)
				 {
				  chasingId=beeId;
				  seen=true;	
				 }
				//有目标时，只追当前目标对象
				 if((seen==true)&&(beeId==chasingId))
				 {
			       beeAngle=Double.parseDouble(beeInfos[i].substring(beeInfos[i].indexOf(',')+1,beeInfos[i].indexOf(',',4)));
			       beeHeading=Double.parseDouble(beeInfos[i].substring(beeInfos[i].indexOf(',',4)+1));
				  
				  //归一化观测到的蜜蜂的角度值
			      beeAngle1=90-angle+beeAngle;
			      beeHeading1=90-angle+beeHeading;	  
			     while(beeAngle1<0)
			     {
			      beeAngle1=beeAngle1+360;
			     }
			      while(beeAngle1>360)
			     {
			      beeAngle1=beeAngle1-360;
			     }		  
			      while(beeHeading1<0)
			     {
			      beeHeading1=beeHeading1+360;
			     }
			      while(beeHeading1>360)
			     {
			      beeHeading1=beeHeading1-360; 
			     }
				 
				 
				  if((x<80)||(y<80)||(x>740)||(y>540))
				   angle=beeAngle1+60*Math.cos(Math.toRadians(90-beeHeading1+beeAngle1))-90+angle;
                  else 
				   angle=beeAngle1+30*Math.cos(Math.toRadians(90-beeHeading1+beeAngle1))-90+angle;		  
				}			    		   
			   }
			  
			 }
			}
			  
			}  
///////////////////////////////////////////////////////////////////////////////


			//没蜂的情况下，才考虑花
			else
			{
			 seen=false;
			 chasingId=9;
			 for(int i=0;i<flwInfos.length;i++)
		     {     
		      if((flwInfos[i].indexOf('-')!=0)&&(flwInfos[i].length()>0)&&(flwInfos[i].indexOf("ON")==-1))
			  {	  
		        flwVolumn=Integer.parseInt(flwInfos[i].substring(0,flwInfos[i].indexOf(',')));
			    flwAngle=Double.parseDouble(flwInfos[i].substring(flwInfos[i].indexOf(',')+1));
			    if (flwVolumn>maxVolumn)
			    {
			     maxVolumn=flwVolumn;
			     newAngle=flwAngle;
			    }
			  }
		     }
            if(maxVolumn>0)
		    {
		      angle=newAngle;	      
		     }			 
			}
			
		   }
		   
		 
//////////////////////////////////////////////////////////////////////////////////////////////		 
//其次处理边界信息，确定一种碰边转向方法，花在x=【30,770】，y=【30,590】范围均匀分布//////////
//////////////////////////////////////////////////////////////////////////////////////////////
		else if(strVision.indexOf('*')==0)
		{	
		   num=0;
			 //碰边随机转向,并满足入射和反射在法线两边
            switch(strVision.charAt(1)	)
            {
			 case 'W':  if(Math.sin(Math.toRadians(angle))<0)
						 {
						  angle=ra.nextInt(90);
						  angle=angle+270;
						 }
						 else
						 angle=ra.nextInt(90);
			             break;
			 case 'N': if(Math.cos(Math.toRadians(angle))<0)
						 {
						  angle=ra.nextInt(90);
						  angle=angle+90;
						 }
						 else
						 angle=ra.nextInt(90);
			           break;
			 case 'E':  if(Math.sin(Math.toRadians(angle))<0)
						{
						  angle=ra.nextInt(90);
						  angle=angle+180;
						}
						else
						{
						  angle=ra.nextInt(90);
						  angle=angle+90;
						}
			            break;
			 case 'S': if(Math.cos(Math.toRadians(angle))<0)
						 {
						  angle=ra.nextInt(90);
						  angle=angle+180;
						 }
						 else
						 {
						  angle=ra.nextInt(90);
						  angle+=270;
						 }
			           break;
		     default : break;
			 }			 
		}
		//连续10轮无花无蜂无边界，则随机转90度内的一个角
		else
		{
		 if(num==10)
		 {
		  angle+=ra.nextInt(180)-90;
		  num=0;
		 }
		 else
		 num++;
		}
		
		//计算下一时刻自身位置
		dx_sum = 0;
		dy_sum = 0;
		deltaX = (int)(18 *  Math.cos(Math.toRadians(angle)));
		deltaY = (int)(18 *  Math.sin(Math.toRadians(angle)));
		for(int i=0;i<9;i++){
			nextX[i] = (int)(deltaX * (i+1)/ 9.0)-dx_sum;
			nextY[i] = (int)(deltaY * (i+1)/ 9.0)-dy_sum;
			dx_sum += nextX[i];
			dy_sum += nextY[i];
		}
		x +=dx_sum;
		y +=dy_sum;
		
		
		ratoteImage(angle);
		setXYs(0);
	}
	
	/**如果黄蜂抓到了蜜蜂，则boolean dead==true，黄蜂可以根据dead的值判断蜜蜂知否被杀死。
	本方法可以修改，在BeeFarming的killBee方法中当蜜蜂被黄蜂消灭后将被调用*/
	public boolean isCatched(){
	    chasingId=9;
 	    seen=false;
	    dead = true;
	    return dead;
	}
	  
}