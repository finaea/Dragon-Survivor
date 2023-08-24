package com.survivor.view

import com.survivor.MainApp
import com.survivor.model.GameController
import scalafx.scene.control.Alert
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController {
  def showHome(): Unit = {
    if (!GameController.gameStarted.value || GameController.gamePause) {
      MainApp.rootBorderPane.setLeft(null)
      MainApp.rootBorderPane.setRight(null)
      MainApp.showHome()
    }
    else
      alert.showAndWait()
  }
  def showTutorial = {
    if (!GameController.gameStarted.value || GameController.gamePause) {
      MainApp.rootBorderPane.setLeft(null)
      MainApp.rootBorderPane.setRight(null)
      MainApp.showTutorial()
    }
    else
      alert.showAndWait()
  }
  def showLeaderboard = {

  }
  def showGame = {
    if (!GameController.gameStarted.value || GameController.gamePause) {
      MainApp.rootBorderPane.setLeft(null)
      MainApp.rootBorderPane.setRight(null)
      MainApp.rootBorderPane.setCenter(null)
      MainApp.showGame()
    }
    else
      alert.showAndWait()
  }

  def handleSound = {
    if (GameController.soundState)
      GameController.soundState = false
    else
      GameController.soundState = true
  }

  var musicState: Boolean = true
  def handleMusic = {
    if (musicState) {
      MainApp.bgmPlayer.pause()
      musicState = false
    }
    else {
      MainApp.bgmPlayer.play()
      musicState = true
    }
  }

  val alert = new Alert(Alert.AlertType.Error) {
    title = "Invalid Action"
    headerText = "Please pause the game first before navigating!!"
  }
}
