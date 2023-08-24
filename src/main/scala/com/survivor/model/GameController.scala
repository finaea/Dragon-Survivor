package com.survivor.model

import scalafx.beans.property.BooleanProperty

object GameController {
  var gamePause: Boolean = false
  var gameStarted: BooleanProperty = BooleanProperty(false)
  var soundState: Boolean = true
}
