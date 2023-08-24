package com.survivor.view

import com.survivor.MainApp
import scalafx.scene.control.{Label, ProgressBar}
import scalafxml.core.macros.sfxml
import com.survivor.model.Player
import scalafx.event.ActionEvent
import scalafx.scene.image.Image

@sfxml
class StatController ( private val hpLabelLabel : Label,
                       private val damageLabel: Label,
                       private val speedLabel: Label,
                       private val pointLabel: Label,
                       private val timeLabel: Label,
                       private val responseLabel: Label,
                       private val progressBar: ProgressBar) {

  responseLabel.text = "5 points to upgrade Damage!\n5 points to upgrade Speed!\nYour character stops moving when upgrading!"
  def showCurrentStats(currentTime: String) = {
    hpLabelLabel.text = Player.hp.toString
    damageLabel.text = Player.damage.toString
    speedLabel.text = Player.speed.toString
    pointLabel.text = Player.points.toString
    timeLabel.text = currentTime
    progressBar.progress = Player.hp / Player.initialHP
    }

  val playerStandingImage = new Image(getClass.getResourceAsStream("/com/survivor/images/standing.png"))

  var costDamage = 5
  def upgradeDamage (action: ActionEvent): Unit = {
    MainApp.rootBorderPane.getRight.requestFocus()
    Player.upgrading = true
    Player.imageView.image = playerStandingImage
    if (Player.points >= costDamage) {
      Player.damage += 1
      damageLabel.text = Player.damage.toString
      Player.points -= costDamage
      pointLabel.text = Player.points.toString
      costDamage *= 2
      responseLabel.text = s"Player Damage successfully Upgraded. Next Damage upgrade will cost $costDamage points."
    }
    else {
      responseLabel.text = s"Insufficient points for upgrade. Damage upgrade requires $costDamage points."
    }
  }

  var costSpeed = 5
  def upgradeSpeed(action: ActionEvent): Unit = {
    MainApp.rootBorderPane.getRight.requestFocus()
    Player.upgrading = true
    Player.imageView.image = playerStandingImage
    if (Player.points >= costSpeed) {
      Player.speed += 1
      speedLabel.text = Player.speed.toString
      Player.points -= costSpeed
      pointLabel.text = Player.points.toString
      costSpeed *= 2
      responseLabel.text = s"Player Speed successfully Upgraded. Next Speed upgrade will cost $costSpeed points."
    }
    else {
      responseLabel.text = s"Insufficient points for upgrade. Speed upgrade requires $costSpeed points."
    }
  }
}
