package me.syari.vectorLeaper

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import java.util.UUID

object EventListener: Listener {
    private val LEAP_ITEM_TYPE = Material.STICK
    private val yVelocityList = mutableMapOf<UUID, Double>()
    private val targetLocationList = mutableMapOf<UUID, Location>()

    @EventHandler
    fun on(e: PlayerInteractEvent) {
        if (e.item?.type != LEAP_ITEM_TYPE) return
        val player = e.player
        val isSneak = player.isSneaking
        when (e.action) {
            Action.RIGHT_CLICK_BLOCK -> {
                if (isSneak) {
                    setTarget(e)
                }
            }
            Action.LEFT_CLICK_AIR -> {
                val uuid = player.uniqueId
                var yVelocity = yVelocityList.getOrDefault(uuid, 2.0)
                val change = if (isSneak) -0.1 else 0.1
                yVelocity += change
                yVelocityList[uuid] = yVelocity
                player.sendActionBar('&', "&0&lyVelocity: ${String.format("%.1f", yVelocity)}")
            }
            else -> return
        }
    }

    private fun setTarget(e: PlayerInteractEvent) {
        val clickedBlock = e.clickedBlock ?: return
        val location = clickedBlock.location
        val player = e.player
        targetLocationList[player.uniqueId] = location
        player.sendActionBar('&', "&0&l選択したブロック: ${location.blockX}, ${location.blockY}, ${location.blockZ}")
    }

    @EventHandler
    fun on(e: PlayerInteractAtEntityEvent) {
        val player = e.player
        if (player.isSneaking) return
        if (player.inventory.itemInMainHand.type != LEAP_ITEM_TYPE) return
        val uuid = player.uniqueId
        val targetLocation = targetLocationList[uuid] ?: return player.sendActionBar('&', "&c&l目標地点が登録されていません")
        val leapEntity = e.rightClicked
        val yVelocity = yVelocityList.getOrDefault(uuid, 2.0)
        leap(leapEntity, targetLocation, yVelocity)
    }

    private fun leap(leapEntity: Entity, targetLocation: Location, yVelocity: Double) {
        val leapEntityLocation = leapEntity.location
        val leapVector = targetLocation.toVector()
        leapVector.subtract(leapEntityLocation.toVector())
        leapVector.normalize()
        val distance = targetLocation.distance(leapEntityLocation)
        leapVector.multiply(distance * 0.15)
        leapVector.multiply(Vector(1.0, yVelocity, 1.0))
        leapEntity.velocity = leapVector
    }
}