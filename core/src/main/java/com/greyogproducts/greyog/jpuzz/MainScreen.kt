package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.greyogproducts.greyog.jpuzz.Assets.skin

/**
 * Created by mac on 12/05/2018.
 */

/** First screen of the application. Displayed after the application is created.  */
class MainScreen(private var boardsMap: MutableMap<String, Board>) : Screen {

    private lateinit var stage: Stage
    private lateinit var aConst: Const

    override fun show() {
//        stage = Stage(FitViewport(640f, 480f))
        val camera = OrthographicCamera()
        stage = Stage(ScreenViewport(camera))
//        stage.setDebugAll(true)
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()
        val ppcX = Gdx.graphics.ppcX
        val ppcY = Gdx.graphics.ppcY
//        Gdx.app.log("Tag", "screen: $w / $h")
//        Gdx.app.log("Tag", "ppc x/y: $ppcX / $ppcY")
        Assets.create()
        val window = Window("Example screen", skin, "border")
        window.defaults().pad(4f)
        window.add("This is a simple Scene2D view.").row()
        val button = TextButton("Click me!", skin)
        button.pad(8f)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                button.setText("Clicked.")
            }
        })
        window.add<TextButton>(button)
        window.pack()
        window.setPosition(stage.getWidth() / 2f - window.width / 2f,
                stage.getHeight() / 2f - window.height / 2f)
        window.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))

        val label = Label("", skin)
        stage.addActor(label)
        stage.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                label.setText("x = $x , y = $y")
                label.setPosition(stage.width - label.prefWidth - 10, stage.height - label.height - 10)
                return super.touchDown(event, x, y, pointer, button)
            }
        })

        val boardTable = Table(skin)
        val initialBoard = this.boardsMap["initial"]
//        stage.addActor(boardTable)
        stage.addActor(initialBoard)
//        boardTable.setFillParent(true)
        initialBoard?.setFillParent(true)
        aConst = Const(initialBoard!!.sizeX, initialBoard.sizeY, w, h, ppcX, ppcY)
        initialBoard.create(aConst)
//        boardTable.add(initialBoard)
//        initialBoard.children?.forEach {
//            boardTable.row()
//            (it as Piece).children.forEach {
//                val block =  it as Block
//                val label = Label(block.name, skin)
//                label.setSize(aConst.ITEM_DIM_X, aConst.ITEM_DIM_Y)
//                boardTable.add(label)
//            }
//        }
        ///////////////////////////
//        initialBoard.

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        // Resize your screen here. The parameters represent the new window size.
        stage.viewport.update(width, height)
    }

    override fun pause() {
        // Invoked when your application is paused.
    }

    override fun resume() {
        // Invoked when your application is resumed after pause.
    }

    override fun hide() {
        // This method is called when another screen replaces this one.
    }

    override fun dispose() {
        // Destroy screen's assets here.
        stage.dispose()
        skin.dispose()
    }
}