/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package falsecolors;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FlsClr extends Applet implements Runnable, ActionListener, ItemListener, ChangeListener {
	//Předem se omlouvám za nevzhledný kód (ale funkční). Je to guláš, já vím. Kdyby se jednalo o obsáhlejší projekt a ne jen jednoúčelový aplet, dal bych si na čistotě záležet víc.	
	Color hsvCol;
	int color;	
	int[][] pallet;
	int[][] palettGamma;
	int[] startCerna;
	int[] konecBila;
	float[] sliderToFloat;
	int width=1080, height=650;
	int xCounter, yCounter;
	BufferedImage[] greyPictures;
	BufferedImage greyscaleInput;
	Image palletStrip;
	Image greyStrip;
	Image colorOutput;
	Graphics drawSurface;
	int frame;
	int delay;
	Thread animator;
	Label vyberObL;
	Choice vyberObCh;
	Label vyberPal;
	Choice vyberPalCh;
	Button dyeStart;
	Label redL;
	Label greenL;
	Label blueL;
	Label koncovebody;
	TextField textR;
	TextField textG;
	TextField textB;
	CheckboxGroup checkGroup;		//checkboxy
	Checkbox checkB0;
	Checkbox checkB1;
	JSlider gammaCh;
	Label gammaPop;
	Label gammaAkt;
	
	int [][] palleteHot = {	{3,5,8,11,13,16,19,21,24,27,29,32,35,37,40,43,45,48,50,53,56,58,61,64,66,69,72,74,77,80,82,85,88,90,93,96,98,101,104,106,109,112,114,117,120,122,125,128,130,133,135,138,141,143,146,149,151,154,157,159,162,165,167,170,173,175,178,181,183,186,189,191,194,197,199,202,205,207,210,212,215,218,220,223,226,228,231,234,236,239,242,244,247,250,252,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,5,8,11,13,16,19,21,24,27,29,32,35,37,40,43,45,48,50,53,56,58,61,64,66,69,72,74,77,80,82,85,88,90,93,96,98,101,104,106,109,112,114,117,120,122,125,128,130,133,135,138,141,143,146,149,151,154,157,159,162,165,167,170,173,175,178,181,183,186,189,191,194,197,199,202,205,207,210,212,215,218,220,223,226,228,231,234,236,239,242,244,247,250,252,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,8,12,16,20,24,28,32,36,40,44,48,52,56,60,64,68,72,76,80,84,88,92,96,100,104,108,112,116,120,124,128,131,135,139,143,147,151,155,159,163,167,171,175,179,183,187,191,195,199,203,207,211,215,219,223,227,231,235,239,243,247,251,255}};
	int [][] palleteJet = {	{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,8,12,16,20,24,28,32,36,40,44,48,52,56,60,64,68,72,76,80,84,88,92,96,100,104,108,112,116,120,124,128,131,135,139,143,147,151,155,159,163,167,171,175,179,183,187,191,195,199,203,207,211,215,219,223,227,231,235,239,243,247,251,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,251,247,243,239,235,231,227,223,219,215,211,207,203,199,195,191,187,183,179,175,171,167,163,159,155,151,147,143,139,135,131,128},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,8,12,16,20,24,28,32,36,40,44,48,52,56,60,64,68,72,76,80,84,88,92,96,100,104,108,112,116,120,124,128,131,135,139,143,147,151,155,159,163,167,171,175,179,183,187,191,195,199,203,207,211,215,219,223,227,231,235,239,243,247,251,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,251,247,243,239,235,231,227,223,219,215,211,207,203,199,195,191,187,183,179,175,171,167,163,159,155,151,147,143,139,135,131,128,124,120,116,112,108,104,100,96,92,88,84,80,76,72,68,64,60,56,52,48,44,40,36,32,28,24,20,16,12,8,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{131,135,139,143,147,151,155,159,163,167,171,175,179,183,187,191,195,199,203,207,211,215,219,223,227,231,235,239,243,247,251,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,251,247,243,239,235,231,227,223,219,215,211,207,203,199,195,191,187,183,179,175,171,167,163,159,155,151,147,143,139,135,131,128,124,120,116,112,108,104,100,96,92,88,84,80,76,72,68,64,60,56,52,48,44,40,36,32,28,24,20,16,12,8,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
	
	public void init(){
		 setLayout(null); 
		 greyPictures = new BufferedImage[12];
		  width = getSize().width;		//nastavení velikosti okna
	      height = getSize().height;
	      setBackground(new Color(151, 183, 193));	//barva pozadí
	      try { 
	    	  
	    	  	greyscaleInput = ImageIO.read(getClass().getClassLoader().getResourceAsStream("1.png"));		
				greyPictures[0] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("1.png"));
				greyPictures[1] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("2.png"));
				greyPictures[2] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("3.png"));
				greyPictures[3] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("4.png"));
				greyPictures[4] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("5.png"));
				greyPictures[5] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("6.png"));
				greyPictures[6] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("7.png"));
				greyPictures[7] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("9.png"));
				greyPictures[8] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("10.png"));
				greyPictures[9] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("11.png"));
				greyPictures[10] = ImageIO.read(getClass().getClassLoader().getResourceAsStream("12.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
			}
	/////////////////////////////////////////////////////// šedý referenční pruh		
	      greyStrip = createImage(20, 512);
	      drawSurface = greyStrip.getGraphics();
	      while (xCounter <= 255){  
			drawSurface.setColor(new Color(xCounter,xCounter,xCounter)); 
			drawSurface.drawRect(0, 510-(xCounter*2), 20, 1);
			xCounter++; }
	    xCounter=0;
			drawSurface.dispose();
/////////////////////////////////////////////////////// šedý referenční pruh	
			
/////////////////////////////////////////////////// počáteční inicializace palety	
	pallet = new int[3][256];			
	palettGamma = new int[3][256];
	/* int rgb;									// generování pole
	 float hue =0;
	 float saturation=(float) 1;
	float brightness=(float) 1;*/
	
	  /*   while( xCounter <= 255) {	    	 
	    	 hue = (360/255)*xCounter;	    	 
	    	 rgb = Color.HSBtoRGB(hue, saturation, brightness);
	    	 pallet[0][xCounter] =10;
	    	 pallet[1][xCounter] = xCounter;
	    	 pallet[2][xCounter] =100;//
	    	 xCounter++;}*/
	   //  xCounter=0;
	     
	     palletStrip = createImage(20, 512);			//vykreslení palety z pole
	     drawSurface = palletStrip.getGraphics();
	     while (xCounter <= 255){  
				drawSurface.setColor(new Color(pallet[0][xCounter],pallet[1][xCounter],pallet[2][xCounter])); 
				drawSurface.drawRect(0, 510-(xCounter*2), 20, 1);
				xCounter++;}
		    xCounter=0;
				drawSurface.dispose();
/////////////////////////////////////////////////// počáteční inicializace palety	
			
			String str = "15";										//výpočet zpoždění pro 15 fps 
		  	int fps = (str != null) ? Integer.parseInt(str) : 10;
		  	delay = (fps > 0) ? (1000 / fps) : 100;
		  	
		  	startCerna = new int[3];
		  	startCerna[0] = 0;
		  	startCerna[1] = 0;
		  	startCerna[2] = 0;
			konecBila = new int[3];
			konecBila[0] = 255;
			konecBila[1] = 255;
			konecBila[2] = 255;
					
		  	colorOutput = createImage(512, 512);
		  	
		  	vyberObL = new Label("Výběr obrázku:");
		  	add(vyberObL);
		  	vyberObL.setBounds(100, 35, 80, 20);
		  	
		  	vyberObCh = new Choice();
		  	vyberObCh.addItem("rentgen - hlava");
		  	vyberObCh.addItem("rentgen - ruce");
		  	vyberObCh.addItem("rentgen - hrudník");
		  	vyberObCh.addItem("rentgen - ruce 2");
		  	vyberObCh.addItem("rentgen - koleno");
		  	vyberObCh.addItem("rentgen - trup");
		  	vyberObCh.addItem("2D sinus");
		  	vyberObCh.addItem("lineární gradient");
		  	vyberObCh.addItem("mraky");
		  	vyberObCh.addItem("pes");
		  	vyberObCh.addItem("testovací vzor");
		  	vyberObCh.addItemListener(this);
		  	add(vyberObCh);
		  	vyberObCh.setBounds(100, 60, 140, 20);
		  	
		  	vyberPal = new Label("Výběr barvící palety:");
		  	add(vyberPal);
		  	vyberPal.setBounds(690, 35, 130, 20);
		  	
		  	vyberPalCh = new Choice();
		  	vyberPalCh.addItem("Lineární gradient  -->>");
		  	vyberPalCh.addItem("HSV");
		  	vyberPalCh.addItem("Jet");
		  	vyberPalCh.addItem("cool");
		  	vyberPalCh.addItem("hot");
		 // 	vyberPalCh.addItemListener(this);
		  	add(vyberPalCh);
		  	vyberPalCh.setBounds(690, 60, 180, 20);
		  	
		  	
		  	dyeStart = new Button("Proveď přebarvení");
		  	dyeStart.addActionListener(this);
		  	add(dyeStart);
		  	dyeStart.setBounds(480, 50, 120, 40);
		  	
		  	redL = new Label("R");
		  	greenL = new Label("G");
		  	blueL = new Label("B");
		  	add(redL);
		  	add(greenL);
		  	add(blueL);
		  	redL.setBounds(910, 25, 10, 20);
		  	greenL.setBounds(910, 50, 10, 20);
		  	blueL.setBounds(910, 75, 10, 20);
		  	
		  	textR = new TextField();
		  	textG = new TextField();
		  	textB = new TextField();
		  	add(textR);
		  	add(textG);
		  	add(textB);
		  	textR.setBounds(930, 25, 50, 20);
		  	textG.setBounds(930, 50, 50, 20);
		  	textB.setBounds(930, 75, 50, 20);
		  	textR.setText("0");
		  	textG.setText("0");
		  	textB.setText("0");
		  	
		  	koncovebody = new Label("Barvy koncových bodů:");
		  	add(koncovebody);
		  	koncovebody.setBounds(780, 5, 130, 20);
		  	
		  	CheckboxGroup checkGroup = new CheckboxGroup();				
		    checkB0 = new Checkbox("Černá", checkGroup,true);
		    checkB0.addItemListener(this);
		    checkB1 = new Checkbox("Bílá", checkGroup,false);
		    checkB1.addItemListener(this);
		    add(checkB0);
		    add(checkB1);
		    checkB0.setBounds(910, 5, 50, 20);
		    checkB1.setBounds(970, 5, 50, 20);
		    
		    gammaCh = new JSlider(0, 790);
		    gammaCh.setValue(90);
		    gammaCh.addChangeListener(this);
		    add(gammaCh);
		    gammaCh.setBounds(730, 622, 320, 20);
		    sliderToFloat = new float[791];
		   for (int i = 1; i < 791; i++) {
			   sliderToFloat[0]=0.1f;
			   sliderToFloat[i] = sliderToFloat[i-1]+0.01f;}
		   
		    gammaPop = new Label("Nastavit gamma korekci:");
		    add(gammaPop);
		    gammaPop.setBounds(530, 622, 150, 20);
		    
		    gammaAkt = new Label("1,00");
		    add(gammaAkt);
		    gammaAkt.setBounds(690, 622, 50, 20);
		    
			resetGamma();
			generateSimplePalette();
			pallettToPalettGamma();
			drawColorStrip();
			
		  pallettToPalettGamma();
		    usePallet();
		    vyberPalCh.select(1);
		    vyberPalCh.addItemListener(this);
		    generateHSBpalette();
			resetGamma();
			pallettToPalettGamma();
			drawColorStrip();
			checkB0.setEnabled(false);
			checkB1.setEnabled(false);
			textR.setEnabled(false);
			textG.setEnabled(false);
			textB.setEnabled(false);
			usePallet();
	}
		
	public void start() {
		animator = new Thread(this);
		animator.start(); }
   
   public void stop() {
		animator = null;}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == dyeStart){
			if(vyberPalCh.getSelectedIndex()==0){
				
				if (checkB1.getState()==true){			// kliknutí na černou uloží bílou
					try{	
						konecBila[0] = Integer.parseInt(textR.getText());
						konecBila[1] = Integer.parseInt(textG.getText());
						konecBila[2] = Integer.parseInt(textB.getText());
						
						if ((konecBila[0] >= 256)||(konecBila[0] < 0)){
							textR.setText("CHYBA!");
							konecBila[0] = 255;
							}
						if ((konecBila[1] >= 256)||(konecBila[1] < 0)){
							textG.setText("CHYBA!");
							konecBila[1] = 255;
							}
						if ((konecBila[2] >= 256)||(konecBila[2] < 0)){
							textB.setText("CHYBA!");
							konecBila[2] = 255;
							}
						}
					catch(NumberFormatException nme){
						textR.setText("Musíte");
						textG.setText("zadat");
						textB.setText("čísla");
						System.out.println("chyba");}
						
					generateSimplePalette();					
					pallettToPalettGamma();
					drawColorStrip();
					}
					else if (checkB0.getState()==true){						// kliknutí na bílou uloží černou
					try{	
						startCerna[0] = Integer.parseInt(textR.getText());
						startCerna[1] = Integer.parseInt(textG.getText());
						startCerna[2] = Integer.parseInt(textB.getText());
						
						if ((startCerna[0] >= 256)||(startCerna[0] < 0)){
							textR.setText("CHYBA!");
							startCerna[0] = 255;
							}
						if ((startCerna[1] >= 256)||(startCerna[1] < 0)){
							textG.setText("CHYBA!");
							startCerna[1] = 255;
							}
						if ((startCerna[2] >= 256)||(startCerna[2] < 0)){
							textB.setText("CHYBA!");
							startCerna[2] = 255;
							}
					}
					catch(NumberFormatException nme){
						textR.setText("Musíte");
						textG.setText("zadat");
						textB.setText("čísla");
						System.out.println("chyba");}
					generateSimplePalette();
					
					pallettToPalettGamma();
					drawColorStrip();}
			}			
			usePallet();}}

	@Override
	public void run() {
		long tm = System.currentTimeMillis();
		while (Thread.currentThread() == animator) {	
		// TODO Auto-generated method stub*/
		repaint();
		try {					//Thread řídí vykreslovací frekvenci
			tm += delay;
			Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
		    } catch (InterruptedException e) {
			break;
		    }
		    // posune snímek
		    frame++;}}
	
	public void update (Graphics g){
		g.drawImage(greyscaleInput, 5, 100, this);
		g.drawImage(colorOutput, 574, 100, this);
		g.drawImage(greyStrip, 524, 100, this);
		g.drawImage(palletStrip, 547, 100, this);}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == checkB0){			// kliknutí na černou uloží bílou
		try{	
			konecBila[0] = Integer.parseInt(textR.getText());
			konecBila[1] = Integer.parseInt(textG.getText());
			konecBila[2] = Integer.parseInt(textB.getText());
			textR.setText(String.format("%d", startCerna[0]));		//vypíše černou
			textG.setText(String.format("%d", startCerna[1]));
			textB.setText(String.format("%d", startCerna[2]));
			if ((konecBila[0] >= 256)||(konecBila[0] < 0)){
				textR.setText("CHYBA!");
				konecBila[0] = 255;
				}
			if ((konecBila[1] >= 256)||(konecBila[1] < 0)){
				textG.setText("CHYBA!");
				konecBila[1] = 255;
				}
			if ((konecBila[2] >= 256)||(konecBila[2] < 0)){
				textB.setText("CHYBA!");
				konecBila[2] = 255;
				}
			}
		catch(NumberFormatException nme){
			textR.setText("Musíte");
			textG.setText("zadat");
			textB.setText("čísla");
			System.out.println("chyba");}
			
		generateSimplePalette();
		resetGamma();
		pallettToPalettGamma();
		drawColorStrip();
		}
		else if (e.getSource() == checkB1){						// kliknutí na bílou uloží černou
		try{	
			startCerna[0] = Integer.parseInt(textR.getText());
			startCerna[1] = Integer.parseInt(textG.getText());
			startCerna[2] = Integer.parseInt(textB.getText());
			textR.setText(String.format("%d", konecBila[0]));
			textG.setText(String.format("%d", konecBila[1]));
			textB.setText(String.format("%d", konecBila[2]));
			if ((startCerna[0] >= 256)||(startCerna[0] < 0)){
				textR.setText("CHYBA!");
				startCerna[0] = 255;
				}
			if ((startCerna[1] >= 256)||(startCerna[1] < 0)){
				textG.setText("CHYBA!");
				startCerna[1] = 255;
				}
			if ((startCerna[2] >= 256)||(startCerna[2] < 0)){
				textB.setText("CHYBA!");
				startCerna[2] = 255;
				}
		}
		catch(NumberFormatException nme){
			textR.setText("Musíte");
			textG.setText("zadat");
			textB.setText("čísla");
			System.out.println("chyba");}
		generateSimplePalette();
		resetGamma();
		pallettToPalettGamma();
		drawColorStrip();}
		
		else if (e.getSource() == vyberPalCh){				// výběr barvící palety
			if (vyberPalCh.getSelectedIndex() == 1){			//vybrána HSV
				generateHSBpalette();
				resetGamma();
				pallettToPalettGamma();
				drawColorStrip();
				checkB0.setEnabled(false);
				checkB1.setEnabled(false);
				textR.setEnabled(false);
				textG.setEnabled(false);
				textB.setEnabled(false);
				usePallet();
			}	
			if (vyberPalCh.getSelectedIndex() == 0){	//vybrán jednoduchý gradient
				checkB0.setEnabled(true);
				checkB1.setEnabled(true);
				textR.setEnabled(true);
				textG.setEnabled(true);
				textB.setEnabled(true);
				resetGamma();
				generateSimplePalette();
				pallettToPalettGamma();
				drawColorStrip();
			}
			if (vyberPalCh.getSelectedIndex() == 2){	//vybrán Jet
				checkB0.setEnabled(false);
				checkB1.setEnabled(false);
				textR.setEnabled(false);
				textG.setEnabled(false);
				textB.setEnabled(false);
				resetGamma();
				generateJet();
				pallettToPalettGamma();
				drawColorStrip();
				usePallet();
			}
			
			if (vyberPalCh.getSelectedIndex() == 3){	//vybrán cool
				checkB0.setEnabled(false);
				checkB1.setEnabled(false);
				textR.setEnabled(false);
				textG.setEnabled(false);
				textB.setEnabled(false);
				resetGamma();
				generateGradient(0.0f, 255.0f, 255.0f, 255.0f, 0.0f, 255.0f);
				pallettToPalettGamma();
				drawColorStrip();
				usePallet();
			}
			
			if (vyberPalCh.getSelectedIndex() == 4){		//vybrán hot
				checkB0.setEnabled(false);
				checkB1.setEnabled(false);
				textR.setEnabled(false);
				textG.setEnabled(false);
				textB.setEnabled(false);
				resetGamma();
				generateHot();
				pallettToPalettGamma();
				drawColorStrip();
				usePallet();
			}
		}
		
		else if (e.getSource()== vyberObCh){
			if (vyberObCh.getSelectedIndex() == 0){greyscaleInput = greyPictures[0]; }
			else if (vyberObCh.getSelectedIndex() == 1) {greyscaleInput = greyPictures[1]; }
			else if (vyberObCh.getSelectedIndex() == 2) {greyscaleInput = greyPictures[2]; }
			else if (vyberObCh.getSelectedIndex() == 3) {greyscaleInput = greyPictures[3]; }
			else if (vyberObCh.getSelectedIndex() == 4) {greyscaleInput = greyPictures[4]; }
			else if (vyberObCh.getSelectedIndex() == 5) {greyscaleInput = greyPictures[5]; }
			else if (vyberObCh.getSelectedIndex() == 6) {greyscaleInput = greyPictures[6]; }
			else if (vyberObCh.getSelectedIndex() == 7) {greyscaleInput = greyPictures[7]; }
			else if (vyberObCh.getSelectedIndex() == 8) {greyscaleInput = greyPictures[8]; }
			else if (vyberObCh.getSelectedIndex() == 9) {greyscaleInput = greyPictures[9]; }
			else{greyscaleInput = greyPictures[10];}
		}
	}

