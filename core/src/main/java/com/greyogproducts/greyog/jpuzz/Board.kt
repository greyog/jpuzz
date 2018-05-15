package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Color


class Board(val sizeX: Int, val sizeY: Int, val lines: ArrayList<String>) : WidgetGroup() {
    init {
        sozdatRamku()
    }

    private fun sozdatRamku() {
        val ramka = Image()
        val pixmap = Pixmap(10, 10, Pixmap.Format.RGBA8888)

        pixmap.setColor(Color.BLUE)
        pixmap.fillRectangle(0, 0, 50, 50)

        ramka.drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        ramka.setScaling(Scaling.fit)
        addActor(ramka)
    }
}

class Block(posX: Int, posY: Int) : Actor(){
    var bx = posX
    var by = posY
    var isBorder = false
}
class Piece : Group() {
}