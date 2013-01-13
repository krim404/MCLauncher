package com.kokakiwi.mclauncher.ui.simple;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.ui.simple.components.TransparentPanel;

public class UpdateDialog extends JDialog 
{
	public JProgressBar progressBar;
	public LauncherAPI api;
	public JLabel l;
	private static final long serialVersionUID = -3808152035558318536L;
	private int maxValue = 0, value = 0;
	public UpdateDialog(LauncherAPI api, String title,int maxValue)
	{
		super(api.getFrame());
        this.api = api;
        
      
        
        addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent event)
            {
                setVisible(false);
            }
        });
        
        setTitle("Updating "+title);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        
        l = new JLabel();
        l.setText("Starting Update...");
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		this.maxValue = maxValue;
		
		TransparentPanel pan = new TransparentPanel();
		pan.add(progressBar,"North");
		pan.add(l);

		getContentPane().add(pan);
		progressBar.setBorder(new EmptyBorder(16, 24, 24, 24));
        pack();
        
        Dimension size = getSize();
        size.width = 200;
        size.height = 120;
        setSize(size);
        
        setLocationRelativeTo(api.getFrame());
        
	}
	
	public void updateValue()
	{
		++value;
		int n = 100 / maxValue * value;
		progressBar.setValue(n);
	}
	
	public void setValue(int v)
	{
		value = v;
		int n = 100 / maxValue * value;
		progressBar.setValue(n);
	}

	public void setCurr(String up) 
	{
		l.setText(up);
	}
}
