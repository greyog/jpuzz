package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.greyogproducts.greyog.jpuzz.Assets.rgnPole


class Board(val sizeX: Int, val sizeY: Int, val lines: ArrayList<String>) : WidgetGroup() {
    var basicSizeX = 50f
    var otstupX = 10f
    var basicSizeY = 50f
    var otstupY = 10f
    private lateinit var ramka: Ramka
    lateinit var ramkaRectangle: Rectangle
//    val array = Array(sizeX, { arrayOfNulls<Block?>(sizeY)})
    val allBlocks = ArrayList<Block>()
    val targetBlocks = ArrayList<Block>()
    val hintBlocks = ArrayList<Block>()

    fun naidiBlock(bx: Int, by: Int): Block? {
        allBlocks.forEach {
            if (it.by == by && it.bx == bx)
                return it
        }
        return null
    }

    fun setTarget(target: ArrayList<String>) {
        target.forEachIndexed { ay, it ->
            it.forEachIndexed { ax, c ->
                if (c != '.' && c!= '#') {
                    val block = Block(ax, ay)
                    targetBlocks.add(block)
                    block.isTarget = true
                    block.name = c.toString()
                }
            }
        }
    }

    fun create(aConst: Const) {
        basicSizeX = aConst.ITEM_DIM_X
        basicSizeY = aConst.ITEM_DIM_Y
        otstupX = aConst.ITEM_PAD_X
        otstupY = aConst.ITEM_PAD_Y
        ramka = Ramka()
        this.addActor(ramka)
        ramkaRectangle = Rectangle(ramka.x+otstupX, ramka.y + otstupY, ramka.x + ramka.width - otstupX, ramka.y + ramka.height - otstupY)
        if (hintBlocks.size > 0) {
            val dyrka = Ramka(true)
            this.addActor(dyrka)
        }

        lines.forEachIndexed { ay, it ->
            it.forEachIndexed { ax, c ->
               if (c != '.') {
                    val block = Block( ax, ay)
                   allBlocks.add(block)
//                   array[ax][ay] = block
                    if (c == '#') block.isBorder = true
                    block.name = c.toString()
                    var piece : Piece? = null
                    children.forEach {
                        if (it.name == block.name){
                            piece = it as Piece?
                            return@forEach
                        }
                    }
                    if (piece == null) {
                        piece = Piece()
                        piece?.name = block.name
                        addActor(piece)
                    }
                    piece?.addActor(block)
                }
            }
        }
        for (i in 0 until children.size) {
            (children[i] as? Piece)?.komponovka()
        }
        Gdx.app.log("target", targetBlocks.toString())
        Gdx.app.log("hint", hintBlocks.toString())
    }

    fun setHint(hint: ArrayList<String>) {
        hint.forEachIndexed { ay, it ->
            it.forEachIndexed { ax, c ->
                if (c != '.' && c!= '#') {
                    val block = Block(ax, ay)
                    hintBlocks.add(block)
                    block.isTarget = true
                    block.name = c.toString()
                }
            }
        }
    }

    fun isPobeda() {
        var rez = true
        targetBlocks.forEach {
            val cBlk = naidiBlock(it.bx, it.by)
            if (cBlk == null) rez = false
            if (it.name != cBlk?.name) rez = false
        }
        if (rez) {
            val window = Window("Congratulations!", Assets.skin, "default")
            window.defaults().pad(4f)
            window.add("You solved this puzzle!").row()
//            val button = TextButton("Click me!", Assets.skin)
//            button.pad(8f)
//            button.addListener(object : ChangeListener() {
//                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
//                    button.setText("Clicked.")
//                }
//            })
//            window.add<TextButton>(button)
            window.pack()
            window.setScale(2f)
            window.setPosition(stage.width / 2f - window.width / 2f,
                    stage.height / 2f - window.height / 2f)
            window.addAction(Actions.sequence(Actions.scaleTo(10f, 10f),
                    Actions.scaleTo(2f, 2f, 0.5f)))
            addListener(object : ClickListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    window.addAction(Actions.moveTo(stage.width / 2 - window.width / 2, -stage.height,
                            0.5f, Interpolation.swingIn))
                    return super.touchDown(event, x, y, pointer, button)
                }
            })
            addActor(window)
        }
    }
}

class Ramka(private val isDyrka: Boolean = false) : Widget() {
    private val tolshinaX: Float
        get() = board.otstupX
    private val tolshinaY: Float
        get() = board.otstupY
    private lateinit var board: Board
    private var rgn: TextureRegion = Assets.rgnRamka
    private var dyrkaHor: Rectangle? = null
    private var dyrkaVer: Rectangle? = null

    override fun setParent(parent: Group?) {
        super.setParent(parent)
        board = parent as Board
        width = board.sizeX * board.basicSizeX + 2*tolshinaX
        height = board.sizeY * board.basicSizeY + 2*tolshinaY
        setPosition(0f,0f)
        if (isDyrka) {
            rgn = Assets.rgnPole
            board.hintBlocks.forEach {
                it.width = board.basicSizeX
                it.height = board.basicSizeY
                it.setPosition(it.bx * it.width + tolshinaX, (board.sizeY - 1 - it.by) * it.height + tolshinaY)
                val horRect = Rectangle(it.x - tolshinaX, it.y, it.width + 2 * tolshinaX, it.height)
                val vertRect = Rectangle(it.x, it.y - tolshinaX, it.width, it.height + 2 * tolshinaY)
                if (dyrkaHor == null) dyrkaHor = horRect else dyrkaHor?.merge(horRect)
                if (dyrkaVer == null) dyrkaVer = vertRect else dyrkaVer?.merge(vertRect)
            }
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (isDyrka) {
            batch?.draw(rgnPole, dyrkaHor!!.x, dyrkaHor!!.y, dyrkaHor!!.width, dyrkaHor!!.height)
            batch?.draw(rgnPole, dyrkaVer!!.x, dyrkaVer!!.y, dyrkaVer!!.width, dyrkaVer!!.height)

        } else {
            drawRect(batch, rgn, x, y, width, height, tolshinaX, tolshinaY)
            batch?.draw(rgnPole, x + tolshinaX, y + tolshinaY, width - 2 * tolshinaX, height - 2 * tolshinaY)
        }
        super.draw(batch, parentAlpha)
    }

    private fun drawRect(batch: Batch?, rect: TextureRegion, x: Float, y: Float, width: Float, height: Float, thX: Float, thY: Float) {
        batch?.draw(rect, x, y, width, thY)
        batch?.draw(rect, x, y, thX, height)
        batch?.draw(rect, x, y + height - thY, width, thY)
        batch?.draw(rect, x + width - thX, y, thX, height)
    }
}