package com.apql.Apql;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class QueryFrame extends JFrame {

	private static final long serialVersionUID = 7476203503736619817L;

	public QueryFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension p = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (p.getWidth() - 700) / 2;
		int height = (int) (p.getHeight() - 500) / 2;
		setLocation(new Point(width, height));
		setMinimumSize(new Dimension(700, 500));
	}

}
