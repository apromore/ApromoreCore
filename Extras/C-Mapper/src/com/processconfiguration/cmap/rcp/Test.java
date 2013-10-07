package com.processconfiguration.cmap.rcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Test {
	private Display display;
	private Shell shell;
	private Canvas canvas;
	private Image imageBuffer;

	public Test() {
		display = new Display();
		shell = new Shell(display);
		createGUI();

		shell.open();

		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createGUI() {
		shell.setLayout(new FillLayout());

		canvas = new Canvas(shell, SWT.BORDER);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(imageBuffer, 0, 0, 500, 500, 0, 0, 500, 500);
			}});
		
		imageBuffer = new Image(display, 500, 500);
		Listener listener = new Listener() {
			private GC gc = new GC(imageBuffer);
			
			private int oldX = 0;
			private int oldY = 0;
			private int newX = 0;
			private int newY = 0;

			@Override
			public void handleEvent(Event event) {
				switch(event.type) {
				case SWT.MouseDown:
					oldX = event.x;
					oldY = event.y;
					break;
				case SWT.MouseUp:
					newX = event.x;
					newY = event.y;

					gc.drawLine(oldX, oldY, newX, newY);
					int x = (oldX < newX) ? oldX : newX;
					int y = (oldY < newY) ? oldY : newY;
					int width = (oldX < newX) ? (newX-oldX) : (oldX - newX);
					int height = (oldY < newY) ? (newY-oldY) : (oldY - newY);
					if(width < 20) width = 20;
					if(height < 20) height = 20;
					canvas.redraw(x, y, width, height, true);
					break;
				}
			}
		};
		
		canvas.addListener(SWT.MouseDown, listener);
		canvas.addListener(SWT.MouseUp, listener);

		}

	public static void main(String[] args) {
		new Test();
	}
}
