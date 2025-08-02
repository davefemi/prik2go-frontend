package nl.davefemi.prik2go.gui.factory.components;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Klasse die een Bar (staafje) representeert. Een bar krijgt een label (label name - value).
 * @author Medewerker OU
 */
public class Bar extends JPanel {
	private static final long serialVersionUID = 1L;
	private String labelName;
	private int labelValue = 0;

	/**
	 * Constructor voor de Bar
	 * @param labelName 
	 * @param labelValue
	 * @param x 
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public Bar(String labelName, int labelValue, int x, int y, int width, int height, Color color) {
		super();
		this.labelName = labelName;
		this.labelValue = labelValue;
		JLabel label = new JLabel(labelName + " - " + labelValue);
		label.setFont(new Font("", Font.ITALIC | Font.BOLD , 9));
		label.setForeground(Color.WHITE);
		this.add(label);
		this.setBounds(x, y, width, height);
		this.setBackground(color);
	}	
	
	public String getName() {
		return labelName;
	}

	public int getLabelValue() {
		return labelValue;
	}

}
