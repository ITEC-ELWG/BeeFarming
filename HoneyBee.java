import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class HoneyBee extends Bee{
	private int id;
	private boolean isDanger=false;
	private int idDanger;
	private int num=0;
	private double[] list = new double[2000];
	private boolean wantback = false;
	private int timer=0;
	private int n_list=0;
	public HoneyBee(int id,int x, int y, double angle,boolean isAlive,Image img){
		super(id,x,y,angle,isAlive,img);
		this.id = id;
		
	}
	
	/**此方法是需要重写的核心代码，蜜蜂采蜜的主要个性在此类体现*/
	
	public void search(){
		
		int i,j,k;
		String strVision = BeeFarming.search(id);
		//System.out.println(strVision);
		isDanger = false;
		int tempid=id;
		boolean outofw = false;
		boolean outofe = false;
		boolean outofn = false;
		boolean outofs = false;
		boolean outof = false;
		
		if (strVision.indexOf("*W~")>=0) outofw = true;
		if (strVision.indexOf("*E~")>=0) outofe = true;
		if (strVision.indexOf("*N~")>=0) outofn = true;
		if (strVision.indexOf("*S~")>=0) outofs = true;
		outof = outofw || outofe || outofn || outofs;
		
		boolean onflower = false;
		int honey_onflower =0;
		
		int n_findflower=0;
	    int[] honey_findflower = new int[20];
	    double[] angle_findflower = new double[20];
	    
	    int n_findbee=0;
	    int[] id_findbee = new int[20];
	    double[] posangle_findbee = new double[20];
	    double[] flyangle_findbee = new double[20];
	    tempid=9;
		
		String regexp="(?<=\\().*?(?=\\))";
		Pattern p = Pattern.compile(regexp); 
	    Matcher m = p.matcher(strVision); 
	    while(m.find()) {
	    		onflower= true;
	    		String[] cc =(m.group().split(","));
	    		if (cc.length==2)
	    		{
	    			if (cc[1].compareTo("ON")==0)
	    			{
	    				onflower = true;
	    				honey_onflower = Integer.parseInt(cc[0]);
	    	            //System.out.println(honey_onflower);
	    			}
	    			else {
	    				n_findflower++;
	    				honey_findflower[n_findflower] = Integer.parseInt(cc[0]);
	    	        	angle_findflower[n_findflower] = correctvalue(Double.parseDouble(cc[1]));
	    	            //System.out.println(honey_findflower[n_findflower]+" "+angle_findflower[n_findflower]);
	    			}
	
	    		}
	      
	    }
	    
	    //find run away angle
	    
	    double runangle=0;
	    strVision = BeeFarming.search(tempid);
	    regexp="(?<=\\().*?(?=\\))";
		p = Pattern.compile(regexp); 
	    m = p.matcher(strVision); 
	    while(m.find()) {
	    		
	    		String[] cc =(m.group().split(","));
	    		if (cc.length==3){
	    			n_findbee++;
	    			id_findbee[n_findbee] = Integer.parseInt(cc[0]);
		    		posangle_findbee[n_findbee] = correctvalue(Double.parseDouble(cc[1]));
		    		flyangle_findbee[n_findbee] = correctvalue(Double.parseDouble(cc[2]));
		    		if (id_findbee[n_findbee]==id)
					{
		    			//System.out.println("runrunrunrunrunrunrunrunrunrun");
						isDanger = true;
						runangle = posangle_findbee[n_findbee];
					}
	    		}
	    }
	    strVision = BeeFarming.search(id);

	    if (isDanger)
	    {
	    	angle = runangle;
	    }
	    else if (isDanger==false && n_findflower>0)
		{
			k=1;
			for (i=2;i<=n_findflower;i++)
			if (honey_findflower[k]<honey_findflower[i])
				k=i;
			angle = angle_findflower[k];
		}
	    else if(strVision.indexOf('*')==0)
		{	
			Random ra = new Random();
			angle += ra.nextInt(90);
			if (angle>360)
				angle-=360;
		}
	    
	    
	    
	    if (isDanger == true)
		{
	    	//timer=0;
			if (outof==false)
			{
				n_list=0;
			}
			else {
				list[++n_list] = angle;
			}
		}
		else if (isDanger == false){
		 if (n_list>0){
				angle = correctvalue(list[n_list]+180);
				n_list--;
			}
		}
		
		
		
		ratoteImage(angle);
		
		setXYs(0);
	}
	
	
	public double correctvalue( double angle1)
	{
		if (angle1<0)
			while (angle1<0) angle1+=360;
		if (angle1>=360)
			while (angle1>=360) angle1 -=360;
		return angle1;
	}
	
	public double diffangle(double angle1, double angle2){
		double temp = Math.max(angle1,angle2) - Math.min(angle1,angle2);
		temp = Math.min(temp,360-temp);
		return temp;
	}
	
}
//String params[] = searchInfo.substring(pos+2, searchInfo.indexOf(')', pos)).split(",");