public void generateSimplePalette(){			//generování jednoduché lineární palety ze vstupu
	float[] stepRGB = new float[3];
	stepRGB[0] = (float) (konecBila[0] - startCerna[0])/255;
	stepRGB[1] = (float) (konecBila[1] - startCerna[1])/255;		
	stepRGB[2] = (float) (konecBila[2] - startCerna[2])/255;
	float[] floatRGBvalue = new float[3];
	floatRGBvalue[0]= (float)startCerna[0];
	floatRGBvalue[1]= (float)startCerna[1];
	floatRGBvalue[2]= (float)startCerna[2];
	pallet[0][0] = startCerna[0];
	pallet[1][0] = startCerna[1];
	pallet[2][0] = startCerna[2];
	xCounter = 1;
	while( xCounter <= 255) {	   
		floatRGBvalue[0] = floatRGBvalue[0] +stepRGB[0];		//step se přičítá už od první smyčky, což je špatně (OPRAVENO)
		pallet[0][xCounter] =(int)	floatRGBvalue[0];
		floatRGBvalue[1] = floatRGBvalue[1]+stepRGB[1];
    	pallet[1][xCounter] = (int)	floatRGBvalue[1];
    	floatRGBvalue[2] = floatRGBvalue[2]+stepRGB[2];
    	pallet[2][xCounter] = (int)	floatRGBvalue[2];
    	xCounter++;}
     xCounter=0;}	

