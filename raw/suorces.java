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
import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	£Ó£ì£é£ä£å  £Á£ð£ð£ì£å£ô
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

public class Slide extends Applet {

    public void snapThatCrap() {
        int GapX = 8
        int GapY = 8;
        int SizeX = 32;
        int SizeY = 32;

        int FullSizeX = SizeX + GapX;
        int FullSizeX = SizeX + GapX;

        public void snapThatCrap(Vector3 inout)
        {
            // Convert the point to tile space
            int TileX = (int) (inout.x / FullSizeX);
            int TileY = (int) (inout.y / FullSizeY);

            // Find how far into the tile the point is
            float IntoTileX = inout.x - (TileX * FullSizeX);
            float IntoTileY = inout.y - (TileY * FullSizeY);

            // If we are less than half into the tile
            if (IntoTileX < (SizeX / 2))
            {
                // Snap to the left side of the tile
                inout.x = TileX * FullSizeX;
            }
            else
                // If we are more than half into the tile, and less than half of the gap
                if (IntoTileX < (SizeX + (GapX / 2)))
                {
                    // Snap to the right side of the tile
                    inout.x = TileX * FullSizeX + SizeX;
                }
                else
                {
                    // Snap to the left side of the next tile
                    inout.x = TileX * FullSizeX + FullSizeX;
                }

            // If we are less than half into the tile
            if (IntoTileY < (SizeY / 2))
            {
                // Snap to the bottom side of the tile
                inout.y = TileY * FullSizeY;
            }
            else
                // If we are more than half into the tile, and less than half of the gap
                if (IntoTileY < (SizeY + (GapY / 2)))
                {
                    // Snap to the top side of the tile
                    inout.y = TileY * FullSizeY + SizeY;
                }
                else
                {
                    // Snap to the bottom side of the next tile
                    inout.y = TileY * FullSizeY + FullSizeY;
                }

            // If we are off the grid to the left, fix it
            if (inout.x < 0)
            {
                inout.x = 0;
            }
            else
                // If we are off the grid to the right, fix it
                if (inout.x > Cols * FullSizeX - GapX)
                {
                    inout.x = Cols * FullSizeX - GapX;
                }
            // If we are off the grid to the bottom, fix it
            if (inout.y < 0)
            {
                inout.y = 0;
            }
            else
                // If we are off the grid to the top, fix it
                if (inout.y > Rows * FullSizeY - GapY)
                {
                    inout.y = Rows * FullSizeY - GapY;
                }
        }
    }

    final	int	BUTTON_H 	=  35;
    final	int	MSG_H		=  35;
    final	int	COPY_H	=  30;

    public	Dimension  app_size;		// ¥¢¥×¥ì¥Ã¥È¤Î¥µ¥¤¥º
    Board		board;			// È×
    AudioClip	sound = null;		// ¶ð¤òÆ°¤«¤¹»þ¤Î²»
    String	filename;		// ÌÌ¥Ç¡¼¥¿¤Î¥Õ¥¡¥¤¥ë
    String	nextfile;		// ¼¡¤Ë¿Ê¤àÌÌ¤Î¥Õ¥¡¥¤¥ëÌ¾
    private
    Label			stepCounter;		// stepÉ½¼¨
    Label			doneMsg;
    ControlButton	nextButton;
    ControlButton	backButton;
    ControlButton	redoButton;
    ControlButton	replayButton;

    final	Color	backcolor  = new Color(180,180,180);	// background - grey
    final Color pagecolor = new Color(222,222,222);
    //	final Color pagecolor = Color.white;		 	// for white background
    final	Color	shadowLight = Color.white;			// top, light shadow
    final	Color	shadowDark  = Color.black;			// bottom, dark shadow
    final	Color	piececolor = new Color(0.40f,0.40f,1.0f);	// default piece color
    final Color limitcolor = Color.green;
    final	Color	hintcolor  = Color.black;			// goal line color
    final Color msgcolor	= Color.red;

    //----------------------------------------------------------------
    // ½é´ü²½
    public void init() {
        String crMessage = "Slide Puzzle V3.5  " +
                "Copyright(c)1996-1999 Hirofumi Fujiwara, Nick Baxter";
        System.out.println(crMessage);
        System.out.println("Version 3.5.1 - Netscape 4.5 fix part 1");

        sound = getAudioClip(getCodeBase(),"tick.au");
        filename = getParameter( "problemfile" );
        app_size = this.size();

        Rectangle r = new Rectangle(0,BUTTON_H + MSG_H,
                app_size.width,
                app_size.height - 2*BUTTON_H - MSG_H - COPY_H);

        board = new Board(this,r);

        Font	f10 = new java.awt.Font( "Helvetica", 0, 10 );
        Font	f14 = new java.awt.Font( "Helvetica", 0, 14 );
        Font	bf16 = new java.awt.Font( "Helvetica", 1, 16 );
        Font	f20 = new java.awt.Font( "Helvetica", 0, 20 );

        setBackground( pagecolor );

        setLayout( new BorderLayout() );
        {
            Panel p1 = new Panel();
            add("North",p1);
            p1.setLayout(new FlowLayout());
            backButton = new ControlButton(this,"Back"," Back ");
            backButton.setFont( f14 );
            redoButton = new ControlButton(this,"Forward"," Forward ");
            redoButton.setFont( f14 );
            p1.add(backButton);
            p1.add(redoButton);

            stepCounter = new Label();
            stepCounter.setFont( f20 );
            showcounter();
            p1.add( stepCounter );
            replayButton = new ControlButton(this,"Replay"," Restart ");
            replayButton.setFont( f14 );
            p1.add(replayButton);

            p1.reshape(0,0,app_size.width,BUTTON_H);

        }

        setLayout(new BorderLayout());
        {
            Panel p4 = new Panel();
            add("North",p4);
            p4.setLayout(new FlowLayout());

            doneMsg = new Label();
            doneMsg.setFont(bf16);
            doneMsg.setForeground(msgcolor);
            doneMsg.setAlignment(Label.CENTER);
            doneMsg.setText("                                    "+
                    "                                    ");
            p4.add(doneMsg);

            p4.reshape(0, BUTTON_H, app_size.width, MSG_H);
        }

        setLayout(new BorderLayout());
        {
            Panel p3 = new Panel();
            add("South", p3 );
            p3.setLayout( new FlowLayout());

            nextButton = new ControlButton(this,"Next"," Go to next problem ");
            nextButton.setFont(f14);
            p3.add(nextButton);

            p3.reshape( 0, app_size.height - BUTTON_H - COPY_H,
                    app_size.width, BUTTON_H);

        }

        setLayout(new BorderLayout());
        {
            Panel p2 = new Panel();
            p2.setLayout( new FlowLayout());
            add("South", p2 );

            Label copyright = new Label( crMessage, Label.CENTER );
            copyright.setFont( f10 );
            p2.add( copyright );

            p2.reshape( 0, app_size.height - COPY_H,
                    app_size.width, COPY_H);
        }
        replay();
    }

