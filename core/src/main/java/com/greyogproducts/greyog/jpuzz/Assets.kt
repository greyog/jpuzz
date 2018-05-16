package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin


object Assets {
    lateinit var manager: AssetManager
    lateinit var managerError: AssetErrorListener
    lateinit var atlas: TextureAtlas
    lateinit var skin: Skin
    lateinit var rgnRamka: TextureAtlas.AtlasRegion
    lateinit var rgnPole: TextureAtlas.AtlasRegion
    lateinit var rgnTile: TextureAtlas.AtlasRegion


    val NUMBER_CHARACTERS = "1234567890"

    object Colors {
        val DARK_NAVY = Color(53.0f / 255.0f,
                58.0f / 255.0f, 61.0f / 255.0f, 1.0f)
        val LIGHT_NAVY = Color(133 / 255.0f,
                142 / 255.0f, 148 / 255.0f, 1.0f)
        val TAN = Color(192 / 255.0f, 164 / 255.0f,
                91 / 255.0f, 1.0f)
    }

//    object Fonts {
//        val TEACHERS_PET_SS_NUM = TrueTypeFontFactory
//                .createBitmapFont(Gdx.files.internal("data/teacpss.ttf"),
//                        NUMBER_CHARACTERS, 12.5f, 7.0f, 1.3f,
//                        PmGame.SCREEN_WIDTH, PmGame.SCREEN_HEIGHT)
//    }
//
//    object LabelStyles {
//        val MOVES_COUNTER = LabelStyle(
//                Fonts.TEACHERS_PET_SS_NUM, Colors.LIGHT_NAVY)
//        val TILE_NUMBERS = LabelStyle(
//                Fonts.TEACHERS_PET_SS_NUM, Colors.TAN)
//    }

    fun create() {
        manager = AssetManager()
        managerError = AssetErrorListener { asset, throwable ->
            Gdx.app.error("AssetManagerTest", "couldn't load asset '"
                    + asset.toString() + "'", throwable as Exception)
        }

        manager.setErrorListener(managerError)
        Texture.setAssetManager(manager)
        skin = Skin(Gdx.files.internal("ui/skin.json"))
        init()
    }

    private fun init() {
        manager.load("ui/skin.atlas", TextureAtlas::class.java)
        manager.finishLoading()
        atlas = manager.get("ui/skin.atlas", TextureAtlas::class.java)
        rgnRamka = atlas.findRegion("border")
        rgnPole = atlas.findRegion("field")
        rgnTile = Assets.atlas.findRegion("tile_basic")

    }

    fun loadTexture(src: Array<String>) {
        for (file in src)
            manager.load(file, Texture::class.java)
    }

    fun getTexture(src: String): Texture {
        return manager.get(src, Texture::class.java)
    }

    fun unload(toUnload: Array<String>) {
        for (file in toUnload)
            manager.unload(file)
    }
}