public void drawColorStrip(){				//vykreslí barevný pruh z palety
	drawSurface = palletStrip.getGraphics();
    while (xCounter <= 255){  
			drawSurface.setColor(new Color(palettGamma[0][xCounter],palettGamma[1][xCounter],palettGamma[2][xCounter])); 
			drawSurface.drawRect(0, 510-(xCounter*2), 20, 1);
			xCounter++;}
	    xCounter=0;
			drawSurface.dispose();}
	
public void usePallet(){		// funkce co vytvoří výstupní obrázek z palety
	drawSurface = colorOutput.getGraphics();	// výstup
while (yCounter < 512){		
while (xCounter < 512 ){			
	color = greyscaleInput.getRGB(xCounter, yCounter);
	color = color & 0x000000ff;		// pro zjištění modré (je jedno kterou barvu, protože jsou stejné)
	drawSurface.setColor(new Color(palettGamma[0][color],palettGamma[1][color],palettGamma[2][color])); 
	drawSurface.drawLine(xCounter, yCounter, xCounter, yCounter);
	xCounter++;}
yCounter++;
xCounter=0;}
drawSurface.dispose();
yCounter=0;
xCounter=0;}

public void generateHSBpalette(){			//generuje HSB paletu
	float hue = 0.0f;
	xCounter = 0;
while (xCounter <= 255){	
	Color hsbC = Color.getHSBColor(hue, 1, 1);
	pallet[0][xCounter] = hsbC.getRed(); 
	pallet[1][xCounter] = hsbC.getGreen();
	pallet[2][xCounter] = hsbC.getBlue();
	xCounter++;
	hue = hue + 1.0f/255.0f;}
	xCounter = 0;}

