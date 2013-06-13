package gui.base;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListenMenuQuit implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}