    //----------------------------------------------------------------
    // ¥«¥¦¥ó¥¿É½¼¨
    public	void showcounter() {
        if(board.minStep==0)
            stepCounter.setText( "   Step  "+board.step+"   " );
        else stepCounter.setText( "   Step  "+board.step+"/"+
                board.minStep+"   " );
    }

    //----------------------------------------------------------------
    // ¥«¥¦¥ó¥¿É½¼¨
    public	void showMsg(String msg) {
        doneMsg.setText( msg );
    }

    //----------------------------------------------------------------
    // ºÆ¥¹¥¿¡¼¥È
    public void replay() {
        board.setup(filename);
        showcounter();
        backButton.disable();
        redoButton.disable();
        if(nextfile == null) nextButton.disable();
        else enable();
        paint( getGraphics() );
    }

    //----------------------------------------------------------------
    // ¼¡¤ÎÌÌ¤Ø
    public void next() {
        if(nextfile != null) filename = nextfile;
        replay();
    }

    //----------------------------------------------------------------
    // ¥Þ¥¦¥¹Áàºî
    public boolean mouseDown( Event evt, int x, int y ) {
        board.mouseDown(x,y);
        return	true;
    }

    public boolean mouseDrag( Event evt, int x, int y ) {
        board.mouseDrag(x,y);
        return	true;
    }

    public boolean mouseUp( Event evt, int x, int y ) {
        board.mouseUp(x,y);
        return	true;
    }

    //----------------------------------------------------------------
    //	¥Ú¥¤¥ó¥È
    public void paint( Graphics g ) {

//		boolean flg = g.drawImage(backImage,0,CTRLPANEL_H*2,
//						app_size.width,app_size.height-CTRLPANEL_H*2-COPY_H,
//						shadowLight, null);

        board.paintHint(g);
        board.paint(g);
    }
    public void repaint() {
        paint(getGraphics());
    }
}




//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	£Ã£ï£î£ô£ò£ï£ì£Â£õ£ô£ô£ï£î	À©¸æ¥Ü¥¿¥ó¥¯¥é¥¹
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

class  ControlButton extends Button {
    Slide		slide;
    String		name;		// ¥Ü¥¿¥ó¤ÎÌ¾Á°

    ControlButton( Slide s, String str, String label ) {
        super();
        slide = s;
        name = str;
        setLabel( label );
    }

    //----------------------------------------------------------------
    //	¥Ü¥¿¥óÁàºî
    public boolean action( Event evt, Object obj ) {
        if(name.equals("Replay")) slide.replay();
        if(name.equals("Back")) slide.board.backHistory();
        if(name.equals("Forward")) slide.board.forwardHistory();
        if(name.equals("Next")) slide.next();
        return true;
    }
}







//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	£Â£ï£á£ò£ä	È×Æâ¤Ç¤ÎÁàºî¤ò´ÉÍý¤¹¤ë¥¯¥é¥¹
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

class Board extends Rectangle {
    final	int	maxPiece = 32;		//°ì¶ð¤Î¥Ö¥í¥Ã¥¯¿ô¤Î¾å¸Â
    final	int	maxHistory = 512;	//¥Ò¥¹¥È¥ê¾å¸Â

    public
    Block	blocks[][];		//È×¤òÉ½¤¹ÇÛÎó
    String	targetBlocks[][];	//ÌÜÉ¸¤È¤¹¤ë¾õÂÖ
    char	hintBlocks[][];
    int	ax,ay;			//È×¤Î¥µ¥¤¥º
    Slide	slide;			// ¸Æ¤Ó½Ð¤·¤¿ Applet
    int	unit;			//£±¥Ö¥í¥Ã¥¯¤ÎÂç¤­¤µ
    int	minStep;		//ÌÜÉ¸¥¹¥Æ¥Ã¥×¿ô
    int	step;			//¸½ºß¤Î¥¹¥Æ¥Ã¥×¿ô
    private
    Rectangle req;			//È×¤ÎÂç¤­¤µ¤Î¸Â³¦
    Piece	pieces[];		//¶ð
    int	piecen;			//¶ð¤Î¿ô
    Piece	currentPiece;		//¥É¥é¥Ã¥°Ãæ¤Î¶ð
    Point	dragPoint;		//¥É¥é¥Ã¥°¸½ºßÃÏ
    Point	dPoint;			//¥É¥é¥Ã¥°Ãæ¤Î°ÜÆ°µ÷Î¥
    boolean	dragFlag;		//¥É¥é¥Ã¥°Ãæ
    String	boardType = "none";
    boolean	painting;		//ÉÁ²èÃæ
    Piece	pieceHistory[];		//¶ð¤Î¥Ò¥¹¥È¥ê
    Point	pointHistory[];		//°ÌÃÖ¤Î¥Ò¥¹¥È¥ê
    int	historyCount;		//¥Ò¥¹¥È¥ê¸½ºßÃÏ
    int	redoLimit;
    int	originalRedoLimit;
    int	originalX;
    int	originalY;
    boolean	adjusting;

