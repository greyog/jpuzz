package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import kotlin.math.roundToInt

class Piece : WidgetGroup() {
    private var x0 : Float? = null
    private var y0 : Float? = null

    private var minx: Int? = null
    private var miny: Int? = null
    private var maxy: Int = 0
    private var maxx = 0
    private lateinit var minXblk : Block
    private lateinit var minYblk : Block
    private lateinit var maxXblk : Block
    private lateinit var maxYblk : Block

    init {
        addListener(object : ActorGestureListener() {
            var isMovingHor = false
            var isMovingVer = false

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (this@Piece.hasActions()) return
                if ((this@Piece.children.first() as Block).isBorder) return
                x0 = this@Piece.x
                y0 = this@Piece.y
                super.touchDown(event, x, y, pointer, button)
            }

            override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float) {
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (!isMovingVer) {
                        isMovingHor = true
                        swipeHor(deltaX)
                    }
                } else {
                    if (!isMovingHor) {
                        isMovingVer = true
                        swipeVer(deltaY)
                    }
                }
                super.pan(event, x, y, deltaX, deltaY)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (isMovingVer) {
                    val sdvig = this@Piece.y - y0!!
                    val bSdvig = (sdvig/board.basicSize).roundToInt()
                    children.forEach { val blk = it as Block
                        blk.by -= bSdvig
                    }
                    val celSdvig = bSdvig * board.basicSize
                    val ostatok = celSdvig - sdvig
                    val newPos = calcPos()
                    this@Piece.addAction(Actions.moveTo(newPos.x, newPos.y, 0.4f))
//                    Gdx.app.log("UP", "sdvig=$sdvig , ostatok = $ostatok")
                }
                if (isMovingHor) {
                    val sdvig = this@Piece.x - x0!!
                    val bSdvig = (sdvig/board.basicSize).roundToInt()
                    children.forEach { val blk = it as Block
                        blk.bx += bSdvig
                    }
                    val celSdvig = bSdvig * board.basicSize
                    val ostatok = celSdvig - sdvig
                    val newPos = calcPos()
                    this@Piece.addAction(Actions.moveTo(newPos.x, newPos.y, 0.4f))
//                    Gdx.app.log("UP", "sdvig=$sdvig , ostatok = $ostatok")
                }
                isMovingHor = false
                isMovingVer = false
                x0 = null
                y0 = null
                super.touchUp(event, x, y, pointer, button)
            }
        })
    }

    private fun swipeVer(dy: Float) {
        if (canMoveUp(dy)) {
            moveBy(0f, dy)
       }
    }

    private fun canMoveUp(dy: Float): Boolean {
        var found = false
        children.forEach {
            val blk = it as Block
            val v1l = this.localToStageCoordinates(Vector2(blk.x, blk.y + dy))
            val v1r = this.localToStageCoordinates(Vector2(blk.x + blk.width, blk.y + dy + blk.height))
            val r1 = Rectangle(v1l.x, v1l.y, v1r.x, v1r.y)
            if (v1l.y <= board.ramkaRectangle.y || v1r.y >= board.ramkaRectangle.height) found = true
            board.allBlocks.forEach {
                if (it.piece != blk.piece) {
                    val v2l = it.piece.localToStageCoordinates(Vector2(it.x, it.y))
                    val v2r = it.piece.localToStageCoordinates(Vector2(it.x + it.width, it.y + it.height))
                    val r2 = Rectangle(v2l.x, v2l.y, v2r.x, v2r.y)
                    if (!(v1r.y <= v2l.y || v1l.y >= v2r.y || v1r.x <= v2l.x || v1l.x >= v2r.x)) found = true
                    if (found) {
                        Gdx.app.log("check", "r1=$r1 , r2 = $r2")
                        return@forEach
                    }
                }
            }
            if (found) {
//                Gdx.app.log("check", "ramka!")
                return@forEach
            }
        }
        return !found
    }

    private fun swipeHor(dx: Float) {
        if (canMoveHor(dx)) {
            moveBy(dx, 0f)
        }
    }

    private fun canMoveHor(dx: Float): Boolean {
        var found = false
        children.forEach {
            val blk = it as Block
            val v1l = this.localToStageCoordinates(Vector2(blk.x + dx, blk.y))
            val v1r = this.localToStageCoordinates(Vector2(blk.x + dx + blk.width, blk.y + blk.height))
            if (v1l.x <= board.ramkaRectangle.x || v1r.x >= board.ramkaRectangle.width) found = true
            board.allBlocks.forEach {
                if (it.piece != blk.piece) {
                    val v2l = it.piece.localToStageCoordinates(Vector2(it.x, it.y))
                    val v2r = it.piece.localToStageCoordinates(Vector2(it.x + it.width, it.y + it.height))
                    if (!(v1r.y <= v2l.y || v1l.y >= v2r.y || v1r.x <= v2l.x || v1l.x >= v2r.x)) found = true
                    if (found) {
                        return@forEach
                    }
                }
            }
            if (found) {
//                Gdx.app.log("check", "ramka!")
                return@forEach
            }
        }
        return !found
    }

    override fun addActor(actor: Actor?) {
        super.addActor(actor)
        val blk = actor as Block
        minx = when {
            minx == null -> {
                minXblk = blk
                blk.bx
            }
            blk.bx < minx!! -> {
                minXblk = blk
                blk.bx
            }
            else -> minx
        }
        miny = when {
            miny == null -> {
                minYblk = blk
                blk.by
            }
            blk.by < miny!! -> {
                minYblk = blk
                blk.by
            }
            else -> miny
        }
        maxy = if (blk.by >= maxy) {
            maxYblk = blk
            blk.by
        } else maxy
        maxx = if (blk.bx >= maxx) {
            maxXblk = blk
            blk.bx
        } else maxx
    }

    fun komponovka() {
        val vPos = Vector2(minXblk.x, maxYblk.y)
        for (j in 0 until this.children.size) {
            val blk = this.children[j] as Block
            blk.znaiSoseda()
            blk.moveBy(-vPos.x, -vPos.y)
        }
        val vNewPos = calcPos()
        val newWidth = (maxXblk.bx-minXblk.bx +1 ) * maxXblk.width
        val newHeight = (maxYblk.by-minYblk.by + 1) * maxYblk.height
        setBounds(vNewPos.x, vNewPos.y,
                newWidth,
                newHeight)

    }

    private fun calcPos(): Vector2 {
        val otstup = board.otstup
        val res = Vector2(minXblk.bx * minXblk.width + otstup, (board.sizeY - 1 - maxYblk.by) * maxYblk.height + otstup)
//        Gdx.app.log("calcPos", "$res")
        return  res
    }

    val board: Board
        get() {
            return parent as Board
        }
}