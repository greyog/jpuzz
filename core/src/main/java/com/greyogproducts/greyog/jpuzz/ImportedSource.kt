package com.greyogproducts.greyog.jpuzz

/**
 * Created by mac on 12/05/2018.
 */
//================================================================
//	Sliding Block Program
//
//	Copyright(C) 1996 Hirofumi Fujiwara (arranged by Yo-zi Ito)
//	Copyright(C) 1998-1999 Nick Baxter
//
//	v2.0	1997/12/2	Developed by Fujiwara and Ito
//	March 1998 enhancements by Jared Weinberger
//	Jan.  1999 bug fixed by Hirofumi Fujiwara
//
//	Enhancements by Nick Baxter:
//	v3.0	Jun 22, 98	images for pieces; forward button; layout update;
//				solution indicator; piece labels; revised shadows
//	v3.1	Dec 04, 98	"color" keyword to assign piece color in datafile
//	v3.2	Dec 15, 98	"equiv" keyword in datafile to establish piece equivalence
//	v3.3	Dec 28, 98	adjusting previous piece does not increment counter
//	v3.4	Dec 29, 98	"labeloffset" keyword for precise label placement
//	v3.5	Jan 26, 99	Fujiwara's Netscape bug fix merged and enhanced
//
//================================================================

//================================================================
import java.awt.*
import java.applet.*
import java.lang.*
import java.io.*
import java.util.*
import java.net.*

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	Ｓｌｉｄｅ  Ａｐｐｌｅｔ
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

class Slide : Applet() {
    internal val BUTTON_H = 35
    internal val MSG_H = 35
    internal val COPY_H = 30

    var app_size: Dimension        // アプレットのサイズ
    internal var board: Board            // 盤
    internal var sound: AudioClip? = null        // 駒を動かす時の音
    internal var filename: String        // 面データのファイル
    internal var nextfile: String? = null        // 次に進む面のファイル名
    private var stepCounter: Label? = null        // step表示
    internal var doneMsg: Label
    internal var nextButton: ControlButton
    internal var backButton: ControlButton
    internal var redoButton: ControlButton
    internal var replayButton: ControlButton

    internal val backcolor = Color(180, 180, 180)    // background - grey
    internal val pagecolor = Color(222, 222, 222)
    //	final Color pagecolor = Color.white;		 	// for white background
    internal val shadowLight = Color.white            // top, light shadow
    internal val shadowDark = Color.black            // bottom, dark shadow
    internal val piececolor = Color(0.40f, 0.40f, 1.0f)    // default piece color
    internal val limitcolor = Color.green
    internal val hintcolor = Color.black            // goal line color
    internal val msgcolor = Color.red

    //----------------------------------------------------------------
    // 初期化
    override fun init() {
        val crMessage = "Slide Puzzle V3.5  " + "Copyright(c)1996-1999 Hirofumi Fujiwara, Nick Baxter"
        println(crMessage)
        println("Version 3.5.1 - Netscape 4.5 fix part 1")

        sound = getAudioClip(codeBase, "tick.au")
        filename = getParameter("problemfile")
        app_size = this.size()

        val r = Rectangle(0, BUTTON_H + MSG_H,
                app_size.width,
                app_size.height - 2 * BUTTON_H - MSG_H - COPY_H)

        board = Board(this, r)

        val f10 = java.awt.Font("Helvetica", 0, 10)
        val f14 = java.awt.Font("Helvetica", 0, 14)
        val bf16 = java.awt.Font("Helvetica", 1, 16)
        val f20 = java.awt.Font("Helvetica", 0, 20)

        background = pagecolor

        layout = BorderLayout()
        run {
            val p1 = Panel()
            add("North", p1)
            p1.layout = FlowLayout()
            backButton = ControlButton(this, "Back", " Back ")
            backButton.font = f14
            redoButton = ControlButton(this, "Forward", " Forward ")
            redoButton.font = f14
            p1.add(backButton)
            p1.add(redoButton)

            stepCounter = Label()
            stepCounter!!.font = f20
            showcounter()
            p1.add(stepCounter)
            replayButton = ControlButton(this, "Replay", " Restart ")
            replayButton.font = f14
            p1.add(replayButton)

            p1.reshape(0, 0, app_size.width, BUTTON_H)

        }

        layout = BorderLayout()
        run {
            val p4 = Panel()
            add("North", p4)
            p4.layout = FlowLayout()

            doneMsg = Label()
            doneMsg.font = bf16
            doneMsg.foreground = msgcolor
            doneMsg.alignment = Label.CENTER
            doneMsg.text = "                                    " + "                                    "
            p4.add(doneMsg)

            p4.reshape(0, BUTTON_H, app_size.width, MSG_H)
        }

        layout = BorderLayout()
        run {
            val p3 = Panel()
            add("South", p3)
            p3.layout = FlowLayout()

            nextButton = ControlButton(this, "Next", " Go to next problem ")
            nextButton.font = f14
            p3.add(nextButton)

            p3.reshape(0, app_size.height - BUTTON_H - COPY_H,
                    app_size.width, BUTTON_H)

        }

        layout = BorderLayout()
        run {
            val p2 = Panel()
            p2.layout = FlowLayout()
            add("South", p2)

            val copyright = Label(crMessage, Label.CENTER)
            copyright.font = f10
            p2.add(copyright)

            p2.reshape(0, app_size.height - COPY_H,
                    app_size.width, COPY_H)
        }
        replay()
    }

    //----------------------------------------------------------------
    // カウンタ表示
    fun showcounter() {
        if (board.minStep == 0)
            stepCounter!!.text = "   Step  " + board.step + "   "
        else
            stepCounter!!.text = "   Step  " + board.step + "/" +
                    board.minStep + "   "
    }

