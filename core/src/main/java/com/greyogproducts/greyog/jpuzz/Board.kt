package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup


class Board(val sizeX: Int, val sizeY: Int, val lines: ArrayList<String>) : WidgetGroup() {
    val basicSize = 80f
    val otstup = 10f
    lateinit var ramka: Ramka

    fun create() {
        ramka = Ramka(this)
        this.addActor(ramka)

        lines.forEachIndexed { ay, it ->
            it.forEachIndexed { ax, c ->
               if (c != '.') {
                    val block = Block( ax, ay)
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
            val piece =  (children[i] as? Piece)
            if (piece != null)
                for (j in 0 until piece.children.size) {
                    (piece.children[j] as Block).znaiSoseda()
                }
        }
    }
}

class Block(posX: Int, posY: Int) : Widget(){
    var bx = posX
    var by = posY
    var isBorder = false
    var hasUp = false
    var hasRight = false
    var hasDown = false
    var hasLeft = false
    private val lineTh = 3f
    private val rgnTile = Assets.atlas.findRegion("tile_basic")
    private val rgnRamka = Assets.atlas.findRegion("border")
    private lateinit var rgn : TextureRegion
    override fun setParent(parent: Group?) {
        super.setParent(parent)
        width = getBoard().basicSize
        height = getBoard().basicSize
        val otstup = getBoard().otstup
        setPosition(bx*width+otstup, (getBoard().sizeY - by-1)*height+otstup)
        rgn = if (isBorder) rgnRamka else rgnTile
    }

    fun getBoard(): Board {
        return (parent as Piece).getBoard()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(rgn, x, y, width, height)
        if (!hasUp) batch?.draw(rgnRamka,x,y+height, width, lineTh)
        if (!hasDown) batch?.draw(rgnRamka,x,y, width, lineTh)
        if (!hasRight) batch?.draw(rgnRamka,x+width,y, lineTh, height)
        if (!hasLeft) batch?.draw(rgnRamka,x,y, lineTh, height)
        super.draw(batch, parentAlpha)
    }

    fun znaiSoseda() {
        parent.children.forEach {
            val blk = it as Block
            if (blk != this) {
                if (blk.by == by + 1) hasUp = true
                if (blk.by == by - 1) hasDown = true
                if (blk.bx == bx + 1) hasRight = true
                if (blk.bx == bx - 1) hasLeft = true
            }
        }
    }
}
class Piece : WidgetGroup() {
    fun getBoard(): Board {
        return parent as Board
    }
}

class Ramka(board: Board) : Widget() {
    private val tolshina = board.otstup
    private val rgnRamka = Assets.atlas.findRegion("border")
    private val rgnPole = Assets.atlas.findRegion("field")
    init {
        width = board.sizeX * board.basicSize + 2*tolshina
        height = board.sizeY * board.basicSize + 2*tolshina
        setPosition(0f,0f)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        drawRect(batch, rgnRamka, x,y, width, height, tolshina)
        batch?.draw(rgnPole, x+tolshina, y+tolshina, width - 2*tolshina, height - 2*tolshina)
        super.draw(batch, parentAlpha)
    }

    private fun drawRect(batch: Batch?, rect: TextureRegion, x: Float, y: Float, width: Float, height: Float, thickness: Float) {
        batch?.draw(rect, x, y, width, thickness)
        batch?.draw(rect, x, y, thickness, height)
        batch?.draw(rect, x, y + height - thickness, width, thickness)
        batch?.draw(rect, x + width - thickness, y, thickness, height)
    }
}