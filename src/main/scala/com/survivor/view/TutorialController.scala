package com.survivor.view

import com.survivor.MainApp
import scalafxml.core.macros.sfxml

@sfxml
class TutorialController {
  def showGame(): Unit = {
    MainApp.rootBorderPane.setCenter(null)
    MainApp.showGame()
  }
  def cancelGame() = {
    MainApp.showHome()
  }
}