    //----------------------------------------------------------------
    // カウンタ表示
    fun showMsg(msg: String) {
        doneMsg.text = msg
    }

    //----------------------------------------------------------------
    // 再スタート
    fun replay() {
        board.setup(filename)
        showcounter()
        backButton.disable()
        redoButton.disable()
        if (nextfile == null)
            nextButton.disable()
        else
            enable()
        paint(graphics)
    }

    //----------------------------------------------------------------
    // 次の面へ
    operator fun next() {
        if (nextfile != null) filename = nextfile
        replay()
    }

    //----------------------------------------------------------------
    // マウス操作
    override fun mouseDown(evt: Event?, x: Int, y: Int): Boolean {
        board.mouseDown(x, y)
        return true
    }

    override fun mouseDrag(evt: Event?, x: Int, y: Int): Boolean {
        board.mouseDrag(x, y)
        return true
    }

    override fun mouseUp(evt: Event?, x: Int, y: Int): Boolean {
        board.mouseUp(x, y)
        return true
    }

    //----------------------------------------------------------------
    //	ペイント
    override fun paint(g: Graphics?) {

        //		boolean flg = g.drawImage(backImage,0,CTRLPANEL_H*2,
        //						app_size.width,app_size.height-CTRLPANEL_H*2-COPY_H,
        //						shadowLight, null);

        board.paintHint(g)
        board.paint(g)
    }

    override fun repaint() {
        paint(graphics)
    }
}


//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	ＣｏｎｔｒｏｌＢｕｔｔｏｎ	制御ボタンクラス
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

internal class ControlButton(var slide: Slide, var name: String        // ボタンの名前
                             , label: String) : Button() {

    init {
        setLabel(label)
    }

    //----------------------------------------------------------------
    //	ボタン操作
    override fun action(evt: Event?, obj: Any?): Boolean {
        if (name == "Replay") slide.replay()
        if (name == "Back") slide.board.backHistory()
        if (name == "Forward") slide.board.forwardHistory()
        if (name == "Next") slide.next()
        return true
    }
}


//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	Ｂｏａｒｄ	盤内での操作を管理するクラス
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

