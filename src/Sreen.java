


import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;
/**
 *  截屏
 *  在选中区域内双击复制到粘贴板 
 * @author HP
 *
 */

public class Sreen extends JFrame implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage get;
   
    /** Creates a new instance of CaptureScreen */
    public void doStart(){
        try{
            Robot ro=new Robot(); // （通过本地操作）控制鼠标、键盘等实际输入源（java.awt）
            Toolkit tk=Toolkit.getDefaultToolkit(); // AWT组件的抽象父类（java.awt）
            Dimension di=tk.getScreenSize();
            Rectangle rec=new Rectangle(0,0,di.width,di.height);
            BufferedImage bi=ro.createScreenCapture(rec);
            JFrame jf=new JFrame();
            Temp temp=new Temp(jf,bi,di.width,di.height); // 自定义的Temp类的对象
            jf.getContentPane().add(temp,BorderLayout.CENTER);
            jf.setUndecorated(true);
            jf.setSize(di);
            jf.setVisible(true);
            jf.setAlwaysOnTop(true);
          
        } catch(Exception exe){
            exe.printStackTrace();
        }
    }
    
    /** 
     *公共的处理把当前的图片加入剪帖板的方法
     */
    public void doCopy(final BufferedImage image){
        try{
            Transferable trans = new Transferable(){ // 内部类
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[] { DataFlavor.imageFlavor };
                }
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.imageFlavor.equals(flavor);
                }
                public Object getTransferData(DataFlavor flavor)
                  throws UnsupportedFlavorException, IOException {
                    if(isDataFlavorSupported(flavor))
                        return image;
                    throw new UnsupportedFlavorException(flavor);
                }
            };
            
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
           
        }catch(Exception exe){
            exe.printStackTrace();
        }
    }
    
    
   
    private class Temp extends JPanel implements MouseListener,MouseMotionListener{
        /**
		 *  //一个临时类，用于显示当前的屏幕图像
		 */
		private static final long serialVersionUID = 1L;
		private BufferedImage bi;
        private int width,height;
        private int startX,startY,endX,endY,tempX,tempY;
        private JFrame jf;
        private Rectangle select=new Rectangle(0,0,0,0);//表示选中的区域
        private Cursor cs=new Cursor(Cursor.CROSSHAIR_CURSOR);//表示一般情况下的鼠标状态（十字线）
        private States current=States.DEFAULT;// 表示当前的编辑状态
        private Rectangle[] rec;//表示八个编辑点的区域
        //下面四个常量,分别表示谁是被选中的那条线上的端点
        public static final int START_X=1;
        public static final int START_Y=2;
        public static final int END_X=3;
        public static final int END_Y=4;
        private int currentX,currentY;//当前被选中的X和Y,只有这两个需要改变
        private Point p=new Point();//当前鼠标移的地点
        private boolean showTip=true;//是否显示提示.如果鼠标左键一按,则提示就不再显示了
        
        public Temp(JFrame jf,BufferedImage bi,int width,int height){
            this.jf=jf;
            this.bi=bi;
            this.width=width;
            this.height=height;
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            initRecs();
        }
        
        private void initRecs(){
            rec=new Rectangle[8];
            for(int i=0;i<rec.length;i++){
                rec[i]=new Rectangle();
            }
        }
        
        public void paintComponent(Graphics g){
            g.drawImage(bi,0,0,width,height,this);
            g.setColor(Color.RED);
            g.drawLine(startX,startY,endX,startY);
            g.drawLine(startX,endY,endX,endY);
            g.drawLine(startX,startY,startX,endY);
            g.drawLine(endX,startY,endX,endY);
            int x=startX<endX?startX:endX;
            int y=startY<endY?startY:endY;
            select=new Rectangle(x,y,Math.abs(endX-startX),Math.abs(endY-startY));
            int x1=(startX+endX)/2;
            int y1=(startY+endY)/2;
            
            //画出所截区域四个顶点及各边中点
            g.fillRect(x1-2,startY-2,5,5);
            g.fillRect(x1-2,endY-2,5,5);
            g.fillRect(startX-2,y1-2,5,5);
            g.fillRect(endX-2,y1-2,5,5);
            g.fillRect(startX-2,startY-2,5,5);
            g.fillRect(startX-2,endY-2,5,5);
            g.fillRect(endX-2,startY-2,5,5);
            g.fillRect(endX-2,endY-2,5,5);
            //标记出八个点所在的小矩形
            rec[0]=new Rectangle(x-5,y-5,10,10);
            rec[1]=new Rectangle(x1-5,y-5,10,10);
            rec[2]=new Rectangle((startX>endX?startX:endX)-5,y-5,10,10);
            rec[3]=new Rectangle((startX>endX?startX:endX)-5,y1-5,10,10);
            rec[4]=new Rectangle((startX>endX?startX:endX)-5,(startY>endY?startY:endY)-5,10,10);
            rec[5]=new Rectangle(x1-5,(startY>endY?startY:endY)-5,10,10);
            rec[6]=new Rectangle(x-5,(startY>endY?startY:endY)-5,10,10);
            rec[7]=new Rectangle(x-5,y1-5,10,10);
            //提示信息
            if(showTip){
                g.setColor(Color.CYAN);
                g.fillRect(p.x,p.y,170,40);
                g.setColor(Color.RED);
                g.drawRect(p.x,p.y,170,40);
                g.setColor(Color.BLACK);
                g.drawString("请按住鼠标左键不放选择",p.x,p.y+15);
                g.drawString("截图区  x:"+p.x+"\t  y:"+p.y,p.x,p.y+35);
                
            }
        }
        
        //根据东南西北等八个方向决定选中的要修改的X和Y的座标
        private void initSelect(States state){
            switch(state){
                case DEFAULT:
                    currentX=0;
                    currentY=0;
                    break;
                case EAST:
                    currentX=(endX>startX?END_X:START_X);
                    currentY=0;
                    break;
                case WEST:
                    currentX=(endX>startX?START_X:END_X);
                    currentY=0;
                    break;
                case NORTH:
                    currentX=0;
                    currentY=(startY>endY?END_Y:START_Y);
                    break;
                case SOUTH:
                    currentX=0;
                    currentY=(startY>endY?START_Y:END_Y);
                    break;
                case NORTH_EAST:
                    currentY=(startY>endY?END_Y:START_Y);
                    currentX=(endX>startX?END_X:START_X);
                    break;
                case NORTH_WEST:
                    currentY=(startY>endY?END_Y:START_Y);
                    currentX=(endX>startX?START_X:END_X);
                    break;
                case SOUTH_EAST:
                    currentY=(startY>endY?START_Y:END_Y);
                    currentX=(endX>startX?END_X:START_X);
                    break;
                case SOUTH_WEST:
                    currentY=(startY>endY?START_Y:END_Y);
                    currentX=(endX>startX?START_X:END_X);
                    break;
                default:
                    currentX=0;
                    currentY=0;
                    break;
            }
        }
        
        public void mouseMoved(MouseEvent me){
            doMouseMoved(me);
            initSelect(current); // current：当前状态（state）
            if(showTip){
                p=me.getPoint();
                repaint();
            }
        }
        
        //特意定义一个方法处理鼠标移动,是为了每次都能初始化一下所要选择的区域
        private void doMouseMoved(MouseEvent me){
            if(select.contains(me.getPoint())){
                this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                current=States.MOVE;
            } else{
                States[] st=States.values();
                for(int i=0;i<rec.length;i++){
                    if(rec[i].contains(me.getPoint())){
                        current=st[i];
                        this.setCursor(st[i].getCursor());
                        return;
                    }
                }
                this.setCursor(cs);
                current=States.DEFAULT;
            }
        }
        
        public void mouseExited(MouseEvent me){}
        
        public void mouseEntered(MouseEvent me){}
        
        public void mouseDragged(MouseEvent me){
            int x=me.getX();
            int y=me.getY();
            // 分别处理一系列的（光标）状态（枚举值）
            if(current==States.MOVE){
                startX+=(x-tempX);
                startY+=(y-tempY);
                endX+=(x-tempX);
                endY+=(y-tempY);
                tempX=x;
                tempY=y;
            }else if(current==States.EAST||current==States.WEST){
                if(currentX==START_X){
                    startX+=(x-tempX);
                    tempX=x;
                }else{
                    endX+=(x-tempX);
                    tempX=x;
                }
            }else if(current==States.NORTH||current==States.SOUTH){
                if(currentY==START_Y){
                    startY+=(y-tempY);
                    tempY=y;
                }else{
                    endY+=(y-tempY);
                    tempY=y;
                }
            }else if(current==States.NORTH_EAST||current==States.NORTH_EAST||
                    current==States.SOUTH_EAST||current==States.SOUTH_WEST){
                if(currentY==START_Y){
                    startY+=(y-tempY);
                    tempY=y;
                }else{
                    endY+=(y-tempY);
                    tempY=y;
                }
                if(currentX==START_X){
                    startX+=(x-tempX);
                    tempX=x;
                }else{
                    endX+=(x-tempX);
                    tempX=x;
                }                
            }else{
                startX=tempX;
                startY=tempY;
                endX=me.getX();
                endY=me.getY();
            }
            this.repaint();
        }
        //取消提示，获取当前点坐标
        public void mousePressed(MouseEvent me){
            showTip=false;
            tempX=me.getX();
            tempY=me.getY();
        }
        
        public void mouseReleased(MouseEvent me){
            if(me.isPopupTrigger()){ // 右键
                if(current==States.MOVE){//重新截屏
                    showTip=true;
                    p=me.getPoint();
                    startX=0;
                    startY=0;
                    endX=0;
                    endY=0;
                    repaint();
                } else{ // 普通情况，结束截屏
                    jf.dispose();
                   
                }
            }
        }
        //在所选区域内双击代表完成截屏
        public void mouseClicked(MouseEvent me){
            if(me.getClickCount()==2){
                
                Point p=me.getPoint();
                if(select.contains(p)){
                    if(select.x+select.width<this.getWidth()&&select.y+select.height<this.getHeight()){
                        get=bi.getSubimage(select.x,select.y,select.width,select.height);
                        jf.dispose();
                        doCopy(get);//保存到粘贴板
                    
                    }
                }
            }
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}

//一些表示状态的枚举
enum States{
    NORTH_WEST(new Cursor(Cursor.NW_RESIZE_CURSOR)),//表示西北角
    NORTH(new Cursor(Cursor.N_RESIZE_CURSOR)),
    NORTH_EAST(new Cursor(Cursor.NE_RESIZE_CURSOR)),
    EAST(new Cursor(Cursor.E_RESIZE_CURSOR)),
    SOUTH_EAST(new Cursor(Cursor.SE_RESIZE_CURSOR)),
    SOUTH(new Cursor(Cursor.S_RESIZE_CURSOR)),
    SOUTH_WEST(new Cursor(Cursor.SW_RESIZE_CURSOR)),
    WEST(new Cursor(Cursor.W_RESIZE_CURSOR)),
    MOVE(new Cursor(Cursor.MOVE_CURSOR)),
    DEFAULT(new Cursor(Cursor.DEFAULT_CURSOR));
    
    private Cursor cs;
    
    States(Cursor cs){
        this.cs=cs;
    }
    
    public Cursor getCursor(){
        return cs;
    }
}