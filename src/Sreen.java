


import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;
/**
 *  ����
 *  ��ѡ��������˫�����Ƶ�ճ���� 
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
            Robot ro=new Robot(); // ��ͨ�����ز�����������ꡢ���̵�ʵ������Դ��java.awt��
            Toolkit tk=Toolkit.getDefaultToolkit(); // AWT����ĳ����ࣨjava.awt��
            Dimension di=tk.getScreenSize();
            Rectangle rec=new Rectangle(0,0,di.width,di.height);
            BufferedImage bi=ro.createScreenCapture(rec);
            JFrame jf=new JFrame();
            Temp temp=new Temp(jf,bi,di.width,di.height); // �Զ����Temp��Ķ���
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
     *�����Ĵ���ѵ�ǰ��ͼƬ���������ķ���
     */
    public void doCopy(final BufferedImage image){
        try{
            Transferable trans = new Transferable(){ // �ڲ���
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
		 *  //һ����ʱ�࣬������ʾ��ǰ����Ļͼ��
		 */
		private static final long serialVersionUID = 1L;
		private BufferedImage bi;
        private int width,height;
        private int startX,startY,endX,endY,tempX,tempY;
        private JFrame jf;
        private Rectangle select=new Rectangle(0,0,0,0);//��ʾѡ�е�����
        private Cursor cs=new Cursor(Cursor.CROSSHAIR_CURSOR);//��ʾһ������µ����״̬��ʮ���ߣ�
        private States current=States.DEFAULT;// ��ʾ��ǰ�ı༭״̬
        private Rectangle[] rec;//��ʾ�˸��༭�������
        //�����ĸ�����,�ֱ��ʾ˭�Ǳ�ѡ�е��������ϵĶ˵�
        public static final int START_X=1;
        public static final int START_Y=2;
        public static final int END_X=3;
        public static final int END_Y=4;
        private int currentX,currentY;//��ǰ��ѡ�е�X��Y,ֻ����������Ҫ�ı�
        private Point p=new Point();//��ǰ����Ƶĵص�
        private boolean showTip=true;//�Ƿ���ʾ��ʾ.���������һ��,����ʾ�Ͳ�����ʾ��
        
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
            
            //�������������ĸ����㼰�����е�
            g.fillRect(x1-2,startY-2,5,5);
            g.fillRect(x1-2,endY-2,5,5);
            g.fillRect(startX-2,y1-2,5,5);
            g.fillRect(endX-2,y1-2,5,5);
            g.fillRect(startX-2,startY-2,5,5);
            g.fillRect(startX-2,endY-2,5,5);
            g.fillRect(endX-2,startY-2,5,5);
            g.fillRect(endX-2,endY-2,5,5);
            //��ǳ��˸������ڵ�С����
            rec[0]=new Rectangle(x-5,y-5,10,10);
            rec[1]=new Rectangle(x1-5,y-5,10,10);
            rec[2]=new Rectangle((startX>endX?startX:endX)-5,y-5,10,10);
            rec[3]=new Rectangle((startX>endX?startX:endX)-5,y1-5,10,10);
            rec[4]=new Rectangle((startX>endX?startX:endX)-5,(startY>endY?startY:endY)-5,10,10);
            rec[5]=new Rectangle(x1-5,(startY>endY?startY:endY)-5,10,10);
            rec[6]=new Rectangle(x-5,(startY>endY?startY:endY)-5,10,10);
            rec[7]=new Rectangle(x-5,y1-5,10,10);
            //��ʾ��Ϣ
            if(showTip){
                g.setColor(Color.CYAN);
                g.fillRect(p.x,p.y,170,40);
                g.setColor(Color.RED);
                g.drawRect(p.x,p.y,170,40);
                g.setColor(Color.BLACK);
                g.drawString("�밴ס����������ѡ��",p.x,p.y+15);
                g.drawString("��ͼ��  x:"+p.x+"\t  y:"+p.y,p.x,p.y+35);
                
            }
        }
        
        //���ݶ��������Ȱ˸��������ѡ�е�Ҫ�޸ĵ�X��Y������
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
            initSelect(current); // current����ǰ״̬��state��
            if(showTip){
                p=me.getPoint();
                repaint();
            }
        }
        
        //���ⶨ��һ��������������ƶ�,��Ϊ��ÿ�ζ��ܳ�ʼ��һ����Ҫѡ�������
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
            // �ֱ���һϵ�еģ���꣩״̬��ö��ֵ��
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
        //ȡ����ʾ����ȡ��ǰ������
        public void mousePressed(MouseEvent me){
            showTip=false;
            tempX=me.getX();
            tempY=me.getY();
        }
        
        public void mouseReleased(MouseEvent me){
            if(me.isPopupTrigger()){ // �Ҽ�
                if(current==States.MOVE){//���½���
                    showTip=true;
                    p=me.getPoint();
                    startX=0;
                    startY=0;
                    endX=0;
                    endY=0;
                    repaint();
                } else{ // ��ͨ�������������
                    jf.dispose();
                   
                }
            }
        }
        //����ѡ������˫��������ɽ���
        public void mouseClicked(MouseEvent me){
            if(me.getClickCount()==2){
                
                Point p=me.getPoint();
                if(select.contains(p)){
                    if(select.x+select.width<this.getWidth()&&select.y+select.height<this.getHeight()){
                        get=bi.getSubimage(select.x,select.y,select.width,select.height);
                        jf.dispose();
                        doCopy(get);//���浽ճ����
                    
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

//һЩ��ʾ״̬��ö��
enum States{
    NORTH_WEST(new Cursor(Cursor.NW_RESIZE_CURSOR)),//��ʾ������
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