internal class Board//----------------------------------------------------------------
// コンストラクタ  s:呼び出したApplet  r:盤の位置と大きさ(最大)
(var slide: Slide            // 呼び出した Applet
 , private val req: Rectangle            //盤の大きさの限界
) : Rectangle() {
    val maxPiece = 32        //一駒のブロック数の上限
    val maxHistory = 512    //ヒストリ上限

    var blocks: Array<Array<Block>>        //盤を表す配列
    var targetBlocks: Array<Array<String>>? = null    //目標とする状態
    var hintBlocks: Array<CharArray>? = null
    var ax: Int = 0
    var ay: Int = 0            //盤のサイズ
    var unit: Int = 0            //１ブロックの大きさ
    var minStep: Int = 0        //目標ステップ数
    var step: Int = 0            //現在のステップ数
    var pieces: Array<Piece>        //駒
    var piecen: Int = 0            //駒の数
    var currentPiece: Piece? = null        //ドラッグ中の駒
    var dragPoint: Point        //ドラッグ現在地
    var dPoint: Point            //ドラッグ中の移動距離
    var dragFlag: Boolean = false        //ドラッグ中
    var boardType = "none"
    var painting: Boolean = false        //描画中
    var pieceHistory: Array<Piece>        //駒のヒストリ
    var pointHistory: Array<Point>        //位置のヒストリ
    var historyCount: Int = 0        //ヒストリ現在地
    var redoLimit: Int = 0
    var originalRedoLimit: Int = 0
    var originalX: Int = 0
    var originalY: Int = 0
    var adjusting: Boolean = false

    //----------------------------------------------------------------
    // ヒストリバッファが溢れた時の処理
    private val isSolution: Boolean
        get() {
            var c: Char
            for (j in 0 until ay)
                for (i in 0 until ax) {
                    c = targetBlocks!![i][j][0]
                    if (c == '.' || c == '#') continue
                    if (blocks[i][j] == null) return false
                    if (targetBlocks!![i][j].indexOf(blocks[i][j].piece!!.cid.toInt()) >= 0)
                        continue
                    return false
                }
            return true
        }

    init {
        pieces = arrayOfNulls(maxPiece)
        unit = 16
        y = 0
        x = y
        step = 0
        pieceHistory = arrayOfNulls(maxHistory)
        pointHistory = arrayOfNulls(maxHistory)
    }

    //----------------------------------------------------------------
    // 初期化  filename:面データのファイル名
    fun setup(filename: String) {
        step = 0
        minStep = step
        targetBlocks = null
        hintBlocks = null
        currentPiece = null
        painting = false
        ay = 0
        ax = ay
        piecen = 0
        dragFlag = false
        loadData(filename)

        if (ax == 0 || ay == 0) {
            ay = 1
            ax = ay
            println("File read error!")
        }
        val u1 = (req.width * 0.92).toInt() / ax
        val u2 = (req.height * 0.92).toInt() / ay
        unit = if (u1 < u2) u1 else u2
        width = ax * unit
        height = ay * unit
        x = (req.width - width) / 2 + req.x
        y = (req.height - height) / 2 + req.y
        setBlockShape()
        dragPoint = Point(0, 0)
        dPoint = Point(0, 0)
        redoLimit = 0
        historyCount = redoLimit
        originalY = 0
        originalX = originalY
        originalRedoLimit = originalX
        adjusting = false
        slide.showMsg("                                    " + "                                    ")
    }

    //----------------------------------------------------------------
    // マウスを押した時  v,w:座標
    fun mouseDown(v: Int, w: Int) {
        var v = v
        var w = w
        if (dragFlag) return
        if (!this.inside(v, w)) return
        v -= x
        w -= y
        painting = true
        dragPoint.move(v, w)
        v /= unit
        w /= unit
        if (blocks[v][w] != null && blocks[v][w].outside)
            return
        if (blocks[v][w] != null) {
            currentPiece = blocks[v][w].piece
            currentPiece!!.resetAdjust()
            dragFlag = true
        }
        dPoint.move(0, 0)
        painting = false
    }

    //----------------------------------------------------------------
    // ドラッグ中  v,w:座標
    fun mouseDrag(v: Int, w: Int) {
        var v = v
        var w = w
        val radius = 5        // radius of pieces corner
        if (!dragFlag || painting) return
        painting = true
        v -= x
        w -= y
        dragFlag = false
        val dx = v - dragPoint.x
        val dy = w - dragPoint.y
        var f = false

        val dx2 = currentPiece!!.moveX(dPoint.x, dPoint.y, dx)

        //		if (dx!=0 && dx2==0) {
        //			for (int d=-radius;d<=radius;d++) {
        //				dx2 = currentPiece.moveX(dPoint.x,dPoint.y-d,dx);
        //				if (dx2 != 0) {
        //					System.out.println("ADJUST-y: "+ -d);
        //					dPoint.y-=d;
        //					dy+=d;
        //
        //					int ddx;
        //					if(dx>0) ddx=2*radius-dx; else ddx=dx-2*radius;
        //					dPoint.x-=ddx;
        //					dx+=ddx;
        //					System.out.println("BOOST-x: "+dx);
        //					dx2 = currentPiece.moveX(dPoint.x,dPoint.y,dx);
        //					break;
        //				}
        //			}
        //		}

        dPoint.x += dx2
        if (dx2 != dx && dx2 != 0 &&
                currentPiece!!.moveX(dPoint.x, dPoint.y, dx) == 0) {
            f = true
        }

        val dy2 = currentPiece!!.moveY(dPoint.x, dPoint.y, dy)

        //		if (dy!=0 && dy2==0) {
        //			for (int d=-radius;d<=radius;d++) {
        //				dy2 = currentPiece.moveY(dPoint.x-d,dPoint.y,dy);
        //				if (dy2 != 0) {
        //					System.out.println("ADJUST-x: "+ -d);
        //					dPoint.x-=d;
        //					dx+=d;
        //
        //					int ddy;
        //					if(dy>0) ddy=2*radius-dy; else ddy=dy-2*radius;
        //					dPoint.y-=ddy;
        //					dy+=ddy;
        //					System.out.println("BOOST-y: "+dy);
        //					dy2 = currentPiece.moveY(dPoint.x,dPoint.y,dy);
        //					break;
        //				}
        //			}
        //		}

        dPoint.y += dy2
        if (dy2 != dx && dy2 != 0 &&
                currentPiece!!.moveY(dPoint.x, dPoint.y, dy) == 0) {
            f = true
        }

        dragPoint.move(v, w)
        currentPiece!!.update(slide.graphics, dPoint.x, dPoint.y)

        if (f) slide.sound!!.play()
        dragFlag = true
        painting = false
    }

    //----------------------------------------------------------------
    // マウスを離した時  v,w:座標
    fun mouseUp(v: Int, w: Int) {
        var v = v
        var w = w
        v -= x
        w -= y
        if (!dragFlag) return
        dragFlag = false
        val offx = (dPoint.x + unit / 2 + unit * 1024) / unit - 1024
        val offy = (dPoint.y + unit / 2 + unit * 1024) / unit - 1024

        // see if this move is really same as Back
        if (historyCount > 0)
            if (pieceHistory[historyCount - 1].cid == currentPiece!!.cid &&
                    pointHistory[historyCount - 1].x == -offx &&
                    pointHistory[historyCount - 1].y == -offy) {
                backHistory()
                return
            }
        // see if this move is really same as Forward
        if (historyCount < redoLimit)
            if (pieceHistory[historyCount].cid == currentPiece!!.cid)
                if (pointHistory[historyCount].x == offx && pointHistory[historyCount].y == offy) {
                    forwardHistory()
                    return
                }

        currentPiece!!.update(slide.graphics, offx * unit, offy * unit)
        currentPiece!!.move(offx, offy)

        // check to see if this is same piece as previous move
        // know it can't be a "back", but maybe a "forward"
        if (historyCount > 0 && pieceHistory[historyCount - 1].cid == currentPiece!!.cid) {
            val offxx = pointHistory[historyCount - 1].x + offx
            val offyy = pointHistory[historyCount - 1].y + offy
            println("Replace " + step.toString() + ": " +
                    currentPiece!!.cid + ", " + offxx.toString() + "," +
                    offyy.toString())

            // check to see if Forward should be enabled or disabled
            if (originalRedoLimit > 0 && offxx == originalX &&
                    offyy == originalY) {
                redoLimit = originalRedoLimit
                if (historyCount < redoLimit)
                    slide.redoButton.enable()
            } else {
                slide.redoButton.disable()
            }
            pointHistory[historyCount - 1].x = offxx
            pointHistory[historyCount - 1].y = offyy
            pieceHistory[historyCount - 1] = currentPiece
        } else if (offx != 0 || offy != 0) {
            println("Move " + (step + 1).toString() + ": " +
                    currentPiece!!.cid +
                    ", " + offx.toString() + "," +
                    offy.toString())

            originalRedoLimit = 0
            if (historyCount < redoLimit)
                if (pieceHistory[historyCount].cid == currentPiece!!.cid) {
                    // save information about now-current move
                    // for future reinstatement of forward history
                    originalRedoLimit = redoLimit
                    originalX = pointHistory[historyCount].x
                    originalY = pointHistory[historyCount].y
                }
            pieceHistory[historyCount] = currentPiece
            pointHistory[historyCount++] = Point(offx, offy)
            redoLimit = historyCount

            slide.backButton.enable()
            slide.redoButton.disable()
            reduceHistory()
            step++
            slide.showcounter()
        }

        if (isSolution) {
            println("SOLUTION - " + step.toString())
            if (step > minStep && minStep > 0)
                slide.showMsg("Done!")
            if (minStep == 0)
                slide.showMsg("Done - please report results!")
            if (step == minStep)
                slide.showMsg("Complete with minimum moves!!")
            if (step < minStep)
                slide.showMsg("Minimum IMPROVED!!!")
        }

        borderpaint(slide.graphics)

        currentPiece = null
        if (dPoint.x != offx * unit || dPoint.y != offy * unit)
            slide.sound!!.play()
        painting = false
    }

    //----------------------------------------------------------------
    // Backボタンを押した時
    fun backHistory() {
        if (dragFlag) return
        if (historyCount <= 0) return
        val p = pointHistory[--historyCount]
        pieceHistory[historyCount].move(-p.x, -p.y)
        slide.paint(slide.graphics)
        slide.sound!!.play()
        step--
        slide.showcounter()
        println("Back " + (step + 1).toString() + ": " +
                pieceHistory[historyCount].cid + ", " +
                (-p.x).toString() + ", " + (-p.y).toString())
        slide.redoButton.enable()
        if (historyCount == 0) slide.backButton.disable()

        originalRedoLimit = 0
    }

    //----------------------------------------------------------------
    // Backボタンを押した時
    fun forwardHistory() {
        if (dragFlag) return
        if (historyCount == redoLimit) return
        val p = pointHistory[historyCount]
        println("Forward " + (step + 1).toString() + ": " +
                pieceHistory[historyCount].cid + ", " +
                p.x.toString() + ", " + p.y.toString())
        pieceHistory[historyCount++].move(p.x, p.y)
        slide.paint(slide.graphics)
        slide.sound!!.play()
        step++
        slide.showcounter()
        slide.backButton.enable()
        if (historyCount == redoLimit) slide.redoButton.disable()

        originalRedoLimit = 0
    }

    //----------------------------------------------------------------
    // ヒストリバッファが溢れた時の処理
    private fun reduceHistory() {
        if (historyCount >= maxHistory) {
            println("Reduce history")
            var i = 0
            var j = maxHistory / 2
            while (j < maxHistory) {
                pieceHistory[i] = pieceHistory[j]
                pointHistory[i] = pointHistory[j]
                i++
                j++
            }
            historyCount = i
            redoLimit = i
        }
    }

    //----------------------------------------------------------------
    // Ｂｌｏｃｋの形状設定
    fun setBlockShape() {
        var j = ay
        while (--j >= 0) {
            var i = ax
            while (--i >= 0) {
                var bk: Block? = null
                var c = '.'
                try {
                    bk = blocks[i][j]
                    c = bk.piece!!.cid
                } catch (e: Exception) {
                    continue
                }

                try {
                    if (blocks[i - 1][j].piece!!.cid == c) bk.linkW = true
                } catch (e: Exception) {
                }

                try {
                    if (blocks[i + 1][j].piece!!.cid == c) bk.linkE = true
                } catch (e: Exception) {
                }

                try {
                    if (blocks[i][j - 1].piece!!.cid == c) bk.linkN = true
                } catch (e: Exception) {
                }

                try {
                    if (blocks[i][j + 1].piece!!.cid == c) bk.linkS = true
                } catch (e: Exception) {
                }

            }
        }
    }

    //----------------------------------------------------------------
    // 盤の内外判定
    fun isinside(xx: Int, yy: Int): Boolean {
        if (xx < 0 || xx >= ax)
            return false
        if (yy < 0 || yy >= ay)
            return false
        val blk = blocks[xx][yy]
        return if (blk != null && blk.outside) false else true
    }

    //----------------------------------------------------------------
    // returns true if is a blocking block
    fun isblocking(xx: Int, yy: Int): Boolean {
        if (xx < 0 || xx >= ax)
            return false
        if (yy < 0 || yy >= ay)
            return false
        val blk = blocks[xx][yy]
        return if (blk != null && blk.outside) true else false
    }

    private fun borderpaint(g: Graphics?) {
        val thick = 4
        val bWidth: Int
        val nwDelta: IntArray
        val swDelta: IntArray
        val neDelta: IntArray
        val seDelta: IntArray
        val borderColor: Color

        if (isSolution) {
            borderColor = slide.msgcolor
            bWidth = thick
        } else {
            borderColor = slide.shadowDark
            bWidth = 1
        }
        nwDelta = IntArray(thick + 1)
        swDelta = IntArray(thick + 1)
        neDelta = IntArray(thick + 1)
        seDelta = IntArray(thick + 1)

        run {
            var j = ay
            while (--j >= 0) {
                val y0 = y + unit * j
                var i = ax
                while (--i >= 0) {
                    val x0 = x + unit * i
                    if (!isinside(i, j)) {
                        continue
                    }
                    for (b in 0..thick) {
                        seDelta[b] = b + 1
                        neDelta[b] = seDelta[b]
                        swDelta[b] = neDelta[b]
                        nwDelta[b] = swDelta[b]
                    }
                    if (isinside(i - 1, j - 1))
                        for (b in 0..thick) nwDelta[b] = 1 - nwDelta[b]
                    if (isinside(i - 1, j + 1))
                        for (b in 0..thick) swDelta[b] = 1 - swDelta[b]
                    if (isinside(i + 1, j - 1))
                        for (b in 0..thick) neDelta[b] = 1 - neDelta[b]
                    if (isinside(i + 1, j + 1))
                        for (b in 0..thick) seDelta[b] = 1 - seDelta[b]

                    if (!isinside(i - 1, j)) { // 左側
                        for (b in 0..thick) {
                            if (b == 0)
                                g!!.color = slide.shadowLight
                            else if (b > bWidth)
                                g!!.color = slide.backcolor
                            else
                                g!!.color = borderColor
                            g.drawLine(x0 - b - 1, y0 - nwDelta[b],
                                    x0 - b - 1, y0 + unit - 1 + swDelta[b])
                        }
                    }
                    if (!isinside(i + 1, j)) { // 右側
                        for (b in 0..thick) {
                            if (b == 0)
                                g!!.color = slide.shadowLight
                            else if (b > bWidth)
                                g!!.color = slide.backcolor
                            else
                                g!!.color = borderColor
                            g.drawLine(x0 + unit + b, y0 - neDelta[b],
                                    x0 + unit + b, y0 + unit - 1 + seDelta[b])
                        }
                    }
                    if (!isinside(i, j - 1)) { // 上側
                        for (b in 0..thick) {
                            if (b == 0)
                                g!!.color = slide.shadowLight
                            else if (b > bWidth)
                                g!!.color = slide.backcolor
                            else
                                g!!.color = borderColor
                            g.drawLine(x0 - nwDelta[b], y0 - 1 - b,
                                    x0 + unit - 1 + neDelta[b], y0 - 1 - b)
                        }
                    }
                    if (!isinside(i, j + 1)) { // 下側
                        for (b in 0..thick) {
                            if (b == 0)
                                g!!.color = slide.shadowLight
                            else if (b > bWidth)
                                g!!.color = slide.backcolor
                            else
                                g!!.color = borderColor
                            g.drawLine(x0 - swDelta[b], y0 + unit + b,
                                    x0 + unit - 1 + seDelta[b], y0 + unit + b)
                        }
                    }
                }
            }
        }
        var j = ay
        while (--j >= 0) {
            val y0 = y + unit * j

            var i = ax
            while (--i >= 0) {
                val x0 = x + unit * i
                if (isblocking(i, j)) {
                    val blk = blocks[i][j]
                    if (blk.image != null) {
                        val flg = g!!.drawImage(blk.image,
                                x0, y0, blk.width * unit * blk.wFactor,
                                blk.height * unit * blk.hFactor,
                                slide.shadowLight, null)
                    }
                }
            }
        }
    }

    //----------------------------------------------------------------
    // 盤内ペイント
    fun paint(g: Graphics?) {

        borderpaint(g)

        //		g.setColor( slide.backcolor );
        //		g.fillRect(x,y,width,height);

        //		g.setColor( slide.shadowLight );
        //		g.drawLine(x,y+height,x+width,y+height);
        //		g.drawLine(x+width,y,x+width,y+height);
        //		g.drawLine(x-1,y+height+1,x+width+1,y+height+1);
        //		g.drawLine(x+width+1,y-1,x+width+1,y+height+1);
        //		g.setColor( slide.shadowDark );
        //		g.drawLine(x-1,y-1,x-1,y+height-1);
        //		g.drawLine(x-1,y-1,x+width-1,y-1);
        //		g.drawLine(x-2,y-2,x-2,y+height);
        //		g.drawLine(x-2,y-2,x+width,y-2);

        var j = ay
        while (--j >= 0) {
            var i = ax
            while (--i >= 0) {
                val blk = blocks[i][j]
                if (blk == null) {
                    g!!.color = slide.backcolor
                    g.fillRect(x + i * unit, y + j * unit, unit, unit)
                } else if (!blk.outside) {
                    blk.paint(g)
                }
            }
        }
    }

    //----------------------------------------------------------------
    // 目標を示す線の描画
    fun paintHint(g: Graphics?) {
        var c: Char
        val hWidth = 4

        if (hintBlocks == null) return
        g!!.color = slide.hintcolor
        var v = ax
        while (--v >= 0) {
            c = hintBlocks!![v][0]
            if (c != '.' && c != '#') {
                g.fillRect(x + v * unit, y - 10, unit, hWidth)
            } else {
                g.clearRect(x + v * unit, y - 10, unit, hWidth)
            }
            c = hintBlocks!![v][ay - 1]
            if (c != '.' && c != '#') {
                g.fillRect(x + v * unit, y + height + (10 - hWidth), unit, hWidth)
            } else {
                g.clearRect(x + v * unit, y + height + (10 - hWidth), unit, hWidth)
            }
        }
        var w = ay
        while (--w >= 0) {
            c = hintBlocks!![0][w]
            if (c != '.' && c != '#') {
                g.fillRect(x - 10, y + w * unit, hWidth, unit)
            } else {
                g.clearRect(x - 10, y + w * unit, hWidth, unit)
            }
            c = hintBlocks!![ax - 1][w]
            if (c != '.' && c != '#') {
                g.fillRect(x + width + (10 - hWidth), y + w * unit, hWidth, unit)
            } else {
                g.clearRect(x + width + (10 - hWidth), y + w * unit, hWidth, unit)
            }
        }
    }


    //----------------------------------------------------------------
    //	問題、解答の読み込み filename:面データのファイル名
    private fun loadData(filename: String) {
        val url: URL
        val file: DataInputStream
        try {
            url = URL(slide.documentBase, filename)
            println("Load: $url")
            file = DataInputStream(url.openStream())
        } catch (e: MalformedURLException) {
            slide.showStatus("file name error : $filename")
            return
        } catch (e: IOException) {
            slide.showStatus("file open error : $filename")
            return
        }

        try {
            slide.nextfile = null
            var line: String?
            var str = StringBuffer("")
            var y = 0
            val mTracker: MediaTracker
            mTracker = MediaTracker(slide)

            nextline@ while (true) {
                line = file.readLine()
                if (line == null)
                    break
                if (';' == line[0])
                    continue
                //				System.out.println( "line:"+line );

                val st = StringTokenizer(line)
                try {
                    var tk = st.nextToken()
                    if (tk == "size") {
                        tk = st.nextToken()
                        ax = Integer.valueOf(tk).toInt()
                        tk = st.nextToken()
                        ay = Integer.valueOf(tk).toInt()
                        //						System.out.println("\tboard size=" + String.valueOf(ax) + "," + String.valueOf(ay));
                        blocks = Array(ax) { arrayOfNulls(ay) }
                        targetBlocks = Array(ax) { arrayOfNulls(ay) }
                        continue@nextline
                    }
                    if (tk == "image") {
                        tk = st.nextToken()
                        val row = Integer.valueOf(tk).toInt()

                        tk = st.nextToken()
                        val col = Integer.valueOf(tk).toInt()

                        tk = st.nextToken()
                        blocks[col][row].image = slide.getImage(slide.codeBase, tk)

                        tk = st.nextToken()
                        blocks[col][row].hFactor = Integer.valueOf(tk).toInt()

                        tk = st.nextToken()
                        blocks[col][row].wFactor = Integer.valueOf(tk).toInt()

                        mTracker.addImage(blocks[col][row].image, 0,
                                blocks[col][row].wFactor * unit,
                                blocks[col][row].hFactor * unit)

                        continue@nextline
                    }
                    if (tk == "label") {
                        tk = st.nextToken()
                        val x = tk[0]
                        var k: Int
                        k = piecen
                        while (--k >= 0) {
                            if (pieces[k].cid == x) break
                        }
                        tk = st.nextToken()
                        if (k >= 0)
                            pieces[k].setLabel(tk)
                        continue@nextline
                    }
                    if (tk == "labeloffset") {
                        tk = st.nextToken()
                        val x = tk[0]
                        var k: Int
                        k = piecen
                        while (--k >= 0) {
                            if (pieces[k].cid == x) break
                        }
                        tk = st.nextToken()
                        val offsetX = Integer.valueOf(tk).toInt()
                        tk = st.nextToken()
                        val offsetY = Integer.valueOf(tk).toInt()
                        if (k >= 0)
                            pieces[k].setLabelOffset(offsetX, offsetY)
                        continue@nextline
                    }
                    if (tk == "color") {
                        tk = st.nextToken()
                        val x = tk[0]
                        var k: Int
                        k = piecen
                        while (--k >= 0) {
                            if (pieces[k].cid == x) break
                        }
                        tk = st.nextToken()
                        val r = Integer.valueOf(tk).toInt()
                        tk = st.nextToken()
                        val g = Integer.valueOf(tk).toInt()
                        tk = st.nextToken()
                        val b = Integer.valueOf(tk).toInt()
                        if (k >= 0)
                            pieces[k].setColor(r, g, b)
                        continue@nextline
                    }
                    if (tk == "equiv") {
                        tk = st.nextToken()
                        for (j in 0 until ay)
                            for (i in 0 until ax) {
                                if (tk.indexOf(targetBlocks!![i][j][0].toInt()) >= 0) {
                                    targetBlocks!![i][j] = tk
                                }
                            }
                        continue@nextline
                    }
                    if (tk == "next") {
                        tk = st.nextToken()
                        slide.nextfile = tk
                        //						System.out.println("\tnext:" + tk);
                        boardType = "none"
                    }
                    if (tk == "step") {
                        tk = st.nextToken()
                        minStep = Integer.valueOf(tk)
                                .toInt()
                        boardType = "none"
                    }
                    if (tk == "initial") {
                        //						System.out.println("\tload initial");
                        boardType = tk
                        str = StringBuffer("")
                        y = 0
                        continue@nextline
                    }
                    if (tk == "hint") {
                        //						System.out.println("\tload hint");
                        boardType = tk
                        hintBlocks = Array(ax) { CharArray(ay) }
                        str = StringBuffer("")
                        y = 0
                        continue@nextline
                    }
                    if (tk == "target") {
                        //						System.out.println("\tload target");
                        boardType = tk
                        str = StringBuffer("")
                        y = 0
                        continue@nextline
                    }
                    if (tk == "end") {
                        break@nextline
                    }

                    if (boardType != "none") {
                        ++y
                        str.append(line)
                        if (y >= ay) {
                            setupData(str.toString())
                            boardType = "none"
                        }
                    }
                } catch (e: NoSuchElementException) {
                    println(
                            "No such element(s).(file error)")
                }

            }

            try {
                mTracker.waitForID(0)
            } catch (e: InterruptedException) {
                println("MediaTracker error")
            }

        } catch (e: IOException) {
        }

        println("-- End of LoadData--")
    }

    //----------------------------------------------------------------
    // 初期配列／目標 データを配列に読み込む str:文字列化した面データ
    private fun setupData(str: String) {
        var idx = 0
        if (boardType == "initial") {
            for (j in 0 until ay) {
                for (i in 0 until ax) {
                    val c = str[idx++]
                    if (c == '.') continue
                    val bk = Block(i, j, this)
                    blocks[i][j] = bk
                    if (c == '#') {
                        blocks[i][j].outside = true
                        continue
                    }
                    var k: Int
                    k = piecen
                    while (--k >= 0) {
                        if (pieces[k].cid == c) break
                    }
                    if (k < 0) {
                        k = piecen
                        pieces[piecen++] = Piece(this, c)
                    }
                    pieces[k].add(bk)
                }
            }
        }
        if (boardType == "hint") {
            for (j in 0 until ay)
                for (i in 0 until ax) {
                    hintBlocks!![i][j] = str[idx++]
                }
        }
        if (boardType == "target") {
            for (j in 0 until ay)
                for (i in 0 until ax) {
                    targetBlocks!![i][j] = str[idx++].toString()
                }
        }
    }

}


