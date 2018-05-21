package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
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