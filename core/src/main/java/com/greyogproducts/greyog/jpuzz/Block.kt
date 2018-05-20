package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Widget

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
    val piece: Piece
        get() = parent as Piece

    override fun setParent(parent: Group?) {
        super.setParent(parent)
        width = board.basicSize
        height = board.basicSize
        val otstup = board.otstup
        setPosition(bx*width+otstup, (board.sizeY - by-1)*height+otstup)
        rgn = if (isBorder) Assets.rgnRamka else Assets.rgnTile
    }

    val board: Board
        get() {
            return (piece).board
        }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(rgn, x, y, width, height)
        if (!hasUp) batch?.draw(Assets.rgnPole,x,y+height, width, zazor)
        if (!hasDown) batch?.draw(Assets.rgnPole,x,y, width, zazor)
        if (!hasRight) batch?.draw(Assets.rgnPole,x+width,y, zazor, height)
        if (!hasLeft) batch?.draw(Assets.rgnPole,x,y, zazor, height)
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