//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	Ｂｌｏｃｋ	駒を構成する最小の単位
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
internal class Block//----------------------------------------------------------------
// コンストラクタ  cx,cy:Block配列上での位置  bd:属するBoard
(cx: Int, cy: Int, var board: Board        // 属する盤
) : Rectangle() {
    var piece: Piece? = null        // 属する駒
    var outside: Boolean = false    // 外側（駒は置けない）
    var linkN: Boolean = false        //属する駒において上と繋がっているか
    var linkS: Boolean = false        //属する駒において下と繋がっているか
    var linkW: Boolean = false        //属する駒において左と繋がっているか
    var linkE: Boolean = false        //属する駒において右と繋がっているか
    var image: Image? = null
    var label: String? = null
    var labelX: Int = 0
    var labelY: Int = 0
    var color: Color
    var hFactor: Int = 0
    var wFactor: Int = 0

    init {
        piece = null
        x = cx
        y = cy
        height = 1
        width = height
        outside = false
        linkE = outside
        linkW = linkE
        linkS = linkW
        linkN = linkS
        image = null
        label = null
        color = board.slide.piececolor
        wFactor = 1
        hFactor = wFactor
        // labelX and labelY are offsets from the center of the block
        labelX = -5
        labelY = 5
    }

    @JvmOverloads
    fun paint(g: Graphics, offx: Int = 0, offy: Int = 0) {
        val unit = board.unit
        val x1 = board.x + x * unit + offx
        val y1 = board.y + y * unit + offy
        val x2 = x1 + width * unit - 1
        val y2 = y1 + height * unit - 1
        val slide = board.slide

        if (image != null) {
            val flg = g.drawImage(image,
                    x1, y1, width * unit * wFactor, height * unit * hFactor,
                    slide.shadowLight, board.slide)
            return
        }

        if (piece!!.head!!.image == null) {
            g.color = piece!!.head!!.color
            g.fillRect(x1, y1, width * unit, height * unit)

            if (this === piece!!.head) {
                g.color = slide.shadowDark
                val pf = java.awt.Font("Helvetica", 0, 20)
                g.font = pf
                if (label == null) {
                    val x: CharArray
                    x = CharArray(1)
                    x[0] = piece!!.cid
                    g.drawChars(x, 0, 1,
                            x1 + width * board.unit * wFactor / 2 + labelX,
                            y1 + height * board.unit * hFactor / 2 + labelY)
                } else {
                    if (label!![0] != '*')
                        g.drawString(label,
                                x1 + width * board.unit * wFactor / 2 + labelX,
                                y1 + height * board.unit * hFactor / 2 + labelY)
                }
            }

            g.color = slide.shadowDark
            if (!linkE) {
                g.drawLine(x2 - 1, y1, x2 - 1, y2)
                g.drawLine(x2, y1, x2, y2)
            }
            if (!linkS) {
                g.drawLine(x1, y2 - 1, x2, y2 - 1)
                g.drawLine(x1, y2, x2, y2)
            }
            g.color = slide.shadowLight
            if (!linkW) {
                g.drawLine(x1, y1, x1, y2)
                g.drawLine(x1 + 1, y1, x1 + 1, y2)
            }
            if (!linkN) {
                g.drawLine(x1, y1, x2, y1)
                g.drawLine(x1, y1 + 1, x2, y1 + 1)
            }
        }
    }

    //----------------------------------------------------------------
    // ブロック消去  offx,offy:元の位置からの相対位置
    //		dx,dy:以前の書き換え位置からの相対位置
    fun clear(g: Graphics, offx: Int, offy: Int, dx: Int, dy: Int) {
        val unit = board.unit
        val slide = board.slide
        g.color = slide.backcolor

        val x1 = board.x + unit * x + offx
        val y1 = board.y + unit * y + offy
        val x2: Int
        val w2: Int
        val y2: Int
        val h2: Int
        val x3: Int
        val w3: Int
        val y3: Int
        val h3: Int
        val bx: Boolean
        val by: Boolean

        if (dx >= 0) {
            x2 = x1
            w2 = if (dx < unit) dx else unit
            x3 = x1 + w2
            w3 = unit - w2
            bx = linkW
        } else {
            x2 = x1 + unit + dx
            w2 = if (-dx < unit) -dx else unit
            x3 = x1
            w3 = unit - w2
            bx = linkE
        }
        if (dy >= 0) {
            y2 = y1
            h2 = if (dy < unit) dy else unit
            by = linkN
        } else {
            y2 = y1 + unit + dy
            h2 = if (-dy < unit) -dy else unit
            by = linkS
        }
        if (bx)
            g.fillRect(x2, y2, w2, h2)
        else
            g.fillRect(x2, y1, w2, unit)
        if (!by) g.fillRect(x3, y2, w3, h2)
    }
}//----------------------------------------------------------------
// ペイント  offx,offy:元の位置からの相対位置


