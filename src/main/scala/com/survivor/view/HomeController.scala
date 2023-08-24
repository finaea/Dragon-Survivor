package com.survivor.view

import com.survivor.MainApp
import scalafxml.core.macros.sfxml

@sfxml
class HomeController {
  def showTutorial(): Unit = {
    MainApp.showTutorial()
  }
  def showLeaderboard(): Unit = {

  }
  def exit(): Unit = {
    System.exit(0)
  }
}
