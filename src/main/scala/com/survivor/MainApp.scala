package com.survivor

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import javafx.{scene => jfxs}
import com.survivor.view.{GameController, StatController}
import javafx.scene.layout.BorderPane
import scalafx.scene.image.{Image, ImageView}
import javafx.scene.media.{Media, MediaPlayer}

import java.io.InputStream

object MainApp extends JFXApp {

  val rootResource: InputStream = getClass.getResourceAsStream("/com/survivor/view/RootLayout.fxml")
  val playerStandingImage = new Image(getClass.getResourceAsStream("/com/survivor/images/standing.png"))
  val loader = new FXMLLoader(null, NoDependencyResolver)
  loader.load(rootResource)
  val rootBorderPane: BorderPane = loader.getRoot[jfxs.layout.BorderPane]

  stage = new PrimaryStage {
    title = "Dragon Survivor"
    scene = new Scene {
      root = rootBorderPane
      icons.add(playerStandingImage)
    }
  }

  val bgm = new Media(getClass.getResource("/com/survivor/audio/bgm.mp3").toExternalForm)
  val bgmPlayer = new MediaPlayer(bgm)
  bgmPlayer.volume = 0.05
  bgmPlayer.cycleCount = 666
  bgmPlayer.play()


  def showGame(): Unit = {
    val statFXML = getClass.getResourceAsStream("/com/survivor/view/GameStat.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(statFXML)
    val statPane = loader.getRoot[jfxs.layout.AnchorPane]
    this.rootBorderPane.setLeft(statPane)

    val gameController = new GameController()
    this.rootBorderPane.setRight(gameController.gameStart(loader.getController[StatController#Controller]))
    this.rootBorderPane.getRight.requestFocus()
  }
  def showHome(): Unit = {
    val homeFXML = getClass.getResourceAsStream("/com/survivor/view/Home.fxml")
    val bgImage = new Image(getClass.getResourceAsStream("/com/survivor/images/homeBG.png"))
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(homeFXML)
    val homePane = loader.getRoot[jfxs.layout.AnchorPane]
    homePane.children.+=:(new ImageView(bgImage))
    this.rootBorderPane.setCenter(homePane)
  }
  def showTutorial(): Unit = {
    val tutorialFXML = getClass.getResourceAsStream("/com/survivor/view/Tutorial.fxml")
    val bgImage = new Image(getClass.getResourceAsStream("/com/survivor/images/tutorialBG.png"))
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(tutorialFXML)
    val tutorialPane = loader.getRoot[jfxs.layout.AnchorPane]
    tutorialPane.children.+=:(new ImageView(bgImage))
    this.rootBorderPane.setCenter(tutorialPane)
  }
  showHome()
}