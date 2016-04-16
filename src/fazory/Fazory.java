package fazory;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;



public class Fazory extends Applet implements Runnable, ActionListener, ItemListener {
	//Předem se omlouvám za nevzhledný kód (ale funkční). Je to guláš, já vím. Kdyby se jednalo o obsáhlejší projekt a ne jen jednoúčelový aplet, dal bych si na čistotě záležet víc.
	
	private static final long serialVersionUID = 1L;
	boolean isPaused = true;
	boolean isRendered = false;
	boolean isIdealniOn = false;
	boolean chybaZadani = false;
	
	int width, height;
	
   Image bufferFazor;		//buffer pro fazory
   Image bufferCas;			//buffer pro casovy prubeh
   
   Graphics backgBuffer;	//grafika pro vykreslování
   Graphics backgBuffer2;
   
   Image fazorPoz;		//pozadí grafu fázorů
   Image fazorVyk;		//pro průběžné kreslení dráhy
   Image casPoz;		//pozadí časové osy
   Image casVykr;		//pro průběžné kreslení časové osy
   
   Button startButton;	//tlačítka
   Button pauseButton;
   Button stopButton;
   Button idealSigButton;
   Button kopirujButton;
   
   Choice speedChoice;		//roletka
   Choice harmonicsChoice;
   
   Label speedOfRender;
   Label harmCount;
   Label vyberSig;
   
   CheckboxGroup checkGroup;		//checkboxy
   Checkbox checkB0;
   Checkbox checkB1;
   Checkbox checkB2;
   Checkbox checkB3;
   Checkbox checkB4;
   Checkbox checkB5;
   
   JTable table;
   
   DefaultTableModel model = new DefaultTableModel() {		//první sloupec je needitovatelný


       boolean[] canEdit = new boolean[]{
               false, true, true
       };

       public boolean isCellEditable(int rowIndex, int columnIndex) {
           return canEdit[columnIndex];
       }
   };
   
   int frame;
   int delay;
   Thread animator;
   
   int counter;
   int counter2;
   int counter3;
   int counter4;
   
   double startX;
   double startY;
   double endX = 150; //150 kvůli správnému počátku vykreslování fázora
   double endXprev;
   double endY = 150;
   double endYprev;
      
   double angle;
   double speed;
   
