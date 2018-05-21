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
    val basicSize = 50f
    val otstup = 10f
    private lateinit var ramka: Ramka
    lateinit var ramkaRectangle: Rectangle
//    val array = Array(sizeX, { arrayOfNulls<Block?>(sizeY)})
    val allBlocks = ArrayList<Block>()
    internal val targetBlocks = ArrayList<Block>()

    fun setTarget(target: ArrayList<String>) {
        target.forEachIndexed { ay, it ->
            it.forEachIndexed { ax, c ->
                if (c != '.') {
                    val block = Block(ax, ay)
                    targetBlocks.add(block)
                    block.isTarget = true
                    block.name = c.toString()
                }
            }
        }
    }

    fun create() {
        ramka = Ramka(false)
        this.addActor(ramka)
        ramkaRectangle = Rectangle(ramka.x+otstup, ramka.y + otstup, ramka.x + ramka.width - otstup, ramka.y + ramka.height - otstup)
        val dyrka = Ramka(true)
        this.addActor(dyrka)

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
    }
}

class Ramka(private val isDyrka: Boolean) : Widget() {
    private val tolshina: Float
        get() = board.otstup
    private lateinit var board: Board
    private var rgn: TextureRegion = Assets.rgnRamka
    private var dyrkaHor: Rectangle? = null
    private var dyrkaVer: Rectangle? = null

    override fun setParent(parent: Group?) {
        super.setParent(parent)
        board = parent as Board
        width = board.sizeX * board.basicSize + 2*tolshina
        height = board.sizeY * board.basicSize + 2*tolshina
        setPosition(0f,0f)
        if (isDyrka) {
            rgn = Assets.rgnPole
            board.targetBlocks.forEach {
                it.width = board.basicSize
                it.height = board.basicSize
                it.setPosition(it.bx * it.width + tolshina, (board.sizeY - 1 - it.by) * it.height + tolshina)
                val horRect = Rectangle(it.x - tolshina, it.y, it.width + 2 * tolshina, it.height)
                val vertRect = Rectangle(it.x, it.y - tolshina, it.width, it.height + 2 * tolshina)
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
            drawRect(batch, rgn, x, y, width, height, tolshina)
            batch?.draw(rgnPole, x + tolshina, y + tolshina, width - 2 * tolshina, height - 2 * tolshina)
        }
        super.draw(batch, parentAlpha)
    }

    private fun drawRect(batch: Batch?, rect: TextureRegion, x: Float, y: Float, width: Float, height: Float, thickness: Float) {
        batch?.draw(rect, x, y, width, thickness)
        batch?.draw(rect, x, y, thickness, height)
        batch?.draw(rect, x, y + height - thickness, width, thickness)
        batch?.draw(rect, x + width - thickness, y, thickness, height)
    }
}