@Override
public void stateChanged(ChangeEvent e) {
	gammaAkt.setText(String.format("%.2f",sliderToFloat[gammaCh.getValue()]));
	pallettToPalettGamma();
	drawColorStrip();
	usePallet();}

public void pallettToPalettGamma(){
	while (xCounter <= 255){  
		palettGamma[0][xCounter] = (int) (255*Math.pow((pallet[0][xCounter]/255.0), 1/sliderToFloat[gammaCh.getValue()]));
		palettGamma[1][xCounter] = (int) (255*Math.pow((pallet[1][xCounter]/255.0), 1/sliderToFloat[gammaCh.getValue()]));
		palettGamma[2][xCounter] = (int) (255*Math.pow((pallet[2][xCounter]/255.0), 1/sliderToFloat[gammaCh.getValue()]));
			xCounter++;}
	    xCounter=0;}

public void resetGamma(){
	gammaCh.setValue(90);
	gammaAkt.setText("1,00");}

public void generateGradient(float StartR, float StartG, float StartB, float EndR, float EndG, float EndB){
	
	float[] stepRGB = new float[3];
	stepRGB[0] = (float) (EndR - StartR)/255;
	stepRGB[1] = (float) (EndG - StartG)/255;		
	stepRGB[2] = (float) (EndB - StartB)/255;
	float[] floatRGBvalue = new float[3];
	floatRGBvalue[0]= (float)StartR;
	floatRGBvalue[1]= (float)StartG;
	floatRGBvalue[2]= (float)StartB;
	pallet[0][0] = (int)StartR;
	pallet[1][0] = (int)StartG;
	pallet[2][0] = (int)StartB;
	xCounter = 1;
	
	while( xCounter <= 255) {	   
		floatRGBvalue[0] = floatRGBvalue[0] +stepRGB[0];		//step se přičítá už od první smyčky, což je špatně (OPRAVENO)
		pallet[0][xCounter] =(int)	floatRGBvalue[0];
		floatRGBvalue[1] = floatRGBvalue[1]+stepRGB[1];
    	pallet[1][xCounter] = (int)	floatRGBvalue[1];
    	floatRGBvalue[2] = floatRGBvalue[2]+stepRGB[2];
    	pallet[2][xCounter] = (int)	floatRGBvalue[2];
    	xCounter++;}
	xCounter=0;
}

public void generateHot(){
		while (xCounter <= 255){  
			pallet[0][xCounter] = palleteHot[0][xCounter];
			pallet[1][xCounter] = palleteHot[1][xCounter];
			pallet[2][xCounter] = palleteHot[2][xCounter];
				xCounter++;}
		    xCounter=0;}
public void generateJet(){
	while (xCounter <= 255){  
		pallet[0][xCounter] = palleteJet[0][xCounter];
		pallet[1][xCounter] = palleteJet[1][xCounter];
		pallet[2][xCounter] = palleteJet[2][xCounter];
			xCounter++;}
	    xCounter=0;}	
}