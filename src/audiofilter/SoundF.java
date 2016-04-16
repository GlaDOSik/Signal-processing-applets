package audiofilter;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.jsyn.JSyn;          
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.util.SampleLoader;
import com.jsyn.Synthesizer;
import edu.emory.mathcs.jtransforms.fft.*;


public class SoundF extends Applet implements Runnable, ActionListener, ItemListener, ChangeListener {
	//Předem se omlouvám za nevzhledný kód (ale funkční). Je to guláš, já vím. Kdyby se jednalo o obsáhlejší projekt a ne jen jednoúčelový aplet, dal bych si na čistotě záležet víc.
	float[] dataInput;
	float[] realDataInputBack;
	float[] dataOutput;
	float[] realDataInput;
	float[] realDataOutput;
	int[] filterChar;
	int[] realFilterChar;
	int lenghtOfSample;
	float[] dataBuffer;
	
///// UI	
	Image inputTime;
	Image inputFreq;
	Image outputTime;
	Image outputFreq;
	Image filter;
	Graphics surface;
	
	Choice inputSample;
	Choice filterType;
	
	Label inputSignal;
	Label freqOfHarmon;
	Label outputSignal;
	Label filterTypeLab;
	Label filterFreq;
	Label hlasitostLabel;
	Label sirkaPasma;
	
	Button playInputSamp;			
	
	JSlider freqOfHarmonSlid;
	JSlider filterFreqSlide;
	JSlider hlasitost;
	JSlider sirkaPasmaSlid;
	JSlider freqMark;
	JSlider freqMark2;
	
	CheckboxGroup checkGroup;
	Checkbox vstup;
	Checkbox vystup;
	   
	FloatFFT_1D dfft2 = new FloatFFT_1D(44100);
	
	int frame;
	int delay;
	Thread animator;
	int width=1260, height=520;
	
	Synthesizer synth;						//syntezátor
	LineOut out;							//výstup
	VariableRateMonoReader samplePlayer;	//mono přehrávač
	FloatSample mySample;					//vzorek
	FloatSample mySampleOutput;
	
	public void init(){
		
		
		setLayout(null);
		width = getSize().width;		//nastavení velikosti okna
	    height = getSize().height;
	    String str = "20";
	  	int fps = (str != null) ? Integer.parseInt(str) : 10;
	  	delay = (fps > 0) ? (1000 / fps) : 100;
	  		  	
	  	setBackground(new Color(151, 183, 193));
	  	
	  	inputTime = createImage(400, 200);		// vstup časový průběh
	  	surface = inputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 10, 20);
	  	surface.dispose();
	  	
	  	inputFreq = createImage(400, 200);		// vstup frekvenční charakteristika
	  	surface = inputFreq.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Frekvenční modulová charakteristika", 10, 20);
	  	surface.dispose();
	  	
	  	filter = createImage(400, 200);		// zvolený filtr
	  	surface = filter.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Spektrální charakteristika ideálního filtru", 10, 20);
	  	surface.dispose();
	  	
