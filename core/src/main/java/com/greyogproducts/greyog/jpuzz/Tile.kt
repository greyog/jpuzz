package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label

class Tile(name: String) : Group() {
    private var texture = Image(Assets.atlas.findRegion("tile"))
    private var label: Label

    init {
        this.name = name
        label = Label(name, Assets.skin)

        label.x = texture.width / 2 - label.width / 2
        label.y = -35f

        addActor(texture)
        addActor(label)
    }

}