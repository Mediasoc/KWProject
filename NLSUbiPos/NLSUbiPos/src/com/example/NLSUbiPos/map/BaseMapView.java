package com.example.NLSUbiPos.map;

import com.example.NLSUbiPos.building.Building;
import com.example.NLSUbiPos.position.Position;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class is the basic class whose main job is to draw map and user position. The drawing will
 * be done in another thread since the class extends the SurfaceView class. Other class should extend
 * the class for the desired drawing.
 */
public abstract class BaseMapView extends SurfaceView implements SurfaceHolder.Callback{

	// the SurfaceHolder object wrapped in the SurfaceView
	private SurfaceHolder surfaceHolder;
	
	// this thread will do the drawing
	private SurfaceViewThread surfaceViewThread;
	
	// if the drawing is being executed
	private boolean running;
	
	// the Position object will tell the renderer how to draw the user position on the map
	protected Position position;
	
	// the pixel of the x coordinate of the map's origin
	protected float originX = 0.0f;
	
	// the pixel of the y coordinate of the map's origin
	protected float originY = 0.0f;
	
	// how many pixels represent one meter
	protected float scale = 20.0f;
	
	// the Building object which represents the map
	protected Building building = null;
	
	// if the user is setting the initial position actively
	protected boolean settingPosition = false;
	
	// the Paint object used for drawing
	protected Paint paint;
	
	// the user interaction mode
	protected Mode mode = Mode.NONE;
	
	// three point on the screen
	protected PointF startPoint;
	protected PointF midPoint;
	protected PointF endPoint;
	
	// old and new distance of two points on the screen
	protected float oldDistance;
	protected float newDistance;
	
	// the flag to make the drawing stable
	protected static final String RENDERING = "com.example.indoorposition.map.BaseMapView";
	
	/**
	 * Motion event mode. It is used for user's touch event interaction with the screen. <br>
	 * NONE: nothing will happen <br>
	 * DRAG: the user is dragging the screen <br>
	 * ZOOM: the user is zooming the screen
	 */
	public enum Mode { NONE, DRAG, ZOOM };
	
	/**
	 * The building map style to be shown on the screen. <br>
	 * VECTORMAP: use the vector map <br>
	 * BITMAP: use the bitmap
	 */
	public enum MapStyle {VECTORMAP, BITMAP};
	
	
	/**
	 * Constructor with the given context.
	 * @param context the given context
	 */
	public BaseMapView(Context context) {
		super(context);
		// gets the wrapped SurfaceHolder and add callback
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		paint = new Paint();
		running = false;
		startPoint = new PointF();
		midPoint = new PointF();
		endPoint = new PointF();
	}
	
	/**
	 * Sets the map.
	 * @param building the map representation.
	 */
	public void setBuilding(Building building) {
		this.building = building;
	}
	
	/**
	 * Sets the user position object.
	 * @param position it wraps the user position information
	 */
	public void setPositionObject(Position position) {
		this.position = position;
	}
	
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(surfaceViewThread == null) {
			surfaceViewThread = new SurfaceViewThread();
			surfaceViewThread.start();
		}
		running = true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		running = false;
		if(surfaceViewThread != null) {
			try {
				surfaceViewThread.join();
				surfaceViewThread = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Renders the map on the screen.
	 * @param canvas the Canvas object used for painting
	 */
	public abstract void drawMap(Canvas canvas);
	
	public abstract void drawPosition(Canvas canvas);
	
	
	/**
	 * The thread renders the map and user position. It runs continuously. 
	 */
	private class SurfaceViewThread extends Thread {
		
		// a Canvas object for painting
		private Canvas canvas;
		
		@Override
		public void run() {
			while(running) {	
				try {
					
					// lock the canvas in order to edit the pixel in the surface
					canvas = surfaceHolder.lockCanvas();
					canvas.drawColor(Color.WHITE);
					synchronized(RENDERING){
						// draw map
						drawMap(canvas);
						// draw the position information of the user
						drawPosition(canvas);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// unlock the canvas to show the pixel on the screen
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		
		}
	}
	
	/**
	 * Sets the state if the user will set his position actively.
	 * @param settingPosition new state if the user will set his position actively
	 */
	public void setSettingPosition(boolean settingPosition) {
		this.settingPosition = settingPosition;
	}
	
}
