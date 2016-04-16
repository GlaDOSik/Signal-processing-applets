package huffman;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.SwingConstants;


public class MyCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
	
	ArrayList<String> cellColor = new ArrayList<String>();
		
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    setText(value.toString());
	    setHorizontalAlignment(SwingConstants.CENTER);
	    if(cellColor.contains(String.format("%d%d", row,column))){setBackground(new Color(151,183,193));}
	    else{
	    	setBackground(Color.WHITE);
	    	
	    }
	    	
	    return this;
	}
}