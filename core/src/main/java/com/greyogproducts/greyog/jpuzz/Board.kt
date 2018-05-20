package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.greyogproducts.greyog.jpuzz.Assets.rgnPole
import com.greyogproducts.greyog.jpuzz.Assets.rgnRamka


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