	  	outputTime = createImage(400, 200);		// výstup časový průběh
	  	surface = outputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 10, 20);
	  	surface.dispose();
	  	
	  	outputFreq = createImage(400, 200);		// výstup frekvenční charakteristika
	  	surface = outputFreq.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Frekvenční modulová charakteristika", 10, 20);
	  	surface.dispose();
	  	
	  	inputSignal = new Label("Vstupní signál:");
	  	add(inputSignal);
	  	inputSignal.setBounds(10, 30, 85, 20);
	  	
	  	inputSample = new Choice();
	  	inputSample.add("Sinus");
	  	inputSample.add("Pila");	  	
	  	inputSample.add("Obdélník");
		inputSample.add("Bílý šum");
	  	inputSample.add("Růžový šum");
	  	inputSample.add("Voda");
	  	inputSample.add("Šťek");
	  	inputSample.add("Výstřel");
	  	inputSample.add("Stará televize");
	  	inputSample.add("Buben");
	  	inputSample.add("Klavír");
	  	add(inputSample);
	  	inputSample.addItemListener(this);
	  	inputSample.setBounds(100, 30, 110, 20);
	  	
	  	freqOfHarmon = new Label("Kmitočet základní harmonické složky:");
	  	add(freqOfHarmon);
	  	freqOfHarmon.setBounds(225, 5, 215, 20);
	  	
	  	freqOfHarmonSlid = new JSlider(0, 1990);
	  	freqOfHarmonSlid.setValue(0);
	  	add(freqOfHarmonSlid);
	  	freqOfHarmonSlid.setBounds(225, 30, 200, 20);
	  	
	  	filterTypeLab = new Label("Typ filtru:");
	  	add(filterTypeLab);
	  	filterTypeLab.setBounds(430, 60, 60, 20);
	  	
	  	filterType = new Choice();
	  	filterType.add("Dolní propust");
	  	filterType.add("Horní propust");
	  	filterType.add("Pásmová propust");
	  	filterType.add("Pásmová zádrž");
	  	add(filterType);
	  	filterType.addItemListener(this);
	  	filterType.setBounds(490, 60, 120, 20);
	  	
	  	filterFreq = new Label("Mezní kmitočet:");
	  	add(filterFreq);
	  	filterFreq.setBounds(430, 100, 90, 20);
	  	
	  	filterFreqSlide = new JSlider(0, 11000);
	  	filterFreqSlide.setValue(99);
	  	filterFreqSlide.addChangeListener(this);
	  	add(filterFreqSlide);
	  	filterFreqSlide.setBounds(530, 100, 250, 20);
	  	
	  	sirkaPasma = new Label("Šířka pásma:");
	  	add (sirkaPasma);
	  	sirkaPasma.setBounds(430, 140, 80, 20);
	  	
	  	sirkaPasmaSlid = new JSlider(0, 11000);
	  	sirkaPasmaSlid.setValue(50);
	  	sirkaPasmaSlid.addChangeListener(this);
	  	add(sirkaPasmaSlid);
	  	sirkaPasmaSlid.setBounds(530, 140, 250, 20);
	  	
	 	playInputSamp = new Button("Přehrej");
	  	add(playInputSamp);
	  	playInputSamp.addActionListener(this);
	  	playInputSamp.setBounds(530, 480, 200, 30);
	  	
	  	CheckboxGroup checkGroup = new CheckboxGroup();
	  	vstup = new Checkbox("Vstup", checkGroup,false);
	    vystup = new Checkbox("Výstup", checkGroup,true);
	    vstup.addItemListener(this);
	    vystup.addItemListener(this);
	    vstup.setState(true);
	    add(vstup);
	    add(vystup);
	    vstup.setBounds(470, 485, 50, 20);
	    vystup.setBounds(750, 485, 80, 20);
	  	
	  	hlasitostLabel = new Label("Hlasitost:");
	  	add(hlasitostLabel);
	  	hlasitostLabel.setBounds(460, 520, 55, 20);
	  	
	  	hlasitost = new JSlider(0, 20);
	  	hlasitost.setValue(8);
	  	hlasitost.addChangeListener(this);
	  	add(hlasitost);
	  	hlasitost.setBounds(530, 520, 200, 20);
	  	
	  	outputSignal = new Label("Výstupní signál:");
	  	add(outputSignal);
	  	outputSignal.setBounds(850, 30, 90, 20);
	  	
	  	freqMark = new JSlider(0, 399); 
	  	freqMark.setValue(20);
	  	freqMark.addChangeListener(this);
	  	add(freqMark);
	  	freqMark.setBounds(10, 475, 400, 20);
	  	
	  	freqMark2 = new JSlider(0, 399);
	  	freqMark2.setValue(20);
	  	freqMark2.addChangeListener(this);
	  	add(freqMark2);
	  	freqMark2.setBounds(850, 475, 400, 20);
	  	
	  	realFilterChar = new int[44100];
	  	freqOfHarmonSlid.setValue(590);		//defaultní nastavení vstupu
	  	generateSin(3000);
	  	realDataInput = new float[44100];
	  	realDataInput = dataInput.clone();
	  	realDataInputBack = realDataInput.clone();
	  	renderTimeSin();
	  	rffFFT();
	  	renderFreqChar();
	  	
	  	filterChar = new int[800];
	  	refreshFilter(filterFreqSlide.getValue());
	  	renderFilterChar();
	  	
	  	applyFilter(); 
		renderFreqCharOutput();
		
		marker1apply();
		marker2apply();
		
		rbfFFT();
		renderTimeOutput(); 							
		
		freqOfHarmonSlid.addChangeListener(this);
	 			
	 ///////////////////////////////////////////////////////////inicializace přehrávače			
	 			synth = JSyn.createSynthesizer();
	 			out= new LineOut();
	 			synth.add(out);
	 			synth.add(samplePlayer = new VariableRateMonoReader());
	 			samplePlayer.output.connect( 0, out.input, 0);
	 			samplePlayer.output.connect( 0, out.input, 1);
	 			synth.start();
	 			samplePlayer.amplitude.set(0.07);		//hlasitost
	 			samplePlayer.rate.set(44100);		
	}
		
		public void start() {
			animator = new Thread(this);
			animator.start();
		}
		public void stop() {
			animator = null;
			synth.stop();}

	@Override
	public void run() {long tm = System.currentTimeMillis();
	while (Thread.currentThread() == animator) {	
	
	repaint();	
	
	try {					//Thread řídí vykreslovací frekvenci
		tm += delay;
		Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
	    } catch (InterruptedException e) {
		break;}
	    frame++;}}
	public void update (Graphics g){
		g.drawImage(inputTime, 10, 60, this);
		g.drawImage(inputFreq, 10, 270, this);
		g.drawImage(filter, 430, 270, this);
		g.drawImage(outputTime, 850, 60, this);
		g.drawImage(outputFreq, 850, 270, this);}
	
	@Override
	public void itemStateChanged(ItemEvent ie) {
	if(ie.getSource() == inputSample)	{
		if (inputSample.getSelectedIndex() == 0){ //vybrán sinus
		freqOfHarmonSlid.setEnabled(true);
		freqOfHarmonSlid.setValue(590);		
	  	generateSin(3000);
	  	realDataInput = new float[44100];
	  	realDataInput = dataInput.clone();
	  	realDataInputBack = realDataInput.clone();
	  	lenghtOfSample = realDataInput.length;
	  	renderTimeSin();
	  	rffFFT();
	  	renderFreqChar();
	  	applyFilter(); 
		renderFreqCharOutput();
		refreshFilter(filterFreqSlide.getValue());
	  	renderFilterChar();
	  	applyFilter(); 
		renderFreqCharOutput();
		marker1apply();
		marker2apply();
		rbfFFT();
		renderTimeOutput(); 
		if(vstup.getState()){
			samplePlayer.dataQueue.clear();
			samplePlayer.dataQueue.queueLoop(mySample);
		}
		else if (vystup.getState()){
			samplePlayer.dataQueue.clear();
			samplePlayer.dataQueue.queueLoop(mySampleOutput);	
		}
		return;}
	
		if (inputSample.getSelectedIndex() == 1){ //vybrána pila
		freqOfHarmonSlid.setEnabled(true);
		freqOfHarmonSlid.setValue(590);		
		generateSaw(3000);
		realDataInput = new float[44100];
	  	realDataInput = dataInput.clone();
	  	realDataInputBack = realDataInput.clone();
	  	lenghtOfSample = realDataInput.length;
		renderTimeSaw();
		rffFFT();
		renderFreqChar();
		applyFilter(); 
		renderFreqCharOutput();
		refreshFilter(filterFreqSlide.getValue());
	  	renderFilterChar();
	  	applyFilter(); 
		renderFreqCharOutput();
		marker1apply();
		marker2apply();
		rbfFFT();
		renderTimeOutput(); 
		if(vstup.getState()){
			samplePlayer.dataQueue.clear();
			samplePlayer.dataQueue.queueLoop(mySample);
		}
		else if (vystup.getState()){
			samplePlayer.dataQueue.clear();
			samplePlayer.dataQueue.queueLoop(mySampleOutput);	
		}
		return;}
		
		if (inputSample.getSelectedIndex() == 2){ //vybrán obdélník
		freqOfHarmonSlid.setEnabled(true);
		freqOfHarmonSlid.setValue(590);		
		generateBin(3000);
		realDataInput = new float[44100];
		realDataInput = dataInput.clone();
		realDataInputBack = realDataInput.clone();
		lenghtOfSample = realDataInput.length;
		renderTimeBin();
		rffFFT();
		renderFreqChar();
		applyFilter(); 
		renderFreqCharOutput();
		refreshFilter(filterFreqSlide.getValue());
	  	renderFilterChar();
	  	applyFilter(); 
		renderFreqCharOutput();
		marker1apply();
		marker2apply();
		rbfFFT();
		renderTimeOutput(); 
		if(vstup.getState()){
			samplePlayer.dataQueue.clear();
			samplePlayer.dataQueue.queueLoop(mySample);
		}
		else if (vystup.getState()){
			samplePlayer.dataQueue.clear();
			samplePlayer.dataQueue.queueLoop(mySampleOutput);	
		}
		return;}
		
		if (inputSample.getSelectedIndex() == 3){ //vybrán bílý šum
			
			freqOfHarmonSlid.setEnabled(false);
			try {
			//	mySample = SampleLoader.loadFloatSample(getClass().getResource("/white.wav"));
				mySample = SampleLoader.loadFloatSample(getClass().getClassLoader().getResourceAsStream("white.wav"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataInput = new float[44100];
			mySample.setFrameRate(44100);
			for (int i=0; i<44100; i++){
			dataInput[i] = (float) mySample.readDouble(i);}
			realDataInput = new float[44100];
		  	realDataInput = dataInput.clone();
		  	lenghtOfSample = realDataInput.length;
			renderTimeInput();
			rffFFT();
			renderFreqChar();
			applyFilter(); 
			renderFreqCharOutput(); 	
			refreshFilter(filterFreqSlide.getValue());
		  	renderFilterChar();
		  	applyFilter(); 
			renderFreqCharOutput();
			marker1apply();
			marker2apply();
			rbfFFT();
			renderTimeOutput(); 
			if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);
			}
			else if (vystup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySampleOutput);	
			}
			return;
			}
		
		if (inputSample.getSelectedIndex() == 4){ //vybrán růžový šum
			freqOfHarmonSlid.setEnabled(false);
			try {
				//mySample = SampleLoader.loadFloatSample(getClass().getResource("/pink.wav"));
				mySample = SampleLoader.loadFloatSample(getClass().getClassLoader().getResourceAsStream("pink.wav"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataInput = new float[44100];
		for (int i=0; i<44100; i++){
			dataInput[i] = (float) mySample.readDouble(i);}
		  	realDataInput = dataInput.clone();
		  	lenghtOfSample = realDataInput.length;
			renderTimeInput();
			rffFFT();
			renderFreqChar();
			applyFilter(); 
			renderFreqCharOutput();
			refreshFilter(filterFreqSlide.getValue());
		  	renderFilterChar();	
		  	applyFilter(); 
			renderFreqCharOutput();
			marker1apply();
			marker2apply();
			rbfFFT();
			renderTimeOutput(); 
			if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);
			}
			else if (vystup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySampleOutput);	
			}
			
			}
		
		if (inputSample.getSelectedIndex() == 5){ 
			freqOfHarmonSlid.setEnabled(false);
			try {
			//	mySample = SampleLoader.loadFloatSample(getClass().getResource("/voda.wav"));
				mySample = SampleLoader.loadFloatSample(getClass().getClassLoader().getResourceAsStream("voda.wav"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataInput = new float[44100];
			for (int i=0; i<44100; i++){
			dataInput[i] = (float) mySample.readDouble(i);}
			for(int i=0; i<44100; i++){
			dataInput[i] = dataInput[i]*8.0f;	
			}
			mySample = new FloatSample(dataInput);
		  	realDataInput = dataInput.clone();
		  	lenghtOfSample = realDataInput.length;
			renderTimeInputAll();
			rffFFT();
			renderFreqChar();
			applyFilter(); 
			renderFreqCharOutput();
			refreshFilter(filterFreqSlide.getValue());
		  	renderFilterChar();
		  	applyFilter(); 
			renderFreqCharOutput();
			marker1apply();
			marker2apply();
			rbfFFT();
			renderTimeOutputAll(); 
			if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);
			}
			else if (vystup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySampleOutput);	
			}
			}
		
		if (inputSample.getSelectedIndex() == 6){ 
			freqOfHarmonSlid.setEnabled(false);
			try {
				//mySample = SampleLoader.loadFloatSample(getClass().getResource("/haf.wav"));
				mySample = SampleLoader.loadFloatSample(getClass().getClassLoader().getResourceAsStream("haf.wav"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataInput = new float[44100];
			for (int i=0; i<44100; i++){
			dataInput[i] = (float) mySample.readDouble(i);}
			for(int i=0; i<44100; i++){
			dataInput[i] = dataInput[i]*15.0f;	
			}
			mySample = new FloatSample(dataInput);
		  	realDataInput = dataInput.clone();
		  	lenghtOfSample = realDataInput.length;
			renderTimeInputAll();
			rffFFT();
			renderFreqChar();
			applyFilter(); 
			renderFreqCharOutput();
			refreshFilter(filterFreqSlide.getValue());
		  	renderFilterChar();
		  	applyFilter(); 
			renderFreqCharOutput();
			marker1apply();
			marker2apply();
			rbfFFT();
			renderTimeOutputAll(); 
			if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);
			}
			else if (vystup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySampleOutput);	
			}
		}
		
		if (inputSample.getSelectedIndex() == 7){ 
			freqOfHarmonSlid.setEnabled(false);
			try {
			//	mySample = SampleLoader.loadFloatSample(getClass().getResource("/vystrel.wav"));
				mySample = SampleLoader.loadFloatSample(getClass().getClassLoader().getResourceAsStream("vystrel.wav"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataInput = new float[44100];
			for (int i=0; i<44100; i++){
			dataInput[i] = (float) mySample.readDouble(i);}
			for(int i=0; i<44100; i++){
			dataInput[i] = dataInput[i]*4.0f;	
			}
			mySample = new FloatSample(dataInput);
		  	realDataInput = dataInput.clone();
		  	lenghtOfSample = realDataInput.length;
			renderTimeInputAll();
			rffFFT();
			renderFreqChar();
			applyFilter(); 
			renderFreqCharOutput();
			refreshFilter(filterFreqSlide.getValue());
		  	renderFilterChar();
		  	applyFilter(); 
			renderFreqCharOutput();
			marker1apply();
			marker2apply();
			rbfFFT();
			renderTimeOutputAll(); 
			if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);
			}
			else if (vystup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySampleOutput);	
			}
		}
		
		if (inputSample.getSelectedIndex() == 8){ 
			freqOfHarmonSlid.setEnabled(false);
			try {
			//	mySample = SampleLoader.loadFloatSample(getClass().getResource("/oldtv.wav"));
				mySample = SampleLoader.loadFloatSample(getClass().getClassLoader().getResourceAsStream("oldtv.wav"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataInput = new float[44100];
			for (int i=0; i<44100; i++){
			dataInput[i] = (float) mySample.readDouble(i);}
			for(int i=0; i<44100; i++){
			dataInput[i] = dataInput[i]*15.0f;	
			}
			mySample = new FloatSample(dataInput);
		  	realDataInput = dataInput.clone();
		  	lenghtOfSample = realDataInput.length;
			renderTimeInputAll();
			rffFFT();
			renderFreqChar();
			applyFilter(); 
			renderFreqCharOutput();
			refreshFilter(filterFreqSlide.getValue());
		  	renderFilterChar();
		  	applyFilter(); 
			renderFreqCharOutput();
			marker1apply();
			marker2apply();
			rbfFFT();
			renderTimeOutputAll(); 	
			if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);
			}
			else if (vystup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySampleOutput);	
			}
		}
		
		if (inputSample.getSelectedIndex() == 9){ 
			freqOfHarmonSlid.setEnabled(false);
			try {
			//	mySample = SampleLoader.loadFloatSample(getClass().getResource("/oldtv.wav"));
				mySample = SampleLoader.loadFloatSample(getClass().getClassLoader().getResourceAsStream("buben.wav"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataInput = new float[44100];
			for (int i=0; i<44100; i++){
			dataInput[i] = (float) mySample.readDouble(i);}
			for(int i=0; i<44100; i++){
			dataInput[i] = dataInput[i]*15.0f;	
			}
			mySample = new FloatSample(dataInput);
		  	realDataInput = dataInput.clone();
		  	lenghtOfSample = realDataInput.length;
			renderTimeInputAll();
			rffFFT();
			renderFreqChar();
			applyFilter(); 
			renderFreqCharOutput();
			refreshFilter(filterFreqSlide.getValue());
		  	renderFilterChar();
		  	applyFilter(); 
			renderFreqCharOutput();
			marker1apply();
			marker2apply();
			rbfFFT();
			renderTimeOutputAll(); 	
			if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);
			}
			else if (vystup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySampleOutput);	
			}
		}
		if (inputSample.getSelectedIndex() == 10){ 
			freqOfHarmonSlid.setEnabled(false);
			try {
			//	mySample = SampleLoader.loadFloatSample(getClass().getResource("/oldtv.wav"));
				mySample = SampleLoader.loadFloatSample(getClass().getClassLoader().getResourceAsStream("piano.wav"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataInput = new float[44100];
			for (int i=0; i<44100; i++){
			dataInput[i] = (float) mySample.readDouble(i);}
			for(int i=0; i<44100; i++){
			dataInput[i] = dataInput[i]*15.0f;	
			}
			mySample = new FloatSample(dataInput);
		  	realDataInput = dataInput.clone();
		  	lenghtOfSample = realDataInput.length;
			renderTimeInputAll();
			rffFFT();
			renderFreqChar();
			applyFilter(); 
			renderFreqCharOutput();
			refreshFilter(filterFreqSlide.getValue());
		  	renderFilterChar();
		  	applyFilter(); 
			renderFreqCharOutput();
			marker1apply();
			marker2apply();
			rbfFFT();
			renderTimeOutputAll(); 	
			if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);
			}
			else if (vystup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySampleOutput);	
			}
		}
	}
	
	if(ie.getSource() == filterType){
			refreshFilter(filterFreqSlide.getValue()); renderFilterChar();
			applyFilter(); 
			renderFreqCharOutput(); 
			marker2apply(); 
			rbfFFT(); 
			if(inputSample.getSelectedIndex() >= 5){renderTimeOutputAll();} 
			else {renderTimeOutput();}
			if(vystup.getState()){ 
			samplePlayer.dataQueue.clear();
			samplePlayer.dataQueue.queueLoop(mySampleOutput);
			}
		}
	
	if (ie.getSource() == vstup){
		samplePlayer.dataQueue.queueLoop(mySample);
	}
	
	if (ie.getSource() == vystup){
		samplePlayer.dataQueue.queueLoop(mySampleOutput);
	}
}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==playInputSamp){						/// tlačítko přehrát vstup
				
		if (playInputSamp.getLabel() == "Přehrej"){	
			playInputSamp.setLabel("Zastav");			
		 	applyFilter(); 
			renderFreqCharOutput();			
			marker1apply();
			marker2apply();			
			rbfFFT();
			
		if(inputSample.getSelectedIndex() >= 5){	renderTimeOutputAll();}
		else{renderTimeOutput();}
			
			if (vstup.getState()){	
				samplePlayer.dataQueue.clear();
			samplePlayer.dataQueue.queueLoop(mySample);
				out.start();
				}
			else{
				samplePlayer.dataQueue.clear();	
				samplePlayer.dataQueue.queueLoop(mySampleOutput);
				out.start();}
		}
		else{	playInputSamp.setLabel("Přehrej");
				out.stop();}
			}														
	}

	@Override		//SLIDERY
	public void stateChanged(ChangeEvent che) {
		if(che.getSource() == freqOfHarmonSlid){ 		
															// pohybuje se sliderem vstupní frekvence
			if (inputSample.getSelectedIndex() == 0){
				generateSin((freqOfHarmonSlid.getValue()+10)*5);
				realDataInput = new float[44100];
			  	realDataInput = dataInput.clone();
			  	realDataInputBack = realDataInput.clone();
				rffFFT();
				renderFreqChar();
				renderTimeSin();
				applyFilter(); 
				renderFreqCharOutput();
				marker1apply();
				marker2apply();
				rbfFFT();
				renderTimeOutput(); 
				
				if (vystup.getState()){
					samplePlayer.dataQueue.clear();
					samplePlayer.dataQueue.queueLoop(mySampleOutput);	
				}
				else if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);}
			}
			
			if (inputSample.getSelectedIndex() == 1){
				generateSaw((freqOfHarmonSlid.getValue()+10)*5);
				realDataInput = new float[44100];
			  	realDataInput = dataInput.clone();
			  	realDataInputBack = realDataInput.clone();
				rffFFT();
				renderFreqChar();
				renderTimeSaw();
				applyFilter(); 
				renderFreqCharOutput();
				marker1apply();
				marker2apply();
				rbfFFT();
				renderTimeOutput();
				if (vystup.getState()){
					samplePlayer.dataQueue.clear();
					samplePlayer.dataQueue.queueLoop(mySampleOutput);	
				}
				else if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);}
			}
			
			if (inputSample.getSelectedIndex() == 2){	
				generateBin((freqOfHarmonSlid.getValue()+10)*5);
				realDataInput = new float[44100];
			  	realDataInput = dataInput.clone();
			  	realDataInputBack = realDataInput.clone();
				rffFFT();
				renderFreqChar();
				renderTimeBin();
				applyFilter(); 
				renderFreqCharOutput();
				marker1apply();
				marker2apply();
				rbfFFT();
				renderTimeOutput();
				if (vystup.getState()){
					samplePlayer.dataQueue.clear();
					samplePlayer.dataQueue.queueLoop(mySampleOutput);	
				}
				else if(vstup.getState()){
				samplePlayer.dataQueue.clear();
				samplePlayer.dataQueue.queueLoop(mySample);}
			}
		}
		
		
		if(che.getSource() == freqMark){		// slider značky
			marker1apply();
			freqMark2.setValue(freqMark.getValue());
			}
		if(che.getSource() == freqMark2){		// slider značky
			marker2apply();
			freqMark.setValue(freqMark2.getValue());
			}
		
		if (che.getSource() == filterFreqSlide){refreshFilter(filterFreqSlide.getValue()); renderFilterChar(); applyFilter(); renderFreqCharOutput(); marker2apply(); rbfFFT(); if(inputSample.getSelectedIndex() >= 5){renderTimeOutputAll();} else {renderTimeOutput();} if(vystup.getState()){ samplePlayer.dataQueue.clear(); samplePlayer.dataQueue.queueLoop(mySampleOutput); }}
		
		if (che.getSource() == sirkaPasmaSlid){refreshFilter(filterFreqSlide.getValue()); renderFilterChar(); applyFilter(); renderFreqCharOutput(); marker2apply(); rbfFFT(); if(inputSample.getSelectedIndex() >= 5){renderTimeOutputAll();} else {renderTimeOutput();} if(vystup.getState()) {samplePlayer.dataQueue.clear(); samplePlayer.dataQueue.queueLoop(mySampleOutput);}}
		
		if(che.getSource() == hlasitost){ samplePlayer.amplitude.set(hlasitost.getValue()/100.0);}
		
	}
	
	public void marker1apply(){
		renderFreqChar();
		surface = inputFreq.getGraphics();
	  	surface.setColor(Color.red);
	  	surface.drawLine(freqMark.getValue()+1, 180, freqMark.getValue()+1, 20);
	  	
	  	surface.drawString(String.format("f = %.1f Hz, A(f) = %.1f, phase=%f", freqMark.getValue()*44100.0/1600.0, Math.sqrt(dataInput[freqMark.getValue()*2]*dataInput[freqMark.getValue()*2]+dataInput[freqMark.getValue()*2+1]*dataInput[freqMark.getValue()*2+1]), Math.atan2(dataInput[freqMark.getValue()*2+1], dataInput[freqMark.getValue()*2]) ), 150, 195);
	  	surface.dispose();
	}
	
	public void marker2apply(){
		renderFreqCharOutput();
		surface = outputFreq.getGraphics();
	  	surface.setColor(Color.red);
	  	surface.drawLine(freqMark2.getValue()+1, 180, freqMark2.getValue()+1, 20);
	  	
	  	surface.drawString(String.format("f = %.1f Hz, A(f) = %.1f", freqMark2.getValue()*44100.0/1600.0, Math.sqrt(dataOutput[freqMark2.getValue()*2]*dataOutput[freqMark2.getValue()*2]+dataOutput[freqMark2.getValue()*2+1]*dataOutput[freqMark2.getValue()*2+1])), 150, 195);
	  	surface.dispose();	
	}
	
	public void generateSin(int inputFreq){
		dataInput = new float[44100];
		float value = 44100.0f/inputFreq;
	for( int i=0; i<dataInput.length; i++ ){
		dataInput[i] = (float) Math.sin(Math.toRadians((360.0/value)*i));	
		}	
		mySample= new FloatSample(dataInput);
		mySample.setFrameRate(44100);}
	
	public void generateSaw(int inputFreq){
		dataInput = new float[44100]; 
		float value = 0.0f;
		for( int i=0; i<dataInput.length; i++ ){
			dataInput[i] = value;
			value += 2/(44100.0f/inputFreq);
			  if( value >= 1 ){value = -1;}
			 }
			mySample= new FloatSample(dataInput);
			mySample.setFrameRate(44100);}
	
	public void generateBin(int inputFreq){
		dataInput = new float[44100];
		float value = 44100.0f/inputFreq;
		int counter = 0;
		for( int i=0; i<dataInput.length; i++ ){
			if(counter >= value){counter = 0;}
			else {counter++;}
		if(counter > ((44100.0f/inputFreq)/2)){dataInput[i]=-1;}
		else {dataInput[i]=1;}
			
		}
		mySample= new FloatSample(dataInput);
		mySample.setFrameRate(44100);
	}
	public void renderTimeInput(){
		inputTime = createImage(400, 200);		// vstup časový průběh
	  	surface = inputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 40, 15);
	  	
	  	surface.drawLine(0, 100, 400, 100);
	  	surface.drawLine(20, 0, 20, 200);
	  	surface.drawString("t ->", 385, 120);
	  	surface.drawLine(10, 180, 30, 180);
	  	surface.drawLine(10, 20, 30, 20);
	  	surface.drawString("1", 8, 16);
	  	surface.drawString("-1", 4, 176);
	  	surface.drawString("Je zobrazeno pouze prvních 400 vzorků", 35, 195);
	  	surface.drawString("Vzorkovací kmitočet 44,1 kHz", 140, 15);
	  	
	  	for (int i=0; i<400; i++){
	  		surface.drawLine(i+20, (int)(dataInput[i]*80)+100, i+21, (int) (dataInput[i+1]*80)+100);
	  	}
	  	surface.dispose();	
	}
	
	public void renderTimeInputAll(){
		inputTime = createImage(400, 200);		// vstup časový průběh
	  	surface = inputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 40, 15);
	  	
	  	surface.drawLine(0, 100, 400, 100);
	  	surface.drawLine(20, 0, 20, 200);
	  	surface.drawString("t ->", 385, 120);
	  	surface.drawLine(10, 180, 30, 180);
	  	surface.drawLine(10, 20, 30, 20);
	  	surface.drawString("1", 8, 16);
	  	surface.drawString("-1", 4, 176);
		surface.drawString("Vzorkovací kmitočet 44,1 kHz", 140, 15);
	  	
	  float[] envelope = new float[400];	
	  float max=0;
	  
	  for(int i=0; i<envelope.length; i++){
		  for(int b=0; b<110; b++){
			 if(Math.abs(dataInput[(i*110)+b]) > max ){max = Math.abs(dataInput[(i*110)+b]);} 	
		  }
		  envelope[i] = max;
		  max = 0;
	  }
	  
	  	for (int i=0; i<400; i++){
	  		surface.drawLine(i+20, (int)(-envelope[i]*15)+100, i+20, (int)(envelope[i]*15)+100);//(int)Math.floor(realDataOutput.length/400)
	  	}
	  	surface.dispose();	
		
	}
	
	public void renderTimeOutput(){
		outputTime = createImage(400, 200);		// vstup časový průběh
	  	surface = outputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 40, 15);
	  	
	  	surface.drawLine(0, 100, 400, 100);
	  	surface.drawLine(20, 0, 20, 200);
	  	surface.drawString("t ->", 385, 120);
	  	surface.drawLine(10, 180, 30, 180);
	  	surface.drawLine(10, 20, 30, 20);
	  	surface.drawString("1", 8, 16);
	  	surface.drawString("-1", 4, 176);
	  	
	  	
	  	for (int i=0; i<400; i++){
	  		surface.drawLine(i+20, (int)(realDataOutput[i]*80)+100, i+21, (int) (realDataOutput[i+1]*80)+100);//(int)Math.floor(realDataOutput.length/400)
	  	}
	  	surface.dispose();	
	}
	
	public void renderTimeOutputAll(){
		outputTime = createImage(400, 200);		// vstup časový průběh
	  	surface = outputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 40, 15);
	  	
	  	surface.drawLine(0, 100, 400, 100);
	  	surface.drawLine(20, 0, 20, 200);
	  	surface.drawString("t ->", 385, 120);
	  	surface.drawLine(10, 180, 30, 180);
	  	surface.drawLine(10, 20, 30, 20);
	  	surface.drawString("1", 8, 16);
	  	surface.drawString("-1", 4, 176);
	  	
	  	float[] envelope = new float[400];
	  	float max=0;
	  	
	    for(int i=0; i<envelope.length; i++){
			  for(int b=0; b<110; b++){
				 if(Math.abs(realDataOutput[(i*110)+b]) > max ){max = Math.abs(realDataOutput[(i*110)+b]);} 	
			  }
			  envelope[i] = max;
			  max = 0;
		  }
		  
		  	for (int i=0; i<400; i++){
		  		surface.drawLine(i+20, (int)(-envelope[i]*15)+100, i+20, (int)(envelope[i]*15)+100);//(int)Math.floor(realDataOutput.length/400)
		  	}
	  	

	  	surface.dispose();	
	}
	
	public void renderTimeSin(){
		inputTime = createImage(400, 200);		// vstup časový průběh
	  	surface = inputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 40, 15);
	  	
	  	surface.drawLine(0, 100, 400, 100);
	  	surface.drawLine(20, 0, 20, 200);
	  	surface.drawString("t ->", 375, 120);
	  	surface.drawLine(10, 180, 30, 180);
	  	surface.drawLine(10, 20, 30, 20);
	  	surface.drawString("1", 8, 16);
	  	surface.drawString("-1", 4, 176);
	  	surface.drawString("Je zobrazeno pouze prvních 400 vzorků", 35, 195);
		surface.drawString("Vzorkovací kmitočet 44,1 kHz", 140, 15);
	
	  	for (int i=0; i<400; i++){
	  		surface.drawLine(i+20, (int)(realDataInputBack[i]*80)+100, i+21, (int) (realDataInputBack[i+1]*80)+100);//(int)Math.floor(realDataOutput.length/400)
	  	}
		surface.setColor(Color.red);
		surface.drawString(String.format("f = %d Hz", (freqOfHarmonSlid.getValue()+10)*5), 320, 15);
	  	surface.dispose();
	}
	
	public void renderTimeSaw(){
		inputTime = createImage(400, 200);		// vstup časový průběh
	  	surface = inputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 40, 15);
	  	
	  	surface.drawLine(0, 100, 400, 100);
	  	surface.drawLine(20, 0, 20, 200);
	  	surface.drawString("t ->", 375, 120);
	  	surface.drawLine(10, 180, 30, 180);
	  	surface.drawLine(10, 20, 30, 20);
	  	surface.drawString("1", 8, 16);
	  	surface.drawString("-1", 4, 176);
	  	surface.drawString("Je zobrazeno pouze prvních 400 vzorků", 35, 195);
		surface.drawString("Vzorkovací kmitočet 44,1 kHz", 140, 15);
		
	  	for (int i=0; i<400; i++){
	  		surface.drawLine(i+20, (int)(realDataInputBack[i]*80)+100, i+21, (int) (realDataInputBack[i+1]*80)+100);//(int)Math.floor(realDataOutput.length/400)
	  	}
	  	
	  	surface.setColor(Color.red);
		surface.drawString(String.format("f = %d Hz", (freqOfHarmonSlid.getValue()+10)*5), 320, 15);
	  	surface.dispose();
	}
	
	public void renderTimeBin(){
		inputTime = createImage(400, 200);		// vstup časový průběh
	  	surface = inputTime.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Časový průběh", 40, 15);
	  	
	  	surface.drawLine(0, 100, 400, 100);
	  	surface.drawLine(20, 0, 20, 200);
	  	surface.drawString("t ->", 375, 120);
	  	surface.drawLine(10, 180, 30, 180);
	  	surface.drawLine(10, 20, 30, 20);
	  	surface.drawString("1", 8, 16);
	  	surface.drawString("-1", 4, 176);
	  	surface.drawString("Je zobrazeno pouze prvních 400 vzorků", 35, 195);
		surface.drawString("Vzorkovací kmitočet 44,1 kHz", 140, 15);

	  	for (int i=0; i<400; i++){
	  		surface.drawLine(i+20, (int)(realDataInputBack[i]*80)+100, i+21, (int) (realDataInputBack[i+1]*80)+100);//(int)Math.floor(realDataOutput.length/400)
	  	}
	  	surface.setColor(Color.red);
		surface.drawString(String.format("f = %d Hz", (freqOfHarmonSlid.getValue()+10)*5), 320, 15);
	  	surface.dispose();
	}
	
	public void rffFFT(){
		FloatFFT_1D dfft = new FloatFFT_1D(1600);
		dfft.realForward(dataInput);
		
		float[] dataBuffer = new float[44100];
		float buffer;
		
		for(int i=0; i<44100; i++){
			buffer = realDataInput[i];
			dataBuffer[i] = buffer;	
		}
		realDataInput = new float[88200];
		for(int i=0; i<44100; i++){
			buffer = dataBuffer[i];
			realDataInput[i] = buffer;	
		}
		dfft2.realForward(realDataInput);
	}
	
	public void rbfFFT(){		
		dfft2.realInverse(realDataOutput, true);
		
		float[] dataBuffer = new float[44100];
		float buffer;
		
		for(int i=0; i<44100; i++){
			buffer = realDataOutput[i];
			dataBuffer[i] = buffer;	
		}
		realDataOutput = new float[44100];
		for(int i=0; i<44100; i++){
			buffer = dataBuffer[i];
			realDataOutput[i] = buffer;	
		}
		
		mySampleOutput = new FloatSample(realDataOutput);
	}
	
	public void renderFreqChar(){
		surface = inputFreq.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Frekvenční modulová charakteristika", 10, 20);
	  	surface.drawLine(0, 180, 400, 180);
	  	surface.drawLine(1, 183, 1, 177);
	  	surface.drawString("0 Hz", 5, 195);
	  	surface.drawLine(398, 183, 398, 177);
	  	surface.drawString("11 kHz", 360, 195);
	  	
	  	for(int i=0; i<400; i++){
	  	surface.drawLine(i, 180, i, -(int)((Math.sqrt(dataInput[i*2]*dataInput[i*2]+dataInput[i*2+1]*dataInput[i*2+1])/5)-180));	
	  	}
	  	surface.dispose();	
	}
	
	public void refreshFilter(int sliderValue){
		if (filterType.getSelectedIndex() == 0){			//pokud je vybrána dolní propust
			filterFreq.setText("Mezní kmitočet");
			for(int i=0; i<filterChar.length; i++){  
			 if(i<= (float) (400.0f/11000.0f)*filterFreqSlide.getValue()){filterChar[i]=1; }
			 else{filterChar[i]=0; }
			} sirkaPasmaSlid.setEnabled(false);
			
			for(int i=0; i<realFilterChar.length; i++){
				if(i<= filterFreqSlide.getValue()){realFilterChar[i]=1; }
				 else{realFilterChar[i]=0; }				
			}	
		
		}
		
		else if (filterType.getSelectedIndex() == 1){		//HP
			filterFreq.setText("Mezní kmitočet");
			for(int i=0; i<filterChar.length; i++){  
				 if(i<= (float) (400.0f/11000.0f)*filterFreqSlide.getValue() ){filterChar[i]=0; }
				 else{filterChar[i]=1; }
		}sirkaPasmaSlid.setEnabled(false);
		
		for(int i=0; i<realFilterChar.length; i++){
			if(i<= filterFreqSlide.getValue()){realFilterChar[i]=0; }
			 else{realFilterChar[i]=1; }				
		}
		}
		
		else if (filterType.getSelectedIndex() == 2){		//PP
			filterFreq.setText("Střední kmitočet");
			for(int i=0; i<filterChar.length; i++){
			if((i >= (float) (400.0f/11000.0f)*filterFreqSlide.getValue()-(((400.0f/11000.0f)*sirkaPasmaSlid.getValue())/2))&&(i <= (float) (400.0f/11000.0f)*filterFreqSlide.getValue()+(((400.0f/11000.0f)*sirkaPasmaSlid.getValue())/2))){filterChar[i]=1;}
			else{filterChar[i]=0;}
			}sirkaPasmaSlid.setEnabled(true);
			
			for(int i=0; i<realFilterChar.length; i++){
				if((i >= filterFreqSlide.getValue()-(sirkaPasmaSlid.getValue()/2))&&(i <= (filterFreqSlide.getValue())+(sirkaPasmaSlid.getValue()/2))){realFilterChar[i]=1; }
				 else{realFilterChar[i]=0; }				
			}
			}
		
		else if (filterType.getSelectedIndex() == 3){		//PZ
			filterFreq.setText("Střední kmitočet");
			for(int i=0; i<filterChar.length; i++){
			if((i >= (float) (400.0f/11000.0f)*filterFreqSlide.getValue()-(((400.0f/11000.0f)*sirkaPasmaSlid.getValue())/2))&&(i <= (float) (400.0f/11000.0f)*filterFreqSlide.getValue()+(((400.0f/11000.0f)*sirkaPasmaSlid.getValue())/2))){filterChar[i]=0;}
			else{filterChar[i]=1;}
			}sirkaPasmaSlid.setEnabled(true);
			
			for(int i=0; i<realFilterChar.length; i++){
				if((i >= filterFreqSlide.getValue()-(sirkaPasmaSlid.getValue()/2))&&(i <= (filterFreqSlide.getValue())+(sirkaPasmaSlid.getValue()/2))){realFilterChar[i]=0; }
				 else{realFilterChar[i]=1; }				
			}
			}
	}
	
	public void renderFilterChar(){
		surface = filter.getGraphics();	
		surface = filter.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Spektrální charakteristika ideálního filtru", 30, 20);
	  	surface.drawLine(0, 180, 400, 180);
	  	surface.drawLine(397, 0, 397, 200);
	  	surface.drawLine(394, 80, 399, 80);
	  	surface.drawString("1", 388, 73);
	  	
	  	surface.drawLine(1, 183, 1, 177);
	  	surface.drawString("0 Hz", 5, 195);
	  	surface.drawLine(397, 183, 397, 177);
	  	surface.drawString("11 kHz", 357, 195);

	  	for(int i=0; i<400; i++ ){
	  		if(filterChar[i] == 1) {surface.drawLine(i, 180, i, 80);}
	  	}
	  if (filterType.getSelectedIndex() >=2.0){	
	  	surface.drawString(String.format("fstr = %d Hz", filterFreqSlide.getValue()), 270, 15);}
	  else{
		  surface.drawString(String.format("fmez = %d Hz", filterFreqSlide.getValue()), 270, 15);}	
	  	if((filterType.getSelectedIndex() == 2)||(filterType.getSelectedIndex() == 3)){ 
	  	surface.drawString(String.format("fpas = %d Hz", sirkaPasmaSlid.getValue()), 270, 30);}
	  	surface.dispose();	
	}

	public void applyFilter(){		
		dataOutput = dataInput.clone();		
		for(int i=0; i<800; i++){
			dataOutput[i*2]= dataOutput[i*2]*filterChar[i];
			dataOutput[i*2+1]=dataOutput[i*2+1]*filterChar[i];
		}	
		realDataOutput = realDataInput.clone();
		for (int i=1; i<realFilterChar.length; i++){
			realDataOutput[i*2]= realDataOutput[i*2]*realFilterChar[i];
			realDataOutput[i*2+1]=realDataOutput[i*2+1]*realFilterChar[i];			
		}
	}
	
	public void renderFreqCharOutput(){
		surface = outputFreq.getGraphics();
	  	surface.setColor(Color.white);
	  	surface.fillRect(0, 0, 400, 200);
	  	surface.setColor(new Color(53,147,176));
	  	surface.drawString("Frekvenční modulová charakteristika", 10, 20);
	  	surface.drawLine(0, 180, 400, 180);
	  	surface.drawLine(1, 183, 1, 177);
	  	surface.drawString("0 Hz", 5, 195);
	  	surface.drawLine(398, 183, 398, 177);
	  	surface.drawString("11 kHz", 360, 195);
	  	for(int i=0; i<400; i++){
	  	surface.drawLine(i, 180, i, -(int)((Math.sqrt(dataOutput[i*2]*dataOutput[i*2]+dataOutput[i*2+1]*dataOutput[i*2+1])/5)-180 ));	
	  	}
	  	surface.dispose();
	}
}