package me.syari.vectorLeaper

import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(EventListener, this)
    }
}