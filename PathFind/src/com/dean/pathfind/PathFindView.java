package com.dean.pathfind;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author Dean Guo
 **/
public class PathFindView
    extends SurfaceView
    implements SurfaceHolder.Callback, Runnable
{

    private SurfaceHolder mHolder;

    private Canvas mCanvas;

    private boolean isRun;

    // 屏幕宽高
    public static int WIDTH, HEIGHT;

    private DrawManager drawManager;

    int[][] path;

    public PathFindView( Context context )
    {
        super(context);

        // 设置视图宽高（像素）
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        WIDTH = metric.widthPixels;
        HEIGHT = metric.heightPixels;

        mHolder = this.getHolder();
        mHolder.addCallback(this);

        drawManager = new DrawManager(context, WIDTH, HEIGHT);

    }

    @Override
    public void run()
    {

        Date date = null;
        while (isRun)
        {
            try
            {
                date = new Date();
                mCanvas = mHolder.lockCanvas(null);
                if (mCanvas != null)
                {
                    synchronized (mHolder)
                    {
                        drawManager.drawMap(mCanvas);
                        drawManager.drawButton(mCanvas);
                        if (isOk) path = drawManager.drawPath(mCanvas, Color.RED, path);

                        // 控制帧数
                        Thread.sleep(Math.max(0, 200 - (new Date().getTime() - date.getTime())));
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (mCanvas != null)
                {
                    mHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }
    }

    boolean isOk = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        switch (event.getPointerCount())
        {
        // 单点触摸
        case 1:
            switch (event.getAction())
            {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                ArrayList<int[]> pos = null;
                // 按钮1按下
                if (drawManager.btn1_rect.contains((int) x, (int) y))
                {
                    pos = drawManager.dfs_posList;
                }
                // 按钮2按下
                if (drawManager.btn2_rect.contains((int) x, (int) y))
                {
                    pos = drawManager.bfs_posList;
                }

                if (pos != null)
                {
                    isOk = true;
                    path = new int[pos.size()][3];
                    for (int i = 0; i < pos.size(); i++)
                    {
                        path[i][0] = pos.get(i)[0]; // x
                        path[i][1] = pos.get(i)[1]; // y
                        path[i][2] = 0;             // isActive
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
            }
            break;
        }

        return true;
    }

    // Surface的大小发生改变时调用
    @Override
    public void surfaceChanged( SurfaceHolder holder, int format, int width, int height )
    {
    }

    // Surface创建时激发，一般在这里调用画面的线程
    @Override
    public void surfaceCreated( SurfaceHolder holder )
    {
        isRun = true;
        new Thread(this).start();
    }

    // 销毁时激发，一般在这里将画面的线程停止、释放。
    @Override
    public void surfaceDestroyed( SurfaceHolder argholder0 )
    {
        isRun = false;
    }
}
