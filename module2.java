import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.awt.Graphics;
//import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.table.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.font.*;
import java.awt.Font;

class LinesComponent extends JComponent{

private static class Line{
    final int x1; 
    final int y1;
    final int x2;
    final int y2;   
    final Color color;

    public Line(int x1, int y1, int x2, int y2, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }               
}

private final LinkedList<Line> lines = new LinkedList<Line>();

public void addLine(int x1, int x2, int x3, int x4) {
	//System.out.println("jj0");
    addLine(x1, x2, x3, x4, Color.black);
}

public void addLine(int x1, int x2, int x3, int x4, Color color) {
   // System.out.println("HHHHH");
    lines.add(new Line(x1,x2,x3,x4, color));        
    repaint();
}

public void clearLines() {
    lines.clear();
    repaint();
}

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    //System.out.println("sss0");
    for (Line line : lines) {
        g.setColor(line.color);
        g.drawLine(line.x1, line.y1, line.x2, line.y2);
    }
}
}

class backEnd{
	int buckSize,gDepth,directorySize;
	List<HashMap<String,List<String>>> hashList;

	backEnd(){}
	backEnd(List<HashMap<String,List<String>>> hashList,int buckSize){
		this.buckSize = buckSize;
		this.hashList = hashList;
		directorySize = hashList.get(0).size();
		gDepth = (int)(Math.log(directorySize)/Math.log(2.0));
	}

	String hashFunc(int x,int depth){
		//System.out.println(gDepth);
		String ret = "";
		int leng = 0;
		while(leng<depth){
			int y = x%2;
			ret = y + ret;
			x = x/2;
			leng = leng+1;
		}
		return ret;
	}

	List<String> splitBuck(List<String> curBuck,String curBuckId,int x1,int reqDepth){
		String newId1 = curBuckId + "0";
		String newId2 = curBuckId + "1";
		String hashedVal = hashFunc(x1,reqDepth-1);

		String newHashVal1 = "0" + hashedVal;
		String newHashVal2 = "1" + hashedVal;

		List<String> newList1 = new ArrayList<String>();
		List<String> newList2 = new ArrayList<String>();

		for(String x:curBuck){
			String curHashedVal = hashFunc(Integer.parseInt(x),reqDepth);
			if(curHashedVal.compareTo(newHashVal1)==0){
				newList1.add(x);
			}
			else{
				newList2.add(x);
			}
		}
		hashList.get(1).remove(curBuckId);
		hashList.get(1).put(newId1,newList1);
		hashList.get(1).put(newId2,newList2);

		List<String> ret = new ArrayList<String>(Arrays.asList(newId1,newId2,newHashVal1,newHashVal2));
		return ret;
	}

	List<HashMap<String,List<String>>> insert(int x){
		//System.out.println(gDepth);
		String hashedVal = hashFunc(x,gDepth);
		System.out.println(hashedVal);
		String curBuckId = hashList.get(0).get(hashedVal).get(0);
		int curLocDepth = Integer.parseInt(hashList.get(0).get(hashedVal).get(1));
		List<String> curBuck = new ArrayList<String>();
		curBuck = hashList.get(1).get(curBuckId);
		curBuck.add(Integer.toString(x));
		int curBuckSz = curBuck.size();
		if(curBuckSz>buckSize){
			//hashList.get(1).put(curBuckId,curBuck);

			List<String> newIds = splitBuck(curBuck,curBuckId,x,curLocDepth+1);	
			HashMap<String,List<String>> newMP = new HashMap<String,List<String>>();
		if(curLocDepth<gDepth){
			List<String> ls;
			for(Map.Entry<String,List<String>> ent: hashList.get(0).entrySet()){
				String curKey = ent.getKey();
				List<String> curVal = ent.getValue();
				if(curVal.get(0).compareTo(curBuckId)==0){
					System.out.println(curLocDepth+" "+gDepth +"###" + curKey + " " + curKey.charAt(gDepth-curLocDepth));
					if(curKey.charAt(gDepth-curLocDepth-1)=='0'){
						System.out.println("hah1");
						ls = new ArrayList<String>(Arrays.asList(newIds.get(0),Integer.toString(curLocDepth+1)));
					}
					else{
						System.out.println("hah2");
						ls = new ArrayList<String>(Arrays.asList(newIds.get(1),Integer.toString(curLocDepth+1)));
					}
				}
				else{
					ls = new ArrayList<String>(Arrays.asList(curVal.get(0),curVal.get(1)));
				}
				newMP.put(curKey,ls);
			}
			hashList.set(0,newMP);
		}
		else{
			for(Map.Entry<String,List<String>> ent: hashList.get(0).entrySet()){
				String curKey = ent.getKey();
				List<String> curVal = ent.getValue();

				if(curKey.compareTo(hashedVal)==0){
					List<String> l = new ArrayList<String>(Arrays.asList(newIds.get(0),Integer.toString(curLocDepth+1)));
					newMP.put(newIds.get(2),l);
					l = new ArrayList<String>(Arrays.asList(newIds.get(1),Integer.toString(curLocDepth+1)));
					newMP.put(newIds.get(3),l);
				}
				else{
					String nh1 = "0" + curKey;
					String nh2 = "1" + curKey;
					List<String> l = new ArrayList<String>(Arrays.asList(curVal.get(0),curVal.get(1)));
					newMP.put(nh1,l);
					l = new ArrayList<String>(Arrays.asList(curVal.get(0),curVal.get(1)));
					newMP.put(nh2,l);
				}
			}

			hashList.set(0,newMP);

		}
	}
		System.out.println("\n\n__");
		return hashList;
	}
}

