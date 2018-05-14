package com.greyogproducts.greyog.jpuzz

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle


/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class MainClass : Game() {

    private var data: FileHandle? = null

    override fun create() {
        data = Gdx.files.internal("data/escape.dat")
        readBoardFile(data!!)
        setScreen(MainScreen())
    }

    fun myLog( msg : String) {
        Gdx.app.log("jpuzz", msg)
    }

    override fun dispose() {
        super.dispose()
    }

    private fun readBoardFile(file: FileHandle) {
        var index = 0
        val lines = file.reader().readLines()
        var sizeX = 0
        var sizeY = 0
        var steps = 0
        while ( index < lines.size) {
            val s = lines[index]
            val lineItems = s.split(" ")
//            myLog(lineItems.toString())
            if (lineItems.isNotEmpty()) {
                if (lineItems[0] == "size") {
                    sizeX = lineItems[2].toInt()
                    sizeY = lineItems[4].toInt()
                    myLog("sizes = $sizeX / $sizeY")
                }
                if (lineItems[0] == "step") {
                    steps = lineItems[1].toInt()
                    myLog("steps = $steps")
                }
                if (lineItems[0] == "initial") {
                    val initial = ArrayList<String>()
//                    myLog("lines")
                    for (i in 0 until sizeY) {
                        index += 1
                        val line = lines[index]
                        initial.add(line)
//                        myLog(line)
                    }
                    myLog(initial.toString())
                }
            }
            index += 1
        }
    }
}