   int chosenSingal;
   
   
   short [][] harmonic = {
	  {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
	  {1,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
	  {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	  {1,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
	  {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	  {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
   };
   
   double [][] modules = {
		   	  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	    	  {0, 1, 0.3333, 0.2, 0.1428, 0.1111, 0.0909, 0.0769, 0.0666, 0.0588, 0.0526, 0.0476, 0.0434, 0.04, 0.037, 0.0344 }, //obdélník
	    	  {0, 1, 0.5, 0.3333, 0.25, 0.2, 0.166, 0.1428, 0.125, 0.1111, 0.1, 0.0909, 0.083, 0.0769, 0.071, 0.0666}, 	//pila  
	    	  {0, 2, 0.2222, 0.08, 0.0408, 0.0246, 0.0156, 0.0118, 0.0088, 0.0069, 0.0055, 0.0045, 0.0037, 0.0032, 0.0027, 0.0023},	//trojúhelník
	    	  {0.2423, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375, 0.375}, 	  //impulsy
	    	  {0, 0.25, 0.28, 0.165, 0.5925, 0.345, 0.1675, 0.3325, 0.34, 0.865, 0.24, 0.34, 0.2675, 0.54, 0.58, 0.23, 0.00275}
   };
   double [][] phase = {
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},   
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326},
		{1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326,1.570796326},
		{0, 0.52216,0.026932,-0.867048,-0.788600,-1.018990,0.932362,1.478178,-1.241210,2.192161,0.390221,-1.199971,-0.344500,2.436029,0.248110,-2.492683}
   };
   
   public void init() {
	  // Panel panelDisplay = new Panel();
	   setLayout(null); 
	   
	  width = getSize().width;		//nastavení velikosti okna
      height = getSize().height;
      setBackground(new Color(151, 183, 193));	//barva pozadí
      
      startButton = new Button("Přehrej");		//tlačítka start, pause, stop
      startButton.addActionListener(this);
      
      pauseButton = new Button("Pauza");
      pauseButton.addActionListener(this);
      pauseButton.setEnabled(false);
      
      stopButton = new Button("Stop");
      stopButton.addActionListener(this);
      stopButton.setEnabled(false);
      
      kopirujButton = new Button("Edituj připravené hodnoty");
      kopirujButton.addActionListener(this);
      kopirujButton.setEnabled(false);
      
      idealSigButton = new Button("Ideální signál vypnut");
      idealSigButton.addActionListener(this);
      
      speedChoice = new Choice();			//nastavení rychlostí
      speedChoice.addItem("pomalá");
      speedChoice.addItem("střední");
      speedChoice.addItem("rychlá");
      speedChoice.select(1);				//nastavení základní rychlosti
      
      harmonicsChoice = new Choice();		//počet harmonických do výběru
      while (counter < 16) {
		harmonicsChoice.addItem(String.format("%d", counter));
		counter++;
	}
      counter = 0;
      harmonicsChoice.select(4);
      
      speedOfRender = new Label("Rychlost vykreslování:");			//nastavení labelů
      speedOfRender.setFont(new Font("Arial", Font.PLAIN, 16));
      harmCount = new Label("Počet harmonických složek:");
      harmCount.setFont(new Font("Arial", Font.PLAIN, 16));
      vyberSig = new Label("Výběr typu signálu:");
      vyberSig.setFont(new Font("Arial", Font.PLAIN, 16));
    
      CheckboxGroup checkGroup = new CheckboxGroup();				//nastavení checkboxů výběru signálu
      checkB0 = new Checkbox("Vlastní (definujte spektrum)", checkGroup,false);
      checkB0.addItemListener(this);
      
      checkB1 = new Checkbox("Obdélník", checkGroup,true);
      checkB2 = new Checkbox("Pila", checkGroup,false);
      checkB3 = new Checkbox("Trojúhelník", checkGroup,false);
      checkB4 = new Checkbox("Impuls", checkGroup,false);
      checkB5 = new Checkbox("Pseudošum", checkGroup,false);
      
      	add(startButton);     									//vloží prvky GUI do appletu
      	startButton.setBounds(5, 315, 80, 25);
      	add(pauseButton);
      	pauseButton.setBounds(105, 315, 80, 25);
      	add(stopButton);
      	stopButton.setBounds(205, 315, 80, 25);
      	
      	add(speedOfRender);
      	speedOfRender.setBounds(5, 355, 160, 25);
      	add(speedChoice);
     	speedChoice.setBounds(170, 355, 100, 20);
      	
      	add(harmCount);
      	harmCount.setBounds(350, 315, 195, 25);
      	add(harmonicsChoice);
     	harmonicsChoice.setBounds(610, 315, 50, 20);
     	
     	add(idealSigButton);
     	idealSigButton.setBounds(350, 355, 125, 25);
     	add(kopirujButton);
     	kopirujButton.setBounds(495, 355, 170, 25);
     	
     	add(vyberSig);
     	vyberSig.setBounds(5, 400, 135, 25);
     	add(checkB0);
     	checkB0.setBounds(25, 425, 170, 25);
     	add(checkB1);
     	checkB1.setBounds(25, 450, 120, 25);
     	add(checkB2);
     	checkB2.setBounds(25, 475, 120, 25);
     	add(checkB3);
     	checkB3.setBounds(25, 500, 120, 25);
     	add(checkB4);
     	checkB4.setBounds(25, 525, 120, 25);
     	add(checkB5);
     	checkB5.setBounds(25, 550, 120, 25);
     	
     	  		//definice tabulky a modelu
     	     	
     	table = new JTable(model);     	
        JScrollPane scrollPane = new JScrollPane(table);
         
         model.addColumn("Harmonická");
         model.addColumn("Amplituda");
         model.addColumn("Počáteční fáze (stupně)");
     	
     	JPanel tabulkaPanel = new JPanel();				//nastavení panelu pro tabulku
     	tabulkaPanel.setLayout(new BorderLayout());
     	tabulkaPanel.setBounds(350, 400, 400, 180);
     	add(tabulkaPanel);
     		
     	tabulkaPanel.add(scrollPane,BorderLayout.CENTER);	//vložení scrollPane s tabulkou
     	
  
      
    String str = "60";										//výpočet zpoždění pro 60 fps
  	int fps = (str != null) ? Integer.parseInt(str) : 10;
  	delay = (fps > 0) ? (1000 / fps) : 100;
  	
  	startX = 150;
  	startY = 150;
  	
  	
  	
  	bufferFazor = createImage( 300, 300 );		//vytvoří prázdný buffer fázoru
  	bufferCas = createImage(489, 300);			//buffer časové osy
  	
  //////////////////////////// vytvoří pozadí pro fázory (prázdná záloha)	
  	
  	fazorPoz = createImage(300, 300);
  	backgBuffer = fazorPoz.getGraphics();
  	backgBuffer.setColor( Color.white ); 
  	backgBuffer.fillRect(0, 0, 300, 300);
  	backgBuffer.setColor( Color.black ); 
  	backgBuffer.drawLine(0, 150, 300, 150);
  	backgBuffer.drawLine(150, 300, 150, 0);
  	backgBuffer.drawLine(147, 100, 153, 100);
  	backgBuffer.drawLine(100, 153, 100, 147);
  	backgBuffer.drawString("Re", 156, 15);
  	backgBuffer.drawString("Im", 5, 144);
  	backgBuffer.drawString("1", 140, 90);
  	backgBuffer.drawString("1", 85, 145);
  	backgBuffer.drawString("Komplexní rovina", 5, 296);
  	
  	backgBuffer.dispose();
  	
////////////////////////////vytvoří pozadí pro časovou osu (prázdná záloha)	
  	
  	casPoz = createImage(489, 300);
  	backgBuffer = casPoz.getGraphics();
  	backgBuffer.setColor( Color.white ); 
  	backgBuffer.fillRect(0, 0, 489, 300);
  	backgBuffer.setColor( Color.black ); 
  	backgBuffer.drawLine(0, 150, 489, 150);
  	backgBuffer.drawLine(163, 153, 163, 147);
  	backgBuffer.drawLine(326, 153, 326, 147);
  	backgBuffer.drawString("Časová rovina", 5, 296);
  	backgBuffer.drawString("t", 472, 146);
  	backgBuffer.drawString("T", 150, 170);
  	backgBuffer.drawString("2T", 300, 170);
  	backgBuffer.dispose();
  	
////////////////////////////// vytvoří časovou osu pro průběžné kreslení 	
  	casVykr = createImage(489, 300);
  	backgBuffer = casVykr.getGraphics();
  	backgBuffer.drawImage(casPoz, 0, 0, null);
  	backgBuffer.dispose();
  	
////////////////////////////////////// vytvoří obrázek pro průběžné kreslení fázoru dráhy
  	fazorVyk = createImage(300, 300);
  	backgBuffer = fazorVyk.getGraphics();
  	backgBuffer.drawImage(fazorPoz, 0, 0, null);
  	backgBuffer.dispose();
  	

    backgBuffer = bufferFazor.getGraphics();		//zobrazí po spuštění prázdné osy
  	backgBuffer.drawImage(fazorPoz, 0, 0, null);
  	backgBuffer.dispose();
  	backgBuffer = bufferCas.getGraphics();
  	backgBuffer.drawImage(casPoz, 0, 0, null);
  	backgBuffer.dispose();
  	
   }
   
   
   
   public void start() {
		animator = new Thread(this);
		animator.start();
	    }
   
   public void stop() {
		animator = null;
	    }

   
@Override
public void actionPerformed(ActionEvent event) {
	
	if (event.getSource() == startButton){
		getCheckboxSel();						//zjistí vybraný průběh
		chybaZadani = false;
		angle = 0;
		startX = 150;
		startY = 150;
		
		if (chosenSingal == 0){				//je vybrán vlastní signál - natahování z tabulky
			
			while (counter3 <= 15){			
			
				try {
				modules[chosenSingal][counter3] = Double.parseDouble( table.getValueAt(counter3, 1).toString() );	//uloží hodnotu z tabulky
				phase[chosenSingal][counter3] = ((Integer.parseInt(table.getValueAt(counter3, 2).toString())*Math.PI)/180)  ;
				}
			catch(NumberFormatException nme){
				chybaZadani = true;
				
				}
					
				counter3++;
			}
			counter3 = 0;
			
		if (chybaZadani == false){	//pokud nedošlo k chybě při zadávání
			
			pauseButton.setEnabled(true);
			stopButton.setEnabled(true);
			isPaused = false;		//start odpauzuje průběh
			}
		}
		
		
		else{								//je vybrán předpřipravený signál - ukládání do tabulky
			model.setRowCount(0);		//vymaže tabulku
			
			while (counter4 <= harmonicsChoice.getSelectedIndex()){
			if (harmonic[chosenSingal][counter3] != 0){		//do tabulky se zapíše nenulové hodnoty	
				model.addRow(new Object[]{String.format("%d", counter3), String.format("%.4f", modules[chosenSingal][counter4]),String.format("%d",(int)Math.ceil(((phase[chosenSingal][counter4]*180)/Math.PI)))});	//řádek nových hodnot
				counter4++;
			}
				counter3++;
			}
			counter3 = 0;
			counter4 = 0;
			table.setEnabled(false);
			
			kopirujButton.setEnabled(true);
			pauseButton.setEnabled(true);
			stopButton.setEnabled(true);
			isPaused = false;		//start odpauzuje průběh
		}
	}
	
	if (event.getSource() == pauseButton){
		if (isPaused == true){
			isPaused = false;
			pauseButton.setLabel("Pauza");		
		}
		else{
			isPaused = true;
			pauseButton.setLabel("Pokračovat");
		}
	}
	if (event.getSource() == stopButton){
		isRendered = true;
		isPaused = true;
	 pauseButton.setEnabled(false);
	 startButton.setEnabled(true);
	//	angle = 0;
	 stopButton.setEnabled(false);
		getCheckboxSel();			//při stop otestujeme vybraný signál
	//	startX = 150;
	//    startY = 150;
			}
	if (event.getSource() == idealSigButton){			//ošetření tlačítka pro ideální signál
		if (isIdealniOn == true){
			idealSigButton.setLabel("Ideální signál vypnut");
			isIdealniOn = false;
		}
		else{
			idealSigButton.setLabel("Ideální signál zapnut");
			isIdealniOn = true;		
		}	
		
	}
	
	if (event.getSource() == kopirujButton){			//pokud chci editovat připravené hodnoty
		model.setRowCount(0);
		isPaused = true;
		isRendered = true;
		harmonicsChoice.select(15);
		checkB0.setState(true);
		kopirujButton.setEnabled(false);
		
		while (counter3 <= 15){
			if (harmonic[chosenSingal][counter3] != 0){		//do tabulky se zapíše nenulové hodnoty	
				model.addRow(new Object[]{String.format("%d", counter3), String.format("%.4f", modules[chosenSingal][counter4]).replace(",", ".") ,String.format("%d",(int)Math.ceil(((phase[chosenSingal][counter4]*180)/Math.PI)))});	//řádek nových hodnot
				counter4++;
			}
			else {
				model.addRow(new Object[]{String.format("%d", counter3), "0", "0"});	//řádek nových hodnot
				
			}
				counter3++;
			}
			counter3 = 0;
			counter4 = 0;

			table.setEnabled(true);
			isIdealniOn = false;
			idealSigButton.setLabel("Ideální signál vypnut");
			startButton.setEnabled(true);
			kopirujButton.setEnabled(false);
			pauseButton.setEnabled(false);
			stopButton.setEnabled(false);
			
			chosenSingal = 1;
		
	}
	
}


@Override
public void run() {
	
		long tm = System.currentTimeMillis();
		while (Thread.currentThread() == animator) {
						
			if (isPaused == false) {						//pokud není pauza, probíhají výpočty
								
				if (startButton.isEnabled() == true)		//vypne tlačítko play
					startButton.setEnabled(false);
					
				
				if (isRendered == true){						//je obraz celý vyrenderován? tak se smaže
					backgBuffer = casVykr.getGraphics();			//smaže časový průběh
				  	backgBuffer.drawImage(casPoz, 0, 0, null);
				  	backgBuffer.dispose();
				  	backgBuffer = fazorVyk.getGraphics();			//smaže průběh fázoru
				  	backgBuffer.drawImage(fazorPoz, 0, 0, null);
				  	backgBuffer.dispose();  
				  	endX = 150;
				  	endY = 150;
				  					  	
				  	isRendered = false;
				}				
		
				//////////////////////////////////////////////////////////////////////////////
				backgBuffer = bufferFazor.getGraphics();
				backgBuffer.drawImage(fazorVyk, 0, 0, null);
				
				endYprev = endY;
			    endXprev = endX;
			    startX = 150;
			    startY = 150;
			    
			    
			   while ( counter <= harmonicsChoice.getSelectedIndex()) {		//kolikrát smyčka proběhne
				 
				   if (harmonic[chosenSingal][counter2] != 0){		//vykreslí se jen nenulová složka
				 
				   endX   =  (startX + (modules[chosenSingal][counter]*50) * Math.sin(counter2*angle+1.570796+phase[chosenSingal][counter]));
				   endY   =  (startY + (modules[chosenSingal][counter]*50) * Math.cos(counter2*angle+1.570796+phase[chosenSingal][counter]));
				    
				    backgBuffer.drawLine((int)startX, (int)startY, (int)endX, (int)endY);
				  
				    startX = endX;
				    startY = endY;	
				    counter++;
				   }		 
				  counter2++;
			} 
			   
			  counter = 0;
			  counter2 = 0;
			    
			    backgBuffer.dispose();
			    
			    backgBuffer = fazorVyk.getGraphics();				//vykreslí dráhu fázoru
			    backgBuffer.drawLine((int)endXprev,(int)endYprev, (int)endX, (int)endY);
			    backgBuffer.dispose();
			    
			    
			    backgBuffer = casVykr.getGraphics();
			    backgBuffer.drawLine((int)(25.942*(angle-speed)), (int)endYprev,(int)(angle*25.942), (int)endY);		//vykreslí časovou osu
			    backgBuffer.dispose();
			   
			    backgBuffer = bufferCas.getGraphics();
			  	backgBuffer.drawImage(casVykr, 0, 0, null);
			  	backgBuffer.dispose();
			  	
			  	backgBuffer = bufferFazor.getGraphics();	//vykresluje pomocnou linku do fázoru
			  	backgBuffer.setColor(Color.green);
			  	backgBuffer.drawLine((int)endX, (int)endY, 300, (int)endY);
			  	backgBuffer.dispose();

			  		backgBuffer = bufferCas.getGraphics();		//vykresluje pomocnou linku do času
					backgBuffer.setColor(Color.green);
					backgBuffer.drawLine(0, (int)endY, (int)(25.942*(angle-speed)), (int)endY);
					backgBuffer.dispose();
					
			  	
			  	if (isIdealniOn == true){		//pokud je idealni prubeh zapnut, vykreslí ho	  		
			  		idealniPrubeh();			  		
			 	}
			  	
			    
			    switch(speedChoice.getSelectedIndex()) {		//nastaví rychlost vykreslování
			   case 0:  speed = 0.0130899; break;
			   case 1:  speed = 0.0261799; break;
			   case 2:  speed = 0.0785398; break;
			   }
			   angle += speed; 
			    
			   repaint();			//překreslí obraz
			    
			    if (angle > 18.84955592){				//pokud signál doběhnul na konec
			    	isPaused = true;				//vykreslování se pauzne
			    	startButton.setEnabled(true);			//zapne tlačítko play
			    	pauseButton.setEnabled(false);			//vypne pause
			    	stopButton.setEnabled(false);			//vypne stop
			    	isRendered = true;				//graf je vykreslen			    	
			    	angle = 0.0;					//vynuluje úhel
			    }
			  
					}
			
			else {							//pokud je prubeh zastaven nebo pozastaven
				
				
				if (isIdealniOn == true)		//pokud je ideální průběh zapnut
					{	idealniPrubeh();
										}
			
				else 							//pokud není zapnut
					{
					backgBuffer = bufferCas.getGraphics();
				  	backgBuffer.drawImage(casVykr, 0, 0, null);	
				  	backgBuffer.setColor(Color.green);											//vykreslí pomocnou čáru
				  	backgBuffer.drawLine(0, (int)endY, (int)(25.942*(angle-speed)), (int)endY);
				  	backgBuffer.dispose();		  
					}	
			
					
				repaint();
			}
		    
		    
	    try {					//Thread řídí vykreslovací frekvenci
			tm += delay;
			Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
		    } catch (InterruptedException e) {
			break;
		    }

		    // posune snímek
		    frame++;
		}
}


/*public void paint (Graphics g) {   
   
  }*/

public void update (Graphics g){
	g.drawImage(bufferCas, 306, 5, this);		//vykreslí grafy
    g.drawImage(bufferFazor, 5, 5, this);
}
   
public void getCheckboxSel (){			// při stop nebo doběhnutí signálu testuje
	
	if (checkB0.getState() == true)		// je vybrán vlastní signál
		chosenSingal = 0;
	if (checkB1.getState() == true)		//obdelník	
		chosenSingal = 1;
	if (checkB2.getState() == true)		//pila
		chosenSingal = 2;
	if (checkB3.getState() == true)		//trojúhelník
		chosenSingal = 3;
	if (checkB4.getState() == true)		//impuls
		chosenSingal = 4;
	if (checkB5.getState() == true)		//impuls
		chosenSingal = 5;
	
}


@Override
public void itemStateChanged(ItemEvent e) {				//prázdná tabulka
	if (e.getSource() == checkB0){							//pokud byl checkbox vybrán
		if (e.getStateChange() == ItemEvent.SELECTED){		
		prazdnaTab();
		table.setEnabled(true);
		kopirujButton.setEnabled(false);
		harmonicsChoice.select(15);
			}
		}
	}
   
public void prazdnaTab(){				//vytvoří prázdnou tabulku s počtem řádků podle vybraného počtu harmonických
	model.setRowCount(0);
	while (counter3 <= 15)	{
		model.addRow(new Object[]{String.format("%d", counter3),"0","0"});
		counter3++;
		}
	counter3 = 0;
}

public void idealniPrubeh(){				// vykreslovac idealniho prubehu
	backgBuffer = bufferCas.getGraphics();
	
	if(chosenSingal == 1){					//ideální průběh obdelníku
			backgBuffer.setColor(Color.red);
			backgBuffer.drawLine(1, 150, 1, 110);
			backgBuffer.drawLine(1, 110, 82, 110);
			backgBuffer.drawLine(82, 110, 82, 190);
			backgBuffer.drawLine(82, 190, 163, 190);
			backgBuffer.drawLine(163, 190, 163, 150);
			backgBuffer.drawLine(163, 150, 163, 110);
			backgBuffer.drawLine(163, 110, 245, 110);
			backgBuffer.drawLine(245, 110, 245, 190);
			backgBuffer.drawLine(245, 190, 326, 190);
			backgBuffer.drawLine(326, 190, 326, 150);
			backgBuffer.drawLine(326, 150, 326, 110);
			backgBuffer.drawLine(326, 110, 408, 110);
			backgBuffer.drawLine(408, 110, 408, 190);
			backgBuffer.drawLine(408, 190, 487, 190);
			backgBuffer.drawLine(487, 190, 487, 150);
		}
		
		if(chosenSingal == 2){					//ideální průběh pily
			backgBuffer.setColor(Color.red);
			backgBuffer.drawLine(1, 150, 1, 72);		//72 a 228 jsou výšky
			backgBuffer.drawLine(1, 72, 163, 228);
			backgBuffer.drawLine(163, 228, 163, 75);
			backgBuffer.drawLine(163, 72, 326, 228);
			backgBuffer.drawLine(326, 228, 326, 72);
			backgBuffer.drawLine(326, 72, 487, 228);
			backgBuffer.drawLine(487, 228, 487, 150);
		}
		if(chosenSingal == 3){							//ideální průběh trojúhelníku
			backgBuffer.setColor(Color.red);
			backgBuffer.drawLine(0, 28, 82, 272);
			backgBuffer.drawLine(82, 272, 163, 28);
			backgBuffer.drawLine(163, 28, 245, 272);
			backgBuffer.drawLine(245, 272, 326, 28);
			backgBuffer.drawLine(326, 28, 408, 272);
			backgBuffer.drawLine(408, 272, 489, 28);
		}
		if(chosenSingal == 4){							//ideální průběh impulsů
			backgBuffer.setColor(Color.red);
			backgBuffer.drawLine(1, 150, 1, 0);
			backgBuffer.drawLine(163, 150, 163, 0);
			backgBuffer.drawLine(326, 150, 326, 0);
			backgBuffer.drawLine(488, 150, 488, 0);
			}
		
		backgBuffer.dispose();
}



}