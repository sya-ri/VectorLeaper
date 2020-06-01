package me.syari.vectorLeaper

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import java.util.UUID

object EventListener: Listener {
    private val targetVectorList = mutableMapOf<UUID, Vector>()
    private val targetScalarList = mutableMapOf<UUID, Double>()

    @EventHandler
    fun on(e: PlayerInteractEvent){
        when(e.action){
            Action.RIGHT_CLICK_BLOCK -> setTarget(e)
            Action.LEFT_CLICK_AIR -> addScalar(e.player, -0.1)
            Action.RIGHT_CLICK_AIR -> addScalar(e.player, 0.1)
            else -> return
        }
    }

    private fun setTarget(e: PlayerInteractEvent){
        val clickedBlock = e.clickedBlock ?: return
        val location = clickedBlock.location
        val vector = location.toVector()
        val player = e.player
        targetVectorList[player.uniqueId] = vector
        player.sendActionBar('&', "&0&l選択したブロック: ${location.blockX}, ${location.blockY}, ${location.blockZ}")
    }

    private fun addScalar(player: Player, amount: Double){
        val uuid = player.uniqueId
        val scalar = targetScalarList.getOrDefault(uuid, 1.0) + amount
        targetScalarList[uuid] = scalar
        player.sendActionBar('&', "&0&lスカラー: ${String.format("%.1f", scalar)}")
    }

    @EventHandler
    fun on(e: PlayerInteractAtEntityEvent){
        val player = e.player
        val uuid = player.uniqueId
        val targetVector = targetVectorList[uuid] ?: return player.sendActionBar('&', "&c&l目標地点が登録されていません")
        val leapEntity = e.rightClicked
        val leapEntityVector = leapEntity.location.toVector()
        targetVector.subtract(leapEntityVector)
        targetVector.normalize()
        val scalar = targetScalarList.getOrDefault(uuid, 1.0)
        targetVector.multiply(scalar)
        leapEntity.velocity = targetVector
    }
}