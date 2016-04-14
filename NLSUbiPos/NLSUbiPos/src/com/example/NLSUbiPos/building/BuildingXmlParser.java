package com.example.NLSUbiPos.building;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.example.NLSUbiPos.geometry.Line2d;

import android.os.Environment;

/*
    <?xml version="1.0" encoding="utf-8"?>
	<building name="wdzl">
	<floor name="floor3" number="3" bias="17" scale="1.0" >
		<walls format="text" path="wdzl/walls/wdzl309.txt" />
		<stairs format="text" path="wdlz/stairs/wdzl309.txt" />
		<bitmap path="wdzl/bitmap/wdzl309.png" originPixelX="200" originPixelY="200" scale="20.0" />
	</floor>
	</building>
 */

/**
 * This class is used to parse the building's XML file to get the Building object representation.
 * The used XML parser is SAX(Simple API for XML) parser.
 */
public class BuildingXmlParser {
	
	// the Building object
	private Building building;
	
	// the Floor object
	private Floor floor;
	
	// the pathname of the building's XML file's parent
	private String parentPath;
	
	
	/**
	 * Parses the building's XML file
	 * @param filename the path of the building's XML file
	 * @return the Building object parse from the XML file
	 */
	public Building parse(String filename) {
		File buildingXmlFile = new File(filename);
		
		// changes the file to be absolute
		if(!buildingXmlFile.isAbsolute()) {
			File root = Environment.getExternalStorageDirectory();
			buildingXmlFile = new File(root, filename);
		}
		
		parentPath = buildingXmlFile.getParent() + "/";
		InputStream in = null;
		building = null;
		try {
			in = new BufferedInputStream(new FileInputStream(buildingXmlFile));
			// gets XML parser
			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			xmlReader.setContentHandler(new BuildingXmlHandler());
			xmlReader.parse(new InputSource(in));
		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();

		} catch (SAXException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return building;
	}
	
	/**
	 * A DefaultHandler implementation used for handling the building'x XML file.
	 */
	private class BuildingXmlHandler extends DefaultHandler {

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (localName.equalsIgnoreCase("building")) {
				// the name of the building
				String buildingName = null;
				// gets the attributes of the building
				for (int i=0; i<attributes.getLength(); i++) {
					if (attributes.getLocalName(i).equalsIgnoreCase("name")) {
						// gets the name of the building
						buildingName = attributes.getValue(i);
					}
				}
				// if the building name is not given, set the name "unknown"
				if (buildingName == null) {
					buildingName = "unknown";
				}
				// constructs a Building with the given name
				building = new Building(buildingName);
			} else if (localName.equalsIgnoreCase("floor")) {
				// the name of the floor
				String floorName = null;
				// the index of the floor
				int floorIndex = Integer.MIN_VALUE;
				// The north bias of the Floor. It is how much the floor map is rotated clockwise.
				float floorBias = Float.NaN;
				// the scale of the floor map
				float floorScale = Float.NaN;
				// gets the attributes of the floor
				for (int i=0; i<attributes.getLength(); i++) {
					if (attributes.getLocalName(i).equalsIgnoreCase("name")) {
						// gets the name of the floor
						floorName = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase("number")) {
						// gets the number index of the floor
						floorIndex = Integer.valueOf(attributes.getValue(i));
					} else if (attributes.getLocalName(i).equalsIgnoreCase("bias")) {
						// gets the north bias of the floor
						floorBias = Float.valueOf(attributes.getValue(i));
					} else if (attributes.getLocalName(i).equals("scale")) {
						// gets the scale of the floor map
						floorScale = Float.valueOf(attributes.getValue(i));
					}
				}
				if (floorName==null) {
					floorName = "unknown";
				}
				if (floorIndex==Integer.MIN_VALUE || Float.isNaN(floorBias) || Float.isNaN(floorScale)) {
					//throw new RuntimeException("required attribute for floor not presented (number|bias|scale) !");
					floor = null;
				} else {
					// construct a Floor with the given attributes
					floor = new Floor(floorName, floorIndex, floorBias, floorScale);
				}
				
				
			} else if (localName.equalsIgnoreCase("walls")) {
				String path = null;
				// gets the attributes of the walls area
				for (int i=0; i<attributes.getLength(); i++) {
					if (attributes.getLocalName(i).equalsIgnoreCase("path")) {
						// gets the path of the text file which stores the wall lines information
						path = attributes.getValue(i);
					}
				}
				if (path!=null) {
					// parses the text file to get the wall lines
					ArrayList<Line2d> lines = LinesReader.readLines(parentPath + path);
					if (lines != null) {
						for (Line2d line : lines) {
							floor.getWallsArea().addLine(line);
						}
					}
					// extends the lines of walls
					floor.getWallsArea().scaleLines(floor.getFloorScale());
				} 
			} else if (localName.equalsIgnoreCase("stairs")) {
				String path = null;
				// gets the attributes of the stairs area
				for (int i=0; i<attributes.getLength(); i++) {
					if (attributes.getLocalName(i).equalsIgnoreCase("path")) {
						// gets the path of the text file which stores the wall lines information
						path = attributes.getValue(i);
					}
				}
				if (path != null) {
					// parses the text file to get the stair lines
					ArrayList<Line2d> lines = LinesReader.readLines(parentPath + path);
					if (lines != null) {
						for (Line2d line : lines) {
							floor.getStairsArea().addLine(line);
						}
					}
					// extends the lines of stairs
					floor.getStairsArea().scaleLines(floor.getFloorScale());
				}
			} else if (localName.equalsIgnoreCase("bitmap")) {
				// parse the bitmap information, the bitmap may not be necessary for the program
				String path = null;
				float originPixelX = Float.NaN;
				float originPixelY = Float.NaN;
				float bitmapBias = Float.NaN;
				float bitmapScale = Float.NaN;
				// gets the attributes of the bitmap
				for (int i=0; i<attributes.getLength(); i++) {
					if (attributes.getLocalName(i).equalsIgnoreCase("path")) {
						path = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase("originPixelX")) {
						originPixelX = Float.parseFloat(attributes.getValue(i));
					} else if (attributes.getLocalName(i).equalsIgnoreCase("originPixelY")) {
						originPixelY = Float.parseFloat(attributes.getValue(i));
					} else if (attributes.getLocalName(i).equalsIgnoreCase("bias")) {
						bitmapBias = Float.parseFloat(attributes.getValue(i));
					} else if (attributes.getLocalName(i).equalsIgnoreCase("scale")) {
						bitmapScale = Float.parseFloat(attributes.getValue(i));
					}
				}
				// just set the path null if the bitmap information is not complete enough
				if (path == null || Float.isNaN(originPixelX) || Float.isNaN(originPixelY) 
						|| Float.isNaN(bitmapBias) || Float.isNaN(bitmapScale)) {
					path = null;
				}
				if(path != null) {
					path = parentPath + path;
				}
				floor.setBitmap(path, originPixelX, originPixelY, bitmapBias, bitmapScale);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			super.endElement(uri, localName, qName);
			if (localName.equalsIgnoreCase("floor")) {
				if (building != null && floor !=null) {
					// adds the floor to the building
					building.addFloor(floor);
				}
				floor = null;
			}
		}
	}
}
