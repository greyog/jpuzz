package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.truncate

class Const(private val mSizeX: Int, private val mSizeY: Int, private val mWidth: Float, private val mHeight: Float, private val mPpcX: Float, private val mPpcY: Float) {
    val SHAKE_TIME: Long = 10000
    val SHAKE_CHANCE = 0.001f
    val SHAKE_DURATION = 0.5f
    val SWAP_TIME_SHUFFLE = 0.1f
    val SWAP_TIME = 0.3f
    var ITEM_DIM_X: Float = 0.toFloat()
        get() = truncate(field)
    var ITEM_PAD_X: Float = 0.toFloat()
        get() = truncate(field)
    var ITEM_DIM_Y: Float = 0.toFloat()
        get() = truncate(field)
    var ITEM_PAD_Y: Float = 0.toFloat()
        get() = truncate(field)
    var BT_DIM_X: Float = 0.toFloat()
        get() = truncate(field)
    var BT_DIM_Y: Float = 0.toFloat()
        get() = truncate(field)

    init {
        recalc(mSizeX, mSizeY, mWidth, mHeight, mPpcX, mPpcY)
    }

    fun recalc(sizeX: Int, sizeY: Int) {
        recalc(sizeX, sizeY, mWidth, mHeight, mPpcX, mPpcY)
    }

    private fun recalc(sizeX: Int, sizeY: Int, width: Float, height: Float, ppcX: Float, ppcY: Float) {
        BT_DIM_X = BT_DIM_CM * ppcX
        BT_DIM_Y = BT_DIM_CM * ppcY


        val ratioX = width / ((ITEM_PAD_CM + ITEM_DIM_CM) * sizeX + ITEM_PAD_CM) / ppcX
        val ratioY = (height - BT_DIM_Y - ITEM_PAD_Y * 3) / ((ITEM_PAD_CM + ITEM_DIM_CM) * sizeY + ITEM_PAD_CM) / ppcY
        var ratio = if (ratioX <= ratioY) ratioX else ratioY
        ratio = if (ratio > 1) 1f else ratio
        ITEM_DIM_X = ITEM_DIM_CM * ratio * ppcX
        ITEM_DIM_Y = ITEM_DIM_CM * ratio * ppcY
        ITEM_PAD_X = ITEM_PAD_CM * ratio * ppcX
        ITEM_PAD_Y = ITEM_PAD_CM * ratio * ppcY
    }

    fun setActorSize(actor: Actor) {
        actor.setSize(ITEM_DIM_X, ITEM_DIM_Y)
    }

    companion object {
        private val ITEM_PAD_CM = 0.3f
        private val ITEM_DIM_CM = 1.6f
        private val BT_DIM_CM = 1f
    }
}