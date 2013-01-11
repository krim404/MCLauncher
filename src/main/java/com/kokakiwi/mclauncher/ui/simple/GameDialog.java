package com.kokakiwi.mclauncher.ui.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.ui.simple.components.TransparentLabel;
import com.kokakiwi.mclauncher.utils.lang.Translater;

public class GameDialog extends JDialog
{
    private static final long serialVersionUID = -2663368148236524858L;
    
    private final LauncherAPI api;
    
    public GameDialog(final LauncherAPI api)
    {
        super(api.getFrame());
        this.api = api;
        addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent event)
            {
                setVisible(false);
            }
        });
        
        setTitle(Translater.getString("game.windowTitle"));
        setModalityType(ModalityType.TOOLKIT_MODAL);
        
        final JPanel panel = new JPanel(new BorderLayout());
        
        final JLabel label = new JLabel("Games", 0);
        label.setBorder(new EmptyBorder(0, 0, 16, 0));
        label.setFont(new Font("Default", 1, 16));
        panel.add(label, "North");
        
        final JPanel optionsPanel = new JPanel(new BorderLayout());
        final JPanel labelPanel = new JPanel(new GridLayout(0, 1));
        final JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
        optionsPanel.add(labelPanel, "West");
        optionsPanel.add(fieldPanel, "Center");
        
       
        panel.add(optionsPanel, "Center");
        
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(new JPanel(), "Center");
        final JButton doneButton = new JButton(
                Translater.getString("options.done"));
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae)
            {
                setVisible(false);
            }
        });
        buttonsPanel.add(doneButton, "East");
        buttonsPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        
        panel.add(buttonsPanel, "South");
        
        getContentPane().add(panel);
        panel.setBorder(new EmptyBorder(16, 24, 24, 24));
        
        pack();
        setLocationRelativeTo(api.getFrame());
    }
    
    public LauncherAPI getApi()
    {
        return api;
    }
    
}