//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	Ｐｉｅｃｅ	一つの駒(移動させる時の単位)のクラス
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

internal class Piece//----------------------------------------------------------------
//	構築子／登録子	c:駒を識別するための文字
(var board: Board, var cid: Char    // 駒の識別文字
) {
    val maxBlocks = 36

    private val blocks: Array<Block>
    var blockn: Int = 0
    var delta: Point

    //----------------------------------------------------------------
    // getHead() returns the first block in the piece.

    val head: Block?
        get() = if (blockn == 0) null else blocks[0]

    init {
        blockn = 0
        blocks = arrayOfNulls(maxBlocks)
        delta = Point(0, 0)
    }

    //----------------------------------------------------------------
    // setLabel() sets the optional label of the first block.

    fun setLabel(lab: String) {
        blocks[0].label = lab
        blocks[0].labelX = blocks[0].labelX - 5 * (lab.length - 1)
    }

    //----------------------------------------------------------------
    // setLabelOffset() sets the display offset for a label.

    fun setLabelOffset(x: Int, y: Int) {
        blocks[0].labelX = x
        blocks[0].labelY = y
    }

    //----------------------------------------------------------------
    // setColor() sets the optional RGB color of the first block.

    fun setColor(r: Int, g: Int, b: Int) {
        blocks[0].color = Color(r, g, b)
    }

    //----------------------------------------------------------------
    // ＰｉｅｃｅにＢｌｏｃｋを追加
    fun add(bk: Block) {
        if (bk.piece != null) {
            println("Double definition")
            return
        }
        blocks[blockn++] = bk
        bk.piece = this
    }

    //----------------------------------------------------------------
    // Ｘ方向移動チェック x,y:元の位置からの相対位置  dx:移動量
    fun moveX(x: Int, y: Int, dx: Int): Int {
        var dx = dx
        if (dx == 0) return 0
        val unit = board.unit
        var offx = (x + unit * 1024) / unit - 1024
        val offy = (y + unit * 1024) / unit - 1024
        val adj = x - offx * unit
        if (adj != 0) {
            if (dx < -adj) dx = -adj
            if (dx > unit - adj) dx = unit - adj
            return dx
        } else {
            offx = if (dx > 0) offx + 1 else offx - 1
            var i = blockn
            while (--i >= 0) {
                val bk = blocks[i]
                var bk2: Block? = null
                try {
                    bk2 = board.blocks[bk.x + offx][bk.y + offy]
                    if (bk2 != null) {
                        if (bk2.outside || bk2.piece!!.cid != cid) {
                            return 0
                        }
                    }
                    if (y % unit != 0) {
                        bk2 = board.blocks[bk.x + offx][bk.y + offy + 1]
                        if (bk2 != null && bk2.piece!!.cid != cid) return 0
                    }
                } catch (e: Exception) {
                    return 0
                }

            }
            if (dx > unit) dx = unit
            if (dx < -unit) dx = -unit
            return dx
        }
    }


    //----------------------------------------------------------------
    //  Ｙ方向移動チェック x,y:元の位置からの相対位置  dy:移動量
    fun moveY(x: Int, y: Int, dy: Int): Int {
        var dy = dy
        if (dy == 0) return 0
        val unit = board.unit
        val offx = (x + unit * 1024) / unit - 1024
        var offy = (y + unit * 1024) / unit - 1024
        val adj = y - offy * unit
        if (adj != 0) {
            if (dy < -adj) dy = -adj
            if (dy > unit - adj) dy = unit - adj
            return dy
        } else {
            offy = if (dy > 0) offy + 1 else offy - 1
            var i = blockn
            while (--i >= 0) {
                val bk = blocks[i]
                var bk2: Block? = null
                try {
                    bk2 = board.blocks[bk.x + offx][bk.y + offy]
                    if (bk2 != null) {
                        if (bk2.outside || bk2.piece!!.cid != cid) {
                            return 0
                        }
                    }
                    if (x % unit != 0) {
                        bk2 = board.blocks[bk.x + offx + 1][bk.y + offy]
                        if (bk2 != null && bk2.piece!!.cid != cid) return 0
                    }
                } catch (e: Exception) {
                    return 0
                }

            }
            if (dy > unit) dy = unit
            if (dy < -unit) dy = -unit
            return dy
        }
    }

    //------------------------------------------------------------
    //	実際の移動  dx,dy:盤の配列上の相対位置
    fun move(dx: Int, dy: Int) {
        val unit = board.unit
        var i: Int
        i = blockn
        while (--i >= 0) {
            val bk = blocks[i]
            board.blocks[bk.x][bk.y] = null
        }
        i = blockn
        while (--i >= 0) {
            val bk = blocks[i]
            blocks[i].move(bk.x + dx, bk.y + dy)
            board.blocks[bk.x][bk.y] = blocks[i]

        }
    }


    //----------------------------------------------------------------
    // 描画ルーチンの初期化 updateを使い始める前に使う
    fun resetAdjust() {
        delta.y = 0
        delta.x = delta.y
    }

    //----------------------------------------------------------------
    // ドラッグ中の描画  dx,dy:元の位置からの相対位置
    fun update(g: Graphics, dx: Int, dy: Int) {
        if (delta.x == dx && delta.y == dy) return
        run {
            var i = blockn
            while (--i >= 0) {
                blocks[i].clear(g, delta.x, delta.y, dx - delta.x, dy - delta.y)
            }
        }
        delta.x = dx
        delta.y = dy


        var i = blockn
        while (--i >= 0) {
            blocks[i].paint(g, dx, dy)
        }
    }

}

//================================================================
//			End of File
//================================================================