    //----------------------------------------------------------------
    // ¥³¥ó¥¹¥È¥é¥¯¥¿  s:¸Æ¤Ó½Ð¤·¤¿Applet  r:È×¤Î°ÌÃÖ¤ÈÂç¤­¤µ(ºÇÂç)
    Board(Slide s,Rectangle r){
        pieces = new Piece[maxPiece];
        slide = s;
        unit = 16;
        x = y = 0;
        req = r;
        step = 0;
        pieceHistory = new Piece[maxHistory];
        pointHistory = new Point[maxHistory];
    }

    //----------------------------------------------------------------
    // ½é´ü²½  filename:ÌÌ¥Ç¡¼¥¿¤Î¥Õ¥¡¥¤¥ëÌ¾
    public void setup(String filename){
        minStep = step = 0;
        targetBlocks = null;
        hintBlocks = null;
        currentPiece = null;
        painting = false;
        ax = ay = 0;
        piecen = 0;
        dragFlag = false;
        loadData(filename);

        if(ax==0 || ay==0){
            ax = ay = 1;
            System.out.println("File read error!");
        }
        int u1 = (int)(req.width*0.92)/ax;
        int u2 = (int)(req.height*0.92)/ay;
        unit = (u1<u2)? u1 : u2;
        width = ax*unit;
        height = ay*unit;
        x = (req.width-width)/2+req.x;
        y = (req.height-height)/2+req.y;
        setBlockShape();
        dragPoint = new Point(0,0);
        dPoint = new Point(0,0);
        historyCount = redoLimit = 0;
        originalRedoLimit = originalX = originalY = 0;
        adjusting= false;
        slide.showMsg("                                    "+
                "                                    ");
    }

    //----------------------------------------------------------------
    // ¥Þ¥¦¥¹¤ò²¡¤·¤¿»þ  v,w:ºÂÉ¸
    public void mouseDown(int v, int w){
        if(dragFlag) return;
        if(!this.inside(v,w)) return;
        v-=x; w-=y;
        painting = true;
        dragPoint.move(v,w);
        v/=unit; w/=unit;
        if(blocks[v][w]!=null && blocks[v][w].outside)
            return;
        if(blocks[v][w]!=null){
            currentPiece = blocks[v][w].piece;
            currentPiece.resetAdjust();
            dragFlag = true;
        }
        dPoint.move(0,0);
        painting = false;
    }

    //----------------------------------------------------------------
    // ¥É¥é¥Ã¥°Ãæ  v,w:ºÂÉ¸
    public void mouseDrag(int v, int w){
        int radius = 5;		// radius of pieces corner
        if(!dragFlag || painting) return;
        painting = true;
        v-=x; w-=y;
        dragFlag = false;
        int dx = v - dragPoint.x;
        int dy = w - dragPoint.y;
        boolean f = false;

        int dx2 = currentPiece.moveX(dPoint.x,dPoint.y,dx);

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

        dPoint.x += dx2;
        if(dx2!=dx && dx2!=0 &&
                currentPiece.moveX(dPoint.x,dPoint.y,dx)==0) {
            f = true;
        }

        int dy2 = currentPiece.moveY(dPoint.x,dPoint.y,dy);

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

        dPoint.y += dy2;
        if(dy2!=dx && dy2!=0 &&
                currentPiece.moveY(dPoint.x,dPoint.y,dy)==0){
            f = true;
        }

        dragPoint.move(v,w);
        currentPiece.update(slide.getGraphics(),dPoint.x,dPoint.y);

        if(f) slide.sound.play();
        dragFlag = true;
        painting = false;
    }

    //----------------------------------------------------------------
    // ¥Þ¥¦¥¹¤òÎ¥¤·¤¿»þ  v,w:ºÂÉ¸
    public void mouseUp(int v, int w){
        v-=x; w-=y;
        if(!dragFlag) return;
        dragFlag = false;
        int offx = (dPoint.x+unit/2+unit*1024)/unit-1024;
        int offy = (dPoint.y+unit/2+unit*1024)/unit-1024;

        // see if this move is really same as Back
        if (historyCount>0)
            if (pieceHistory[historyCount-1].cid==currentPiece.cid &&
                    pointHistory[historyCount-1].x == -offx &&
                    pointHistory[historyCount-1].y == -offy) {
                backHistory();
                return;
            }
        // see if this move is really same as Forward
        if (historyCount<redoLimit)
            if (pieceHistory[historyCount].cid==currentPiece.cid)
                if (pointHistory[historyCount].x == offx &&
                        pointHistory[historyCount].y == offy) {
                    forwardHistory();
                    return;
                }

        currentPiece.update(slide.getGraphics(),offx*unit,offy*unit);
        currentPiece.move(offx,offy);

        // check to see if this is same piece as previous move
        // know it can't be a "back", but maybe a "forward"
        if ( (historyCount>0) &&
                (pieceHistory[historyCount-1].cid==currentPiece.cid) ) {
            int offxx = pointHistory[historyCount-1].x + offx;
            int offyy = pointHistory[historyCount-1].y + offy;
            System.out.println("Replace " + String.valueOf(step) + ": " +
                    currentPiece.cid + ", "+String.valueOf(offxx)+","+
                    String.valueOf(offyy));

            // check to see if Forward should be enabled or disabled
            if ((originalRedoLimit>0) && (offxx==originalX) &&
                    (offyy==originalY)) {
                redoLimit = originalRedoLimit;
                if (historyCount<redoLimit)
                    slide.redoButton.enable();
            } else {
                slide.redoButton.disable();
            }
            pointHistory[historyCount-1].x = offxx;
            pointHistory[historyCount-1].y = offyy;
            pieceHistory[historyCount-1] = currentPiece;
        }
        else if(offx!=0 || offy!=0) {
            System.out.println("Move " + String.valueOf(step+1) + ": " +
                    currentPiece.cid+
                    ", "+String.valueOf(offx)+","+
                    String.valueOf(offy));

            originalRedoLimit = 0;
            if (historyCount<redoLimit)
                if (pieceHistory[historyCount].cid==currentPiece.cid) {
                    // save information about now-current move
                    // for future reinstatement of forward history
                    originalRedoLimit = redoLimit;
                    originalX = pointHistory[historyCount].x;
                    originalY = pointHistory[historyCount].y;
                }
            pieceHistory[historyCount] = currentPiece;
            pointHistory[historyCount++] = new Point(offx,offy);
            redoLimit = historyCount;

            slide.backButton.enable();
            slide.redoButton.disable();
            reduceHistory();
            step++;
            slide.showcounter();
        }

        if (isSolution()) {
            System.out.println("SOLUTION - "+String.valueOf(step));
            if (step>minStep && minStep>0)
                slide.showMsg("Done!");
            if (minStep==0)
                slide.showMsg("Done - please report results!");
            if (step==minStep)
                slide.showMsg("Complete with minimum moves!!");
            if (step<minStep)
                slide.showMsg("Minimum IMPROVED!!!");
        }

        borderpaint(slide.getGraphics());

        currentPiece = null;
        if(dPoint.x!=offx*unit || dPoint.y!=offy*unit)
            slide.sound.play();
        painting = false;
    }