class frontEnd implements ActionListener{
	JFrame ff,f;
	JTextField tf;
	JButton insrt,srch;
	int showCount=0;
	Color redi = new Color(255,0,0);
	Color bluei = new Color(0,0,255);
	//JTable j;
	int bSize,directorySize,numOfBucks,gDepth=1;


	List<HashMap<String,List<String>>> hashList;

	frontEnd(){}
	frontEnd(int buckSize){
		bSize = buckSize;

		initHashList();

		ff = new JFrame();
		f = new JFrame();
		tf = new JTextField();
		tf.setBounds(50,50,100,20);

		insrt = new JButton("Insert");
		insrt.setBounds(10,85,85,30);

		srch = new JButton("Search");
		srch.setBounds(105,85,85,30);

		//JTextField tfex = new JTextField();
		//tfex.setBounds(700,700,100,20);

		/*Integer[][] data = {{1,2,3}};
		String[] cols = {"","",""};
		j = new JTable(data,cols);
		j.setBounds(500,500,100,20);*/
	//	JScrollPane sp = new JScrollPane(j);
		//showHashList();

		insrt.addActionListener(this);
		srch.addActionListener(this);


		ff.add(tf);
		ff.add(insrt);
		ff.add(srch);
		//f.add(j);


		ff.setSize(200,200);
		ff.setLayout(null);
		ff.setVisible(true);
		//f.add(tfex);
	}

	public void actionPerformed(ActionEvent e){

		String s1 = tf.getText();
		int x = Integer.parseInt(s1);
		backEnd backEndObj = new backEnd(hashList,bSize);
		String hashedVal = backEndObj.hashFunc(x,gDepth);
		if(e.getSource()==insrt){
			hashList = backEndObj.insert(x);
			printHL();
			showHashList(40+showCount*400,-5,0,hashedVal,x);
		}
		else if(e.getSource()==srch){
			
			showHashList(40+showCount*400,-5,1,hashedVal,x);
		}
		tf.setText("");
	}

	public void printHL(){
		HashMap<String,List<String>> hm1 = hashList.get(0);
		HashMap<String,List<String>> hm2 = hashList.get(1);

		for(Map.Entry<String,List<String>> entry:hm1.entrySet()){
			List<String> li = entry.getValue();
			String key = entry.getKey();
			System.out.println("key:"+key);
			for(String st:li){
				System.out.print(st + " ");
			}
			System.out.println("++");
		}
		for(Map.Entry<String,List<String>> entry:hm2.entrySet()){
			List<String> li = entry.getValue();
			String key = entry.getKey();
			System.out.println("key:"+key);
			for(String st:li){
				System.out.print(st + " ");
			}
			System.out.println("##");
		}
	}

	public void initHashList(){
		HashMap<String,List<String>> hm1 = new HashMap<String,List<String>>();
		HashMap<String,List<String>> hm2 = new HashMap<String,List<String>>();
		List<String> l1 = new ArrayList<String>(Arrays.asList("0","1"));
		hm1.put("0",l1);
		l1 = new ArrayList<String>(Arrays.asList("1","1"));
		hm1.put("1",l1);

		l1 = new ArrayList<String>();
		hm2.put("0",l1);
		l1 = new ArrayList<String>();
		hm2.put("1",l1);
		hashList = new ArrayList<HashMap<String,List<String>>>(Arrays.asList(hm1,hm2));
	}

