package com.kokakiwi.mclauncher.ui.simple;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.kokakiwi.mclauncher.api.LauncherAPI;

public class UpdateDialog extends JDialog 
{
	public JProgressBar progressBar;
	public LauncherAPI api;
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
        
        setTitle("Updating...");
        setModalityType(ModalityType.TOOLKIT_MODAL);
        
		progressBar = new JProgressBar();
		progressBar.setValue(10);
		this.maxValue = maxValue;
		
		getContentPane().add(progressBar);
		progressBar.setBorder(new EmptyBorder(16, 24, 24, 24));
        
        pack();
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
}
