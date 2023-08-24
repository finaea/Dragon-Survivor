package com.survivor.model

import scalafx.scene.image.{Image, ImageView}

object Player {
  val playerStandingImage = new Image(getClass.getResourceAsStream("/com/survivor/images/standing.png"))
  val imageView: ImageView = new ImageView(playerStandingImage) {
    fitWidth = 80;
    fitHeight = 80;
    x = 560;
    y = 360
  }
  var hp: Int = 100
  var damage: Int = 5
  var speed: Int = 1
  var points: Int = 0
  var upgrading: Boolean = false

  val initialHP: Double = 100.0

  def reset(): Unit = {
    imageView.x = 560
    imageView.y = 360
    hp = 100
    damage = 5
    speed = 1
    points = 0
    upgrading = false
  }
}