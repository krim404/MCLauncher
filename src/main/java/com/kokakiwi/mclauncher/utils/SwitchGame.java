package com.kokakiwi.mclauncher.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.ui.simple.SimpleLoginPage;

public class SwitchGame implements ActionListener
{
	SimpleLoginPage p;
	LauncherAPI a;
	
	public SwitchGame(SimpleLoginPage simpleLoginPage, LauncherAPI api) 
	{
		this.p = simpleLoginPage;
		this.a = api;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Configuration g = this.a.getConfigList().getConfig((String)this.p.mode.getSelectedItem());
		if(g != null)
		{
			this.a.getMain().setConfig(g);
			this.a.getMain().setLastMod(g.getString("game.id"));
			this.a.getMain().reload();
		} else MCLogger.debug("Unable to switch game to: "+this.p.mode.getSelectedItem());
	}

}
