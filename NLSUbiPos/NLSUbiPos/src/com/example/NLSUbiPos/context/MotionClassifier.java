package com.example.NLSUbiPos.context;



//The decision tree classifier provided by Weka
public class MotionClassifier {


		  public static double classify(Object[] i)
		    throws Exception {

		    double p = Double.NaN;
		    p = MotionClassifier.N5fd2ba4116(i);
		    return p;
		  }
		  static double N5fd2ba4116(Object []i) {
		    double p = Double.NaN;
		    if (i[0] == null) {
		      p = 1;
		    } else if (((Double) i[0]).doubleValue() <= 0.269535) {
		      p = 1;
		    } else if (((Double) i[0]).doubleValue() > 0.269535) {
		      p = 0;
		    } 
		    return p;
		  }
		}
