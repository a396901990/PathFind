package com.dean.pathfind;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class DrawManager
{

    private Paint linePaint;

    private Paint pathPaint;

    private Paint textPaint;

    private Paint buttonPaint;

    private float[] xLinesPos;

    private float[] yLinesPos;

    private int mWidth;

    private int mHeight;

    private float col_width;

    private float col_height;

    private Context context;

    public static final int WIDTH_COUNT = 8;

    public static final int HEIGHT_COUNT = 8;

    public int margin = 0;

    /** 向下移动动画 **/
    private final static int ANIM_DOWN = 0;

    /** 向左移动动画 **/
    private final static int ANIM_LEFT = 1;

    /** 向右移动动画 **/
    private final static int ANIM_RIGHT = 2;

    /** 向上移动动画 **/
    private final static int ANIM_UP = 3;

    /** 动画的总数量 **/
    private final static int ANIM_COUNT = 4;

    private Animation mHeroAnim[] = new Animation[ANIM_COUNT];

    private Animation mStoneAnim;

    private Animation mTorchwood;

    private Animation mSpikerock;
    
    private Animation mSunFlower;
    
    private Animation mZombieDancing;

    public int endPosX, endPosY;

    public int mapWidth, mapHeight;

    public Rect btn1_rect, btn2_rect;

    public static final int[][] mMapView =
        {
            { 0, 1, 1, 3, 0, 0, 0, 1 },
            { 0, 0, 0, 2, 0, 1, 0, 1 },
            { 1, 0, 3, 1, 0, 3, 0, 0 },
            { 2, 0, 0, 2, 0, 0, 3, 0 },
            { 0, 0, 0, 0, 0, 0, 1, 0 },
            { 1, 1, 0, 1, 1, 0, 0, 0 },
            { 1, 0, 0, 0, 0, 3, 0, 1 },
            { 1, 0, 2, 1, 0, 0, 0, 8 } };

    public DrawManager( Context context, int screenWidth, int screenHeight )
    {
        this.context = context;
        mWidth = screenWidth;
        mHeight = screenHeight;

        initConstant();
        initPaint();
        initLineGrids();
        initAnimation();
    }

    int[][] a = new int[64][64];

    int[][] dfs_book = new int[64][64];

    int[][] bfs_book = new int[64][64];

    public ArrayList<int[]> dfs_posList = new ArrayList<int[]>();

    public ArrayList<int[]> bfs_posList = new ArrayList<int[]>();

    // 深度优先搜索
    public void DFS( int x, int y )
        throws Exception
    {

        int tx, ty;

        int[] pos =
            { x, y };
        dfs_posList.add(pos);

        // 是否到达目的地
        if (mMapView[y][x] == 8)
        {
            throw new Exception("find");
        }

        // 顺时针循环，右下左上四个方向
        for (int k = 0; k < 4; k++)
        {
            tx = x + next[k][1];
            ty = y + next[k][0];

            // 是否出了边界
            boolean isOut = tx < 0 || tx >= mapWidth || ty < 0 || ty >= mapHeight;
            if (!isOut)
            {

                // 是否是障碍物
                if (mMapView[ty][tx] == 0 && dfs_book[tx][ty] == 0 || mMapView[ty][tx] == 8)
                {
                    dfs_book[tx][ty] = 1;
                    DFS(tx, ty);
                    dfs_book[tx][ty] = 0;
                }
            }

        }
    }

    // 广度优先搜索（Breadth First Search）
    public void BFS()
    {

        // 存储点的序列
        Queue<int[]> queue = new LinkedList<int[]>();

        int x, y;
        int[] pos =
            { 0, 0 };
        queue.offer(pos);

        while (!queue.isEmpty())
        {
            // 从队列中取出并移除
            pos = queue.poll();
            bfs_posList.add(pos);

            // 顺时针循环，右下左上四个方向
            for (int k = 0; k < 4; k++)
            {
                x = pos[0];
                y = pos[1];

                // 是否到达目的地
                if (mMapView[y][x] == 8)
                {
                    return;
                }

                x += next[k][1];
                y += next[k][0];

                // 是否出了边界
                boolean isOut = x < 0 || x >= mapWidth || y < 0 || y >= mapHeight;
                if (!isOut)
                {
                    // 是否是障碍物
                    if (mMapView[y][x] == 0 && bfs_book[x][y] == 0  || mMapView[y][x] == 8)
                    {
                        bfs_book[x][y] = 1;
                        queue.offer(new int[]
                            { x, y });
                    }
                }

            }
        }
    }

    int[][] next =
        {
                { 0, 1 }, // 右
                { 1, 0 }, // 下
                { 0, -1 }, // 左
                { -1, 0 } // 上
        };

    private void initConstant()
    {
        col_width = (float) mWidth / WIDTH_COUNT;
        col_height = col_width;
        margin = (mHeight - mWidth) / 2;

        mapWidth = 8;
        mapHeight = 8;

        endPosX = mMapView[0].length;
        endPosY = mMapView.length;

        bfs_book[0][0] = 1;
        dfs_book[0][0] = 1;
        BFS();
        try
        {
            DFS(0, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initLineGrids()
    {
        xLinesPos = new float[(WIDTH_COUNT + 1) * 4];
        yLinesPos = new float[(HEIGHT_COUNT + 1) * 4];

        // 竖线
        for (int i = 0; i <= WIDTH_COUNT; i++)
        {
            xLinesPos[i * 4 + 0] = i * col_width;
            xLinesPos[i * 4 + 1] = margin;
            xLinesPos[i * 4 + 2] = i * col_width;
            xLinesPos[i * 4 + 3] = mHeight - margin;
        }

        // 横线
        for (int i = 0; i <= HEIGHT_COUNT; i++)
        {
            yLinesPos[i * 4 + 0] = 0;
            yLinesPos[i * 4 + 1] = margin + i * col_height;
            yLinesPos[i * 4 + 2] = mWidth;
            yLinesPos[i * 4 + 3] = margin + i * col_height;
        }
    }

    private void initAnimation()
    {
        mStoneAnim = new Animation(context, new int[]
            { R.drawable.zz1, R.drawable.zz2, R.drawable.zz3, R.drawable.zz4, R.drawable.zz5, R.drawable.zz6, R.drawable.zz7, R.drawable.zz8 }, true);

        mTorchwood = new Animation(context, new int[]
            { R.drawable.torchwood1, R.drawable.torchwood2, R.drawable.torchwood3, R.drawable.torchwood4, R.drawable.torchwood5, R.drawable.torchwood6, R.drawable.torchwood7, R.drawable.torchwood8 }, true);

        mSpikerock = new Animation(context, new int[]
            { R.drawable.spikerock1, R.drawable.spikerock2, R.drawable.spikerock3, R.drawable.spikerock4, R.drawable.spikerock5, R.drawable.spikerock6, R.drawable.spikerock7, R.drawable.spikerock8 }, true);
        
        mSunFlower = new Animation(context, new int[]
                { R.drawable.sunflower1, R.drawable.sunflower2, R.drawable.sunflower3, R.drawable.sunflower4, R.drawable.sunflower5, R.drawable.sunflower6, R.drawable.sunflower7, R.drawable.sunflower8 }, true);
        
        mZombieDancing = new Animation(context, new int[]
                { R.drawable.dancing1, R.drawable.dancing2, R.drawable.dancing3, R.drawable.dancing4, R.drawable.dancing5, R.drawable.dancing6, R.drawable.dancing7, R.drawable.dancing8 }, true);
    }

    private void initPaint()
    {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(4.0f);

        pathPaint = new Paint();
        pathPaint.setStyle(Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLACK);

        buttonPaint = new Paint();
        buttonPaint.setStyle(Style.FILL_AND_STROKE);
        buttonPaint.setStrokeWidth(5.0f);

    }

    public void drawMap( Canvas mCanvas )
    {
        mCanvas.drawColor(Color.WHITE);

        mCanvas.drawLines(xLinesPos, linePaint);
        mCanvas.drawLines(yLinesPos, linePaint);

        for (int i = 0; i < mMapView.length; i++)
        {
            int[] colMap = mMapView[i];
            for (int j = 0; j < colMap.length; j++)
            {
                int value = mMapView[i][j];
                if (value == 1)
                {
                    drawStone(mCanvas, j, i);
                }
                else if (value == 2)
                {
                    drawTorchWood(mCanvas, j, i);
                }
                else if (value == 3)
                {
                    drawSpikeRockWood(mCanvas, j, i);
                }
                else if (value == 8)
                {
                    drawSunFlower(mCanvas, j, i);
                }
            }
        }
        
        drawZombieDancing(mCanvas, 0, 0);
    }

    public void drawButton( Canvas mCanvas )
    {

        String text1 = "深度优先搜索";
        String text2 = "广度优先搜索";

        int button1_width = margin / 4 + (int) textPaint.measureText(text1);
        int button2_width = margin / 4 + (int) textPaint.measureText(text2);

        int button_margin = (mWidth - button1_width - button2_width) / 3;
        int left1 = button_margin;
        int top1 = margin / 4;
        int right1 = left1 + button1_width;
        int bottom1 = top1 + margin / 2;

        int left2 = right1 + button_margin;
        int top2 = margin / 4;
        int right2 = left2 + button2_width;
        int bottom2 = top1 + margin / 2;

        btn1_rect = new Rect(left1, top1, right1, bottom1);
        buttonPaint.setColor(Color.BLUE);
        buttonPaint.setAlpha(100);
        mCanvas.drawRect(btn1_rect, buttonPaint);

        buttonPaint.setColor(Color.GREEN);
        buttonPaint.setAlpha(100);
        btn2_rect = new Rect(left2, top2, right2, bottom2);
        mCanvas.drawRect(btn2_rect, buttonPaint);

        float[] textSize1 = getTextSize(btn1_rect, text1);
        mCanvas.drawText(text1, textSize1[0], textSize1[1], textPaint);

        float[] textSize2 = getTextSize(btn2_rect, text2);
        mCanvas.drawText(text2, textSize2[0], textSize2[1], textPaint);
    }

    private float[] getTextSize( Rect rect, String text )
    {
        float textX = rect.left - (rect.left - rect.right) / 2 - textPaint.measureText(text) / 2;
        float textY = rect.top - (rect.top - rect.bottom) / 2 - (textPaint.descent() + textPaint.ascent()) / 2;
        return new float[]
            { textX, textY };
    }

    public int[][] drawPath( Canvas mCanvas, int color, int[][] pathPos )
    {
        int x = 0;
        int y = 0;
        int alpha_unit = 100 / pathPos.length;
        int isActive = 0;
        int active_num = 0;

        for (int i = 0; i < pathPos.length; i++)
        {
            if (i == 0)
            {
                pathPos[i][2] = 1;
            }
            int[] pos = pathPos[i];
            x = pos[0];
            y = pos[1];
            isActive = pos[2];
            if (isActive == 1)
            {
                Rect rect = calcCellPos(x, y);
                int alpah = i * alpha_unit + 50;
                pathPaint.setColor(color);
                pathPaint.setAlpha(alpah);
                mCanvas.drawRect(rect, pathPaint);

                String text = i + "";
                float[] textSize1 = getTextSize(rect, text);
                mCanvas.drawText(text, textSize1[0], textSize1[1], textPaint);

                active_num = i;
            }
        }
        if (active_num + 1 < pathPos.length)
        {
            pathPos[active_num + 1][2] = 1;
        }

        return pathPos;
    }

    public void drawStone( Canvas mCanvas, int xPos, int yPos )
    {
        Rect rect = calcCellPos(xPos, yPos);
        mStoneAnim.DrawAnimation(mCanvas, new Paint(), rect);
    }

    public void drawTorchWood( Canvas mCanvas, int xPos, int yPos )
    {

        Rect rect = calcCellPos(xPos, yPos);
        mTorchwood.DrawAnimation(mCanvas, new Paint(), rect);
    }

    public void drawSpikeRockWood( Canvas mCanvas, int xPos, int yPos )
    {
        Rect rect = calcCellPos(xPos, yPos);
        mSpikerock.DrawAnimation(mCanvas, new Paint(), rect);
    }
    
    public void drawSunFlower( Canvas mCanvas, int xPos, int yPos )
    {
        Rect rect = calcCellPos(xPos, yPos);
        mSunFlower.DrawAnimation(mCanvas, new Paint(), rect);
    }
    
    public void drawZombieDancing( Canvas mCanvas, int xPos, int yPos )
    {
        Rect rect = calcCellPos(xPos, yPos);
        mZombieDancing.DrawAnimation(mCanvas, new Paint(), rect);
    }

    private Rect calcCellPos( int x, int y )
    {
        int left = (int) (x * col_width);
        int top = (int) (y * col_height) + margin;
        int right = left + (int) col_width;
        int bottom = top + (int) col_height;

        return new Rect(left, top, right, bottom);
    }

    public Bitmap ReadBitMap( Context context, int resId )
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