    //----------------------------------------------------------------
    // Back¥Ü¥¿¥ó¤ò²¡¤·¤¿»þ
    public void backHistory(){
        if(dragFlag) return;
        if(historyCount <= 0) return;
        Point p = pointHistory[--historyCount];
        pieceHistory[historyCount].move(-p.x,-p.y);
        slide.paint(slide.getGraphics());
        slide.sound.play();
        step--;
        slide.showcounter();
        System.out.println("Back " + String.valueOf(step+1) + ": "+
                pieceHistory[historyCount].cid+", "+
                String.valueOf(-p.x)+", "+String.valueOf(-p.y));
        slide.redoButton.enable();
        if (historyCount==0) slide.backButton.disable();

        originalRedoLimit = 0;
    }

    //----------------------------------------------------------------
    // Back¥Ü¥¿¥ó¤ò²¡¤·¤¿»þ
    public void forwardHistory(){
        if(dragFlag) return;
        if(historyCount == redoLimit) return;
        Point p = pointHistory[historyCount];
        System.out.println("Forward " + String.valueOf(step+1) + ": " +
                pieceHistory[historyCount].cid+", "+
                String.valueOf(p.x)+", "+String.valueOf(p.y));
        pieceHistory[historyCount++].move(p.x,p.y);
        slide.paint(slide.getGraphics());
        slide.sound.play();
        step++;
        slide.showcounter();
        slide.backButton.enable();
        if (historyCount == redoLimit) slide.redoButton.disable();

        originalRedoLimit = 0;
    }

    //----------------------------------------------------------------
    // ¥Ò¥¹¥È¥ê¥Ð¥Ã¥Õ¥¡¤¬°î¤ì¤¿»þ¤Î½èÍý
    private void reduceHistory(){
        if(historyCount >= maxHistory){
            System.out.println("Reduce history");
            int i=0;
            for(int j=maxHistory/2; j<maxHistory; i++,j++){
                pieceHistory[i] = pieceHistory[j];
                pointHistory[i] = pointHistory[j];
            }
            historyCount = i;
            redoLimit = i;
        }
    }

