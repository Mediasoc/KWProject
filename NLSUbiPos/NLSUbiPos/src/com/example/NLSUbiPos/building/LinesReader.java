package com.example.NLSUbiPos.building;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.os.Environment;

import com.example.NLSUbiPos.geometry.Line2d;

/**
 * This class is used for reading a text file. Every line of the file is the coordinates
 * of the start point and the end point of a real building line.
 * 
 * format: startX startY endX endY
 * eg: 0.0 0.0 1.0 1.0
 */
public class LinesReader {
	/**
	 * Reads the specified file and gets the building lines representation.
	 * @param filename the specified text file including the building lines information
	 * @return the ArrayList data structure representation of the building lines
	 */
	public static ArrayList<Line2d> readLines(String filename) {
		File linesTextFile = new File(filename);
		// if the file does not exists, return null
		if (!linesTextFile.exists()) {
			return null;
		}
		ArrayList<Line2d> lines = new ArrayList<Line2d>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(linesTextFile));
			// regular expression
			Pattern pattern = Pattern.compile(" +");
			String lineText = null;
			while ((lineText=bufferedReader.readLine()) != null) {
				String[] coordinates = pattern.split(lineText);
				if(coordinates.length == 4) {
					// the x coordinate of the start point
					double x1 = Double.valueOf(coordinates[0]);
					// the y coordinate of the start point
					double y1 = Double.valueOf(coordinates[1]);
					// the x coordinate of the end point
					double x2 = Double.valueOf(coordinates[2]);
					// the y coordinate of the end point
					double y2 = Double.valueOf(coordinates[3]);
					
					lines.add(new Line2d(x1, y1, x2, y2));
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		return lines;
	}
}
