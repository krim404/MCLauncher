package com.kokakiwi.mclauncher.ui.simple.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.kokakiwi.mclauncher.ui.simple.SimpleTheme;

public class LogoPanel extends JPanel
{
    private static final long serialVersionUID = -6001125578594995289L;
    private Image             bgImage;
    
    public LogoPanel(URL u)
    {
        setOpaque(true);
        try
        {
            final BufferedImage src = ImageIO.read(u.openStream());
            final int w = src.getWidth();
            final int h = src.getHeight();
            bgImage = src.getScaledInstance(w, h, 16);
            setPreferredSize(new Dimension(w, h));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
    public LogoPanel(String path)
    {
        setOpaque(true);
        try
        {
            final BufferedImage src = ImageIO.read(SimpleTheme.class
                    .getResourceAsStream(path));
            final int w = src.getWidth();
            final int h = src.getHeight();
            bgImage = src.getScaledInstance(w, h, 16);
            setPreferredSize(new Dimension(w, h));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Graphics g)
    {
        paint(g);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(bgImage, 0, 0, null);
    }
}