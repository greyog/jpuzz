package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
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
//    val array = Array(sizeX, { arrayOfNulls<Block?>(sizeY)})
    val allBlocks = ArrayList<Block>()

    fun create() {
        ramka = Ramka(this)
        this.addActor(ramka)

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
            val piece =  (children[i] as? Piece)
            if (piece != null)
                for (j in 0 until piece.children.size) {
                    (piece.children[j] as Block).znaiSoseda()
                }
        }
//        Gdx.app.log("tag", array.toString())
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
    private var x0 : Float? = null
    private var y0 : Float? = null
    init {
        addListener(object : ActorGestureListener() {
            var isMovingHor = false
            var isMovingVer = false

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (hasActions()) return
                x0 = x
                y0 = y
                super.touchDown(event, x, y, pointer, button)
            }

            override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float) {
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (!isMovingVer) {
                        isMovingHor = true
                        if (deltaX > 0) swipeOnRight(deltaX)
                        else swipeOnLeft(deltaX)
                    }
                } else {
                    if (!isMovingHor) {
                        isMovingVer = true
                        if (deltaY > 0) swipeOnUp(deltaY, x, y)
                        else swipeOnDown(deltaY, x, y)
                    }
                }
                super.pan(event, x, y, deltaX, deltaY)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isMovingHor = false
                isMovingVer = false
                x0 = null
                y0 = null
                super.touchUp(event, x, y, pointer, button)
            }
        })
    }

    private fun swipeOnUp(dy: Float, touchX: Float, touchY: Float) {
//        Gdx.app.log("UP", "dy=$dy , tY = $touchY")
        if (canMoveUp()) {
            moveBy(0f, dy)
//            val sdvig = touchY - y0!! - getBoard().basicSize/2
//            if (sdvig > 0) {
//                children.forEach {
//                    (it as Block).by -=1
//                }
//                y0 = touchY
//                Gdx.app.log("tag", "sdvig UP")
//            }
        }
    }

    private fun canMoveUp(): Boolean {
        val v1 = Vector2(minYblk.x, minYblk.y)
        val v2 = minYblk.localToStageCoordinates(v1)
        Gdx.app.log("q", "$v2")
        val vRamka = getBoard().ramka.localToStageCoordinates(
                Vector2(getBoard().ramka.x, getBoard().ramka.y))
//        if (v2.y + minYblk.height >= vRamka.y + getBoard().ramka.height) return false
//        getBoard().allBlocks.forEach {
//            if (it.by == minYblk.by - 1) return false
//        }
        return true
    }

    private fun swipeOnDown(dy: Float, touchX: Float, touchY: Float) {
        if (canMoveDown()) {
            moveBy(0f, dy)
//            val sdvig = touchY - y0!! + getBoard().basicSize/2
//            if (sdvig < 0) {
//                children.forEach {
//                    (it as Block).by +=1
//                }
//                y0 = touchY
//                Gdx.app.log("tag", "sdvig DOWN")
//            }
        }
    }

    private fun canMoveDown(): Boolean {
//        if (maxYblk.by == getBoard().sizeY) return false
//        getBoard().allBlocks.forEach {
//            if (it.by == minYblk.by + 1) return false
//        }
        return true
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
    private lateinit var minXblk : Block
    private lateinit var minYblk : Block
    private lateinit var maxXblk : Block
    private lateinit var maxYblk : Block

    override fun addActor(actor: Actor?) {
        super.addActor(actor)
        val blk = actor as Block
        minx = if (minx == null) {
            minXblk = blk
            blk.bx
        } else if (blk.bx < minx!!) {
            minXblk = blk
            blk.bx
        } else minx
        miny = if (miny == null) {
            minYblk = blk
            blk.by
        } else if (blk.by < miny!!) {
            minYblk = blk
            blk.by
        } else miny
        maxy = if (blk.by >= maxy) {
            maxYblk = blk
            blk.by
        } else maxy
        maxx = if (blk.bx >= maxx) {
            maxXblk = blk
            blk.bx
        } else maxx
//        setBounds(minXblk.x, minYblk.y, maxXblk.x + getBoard().basicSize, maxYblk.y + getBoard().basicSize)

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