/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buffon;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Buffon extends Applet implements Runnable, ChangeListener, ActionListener {

	int width, height;			// p�epsat hodnoty na doubly a zp�esnit tak v�po�ty
	int frame;
	int delay;
	Thread animator;
	boolean isStart = false;

	double delkaJehly = 40;
	double roztecRadku = 40;
	int celkemHodu = 0;
	double piD = 0.0;
	int celkemProtnuti;
	double uhel=0;
	double jehlaX=0, jehlaY=0, jehlaEndX=0, jehlaEndY=0;

	Button startButton;
	Button simulaceHodu;
	Label delka;
	JSlider delkaSlid;
	Label sirka;
	JSlider sirkaSlid;
	Label pocetHodu;
	Label pocetProtnuti;
	Label pi;
	Image window;
	Graphics buffer;

	public void init(){
		setLayout(null);
		width = getSize().width;		//nastaven� velikosti okna
	    height = getSize().height;
	    setBackground(new Color(151, 183, 193));	//barva pozad�

	    delka = new Label("Délka jehly: 40");
	    delka.setFont(new Font("Arial", Font.PLAIN, 16));
	    add(delka);
	    delka.setBounds(10, 5, 175, 20);

	    delkaSlid = new JSlider();
	    add(delkaSlid);
	    delkaSlid.setBounds(10, 30, 200, 20);
	    delkaSlid.setMinimum(20);
	    delkaSlid.setMaximum(100);
	    delkaSlid.setValue(40);
	    delkaSlid.addChangeListener(this);

	    sirka = new Label("Rozte� linek: 40");
	    sirka.setFont(new Font("Arial", Font.PLAIN, 16));
	    add(sirka);
	    sirka.setBounds(10, 55, 175, 20);

	    sirkaSlid = new JSlider();
	    add(sirkaSlid);
	    sirkaSlid.setBounds(10, 80, 200, 20);
	    sirkaSlid.setMinimum(20);
	    sirkaSlid.setMaximum(100);
	    sirkaSlid.setValue(40);
	    sirkaSlid.addChangeListener(this);

	    pocetHodu = new Label("Celkový počet hodů: 0");
	    pocetHodu.setFont(new Font("Arial", Font.PLAIN, 16));
	    add(pocetHodu);
	    pocetHodu.setBounds(250, 5, 250, 20);

	    pocetProtnuti = new Label("Protnutí linky a jehly: 0");
	    pocetProtnuti.setFont(new Font("Arial", Font.PLAIN, 16));
	    add(pocetProtnuti);
	    pocetProtnuti.setBounds(250, 30, 250, 20);

	    pi = new Label("Vypočtené pí: 0");
	    pi.setFont(new Font("Arial", Font.PLAIN, 16));
	    add(pi);
	    pi.setBounds(250, 55, 250, 20);

	    startButton = new Button("Start");
	    add(startButton);
	    startButton.setBounds(250, 85, 100, 20);
	    startButton.addActionListener(this);

	    window = createImage(800, 400);
	    buffer = window.getGraphics();
	    buffer.setColor(Color.white);
	    buffer.fillRect(0, 0, 800, 400);
	    buffer.setColor(new Color(151, 183, 193));
	    for(int i=0; i<=800/roztecRadku; i++){
			buffer.drawLine(i*(int)roztecRadku, 0, i*(int)roztecRadku, 400);
		}
	    buffer.dispose();

	    simulaceHodu = new Button("Milion hodů");
	    add(simulaceHodu);
	    simulaceHodu.setBounds(400, 85, 100, 20);
	    simulaceHodu.addActionListener(this);

	    String str = "30";										//v�po�et zpo�d�n� pro 30 fps
		int fps = (str != null) ? Integer.parseInt(str) : 10;
		delay = (fps > 0) ? (1000 / fps) : 100;
		}

	@Override
	public void run() {
		long tm = System.currentTimeMillis();
		while (Thread.currentThread() == animator) {

		if (isStart){
			//renderLinky();
			randomJehla();
			renderJehla();

			pocetProtnuti.setText(String.format("Protnutí linky a jehly: %d", celkemProtnuti));
			pi.setText(String.format("Vypočtené pí: %.7f", piD));
			pocetHodu.setText(String.format("Celkový počet hodů: %d", celkemHodu));
		}


		repaint();
		try {					//Thread ��d� vykreslovac� frekvenci
			tm += delay;
			Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
		    } catch (InterruptedException e) {
			break;
		    }
		    // posune sn�mek
		    frame++;}
	}

	public void start() {
		animator = new Thread(this);
		animator.start(); }

   public void stop() {
		animator = null;}

   public void update (Graphics g){
	g.drawImage(window, 10, 120, this);
	}

@Override
public void actionPerformed(ActionEvent act) {
	if(act.getSource() == startButton){
		if(startButton.getLabel().equals("Start") ){
			celkemProtnuti = 0;
			celkemHodu = 0;
			piD = 0;
			renderLinky();
			startButton.setLabel("Stop");
			delkaSlid.setEnabled(false);
			sirkaSlid.setEnabled(false);
			isStart = true;
			simulaceHodu.setEnabled(false);
		}
		else{
			startButton.setLabel("Start");
			delkaSlid.setEnabled(true);
			sirkaSlid.setEnabled(true);
			isStart = false;
			simulaceHodu.setEnabled(true);
		}
	}
	if(act.getSource() == simulaceHodu){

		for(int i=0; i<1000000; i++){
			randomJehla();
		}
		pocetProtnuti.setText(String.format("Protnutí linky a jehly: %d", celkemProtnuti));
		pi.setText(String.format("Vypočtené pí: %.8f", piD));
		pocetHodu.setText(String.format("Celkový počet hodů: %d", celkemHodu));
	}
}

@Override
public void stateChanged(ChangeEvent chl) {
	if(chl.getSource() == delkaSlid){
		delkaJehly = delkaSlid.getValue();
		delka.setText(String.format("Délka jehly: %d", delkaSlid.getValue()));
	}
	else if(chl.getSource() == sirkaSlid){
		roztecRadku = sirkaSlid.getValue();
		sirka.setText(String.format("Rozteč linek: %d", sirkaSlid.getValue()));
		renderLinky();
	}
}

public void renderLinky(){
	buffer = window.getGraphics();
	buffer.setColor(Color.white);
	buffer.fillRect(0, 0, 800, 400);
	buffer.setColor(new Color(151, 183, 193));
	for(int i=0; i<=800/roztecRadku; i++){
		buffer.drawLine(i*(int)roztecRadku, 0, i*(int)roztecRadku, 400);
	}
	buffer.dispose();
}

public void renderJehla(){
	buffer = window.getGraphics();
	buffer.setColor(new Color(151, 183, 193));
	buffer.drawLine((int)jehlaX, (int)jehlaY, (int)jehlaEndX, (int)jehlaEndY);
	buffer.drawLine((int)jehlaX+1, (int)jehlaY, (int)jehlaEndX+1, (int)jehlaEndY);
	buffer.dispose();
}

public void randomJehla(){
	celkemHodu++;
	jehlaX = Math.random()*800;
	jehlaY = Math.random()*400;
	uhel = Math.random()*180;
	jehlaEndX   = jehlaX + delkaJehly * Math.sin(Math.toRadians(uhel));
	jehlaEndY   = jehlaY + delkaJehly * Math.cos(Math.toRadians(uhel));



	if (Math.floor(jehlaX/roztecRadku) != Math.floor(jehlaEndX/roztecRadku)){celkemProtnuti++;}

	 if(celkemProtnuti>0){
	piD =(double) (2*delkaJehly*celkemHodu)/(celkemProtnuti*roztecRadku);}


}

}