    //----------------------------------------------------------------
    // ¥Ò¥¹¥È¥ê¥Ð¥Ã¥Õ¥¡¤¬°î¤ì¤¿»þ¤Î½èÍý
    private boolean isSolution(){
        char c;
        for (int j=0;j<ay;++j) for (int i=0;i<ax;++i) {
            c = targetBlocks[i][j].charAt(0);
            if (c=='.' || c=='#') continue;
            if (blocks[i][j] == null) return false;
            if (targetBlocks[i][j].indexOf(blocks[i][j].piece.cid) >= 0)
                continue;
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------
    // £Â£ì£ï£ã£ë¤Î·Á¾õÀßÄê
    public void setBlockShape(){
        for(int j=ay;--j>=0;) for(int i=ax;--i>=0;) {
            Block bk = null;
            char c = '.';
            try{
                bk = blocks[i][j];
                c = bk.piece.cid;
            } catch( Exception e ){ continue; }
            try{
                if(blocks[i-1][j].piece.cid==c) bk.linkW=true;
            } catch( Exception e ){}
            try{
                if(blocks[i+1][j].piece.cid==c) bk.linkE=true;
            } catch( Exception e ){}
            try{
                if(blocks[i][j-1].piece.cid==c) bk.linkN=true;
            } catch( Exception e ){}
            try{
                if(blocks[i][j+1].piece.cid==c) bk.linkS=true;
            } catch( Exception e ){}
        }
    }

    //----------------------------------------------------------------
    // È×¤ÎÆâ³°È½Äê
    public boolean isinside(int xx, int yy) {
        if( xx < 0 || xx >= ax )
            return false;
        if( yy < 0 || yy >= ay )
            return false;
        Block blk = blocks[xx][yy];
        if( blk != null && blk.outside)
            return false;
        return true;
    }

    //----------------------------------------------------------------
    // returns true if is a blocking block
    public boolean isblocking(int xx, int yy) {
        if( xx < 0 || xx >= ax )
            return false;
        if( yy < 0 || yy >= ay )
            return false;
        Block blk = blocks[xx][yy];
        if( blk != null && blk.outside)
            return true;
        return false;
    }

    private void borderpaint(Graphics g){
        int thick = 4;
        int bWidth;
        int nwDelta[],swDelta[],neDelta[],seDelta[];
        Color borderColor;

        if (isSolution()) {
            borderColor = slide.msgcolor;
            bWidth = thick;
        } else {
            borderColor = slide.shadowDark;
            bWidth = 1;
        }
        nwDelta = new int[thick+1];
        swDelta = new int[thick+1];
        neDelta = new int[thick+1];
        seDelta = new int[thick+1];

        for(int j=ay;--j>=0;){
            int y0 = y + unit*j;
            for(int i=ax;--i>=0;){
                int x0 = x + unit*i;
                if( ! isinside(i,j) ) {
                    continue;
                }
                for (int b=0;b<=thick;b++)
                    nwDelta[b]=swDelta[b]=neDelta[b]=seDelta[b] = b+1;
                if (isinside(i-1,j-1))
                    for (int b=0;b<=thick;b++) nwDelta[b] = 1-nwDelta[b];
                if (isinside(i-1,j+1))
                    for (int b=0;b<=thick;b++) swDelta[b] = 1-swDelta[b];
                if (isinside(i+1,j-1))
                    for (int b=0;b<=thick;b++) neDelta[b] = 1-neDelta[b];
                if (isinside(i+1,j+1))
                    for (int b=0;b<=thick;b++) seDelta[b] = 1-seDelta[b];

                if( ! isinside(i-1,j) ) { // º¸Â¦
                    for (int b=0;b<=thick;b++) {
                        if (b==0)
                            g.setColor(slide.shadowLight);
                        else if (b>bWidth)
                            g.setColor(slide.backcolor);
                        else
                            g.setColor(borderColor);
                        g.drawLine(x0-b-1,y0-nwDelta[b],
                                x0-b-1,y0+unit-1+swDelta[b]);
                    }
                }
                if( ! isinside(i+1,j) ) { // ±¦Â¦
                    for (int b=0;b<=thick;b++) {
                        if (b==0)
                            g.setColor(slide.shadowLight);
                        else if (b>bWidth)
                            g.setColor(slide.backcolor);
                        else
                            g.setColor(borderColor);
                        g.drawLine(x0+unit+b,y0-neDelta[b],
                                x0+unit+b,y0+unit-1+seDelta[b]);
                    }
                }
                if( ! isinside(i,j-1) ) { // ¾åÂ¦
                    for (int b=0;b<=thick;b++) {
                        if (b==0)
                            g.setColor(slide.shadowLight);
                        else if (b>bWidth)
                            g.setColor(slide.backcolor);
                        else
                            g.setColor(borderColor);
                        g.drawLine(x0-nwDelta[b],y0-1-b,
                                x0+unit-1+neDelta[b],y0-1-b);
                    }
                }
                if( ! isinside(i,j+1) ) { // ²¼Â¦
                    for (int b=0;b<=thick;b++) {
                        if (b==0)
                            g.setColor(slide.shadowLight);
                        else if (b>bWidth)
                            g.setColor(slide.backcolor);
                        else
                            g.setColor(borderColor);
                        g.drawLine(x0-swDelta[b],y0+unit+b,
                                x0+unit-1+seDelta[b],y0+unit+b);
                    }
                }
            }
        }
        for(int j=ay;--j>=0;){
            int y0 = y + unit*j;

            for(int i=ax;--i>=0;){
                int x0 = x + unit*i;
                if( isblocking(i,j) ) {
                    Block blk = blocks[i][j];
                    if( blk.image != null) {
                        boolean flg = g.drawImage(blk.image,
                                x0, y0, blk.width*unit*blk.wFactor,
                                blk.height*unit*blk.hFactor,
                                slide.shadowLight, null);
                    }
                }
            }
        }
    }

    //----------------------------------------------------------------
    // È×Æâ¥Ú¥¤¥ó¥È
    public void paint(Graphics g){

        borderpaint( g);

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

        for(int j=ay;--j>=0;){
            for(int i=ax;--i>=0;){
                Block blk = blocks[i][j];
                if( blk == null ) {
                    g.setColor( slide.backcolor );
                    g.fillRect(x+i*unit, y+j*unit, unit, unit);
                } else if( ! blk.outside ) {
                    blk.paint(g);
                }
            }
        }
    }

    //----------------------------------------------------------------
    // ÌÜÉ¸¤ò¼¨¤¹Àþ¤ÎÉÁ²è
    public void paintHint(Graphics g){
        char c;
        int hWidth = 4;

        if (hintBlocks==null) return;
        g.setColor( slide.hintcolor );
        for(int v=ax; --v>=0;){
            c = hintBlocks[v][0];
            if(c!='.' && c!='#'){
                g.fillRect(x+v*unit,y-10,unit,hWidth);
            }else{
                g.clearRect(x+v*unit,y-10,unit,hWidth);
            }
            c = hintBlocks[v][ay-1];
            if(c!='.' && c!='#'){
                g.fillRect(x+v*unit,y+height+(10-hWidth),unit,hWidth);
            }else{
                g.clearRect(x+v*unit,y+height+(10-hWidth),unit,hWidth);
            }
        }
        for(int w=ay; --w>=0;){
            c = hintBlocks[0][w];
            if(c!='.' && c!='#'){
                g.fillRect(x-10,y+w*unit,hWidth,unit);
            }else{
                g.clearRect(x-10,y+w*unit,hWidth,unit);
            }
            c = hintBlocks[ax-1][w];
            if(c!='.' && c!='#'){
                g.fillRect(x+width+(10-hWidth),y+w*unit,hWidth,unit);
            }else{
                g.clearRect(x+width+(10-hWidth),y+w*unit,hWidth,unit);
            }
        }
    }


    //----------------------------------------------------------------
    //	ÌäÂê¡¢²òÅú¤ÎÆÉ¤ß¹þ¤ß filename:ÌÌ¥Ç¡¼¥¿¤Î¥Õ¥¡¥¤¥ëÌ¾
    private	void loadData(String filename){
        URL		url;
        DataInputStream file;
        try {
            url = new URL( slide.getDocumentBase(), filename );
            System.out.println("Load: "+url);
            file = new DataInputStream( url.openStream() );
        } catch( MalformedURLException e ) {
            slide.showStatus( "file name error : " + filename );
            return;
        } catch( IOException e ) {
            slide.showStatus( "file open error : " + filename );
            return;
        }

        try {
            slide.nextfile = null;
            String line;
            StringBuffer str = new StringBuffer("");
            int	y = 0;
            MediaTracker mTracker;
            mTracker = new MediaTracker(slide);

            nextline:
            for(;;) {
                line=file.readLine();
                if( line == null )
                    break;
                if( ';' == line.charAt(0) )
                    continue;
//				System.out.println( "line:"+line );

                StringTokenizer st = new StringTokenizer(line);
                try {
                    String tk = st.nextToken();
                    if( tk.equals("size") ) {
                        tk = st.nextToken();
                        ax = (Integer.valueOf(tk)).
                                intValue();
                        tk = st.nextToken();
                        ay = (Integer.valueOf(tk)).
                                intValue();
//						System.out.println("\tboard size=" + String.valueOf(ax) + "," + String.valueOf(ay));
                        blocks = new Block[ax][ay];
                        targetBlocks = new String[ax][ay];
                        continue nextline;
                    }
                    if (tk.equals("image") ) {
                        tk = st.nextToken();
                        int row = (Integer.valueOf(tk)).intValue();

                        tk = st.nextToken();
                        int col = (Integer.valueOf(tk)).intValue();

                        tk = st.nextToken();
                        blocks[col][row].image =
                                slide.getImage(slide.getCodeBase(),tk);

                        tk = st.nextToken();
                        blocks[col][row].hFactor =
                                (Integer.valueOf(tk)).intValue();

                        tk = st.nextToken();
                        blocks[col][row].wFactor =
                                (Integer.valueOf(tk)).intValue();

                        mTracker.addImage(blocks[col][row].image,0,
                                blocks[col][row].wFactor*unit,
                                blocks[col][row].hFactor*unit);

                        continue nextline;
                    }
                    if (tk.equals("label") ) {
                        tk = st.nextToken();
                        char x = tk.charAt(0);
                        int k;
                        for (k=piecen;--k>=0;) {
                            if (pieces[k].cid == x) break;
                        }
                        tk = st.nextToken();
                        if (k>=0)
                            pieces[k].setLabel(tk);
                        continue nextline;
                    }
                    if (tk.equals("labeloffset") ) {
                        tk = st.nextToken();
                        char x = tk.charAt(0);
                        int k;
                        for (k=piecen;--k>=0;) {
                            if (pieces[k].cid == x) break;
                        }
                        tk = st.nextToken();
                        int offsetX = (Integer.valueOf(tk)).intValue();
                        tk = st.nextToken();
                        int offsetY = (Integer.valueOf(tk)).intValue();
                        if (k>=0)
                            pieces[k].setLabelOffset(offsetX,offsetY);
                        continue nextline;
                    }
                    if (tk.equals("color") ) {
                        tk = st.nextToken();
                        char x = tk.charAt(0);
                        int k;
                        for (k=piecen;--k>=0;) {
                            if (pieces[k].cid == x) break;
                        }
                        tk = st.nextToken();
                        int r = (Integer.valueOf(tk)).intValue();
                        tk = st.nextToken();
                        int g = (Integer.valueOf(tk)).intValue();
                        tk = st.nextToken();
                        int b = (Integer.valueOf(tk)).intValue();
                        if (k>=0)
                            pieces[k].setColor(r,g,b);
                        continue nextline;
                    }
                    if (tk.equals("equiv") ) {
                        tk = st.nextToken();
                        for (int j=0;j<ay;++j) for (int i=0;i<ax;++i) {
                            if ( tk.indexOf( targetBlocks[i][j].charAt(0) ) >=0 ) {
                                targetBlocks[i][j] = tk;
                            }
                        }
                        continue nextline;
                    }
                    if( tk.equals("next")){
                        tk = st.nextToken();
                        slide.nextfile = tk;
//						System.out.println("\tnext:" + tk);
                        boardType="none";
                    }
                    if( tk.equals("step")){
                        tk = st.nextToken();
                        minStep = (Integer.valueOf(tk))
                                .intValue();
                        boardType="none";
                    }
                    if( tk.equals("initial") ) {
//						System.out.println("\tload initial");
                        boardType = tk;
                        str = new StringBuffer("");
                        y = 0;
                        continue nextline;
                    }
                    if( tk.equals("hint") ) {
//						System.out.println("\tload hint");
                        boardType = tk;
                        hintBlocks = new char[ax][ay];
                        str = new StringBuffer("");
                        y = 0;
                        continue nextline;
                    }
                    if( tk.equals("target") ) {
//						System.out.println("\tload target");
                        boardType = tk;
                        str = new StringBuffer("");
                        y = 0;
                        continue nextline;
                    }
                    if( tk.equals("end") ) {
                        break nextline;
                    }

                    if( ! boardType.equals("none") ) {
                        ++y;
                        str.append( line );
                        if( y >= ay ) {
                            setupData(str.toString());
                            boardType="none";
                        }
                    }
                } catch( NoSuchElementException e ) {
                    System.out.println(
                            "No such element(s).(file error)");
                }
            }

            try {
                mTracker.waitForID(0);
            } catch(InterruptedException e) {
                System.out.println("MediaTracker error");
            }

        } catch( IOException e ) {}
        System.out.println( "-- End of LoadData--" );
    }

    //----------------------------------------------------------------
    // ½é´üÇÛÎó¡¿ÌÜÉ¸ ¥Ç¡¼¥¿¤òÇÛÎó¤ËÆÉ¤ß¹þ¤à str:Ê¸»úÎó²½¤·¤¿ÌÌ¥Ç¡¼¥¿
    private	void setupData(String str) {
        int	idx = 0;
        if(boardType.equals("initial")) {
            for(int j=0;j<ay;++j) {
                for(int i=0;i<ax;++i ) {
                    char c = str.charAt(idx++);
                    if(c=='.') continue;
                    Block bk = new Block(i,j,this);
                    blocks[i][j] = bk;
                    if(c=='#') {
                        blocks[i][j].outside = true;
                        continue;
                    }
                    int k;
                    for(k=piecen;--k>=0;){
                        if(pieces[k].cid==c) break;
                    }
                    if(k<0){
                        k = piecen;
                        pieces[piecen++] = new Piece(this,c);
                    }
                    pieces[k].add(bk);
                }
            }
        }
        if(boardType.equals("hint")) {
            for(int j=0;j<ay;++j) for(int i=0;i<ax;++i ) {
                hintBlocks[i][j] = str.charAt(idx++);
            }
        }
        if(boardType.equals("target")) {
            for(int j=0;j<ay;++j) for(int i=0;i<ax;++i ) {
                targetBlocks[i][j] = String.valueOf(str.charAt(idx++));
            }
        }
    }

}




//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	£Â£ì£ï£ã£ë	¶ð¤ò¹½À®¤¹¤ëºÇ¾®¤ÎÃ±°Ì
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
class Block extends Rectangle {

    public	Board	board;		// Â°¤¹¤ëÈ×
    Piece	piece;		// Â°¤¹¤ë¶ð
    boolean	outside;	// ³°Â¦¡Ê¶ð¤ÏÃÖ¤±¤Ê¤¤¡Ë
    boolean	linkN;		//Â°¤¹¤ë¶ð¤Ë¤ª¤¤¤Æ¾å¤È·Ò¤¬¤Ã¤Æ¤¤¤ë¤«
    boolean	linkS;		//Â°¤¹¤ë¶ð¤Ë¤ª¤¤¤Æ²¼¤È·Ò¤¬¤Ã¤Æ¤¤¤ë¤«
    boolean	linkW;		//Â°¤¹¤ë¶ð¤Ë¤ª¤¤¤Æº¸¤È·Ò¤¬¤Ã¤Æ¤¤¤ë¤«
    boolean	linkE;		//Â°¤¹¤ë¶ð¤Ë¤ª¤¤¤Æ±¦¤È·Ò¤¬¤Ã¤Æ¤¤¤ë¤«
    Image		image;
    String	label;
    int		labelX;
    int		labelY;
    Color		color;
    int		hFactor;
    int		wFactor;

    //----------------------------------------------------------------
    // ¥³¥ó¥¹¥È¥é¥¯¥¿  cx,cy:BlockÇÛÎó¾å¤Ç¤Î°ÌÃÖ  bd:Â°¤¹¤ëBoard
    Block(int cx,int cy,Board bd){
        board = bd;
        piece = null;
        x = cx; y = cy; width = height = 1;
        linkN = linkS = linkW = linkE = outside = false;
        image = null;
        label = null;
        color = bd.slide.piececolor;
        hFactor = wFactor = 1;
        // labelX and labelY are offsets from the center of the block
        labelX = -5;
        labelY = 5;
    }

    //----------------------------------------------------------------
    // ¥Ú¥¤¥ó¥È  offx,offy:¸µ¤Î°ÌÃÖ¤«¤é¤ÎÁêÂÐ°ÌÃÖ
    public void paint(Graphics g){
        paint(g,0,0);
    }
    public void paint(Graphics g, int offx, int offy){
        int unit = board.unit;
        int x1 = board.x+x*unit+offx;
        int y1 = board.y+y*unit+offy;
        int x2 = x1+width*unit-1;
        int y2 = y1+height*unit-1;
        Slide slide = board.slide;

        if (image != null) {
            boolean flg = g.drawImage(image,
                    x1, y1, width*unit*wFactor, height*unit*hFactor,
                    slide.shadowLight, board.slide);
            return;
        }

        if (piece.getHead().image == null) {
            g.setColor( piece.getHead().color );
            g.fillRect(x1,y1,width*unit,height*unit);

            if (this == piece.getHead()) {
                g.setColor(slide.shadowDark);
                Font pf = new java.awt.Font("Helvetica",0,20);
                g.setFont(pf);
                if (label == null) {
                    char x[];
                    x = new char[1];
                    x[0] = piece.cid;
                    g.drawChars(x, 0, 1,
                            x1+(width*board.unit*wFactor)/2 + labelX,
                            y1+(height*board.unit*hFactor)/2 + labelY);
                } else {
                    if (label.charAt(0) != '*')
                        g.drawString(label,
                                x1+(width*board.unit*wFactor)/2 + labelX,
                                y1+(height*board.unit*hFactor)/2 + labelY);
                }
            }

            g.setColor( slide.shadowDark );
            if(!linkE) {
                g.drawLine(x2-1,y1,x2-1,y2);
                g.drawLine(x2,y1,x2,y2);
            }
            if(!linkS) {
                g.drawLine(x1,y2-1,x2,y2-1);
                g.drawLine(x1,y2,x2,y2);
            }
            g.setColor( slide.shadowLight );
            if(!linkW) {
                g.drawLine(x1,y1,x1,y2);
                g.drawLine(x1+1,y1,x1+1,y2);
            }
            if(!linkN) {
                g.drawLine(x1,y1,x2,y1);
                g.drawLine(x1,y1+1,x2,y1+1);
            }
        }
    }

    //----------------------------------------------------------------
    // ¥Ö¥í¥Ã¥¯¾Ãµî  offx,offy:¸µ¤Î°ÌÃÖ¤«¤é¤ÎÁêÂÐ°ÌÃÖ
    //		dx,dy:°ÊÁ°¤Î½ñ¤­´¹¤¨°ÌÃÖ¤«¤é¤ÎÁêÂÐ°ÌÃÖ
    public void clear(Graphics g, int offx, int offy, int dx, int dy){
        int unit = board.unit;
        Slide slide = board.slide;
        g.setColor( slide.backcolor );

        int x1 = board.x+unit*x+offx;
        int w1 = unit;
        int y1 = board.y+unit*y+offy;
        int h1 = unit;
        int x2,w2,y2,h2,x3,w3,y3,h3;
        boolean bx,by;

        if(dx>=0){
            x2 = x1;	w2 = (dx<unit)? dx : unit;
            x3 = x1+w2;	w3 = unit-w2;
            bx = linkW;
        }else{
            x2 = x1+w1+dx;	w2 = (-dx<unit)? -dx : unit;
            x3 = x1;	w3 = unit-w2;
            bx = linkE;
        }
        if(dy>=0){
            y2 = y1;	h2 = (dy<unit)? dy : unit;
            by = linkN;
        }else{
            y2 = y1+h1+dy;	h2 = (-dy<unit)? -dy : unit;
            by = linkS;
        }
        if(bx) g.fillRect(x2,y2,w2,h2);
        else g.fillRect(x2,y1,w2,h1);
        if(!by) g.fillRect(x3,y2,w3,h2);
    }
}





//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	£Ð£é£å£ã£å	°ì¤Ä¤Î¶ð(°ÜÆ°¤µ¤»¤ë»þ¤ÎÃ±°Ì)¤Î¥¯¥é¥¹
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

class Piece{
    final	int	maxBlocks = 36;
    public
    Board	board;
    char	cid;	// ¶ð¤Î¼±ÊÌÊ¸»ú

    private	Block	blocks[];
    int	blockn;
    Point	delta;

    //----------------------------------------------------------------
    //	¹½ÃÛ»Ò¡¿ÅÐÏ¿»Ò	c:¶ð¤ò¼±ÊÌ¤¹¤ë¤¿¤á¤ÎÊ¸»ú
    Piece( Board b, char c ) {
        board = b;
        blockn = 0;
        blocks = new Block[maxBlocks];
        cid = c;
        delta = new Point(0,0);
    }

    //----------------------------------------------------------------
    // getHead() returns the first block in the piece.

    public Block getHead() {
        if (blockn == 0)
            return null;
        return blocks[0];
    }

    //----------------------------------------------------------------
    // setLabel() sets the optional label of the first block.

    public void setLabel(String lab) {
        blocks[0].label = lab;
        blocks[0].labelX = blocks[0].labelX - 5*(lab.length()-1);
    }

    //----------------------------------------------------------------
    // setLabelOffset() sets the display offset for a label.

    public void setLabelOffset(int x, int y) {
        blocks[0].labelX = x;
        blocks[0].labelY = y;
    }

    //----------------------------------------------------------------
    // setColor() sets the optional RGB color of the first block.

    public void setColor(int r, int g, int b) {
        blocks[0].color = new Color(r,g,b);
    }

    //----------------------------------------------------------------
    // £Ð£é£å£ã£å¤Ë£Â£ì£ï£ã£ë¤òÄÉ²Ã
    public void add(Block bk){
        if(bk.piece!=null){
            System.out.println("Double definition");
            return;
        }
        blocks[blockn++] = bk;
        bk.piece = this;
    }

    //----------------------------------------------------------------
    // £ØÊý¸þ°ÜÆ°¥Á¥§¥Ã¥¯ x,y:¸µ¤Î°ÌÃÖ¤«¤é¤ÎÁêÂÐ°ÌÃÖ  dx:°ÜÆ°ÎÌ
    public int moveX(int x,int y,int dx){
        if(dx == 0) return 0;
        int unit = board.unit;
        int offx = (x+unit*1024)/unit-1024;
        int offy = (y+unit*1024)/unit-1024;
        int adj = x-offx*unit;
        if(adj != 0){
            if(dx < -adj) dx = -adj;
            if(dx > unit-adj) dx = unit-adj;
            return dx;
        }else{
            offx = (dx>0)? offx+1 : offx-1;
            for(int i=blockn;--i>=0;){
                Block bk = blocks[i];
                Block bk2 = null;
                try{
                    bk2 = board.blocks[bk.x+offx][bk.y+offy];
                    if(bk2 != null) {
                        if( bk2.outside || bk2.piece.cid != cid) {
                            return 0;
                        }
                    }
                    if(y%unit != 0){
                        bk2 = board.blocks[bk.x+offx][bk.y+offy+1];
                        if(bk2 != null && bk2.piece.cid != cid) return 0;
                    }
                } catch( Exception e ){return 0;}
            }
            if(dx > unit) dx = unit;
            if(dx <-unit) dx = -unit;
            return dx;
        }
    }


    //----------------------------------------------------------------
    //  £ÙÊý¸þ°ÜÆ°¥Á¥§¥Ã¥¯ x,y:¸µ¤Î°ÌÃÖ¤«¤é¤ÎÁêÂÐ°ÌÃÖ  dy:°ÜÆ°ÎÌ
    public int moveY(int x,int y,int dy){
        if(dy == 0) return 0;
        int unit = board.unit;
        int offx = (x+unit*1024)/unit-1024;
        int offy = (y+unit*1024)/unit-1024;
        int adj = y-offy*unit;
        if(adj != 0){
            if(dy < -adj) dy = -adj;
            if(dy > unit-adj) dy = unit-adj;
            return dy;
        }else{
            offy = (dy>0)? offy+1 : offy-1;
            for(int i=blockn;--i>=0;){
                Block bk = blocks[i];
                Block bk2 = null;
                try{
                    bk2 = board.blocks[bk.x+offx][bk.y+offy];
                    if(bk2 != null) {
                        if( bk2.outside || bk2.piece.cid != cid) {
                            return 0;
                        }
                    }
                    if(x%unit !=0){
                        bk2 = board.blocks[bk.x+offx+1][bk.y+offy];
                        if(bk2 != null && bk2.piece.cid != cid) return 0;
                    }
                } catch( Exception e ){return 0;}
            }
            if(dy > unit) dy = unit;
            if(dy <-unit) dy = -unit;
            return dy;
        }
    }

    //------------------------------------------------------------
    //	¼ÂºÝ¤Î°ÜÆ°  dx,dy:È×¤ÎÇÛÎó¾å¤ÎÁêÂÐ°ÌÃÖ
    public void move( int dx, int dy ) {
        int unit = board.unit;
        int i;
        for(i=blockn;--i>=0;){
            Block bk = blocks[i];
            board.blocks[bk.x][bk.y] = null;
        }
        for(i=blockn;--i>=0;){
            Block bk = blocks[i];
            blocks[i].move(bk.x+dx,bk.y+dy);
            board.blocks[bk.x][bk.y] = blocks[i];

        }
    }


    //----------------------------------------------------------------
    // ÉÁ²è¥ë¡¼¥Á¥ó¤Î½é´ü²½ update¤ò»È¤¤»Ï¤á¤ëÁ°¤Ë»È¤¦
    public void resetAdjust(){
        delta.x = delta.y = 0;
    }

    //----------------------------------------------------------------
    // ¥É¥é¥Ã¥°Ãæ¤ÎÉÁ²è  dx,dy:¸µ¤Î°ÌÃÖ¤«¤é¤ÎÁêÂÐ°ÌÃÖ
    public void update(Graphics g,int dx,int dy){
        if(delta.x == dx && delta.y == dy) return;
        for(int i=blockn;--i>=0;){
            blocks[i].clear(g,delta.x,delta.y,dx-delta.x,dy-delta.y);
        }
        delta.x = dx;
        delta.y = dy;



        for(int i=blockn;--i>=0;){
            blocks[i].paint(g,dx,dy);
        }
    }

}

//================================================================
//			End of File
//================================================================