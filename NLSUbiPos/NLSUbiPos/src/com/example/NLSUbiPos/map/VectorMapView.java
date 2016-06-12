package com.example.NLSUbiPos.map;

import java.util.Collection;

import com.example.NLSUbiPos.building.Building;
import com.example.NLSUbiPos.geometry.Line2d;
import com.example.NLSUbiPos.geometry.Rectangle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;

/**
 * This class draws the vector map on the screen. The map is made of lines completely.
 */
public class VectorMapView extends BaseMapView {
	
	/**
	 * Constructor with the given context.
	 * @param context the given context
	 */
	public VectorMapView(Context context) {
		super(context);
	}

	@Override
	public void drawMap(Canvas canvas) {
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1.0f);
		canvas.save();
		canvas.translate(originX, originY);
		
		// the building and the current floor must exist
		if (building != null && building.getCurrentFloor()!=null) {	
			Collection<Line2d> lines = null;
			// draw walls
			lines = building.getCurrentFloor().getWallsArea().getDisplaySet();
			for (Line2d line : lines) {
				float x1 = (float) line.getStartPoint().getX() * scale;
				float y1 = - (float) line.getStartPoint().getY() * scale;
				float x2 = (float) line.getEndPoint().getX() * scale;
				float y2 = - (float) line.getEndPoint().getY() * scale;
				canvas.drawLine(x1, y1, x2, y2, paint);
			}
				
			// draw stairs
			lines = building.getCurrentFloor().getStairsArea().getDisplaySet();
			for (Line2d line : lines) {
				float x1 = (float) line.getStartPoint().getX() * scale;
				float y1 = - (float) line.getStartPoint().getY() * scale;
				float x2 = (float) line.getEndPoint().getX() * scale;
				float y2 = - (float) line.getEndPoint().getY() * scale;
				canvas.drawLine(x1, y1, x2, y2, paint);
			}
		}
		
		canvas.restore();
	}

	@Override
	public void drawPosition(Canvas canvas) {
		if (position != null) {
			
//			paint.setStyle(Paint.Style.FILL);
//			paint.setStrokeWidth(2);
//			paint.setColor(Color.BLACK);
//			paint.setTextSize(30);
//			canvas.drawText(position.getPositionInformation(), 0, 30, paint);
			TextPaint textPaint = new TextPaint();
			textPaint.setARGB(0xFF, 0xFF, 0, 0);
			textPaint.setTextSize(30);
			StaticLayout layout = new StaticLayout(position.getPositionInformation(), textPaint, 500, Alignment.ALIGN_NORMAL, 1.0F, 0.0F,true);
			canvas.save();
			canvas.translate(20, 20);
			layout.draw(canvas);
			canvas.restore();
			
			canvas.save();
			canvas.translate(originX, originY);
			position.renderPosition(canvas, scale);
			canvas.restore();
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (RENDERING) {
			// the setting position interaction is prior than other user interactions
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				if (settingPosition) {
					mode = Mode.NONE;
				} else {
					// one finger touch point corresponds to the drag mode
					startPoint.set(event.getX(), event.getY());
					mode = Mode.DRAG;
				}
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				if (settingPosition) {
					mode = Mode.NONE;
				} else {
					// two finger touch points correspond to the zoom mode
					mode = Mode.ZOOM;
					oldDistance = PointF.length(event.getX(1)-event.getX(0), event.getY(1)-event.getY(0));
				}
				break;
			case MotionEvent.ACTION_UP:
				if (settingPosition) {
					// resets the user position
					float x = event.getX();
					float y = event.getY();
					float positionX = (x - originX) / scale;
					float positionY = -(y - originY) / scale;
					if (building!=null && building.getCurrentFloor()!=null) {
						position.setPosition(positionX, positionY, building.getCurrentFloorIndex());
					}
					setSettingPosition(false);
				}
				mode = Mode.NONE;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				mode = Mode.NONE;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == Mode.DRAG) {
					// user drags the map
					endPoint.set(event.getX(), event.getY());
					originX += endPoint.x - startPoint.x;
					originY += endPoint.y - startPoint.y;
					startPoint.set(endPoint);
					// updates the display set
					updateBuidingDisplaySet();
				} else if (mode == Mode.ZOOM) {
					// user zooms the map
					// at most two finger touch points are used
					newDistance = PointF.length(event.getX(1)-event.getX(0), event.getY(1)-event.getY(0));
					scale *= newDistance / oldDistance;
					
					// zooms the map centering on the mid point of two finger touch points
					midPoint.set((event.getX(0)+event.getX(1))/2, (event.getY(0)+event.getY(1))/2);
					originX = midPoint.x + (originX-midPoint.x) * newDistance / oldDistance;
					originY = midPoint.y + (originY-midPoint.y) * newDistance / oldDistance;
					
					oldDistance = newDistance; 
					// updates the display set
					updateBuidingDisplaySet();
				}
				break;
			}
		}
		return true;
	}

	
	
	@Override
	public void setBuilding(Building building) {
		super.setBuilding(building);
		// updates the display set
		updateBuidingDisplaySet();
	}

	/**
	 * Updates the line set that will be shown on the screen. If the user drags or zooms the map,
	 * the display set is changed. The screen is like a window on the map. And only the lines within
	 * the window can be seen.
	 */
	private void updateBuidingDisplaySet() {
		synchronized(RENDERING) {
			double width = getWidth();
			double height = getHeight();
			
			// the coordinate of the screen window on the map
			double left = -originX / scale;
			double top = - originY / scale;
			double right = (-originX + width) / scale;
			double bottom = (-originY + height) / scale;
			
			// updates the display set within the window
			Rectangle boundingBox = new Rectangle(left, top, right, bottom);
			if (building!=null && building.getCurrentFloor()!=null) {
				building.getCurrentFloor().getWallsArea().updateDisplaySet(boundingBox);
				building.getCurrentFloor().getStairsArea().updateDisplaySet(boundingBox);
			}
		}
	}
	
}
