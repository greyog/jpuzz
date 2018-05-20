package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener

class Piece : WidgetGroup() {
    private var x0 : Float? = null
    private var y0 : Float? = null
    init {
        addListener(object : ActorGestureListener() {
            var isMovingHor = false
            var isMovingVer = false

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (hasActions()) return
                x0 = this@Piece.x
                y0 = this@Piece.y
                super.touchDown(event, x, y, pointer, button)
            }

            override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float) {
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (!isMovingVer) {
                        isMovingHor = true
//                        if (deltaX > 0)
                            swipeOnRight(deltaX)
//                        else swipeOnLeft(deltaX)
                    }
                } else {
                    if (!isMovingHor) {
                        isMovingVer = true
//                        if (deltaY > 0)
                            swipeOnUp(deltaY)
//                        else swipeOnDown(deltaY, x, y)
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

    private fun swipeOnUp(dy: Float) {
        if (canMoveUp(dy)) {
            moveBy(0f, dy)
            val sdvig = y - y0!!
            Gdx.app.log("UP", "sdvig=$sdvig")
//            if (sdvig > 0) {
//                children.forEach {
//                    (it as Block).by -=1
//                }
//                y0 = touchY
//                Gdx.app.log("tag", "sdvig UP")
//            }
        }
    }

    private fun canMoveUp(dy: Float): Boolean {
//        val v1 = Vector2(minYblk.x, minYblk.y)
//        val v2 = minYblk.localToStageCoordinates(v1)
        val vRamkaMin = board.ramka.localToStageCoordinates(
                Vector2(board.ramka.x, board.ramka.y))
        val vRamkaMax = board.ramka.localToStageCoordinates(
                Vector2(board.ramka.width, board.ramka.height))
        var found = false
        children.forEach {
            val blk = it as Block
            val v1l = blk.localToStageCoordinates(Vector2(blk.x, blk.y + dy))
            val v1r = blk.localToStageCoordinates(Vector2(blk.x + blk.width, blk.y + dy + blk.height))
            val r1 = Rectangle(v1l.x, v1l.y, v1r.x, v1r.y)
            if (v1l.y <= vRamkaMin.y || v1r.y >= vRamkaMax.y) found = true
            if (found) {
                Gdx.app.log("check", "ramka!")
                return@forEach
            }
            board.allBlocks.forEach {
                if (it.piece != blk.piece) {
                    val v2l = it.localToStageCoordinates(Vector2(it.x, it.y))
                    val v2r = it.localToStageCoordinates(Vector2(it.x + it.width, it.y + it.height))
                    val r2 = Rectangle(v2l.x, v2l.y, v2r.x, v2r.y)
                    if (r2.overlaps(r1)) found = true
//                    if (found)
                        Gdx.app.log("check", "r1=$r1 , r2 = $r2")
                }
            }
        }
        return !found
//        Gdx.app.log("move check", "minYblk.y = $v2, vRamkaMin = $vRamkaMin, vRamkaMax = $vRamkaMax")
//        if (v2.y + minYblk.height >= vRamka.y + getBoard().ramka.height) return false
//        getBoard().allBlocks.forEach {
//            if (it.by == minYblk.by - 1) return false
//        }

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
        setBounds(minXblk.x, minYblk.y,
                maxXblk.x-minXblk.x + maxXblk.width,
                maxYblk.y-minYblk.y + maxYblk.height)
    }

    val board: Board
        get() {
            return parent as Board
        }
}