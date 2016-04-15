package com.example.NLSUbiPos.context;

import android.content.Context;
import android.util.Log;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;

public class MotionClassifier extends Classifier {

	/**
	 * Returns only the toString() method.
	 * 
	 * @return a string describing the classifier
	 */
	private Context context;

	MotionClassifier(Context context) {
		this.context = context;
	}

	public String globalInfo() {
		return toString();
	}

	/**
	 * Returns the capabilities of this classifier.
	 * 
	 * @return the capabilities
	 */
	public Capabilities getCapabilities() {
		weka.core.Capabilities result = new weka.core.Capabilities(this);

		result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
		result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
		result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
		result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);

		result.setMinimumNumberInstances(0);

		return result;
	}

	/**
	 * only checks the data against its capabilities.
	 * 
	 * @param i
	 *            the training data
	 */
	public void buildClassifier(Instances i) throws Exception {
		// can classifier handle the data?
		getCapabilities().testWithFail(i);
	}

	/**
	 * Classifies the given instance.
	 * 
	 * @param i
	 *            the instance to classify
	 * @return the classification result
	 */
	public double classifyInstance(Instance i) throws Exception {
		Object[] s = new Object[i.numAttributes()];

		for (int j = 0; j < s.length; j++) {
			if (!i.isMissing(j)) {
				if (i.attribute(j).isNominal())
					s[j] = new String(i.stringValue(j));
				else if (i.attribute(j).isNumeric())
					s[j] = new Double(i.value(j));
			}
		}

		// set class value to missing
		s[i.classIndex()] = null;

		return WekaClassifier.classify(s);

	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("1.0");
	}

	/**
	 * Returns only the classnames and what classifier it is based on.
	 * 
	 * @return a short description
	 */
	public String toString() {
		return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.6.9).\n"
				+ this.getClass().getName() + "/WekaClassifier";
	}

	/**
	 * Runs the classfier from commandline.
	 * 
	 * @param args
	 *            the commandline arguments
	 */
	public double recognize(double[] data) {
		Instances dataset = null;
		DataSource source;
		try {

			source = new DataSource(context.getAssets().open("newformat.arff"));

			dataset = source.getDataSet();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		dataset.setClassIndex(dataset.numAttributes() - 1);
		// double[] data = { 0, 0, 0, 1, 1 };
		double[] fulldata = new double[data.length + 1];
		System.arraycopy(data, 0, fulldata, 0, data.length);
		fulldata[fulldata.length - 1] = 0;
		Instance example = new Instance(1, fulldata);
		example.setDataset(dataset);
		try {
			double motionCode = classifyInstance(example);

			return motionCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

	}
}

class WekaClassifier {

	  public static double classify(Object[] i)
	    throws Exception {

	    double p = Double.NaN;
	    p = WekaClassifier.N7ab5da6f0(i);
	    return p;
	  }
	  static double N7ab5da6f0(Object []i) {
	    double p = Double.NaN;
	    if (i[1] == null) {
	      p = 1;
	    } else if (((Double) i[1]).doubleValue() <= 0.0981407) {
	      p = 1;
	    } else if (((Double) i[1]).doubleValue() > 0.0981407) {
	      p = 0;
	    } 
	    return p;
	  }
	}
