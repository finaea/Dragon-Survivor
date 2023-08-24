package com.survivor.view

import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.scene.layout.AnchorPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.Text
import com.survivor.model.{Bullet, Enemy, GameController, Player}
import com.survivor.MainApp
import javafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.control.Alert
import scalafx.util.Duration

import scala.collection.mutable.{ListBuffer, Map}
import scala.util.Random

class GameController {
  val playerStandingImage = new Image(getClass.getResourceAsStream("/com/survivor/images/standing.png"))
  val playerRunningImage = new Image(getClass.getResourceAsStream("/com/survivor/images/running.gif"))
  val enemyImage = new Image(getClass.getResourceAsStream("/com/survivor/images/enemy.gif"))
  val backgroundImage = new Image(getClass.getResourceAsStream("/com/survivor/images/gameBG.png"))

  val ouch = new Media(getClass.getResource("/com/survivor/audio/oof.mp3").toExternalForm)
  val ouchPlayer = new MediaPlayer(ouch)
  ouchPlayer.volume = 0.4
  val roar = new Media(getClass.getResource("/com/survivor/audio/roar.mp3").toExternalForm)
  val roarPlayer = new MediaPlayer(roar)
  roarPlayer.volume = 0.1
  val shot = new Media(getClass.getResource("/com/survivor/audio/shot.mp3").toExternalForm)
  val shotPlayer = new MediaPlayer(shot)
  shotPlayer.volume = 0.2

  val spawnLocationList = List((0, 0), (565, 0), (1130, 0), (0, 365), (1130, 365), (0, 730), (565, 730), (1130, 730))

  val backgroundd = new ImageView(backgroundImage) {
    fitWidth = 1200
    fitHeight = 800
  }
  val rectangle = new Rectangle {
    width = 1200
    height = 800
    fill = Color.Black
    opacity = 0.3
  }
  val textPause = new Text("Game Paused") {
    fill = Color.White
    stroke = Color.Black
    strokeWidth = 2
    style = "-fx-font-size: 60px; -fx-font-weight: bold"
    layoutX = 400
    layoutY = 400
  }