	public void showHashList(int startx,int starty,int high,String hashedVal,int srx){
		/*Integer[][] data = {{1,2,3}};
		String[] cols = {"","",""};
		j = new JTable(data,cols);
		j.setBounds(500,500,100,20);*/
	//	JScrollPane sp = new JScrollPane(j);
		//showHashList();
		
		//f.setBounds(startx,starty,600,500);
		//showCount = showCount+1;
		
		f.getContentPane().removeAll();
		f.repaint();
		Map<String,Integer> cordsTB1 = new HashMap<String,Integer>();
		numOfBucks = hashList.get(1).size();
		directorySize = hashList.get(0).size();
		gDepth = (int)(Math.log(directorySize)/Math.log(2.0));
		String[][] table1 = new String[directorySize][1];
		int ro=0;
		Map<String,String> lds = new HashMap<String,String>();
		for(Map.Entry<String,List<String>> ent: hashList.get(0).entrySet()){
			table1[ro][0] = ent.getKey();
			List<String> curVal = ent.getValue();
			cordsTB1.put(ent.getKey(),starty+60+40*ro);
			lds.put(curVal.get(0),curVal.get(1));
			ro++;
		}
		String colH = "GD:" + Integer.toString(gDepth);
		String[] cols = {"colh"};
		//for(String x:hashList)
		JTable jt1 = new JTable(table1,cols);
		jt1.setBounds(startx+20,starty+40,80,40*directorySize);
		jt1.setRowHeight(40);





		int tb=0;
		Map<String,Integer> cordsTB2 = new HashMap<String,Integer>();
		for(Map.Entry<String,List<String>> ent: hashList.get(1).entrySet()){
			String curKey = ent.getKey();
			List<String> curVal = ent.getValue();
			String[] tmpAr = new String[curVal.size()];
			curVal.toArray(tmpAr);
			String[][] table2 = {tmpAr};
			String[] cols2 = new String[curVal.size()];
			Arrays.fill(cols2,"");

			JTable jt2 = new JTable(table2,cols2);
			jt2.setBounds(startx+300,starty+20+50*tb,curVal.size()*40,30);
			jt2.setRowHeight(30);

			cordsTB2.put(curKey,starty+35+50*tb);
			tb = tb+1;
			f.add(jt2);

			JTextField locD = new JTextField();

			String curLocD = lds.get(curKey);
			locD.setText("LD:" + curLocD);
			locD.setBounds(startx+330+curVal.size()*40,starty+20+50*(tb-1),40,30);
			locD.setEditable(false);
			f.add(locD);

		//	JTextField tf3 = new JTextField(hashList.get(0).get());
		//	tf3.setBounds(startx+300+curVal.size()*40+10);
		}
		f.add(jt1);

		JTextField tf4 = new JTextField();
		tf4.setBounds(250,550,200,40);
		String disp = srx + " hashed to " + hashedVal + " ,GD:"+ gDepth;
		tf4.setText(disp);

		JTextField tf5 = new JTextField();
		tf5.setBounds(275,600,150,40);
		String disp2;
		if(high==0){
			disp2 = "Inserted";
		}
		else{
			String tof = Integer.toString(srx);
			int fnd=0;
			String curBuckId = hashList.get(0).get(hashedVal).get(0);
			List<String> curList = hashList.get(1).get(curBuckId);
			int cellnum=0;
			for(String xx:curList){
				if(xx.compareTo(tof)==0){
					fnd=1;
					break;
				}
				cellnum = cellnum+1;
			}
			if(fnd==0){
				disp2 = "Not Found";
			}
			else{
				disp2 = "Found";

				Font font1 = new Font("SansSerif", Font.BOLD, 10);
				JTextField tf6 = new JTextField();
				tf6.setBounds(startx+300+cellnum*40,cordsTB2.get(curBuckId)-30,40,15);
				tf6.setFont(font1);
				tf6.setText("HERE");
				tf6.setEditable(false);
				f.add(tf6);
			}
		}
		tf4.setHorizontalAlignment(JTextField.CENTER);
		tf5.setHorizontalAlignment(JTextField.CENTER);
		tf5.setText(disp2);
		tf4.setEditable(false);
		tf5.setEditable(false);
		f.add(tf4);
		f.add(tf5);

		//TableColumnModel columnModel = table.getColumnModel();
		//columnModel.getColumn(0).setPreferredWidth(80);
		//Color randomColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
		final LinesComponent lcObj = new LinesComponent();
		//lcObj.setPreferredSize(new Dimension(320, 200));
		f.add(lcObj,BorderLayout.CENTER);
		for(Map.Entry<String,List<String>> ent:hashList.get(0).entrySet()){
			String curKey = ent.getKey();
			List<String> curVal = ent.getValue();
			int y1 = cordsTB1.get(curKey);
			int y2 = cordsTB2.get(curVal.get(0));
			if(high==1 && curKey.compareTo(hashedVal)==0){
				lcObj.addLine(startx+100,y1,startx+300,y2,bluei);
				continue;
			}
			lcObj.addLine(startx+100,y1,startx+300,y2);
		}
		
		

		//f.revalidate();
	//	f.setLayout(null);
		f.setSize(750,750);
		f.setVisible(true);
		//ff.add(f);
	}
}
public class module2{
	public static void main(String[] args){
		int buckSize = args.length==0 ? 3:Integer.parseInt(args[0]);
		frontEnd frontEndObj = new frontEnd(buckSize);
		//while(true){}

	}
}