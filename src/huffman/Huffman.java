package huffman;

import huffman.MyCellRenderer;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Label;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class Huffman extends Applet implements ActionListener, ChangeListener {
	//Předem se omlouvám za nevzhledný kód (ale funkční). Je to guláš, já vím. Kdyby se jednalo o obsáhlejší projekt a ne jen jednoúčelový aplet, dal bych si na čistotě záležet víc.	
	int width = 1200, height = 500;
	int stepCount;
	int stepCounter = 1;
	int lastMin;
	int lastMinRow;
	int lastMin2;
	int lastMinRow2;
	boolean isTableDone = false;
	String buffer;
	int pointerShift;
	
	String inputTextS;
	HashMap<Character, Integer> characterDB;
	StringBuilder sb;
	List<List<Integer>> parents = new ArrayList<List<Integer>>();
	
	Label vystupniRet;
	Label vstupniRet;
	JTextArea textInput;
	JScrollPane textInputPane;
	JTextArea textOutput;
	JScrollPane textOutputPane;
	Button hotovo;
	Button reset;
	Button krok;
	Button zpet;
	Button next;
	Button prev;
	JSlider outputSlider;
	
	JTable table;

	DefaultTableModel model = new DefaultTableModel(){
		
		 @Override
         public Class<?> getColumnClass(int columnIndex){
             if( columnIndex == 2){
            	 return Integer.class;
             }else{
            	 return String.class;
             }

             
         }
	    
		
	};
	JScrollPane tablePane;
	DefaultTableCellRenderer centerRenderer;
	MyCellRenderer mcr;
	
	public void init(){
		setLayout(null);
		width = getSize().width;		//nastavení velikosti okna
	    height = getSize().height;
	    setBackground(new Color(151, 183, 193));	//barva pozadí
	    
	    hotovo = new Button("Kóduj");
	    hotovo.addActionListener(this);
	    add(hotovo);
	    hotovo.setBounds(340, 130, 80, 20);
	    
	    reset = new Button("Reset");
	    reset.addActionListener(this);
	    add(reset);
	    reset.setBounds(200, 130, 80, 20);
	    reset.setEnabled(false);
	    
	    outputSlider = new JSlider(0, 100);
	    outputSlider.addChangeListener(this);
	    outputSlider.setValue(0);
	    outputSlider.setEnabled(false);
	    add(outputSlider);
	    outputSlider.setBounds(480, 130, 320, 20);
	    
	    next = new Button(">");
	    prev = new Button("<");
	    add(next);
	    add(prev);
	    prev.setBounds(440, 130, 30, 20);
	    next.setBounds(810, 130, 30, 20);
	    next.addActionListener(this);
	    prev.addActionListener(this);
	    
	    textInput = new JTextArea(5,10);
	    textInput.setLineWrap(true);
	    textInputPane = new JScrollPane(textInput);
	    add(textInputPane);
	    textInputPane.setBounds(20, 20, 400, 100);
	    
	    textOutput = new JTextArea(5,10);
	    textOutput.setLineWrap(true);
	    textOutput.setEditable(false);
	    textOutputPane = new JScrollPane(textOutput);
	    add(textOutputPane);
	    textOutputPane.setBounds(440, 20, 400, 100);
	    
	   
	    model.addColumn("Znak");
	    model.addColumn("Kód");
	    model.addColumn("Poč. četnost");
	    
	    table = new JTable(model);
	    	
	    tablePane = new JScrollPane(table);
	    add(tablePane);
	    tablePane.setBounds(20, 170, 820, 350);
	    
	    table.getColumnModel().getColumn(1).setMaxWidth(200);
	    table.getColumnModel().getColumn(1).setMinWidth(200);
	    centerRenderer = new DefaultTableCellRenderer();
	    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
	    table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
	    table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
	    table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
	    table.setEnabled(false);
	    table.setAutoCreateRowSorter(true);
	    
	    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    mcr = new MyCellRenderer();
	   
	    zpet = new Button("Zpět");
	    zpet.addActionListener(this);
	    add(zpet);
	    zpet.setEnabled(false);
	    zpet.setBounds(180, 525, 100, 30);
	    
	    krok = new Button("Sestavení Huffmanovy tabulky: krok 0/0");
	    krok.addActionListener(this);
	    add(krok);
	    krok.setEnabled(false);
	    krok.setBounds(300, 525, 300, 30);
	    
	    vstupniRet = new Label("Vstupní řetězec:");
	    add(vstupniRet);
	    vstupniRet.setBounds(20, 1, 100, 20);
	    
	    vystupniRet = new Label("Výstupní řetězec:");
	    add(vystupniRet);
	    vystupniRet.setBounds(440, 1, 100, 20);
	    
	    characterDB = new HashMap<Character, Integer>();
	    
	    prev.setEnabled(false);
	    next.setEnabled(false);
	}

	@Override
	public void stateChanged(ChangeEvent chev) {
		outputSlider = (JSlider)chev.getSource();
		
		if(chev.getSource() == outputSlider){
			if (isTableDone == true){
			
			buffer = textInput.getText();
			pointerShift=0;
			mcr.cellColor.clear();
			sb = new StringBuilder(buffer);			
			
			String buffer2;
			
		for (int i=0; i<buffer.length(); i++){
			
			if(i <= outputSlider.getValue()){		//pokud je zpracovávnaná hodna pod nebo na slideru, nahradí se kódem
					
				buffer2 = Character.toString(buffer.charAt(i));
					
					for(int b=0; b<table.getRowCount(); b++){		//vyhledávání v tabulce
						if(table.getValueAt(b, 0).toString().equals(buffer2)){
						if (buffer2.equals( Character.toString(buffer.charAt(outputSlider.getValue()))) ){mcr.cellColor.add(String.format("%d1", b));}	//obarvení
						sb.replace(i+pointerShift, i+pointerShift+1, table.getValueAt(b, 1).toString());
						 buffer2 = table.getValueAt(b, 1).toString();
						 
						}	
					}
				pointerShift = pointerShift+buffer2.length()-1;
			}
		}	
		
		
		table.getColumnModel().getColumn(1).setCellRenderer(mcr);
		table.repaint();
				
			textOutput.setText(sb.toString());	
			}			
		}
	}

	@Override
	public void actionPerformed(ActionEvent aev) {
	if(aev.getSource() == hotovo){
		
		if (textInput.getText().length() == 0){
			textInput.setText("Nezadali jste do vstupu žádný text! Tak teď už tu nějaký máte :)");	
		}
		inputTextS = textInput.getText();
		countChar();
		if(characterDB.size()<=2){textInput.setText("Musíte zadat alespoň tři rozdílné znaky."); }
		else{
			
			hotovo.setEnabled(false);
			textInput.setEditable(false);
			textInput.setEnabled(false);
			
			reset.setEnabled(true);
			
			stepCount = characterDB.size();
			sortInTable();
			krok.setLabel(String.format("Sestavení Huffmanovy tabulky: krok 0/%d", stepCount-1));
			krok.setEnabled(true);
			
			table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		    table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		    table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		    
		    table.getRowSorter().toggleSortOrder(2);
		}
		}
		if (aev.getSource() == reset){
			textInput.setText("");
			hotovo.setEnabled(true);
			textInput.setEditable(true);
			textInput.setEnabled(true);
			reset.setEnabled(false);
			krok.setEnabled(false);
			mcr.cellColor.clear();
			parents.clear();
			characterDB.clear();
			model.setRowCount(0);
			model.setColumnCount(3);
			stepCounter = 1;
			isTableDone = false;
			outputSlider.setEnabled(false);
			next.setEnabled(false);
			prev.setEnabled(false);
			isTableDone = false;
			outputSlider.setValue(0);
			textOutput.setText("");
			pointerShift = 0;
			zpet.setEnabled(false);
		}
		if(aev.getSource() == krok){
		
			krok.setLabel(String.format("Sestavení Huffmanovy tabulky: krok %d/%d",stepCounter, stepCount-1));	
		    
		    if((stepCounter < stepCount)&&(stepCounter != 1)){					// DALŠÍ KROKY
		    if(stepCounter > 1){zpet.setEnabled(true);}
		    		krok();	
					tableColor();
					stepCounter++;
			    if (stepCounter == stepCount){krok.setEnabled(false);
			    textOutput.setText(inputTextS);
			    outputSlider.setMaximum(inputTextS.length()-1);
			    outputSlider.setEnabled(true);
			    next.setEnabled(true);
			    prev.setEnabled(true);
			    isTableDone = true;
			//    sb = new StringBuilder(textOutput.getText());
			    mcr.cellColor.clear();
				table.repaint();
			   }
			  
		    }
		    
		    
		   if (stepCounter == 1){												//PRVNÍ KROK
		    krok1();
		    tableColor();
		    stepCounter++;
		    
		 }  
		}
		
		if(aev.getSource() == zpet){
			stepCounter--;
	 
		model.setColumnCount(3+stepCounter);
		
		int spetCounterPrev = stepCounter;
		
		
		mcr.cellColor.clear();
		parents.clear();
		characterDB.clear();
		model.setRowCount(0);
		model.setColumnCount(3);
		stepCounter = 1;
		isTableDone = false;
		countChar();
		sortInTable();
		
		while(stepCounter < spetCounterPrev){
		if(stepCounter == 1){krok1(); stepCounter++;}
		else{krok(); stepCounter++;}
		
		if (krok.isEnabled() == false){
			krok.setEnabled(true);
			isTableDone = false;
			outputSlider.setEnabled(false);
			next.setEnabled(false);
			prev.setEnabled(false);
			textOutput.setText("");
			outputSlider.setValue(0);
			pointerShift = 0;
		}
		}
		
		for(int i=0; i<stepCounter+2; i++){
			table.getColumnModel().getColumn(i).setCellRenderer(mcr);}
		
		if (stepCounter <= 2){
			zpet.setEnabled(false);
		}
		krok.setLabel(String.format("Sestavení Huffmanovy tabulky: krok %d/%d", spetCounterPrev-1, stepCount-1));
		}
	if (aev.getSource() == next){
	if(outputSlider.getValue() != outputSlider.getMaximum() ){	
		outputSlider.setValue(outputSlider.getValue()+1);}
	}
	if (aev.getSource() == prev){
		if (outputSlider.getValue() != outputSlider.getMinimum()){
		outputSlider.setValue(outputSlider.getValue()-1);}
	}
		
	}
	
	public void krok(){
		mcr.cellColor.clear();
    	
    	model.addColumn(String.format("%d. krok", stepCounter));
		for (int i=1; i<stepCounter; i++){
			table.getColumnModel().getColumn(2+i).setMaxWidth(60);
			table.getColumnModel().getColumn(2+i).setMinWidth(60);
			table.getColumnModel().getColumn(2+i).setCellRenderer(centerRenderer);}

	    table.getColumnModel().getColumn(1).setMinWidth(200);
	   
	    table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
	    table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
	    table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

	    lastMin = 500;
	    lastMinRow = 0;
	    lastMin2 = 500;
	    lastMinRow2 = 0;
    	
	    for(int i=1; i<stepCounter; i++){
	    	table.moveColumn(3+i, 3);
	    }
	    
	    for(int i=0; i<parents.size(); i++){		
			   if(parents.get(i).get(0) <= lastMin){lastMin = parents.get(i).get(0); lastMinRow = i;} 
		   }
		for(int i=0; i<parents.size(); i++){
			   if((parents.get(i).get(0) <= lastMin2)&&(i != lastMinRow)){lastMin2 = parents.get(i).get(0); lastMinRow2 = i;} 
		   }
    
		// vytvoří se nový prvek, jehož četnost je součtem dvou nejmenších četností, které se následně vyřadí
		   parents.add(new ArrayList<Integer>());					//nový prvek
		   parents.get(parents.size()-1).add(lastMin+lastMin2);		//nová četnost
		   
	for(int a=1; a<parents.get(lastMinRow).size() ;a++){						//všechny prvky prvku se zkopírují
		parents.get(parents.size()-1).add(parents.get(lastMinRow).get(a));
	}	
	for(int a=1; a<parents.get(lastMinRow2).size() ;a++){
		parents.get(parents.size()-1).add(parents.get(lastMinRow2).get(a));
	}
		   
	for(int a=1; a<parents.get(lastMinRow).size(); a++){							//menším prvkům dáme 0
		 sb = new StringBuilder("0");
		 sb.append(table.getValueAt(parents.get(lastMinRow).get(a), 1).toString());
		 table.setValueAt(sb.toString(), parents.get(lastMinRow).get(a), 1);
	}
	for(int a=1; a<parents.get(lastMinRow2).size(); a++){
		 sb = new StringBuilder("1");
		 sb.append(table.getValueAt(parents.get(lastMinRow2).get(a), 1).toString());	//větším 1
		 table.setValueAt(sb.toString(), parents.get(lastMinRow2).get(a), 1);
	}
	
	for(int a=1; a<parents.get(lastMinRow).size() ;a++){						//BARVENÍ
		mcr.cellColor.add(String.format("%d3", parents.get(lastMinRow).get(a)));
		mcr.cellColor.add(String.format("%d1", parents.get(lastMinRow).get(a)));
		mcr.cellColor.add(String.format("%d4", parents.get(lastMinRow).get(a)));
	}	
	for(int a=1; a<parents.get(lastMinRow2).size() ;a++){
		mcr.cellColor.add(String.format("%d3", parents.get(lastMinRow2).get(a)));
		mcr.cellColor.add(String.format("%d1", parents.get(lastMinRow2).get(a)));
		mcr.cellColor.add(String.format("%d4", parents.get(lastMinRow2).get(a)));
	}
	
	if (lastMinRow>lastMinRow2){parents.remove(lastMinRow); parents.remove(lastMinRow2);}		//smazání prvků
	else{parents.remove(lastMinRow2); parents.remove(lastMinRow);}
	
	
	for (int i=0; i<parents.size(); i++){
		   if(parents.get(i).size() >= 3){					   
			  for(int b=1; b<parents.get(i).size(); b++){
				 table.setValueAt(parents.get(i).get(0), parents.get(i).get(b), 3);
				  }
		   }  
		   else{table.setValueAt(parents.get(i).get(0), parents.get(i).get(1), 3);}
	   }
	    
	table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
	
	table.getRowSorter().toggleSortOrder(2);
	
		
	}
	
	public void krok1(){
		krok.setLabel(String.format("Sestavení Huffmanovy tabulky: krok %d/%d",stepCounter, stepCount-1));
    	model.addColumn(String.format("%d. krok", stepCounter));
		for (int i=1; i<stepCounter; i++){
			table.getColumnModel().getColumn(2+i).setMaxWidth(60);
			table.getColumnModel().getColumn(2+i).setMinWidth(60);
			table.getColumnModel().getColumn(2+i).setCellRenderer(centerRenderer);}

	    table.getColumnModel().getColumn(1).setMinWidth(200);
	    table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
	    table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
	    table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
	    table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
	    
	    lastMin = 500;
	    lastMinRow = 0;
	    lastMin2 = 500;
	    lastMinRow2 = 0;
	  
	    for(int i=0; i<stepCount; i++){			//do listu se přidají všechny prvky tabulky
	    parents.add(new ArrayList<Integer>());
	    parents.get(parents.size()-1).add((int)table.getValueAt(i, 2));
	    parents.get(parents.size()-1).add(i);
	    }
	  
	   //FRONTA
	    
	   for(int i=0; i<parents.size(); i++){		
		   if(parents.get(i).get(0) <= lastMin){lastMin = parents.get(i).get(0); lastMinRow = i;} 
	   }
	   for(int i=0; i<parents.size(); i++){
		   if((parents.get(i).get(0) <= lastMin2)&&(i != lastMinRow)){lastMin2 = parents.get(i).get(0); lastMinRow2 = i;} 
	   } 
// vytvoří se nový prvek, jehož četnost je součtem dvou nejmenších četností, které se následně vyřadí
	   parents.add(new ArrayList<Integer>());					//nový prvek
	   parents.get(parents.size()-1).add(lastMin+lastMin2);		//nová četnost
	   parents.get(parents.size()-1).add(parents.get(lastMinRow).get(1));		//řádek prvního prvku
	   parents.get(parents.size()-1).add(parents.get(lastMinRow2).get(1));		//řádek druhého prvku
	   
	   mcr.cellColor.add(String.format("%d3", parents.get(lastMinRow).get(1)));
	   mcr.cellColor.add(String.format("%d3", parents.get(lastMinRow2).get(1)));
	   mcr.cellColor.add(String.format("%d2", parents.get(lastMinRow).get(1)));
	   mcr.cellColor.add(String.format("%d2", parents.get(lastMinRow2).get(1)));
	   mcr.cellColor.add(String.format("%d1", parents.get(lastMinRow).get(1)));
	   mcr.cellColor.add(String.format("%d1", parents.get(lastMinRow2).get(1)));
	   
	   table.setValueAt(0, parents.get(lastMinRow).get(1), 1); // řádek nejmenšího prvku - přidá se 0			   
	   table.setValueAt(1, parents.get(lastMinRow2).get(1), 1); // druhý nejmenší prvek (větší) - 1
	if (lastMinRow>lastMinRow2){parents.remove(lastMinRow); parents.remove(lastMinRow2);}
	else{parents.remove(lastMinRow2); parents.remove(lastMinRow);}
	   
//projde se list a každý prvek podle hodnoty řádku vypíše četnost	
	   for (int i=0; i<parents.size(); i++){
		   if(parents.get(i).size() >= 3){					   
			  for(int b=1; b<parents.get(i).size(); b++){
				 table.setValueAt(parents.get(i).get(0), parents.get(i).get(b), 3);
				  } 
		   }
		   else{table.setValueAt(parents.get(i).get(0), parents.get(i).get(1), 3);}
	   }
	   table.getRowSorter().toggleSortOrder(2);
	   
	}


	public void tableColor(){
		for(int i=0; i<stepCounter+3; i++){
		table.getColumnModel().getColumn(i).setCellRenderer(mcr);
		}    
	}
	
	public void countChar(){
		for(int i=0; i<inputTextS.length(); i++){
			if (characterDB.containsKey(inputTextS.charAt(i))){
				characterDB.put(inputTextS.charAt(i), characterDB.get(inputTextS.charAt(i))+1);
			}
			else{
				characterDB.put(inputTextS.charAt(i), 1);
			}
		}
	}
	
	public void sortInTable(){
		 Iterator it = characterDB.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        model.insertRow(0, new Object [] {pairs.getKey(),"", pairs.getValue(),});
		        it.remove(); // avoids a ConcurrentModificationException
		    }
	}
	

}