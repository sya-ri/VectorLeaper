package me.syari.vectorLeaper

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import java.util.UUID

object EventListener: Listener {
    private val LEAP_ITEM_TYPE = Material.STICK
    private val targetLocationList = mutableMapOf<UUID, Location>()

    @EventHandler
    fun on(e: PlayerInteractEvent){
        if(e.item?.type != LEAP_ITEM_TYPE) return
        if(e.player.isSneaking) return
        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        val clickedBlock = e.clickedBlock ?: return
        val location = clickedBlock.location
        val player = e.player
        targetLocationList[player.uniqueId] = location
        player.sendActionBar('&', "&0&l選択したブロック: ${location.blockX}, ${location.blockY}, ${location.blockZ}")
    }


    @EventHandler
    fun on(e: PlayerInteractAtEntityEvent){
        val player = e.player
        if(!player.isSneaking) return
        if(player.inventory.itemInMainHand.type != LEAP_ITEM_TYPE) return
        val uuid = player.uniqueId
        val targetLocation = targetLocationList[uuid] ?: return player.sendActionBar('&', "&c&l目標地点が登録されていません")
        val leapEntity = e.rightClicked
        val leapEntityLocation = leapEntity.location
        val leapVector = targetLocation.toVector()
        leapVector.subtract(leapEntityLocation.toVector())
        leapVector.normalize()
        val distance = targetLocation.distance(leapEntityLocation)
        leapVector.multiply(distance / 5.0)
        leapVector.multiply(Vector(0.75, 1.5, 0.75))
        leapEntity.velocity = leapVector
    }
}