  def gameStart(statController: StatController#Controller): AnchorPane = {

    Player.reset()
    GameController.gameStarted.value = true
    var enemies = ListBuffer[Enemy]()
    var bullets = ListBuffer[Bullet]()

    var startTime = System.nanoTime() / 1e9
    val gamePane = new AnchorPane {
      children = ListBuffer(backgroundd, Player.imageView)
      prefWidth = 1200
      prefHeight = 800
    }

    GameController.gamePause = false
    var pressedKeys = Set[KeyCode]()

    val animationPlayer = AnimationTimer { now =>
      var dx = 0.0
      var dy = 0.0
      val modifiedSpeed = 0.4 * Player.speed

      if (pressedKeys.contains(KeyCode.W) && Player.imageView.y.value > 0)
        dy -= modifiedSpeed
      if (pressedKeys.contains(KeyCode.S) && Player.imageView.y.value < gamePane.prefHeight.value - Player.imageView.fitHeight.value -5)
        dy += modifiedSpeed
      if (pressedKeys.contains(KeyCode.A) && Player.imageView.x.value > 0)
        dx -= modifiedSpeed
      if (pressedKeys.contains(KeyCode.D) && Player.imageView.x.value < gamePane.prefWidth.value - Player.imageView.fitWidth.value -2)
        dx += modifiedSpeed

      Player.imageView.x.value += dx
      Player.imageView.y.value += dy
    }

    var lastEnemySpawn = System.nanoTime()
    var enemySpawnInterval = 3e9
    var startingDifficulty = 5e8
    var lastEnemyDamageTime = Map[Enemy, Long]()
    val animationGame: AnimationTimer = AnimationTimer { now =>
      if (now - lastEnemySpawn > enemySpawnInterval) {
        val spawnLocation = Random.nextInt(7)
        val newEnemy = new Enemy(
          imageView = new ImageView(enemyImage) {
            fitWidth = 70;
            fitHeight = 70;
            x = spawnLocationList(spawnLocation)._1;
            y = spawnLocationList(spawnLocation)._2
          },
          hp = 30,
          damage = 5,
          followSpeed = 0.5
        )
        if (GameController.soundState) {
          roarPlayer.seek(Duration.Zero)
          roarPlayer.play()
        }
        enemies += newEnemy
        gamePane.children.add(newEnemy.imageView)
        lastEnemySpawn = now
        enemySpawnInterval -= startingDifficulty
        startingDifficulty *= 0.7
      }

      for (enemy <- enemies) {
        val dx = Player.imageView.x.value - enemy.imageView.x.value
        val dy = Player.imageView.y.value - enemy.imageView.y.value

        val distance = math.sqrt(dx * dx + dy * dy)
        if (distance > 1) {
          enemy.imageView.x.value += (dx / distance) * enemy.followSpeed
          enemy.imageView.y.value += (dy / distance) * enemy.followSpeed
        }

        // Check collision with player
        if (Player.imageView.boundsInParent.value.intersects(enemy.imageView.boundsInParent.value)) {
          val currentTime = System.nanoTime()
          if (lastEnemyDamageTime.contains(enemy) && currentTime - lastEnemyDamageTime(enemy) > 1e9) {
            if (GameController.soundState) {
              ouchPlayer.seek(Duration.Zero)
              ouchPlayer.play()
            }
            Player.hp -= enemy.damage
            lastEnemyDamageTime += (enemy -> currentTime)
            if (Player.hp <= 0) {
              GameController.gameStarted.value = false
            }
          }
          else if (!lastEnemyDamageTime.contains(enemy)) {
            lastEnemyDamageTime += (enemy -> currentTime)
          }
        }
      }

      for (bullet <- bullets) {
        val speed = 5.0
        bullet.bulletCircle.centerX.value += bullet.directionX * speed
        bullet.bulletCircle.centerY.value += bullet.directionY * speed

        for (enemy <- enemies) {
          if (bullet.bulletCircle.boundsInParent.value.intersects(enemy.imageView.boundsInParent.value)) {
            gamePane.children.remove(bullet.bulletCircle)
            bullets = bullets.filterNot(_ == bullet)
            enemy.hp -= Player.damage
            if (enemy.hp <= 0) {
              gamePane.children.remove(enemy.imageView)
              enemies = enemies.filterNot(_ == enemy)
              Player.points += 5
            }
          }
        }
        if (bullet.bulletCircle.centerX.value < 0 ||
          bullet.bulletCircle.centerX.value > 1193 ||
          bullet.bulletCircle.centerY.value < 0 ||
          bullet.bulletCircle.centerY.value > 1193) {
          gamePane.children.remove(bullet.bulletCircle)
          bullets = bullets.filterNot(_ == bullet)
        }
      }
      val currentTime = System.nanoTime() / 1e9
      val totalDuration = currentTime.toInt - startTime.toInt
      val totalDurationString = f"${(totalDuration / 60)}%02d : ${(totalDuration % 60)}%02d"
      statController.showCurrentStats(totalDurationString)

      if (Player.upgrading == true) {
        pressedKeys = Set[KeyCode]()
        Player.upgrading = false
      }
    }
    animationGame.start()

    gamePane.onKeyPressed = (event: KeyEvent) => {
      if (event.code == KeyCode.P && !GameController.gamePause) {
        animationGame.stop()
        animationPlayer.stop()
        GameController.gamePause = true
        gamePane.children.add(rectangle)
        gamePane.children.add(textPause)
      }
      else if (event.code == KeyCode.P && GameController.gamePause) {
        animationGame.start()
        GameController.gamePause = false
        gamePane.children.remove(rectangle)
        gamePane.children.remove(textPause)
      }
      else if (!GameController.gamePause && List(KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D).contains(event.code)) {
        pressedKeys += event.code
        Player.imageView.image = playerRunningImage
        animationPlayer.start()
      }
    }

    gamePane.onKeyReleased = (event: KeyEvent) => {
      pressedKeys -= event.code
      if (pressedKeys.isEmpty) {
        animationPlayer.stop()
        Player.imageView.image = playerStandingImage
      }
    }

    gamePane.onMouseClicked = (e: MouseEvent) => {
      if (!GameController.gamePause) {
        val bulletCircle = new Circle {
          radius = 7
          fill = Color.Yellow
          stroke = Color.Red
          centerX = Player.imageView.x.value + Player.imageView.fitWidth.value / 2
          centerY = Player.imageView.y.value + Player.imageView.fitHeight.value / 2
        }
        if (GameController.soundState) {
          shotPlayer.seek(Duration.Zero)
          shotPlayer.play()
        }

        val dx = e.x - bulletCircle.centerX.value
        val dy = e.y - bulletCircle.centerY.value
        val distance = math.sqrt(dx * dx + dy * dy)
        val bullet = new Bullet(bulletCircle, dx / distance, dy / distance, Player.damage)

        gamePane.children.add(bullet.bulletCircle)
        bullets += bullet
      }
    }

   GameController.gameStarted.onChange { (_, oldValue, newValue) =>
      if (oldValue == true && newValue == false) {
        animationPlayer.stop()
        animationGame.stop()
        val alert = new Alert(Alert.AlertType.Error) {
          title = "Game Over!"
          headerText = "Your character has died."
          contentText = s"Your total points is ${Player.points}. You have been automatically redirected to the Home page after closing this tab. Feel free to play again!"
        }
        alert.show()
        MainApp.rootBorderPane.setLeft(null)
        MainApp.rootBorderPane.setRight(null)
        MainApp.showHome()
      }
    }

    gamePane
  }
}
