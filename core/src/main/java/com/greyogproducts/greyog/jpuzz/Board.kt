package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.greyogproducts.greyog.jpuzz.Assets.rgnPole
import com.greyogproducts.greyog.jpuzz.Assets.rgnRamka
import com.greyogproducts.greyog.jpuzz.Assets.rgnTile


class Board(val sizeX: Int, val sizeY: Int, val lines: ArrayList<String>) : WidgetGroup() {
    val basicSize = 50f
    val otstup = 10f
    lateinit var ramka: Ramka
    val pole : Array<Array<Block>> = arrayOfNulls<>(sizeY)

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
    private val zazor = 1f
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
        if (!hasUp) batch?.draw(rgnPole,x,y+height, width, zazor)
        if (!hasDown) batch?.draw(rgnPole,x,y, width, zazor)
        if (!hasRight) batch?.draw(rgnPole,x+width,y, zazor, height)
        if (!hasLeft) batch?.draw(rgnPole,x,y, zazor, height)
        super.draw(batch, parentAlpha)
    }

    fun znaiSoseda() {
        parent.children.forEach {
            val blk = it as Block
            if (blk != this) {
                if (blk.by == by - 1) hasUp = true
                if (blk.by == by + 1) hasDown = true
                if (blk.bx == bx + 1) hasRight = true
                if (blk.bx == bx - 1) hasLeft = true
            }
        }
    }
}
class Piece : WidgetGroup() {
    init {
        addListener(object : ActorGestureListener() {
            var isMovingHor = false
            var isMovingVer = false

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (hasActions()) return
                super.touchDown(event, x, y, pointer, button)
            }

            override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float) {
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (!isMovingVer) {
                        isMovingHor = true
                        swipeOnRight(deltaX)
                    }
                } else {
                    if (!isMovingHor) {
                        isMovingVer = true
                        swipeOnUp(deltaY)
                    }
                }
                super.pan(event, x, y, deltaX, deltaY)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isMovingHor = false
                isMovingVer = false
                super.touchUp(event, x, y, pointer, button)
            }
        })
    }

    private fun swipeOnUp(dy: Float) {
        if (miny == 0 && dy > 0) return
        if (maxy == getBoard().sizeY && dy < 0) return
        addAction(Actions.moveBy(0f, dy))
    }

    private fun swipeOnDown(dy: Float) {
        addAction(Actions.moveBy(0f, -1f))
    }

    private fun swipeOnLeft(dx: Float) {
        addAction(Actions.moveBy(-1f, 0f))
    }

    private fun swipeOnRight(dx: Float) {
        addAction(Actions.moveBy(dx, 0f))
    }

    private var minx: Int? = null

    private var miny: Int? = null

    private var maxy: Int = 0

    private var maxx = 0

    override fun addActor(actor: Actor?) {
        super.addActor(actor)
        val blk = actor as Block
        minx = if (minx == null) blk.bx else if (blk.bx < minx!!) blk.bx else minx
        miny = if (miny == null) blk.by else if (blk.by < miny!!) blk.by else miny
        maxy = if (blk.by > maxy) blk.by else maxy
        maxx = if (blk.bx > maxx) blk.bx else maxx

    }

    fun getBoard(): Board {
        return parent as Board
    }
}

class Ramka(board: Board) : Widget() {
    private val tolshina = board.otstup
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