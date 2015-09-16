/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.sd;

import java.io.File;
import java.io.IOException;

import opennlp.maxent.DataStream;
import opennlp.maxent.EventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.sentdetect.SDEventStream;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jan 15, 2008, 11:06:19 PM
 *         <p>
 *         This class trains a maxent model on an ensemble of pre-segmented sentences using 
 *         a training corpus. The result of the training is a (binary and compressed) file which 
 *         contains the trained model. The training corpus is a XML file with a simple schema in which 
 *         each sentence is surrounded by a couple of tags <s> and </s>. 
 */
public class SDModelTrainer {

	
	public static GISModel train(EventStream es, int iterations, int cut)
			throws IOException {

		return GIS.trainModel(es, iterations, cut);
	}

	/**
	 * Trains the maxent model using an XML data stream using an eos scanner.
	 * @param trainingCorpus the training corpus
	 * @param iterations the number of iterations
	 * @param cut the cut
	 * @param scanner an end of sentence scanner
	 * @return a GIS model
	 * @throws IOException
	 */
	public static GISModel train(String trainingCorpus, int iterations, int cut,
			EndOfSentenceScanner scanner) throws IOException {
		DataStream ds = new XMLDataStream(trainingCorpus);
		EventStream es = new SDEventStream(ds, scanner);
		return GIS.trainModel(es, iterations, cut);
	}

	/**
	 * Train the maxent model using an XML data stream using the default eos scanner.
 	 * @param trainingCorpus the training corpus
	 * @param iterations the number of iterations
	 * @param cut the cut
	 * @return a GIS model
	 * @throws IOException
	 */
	public static GISModel train(String trainingCorpus, int iterations, int cut) throws IOException {
		return train(trainingCorpus, iterations, cut, new EndOfSentenceScanner());
	}

	/**
	 * Creates a model for a given language (french, vietnamese)
	 * @param language the language to be used.
	 */
	public static void createModel(String language) {
		String trainingCorpus = "";
		String modelFilename = "";
		if (language.equalsIgnoreCase(IConstants.LANG_FRENCH)) {
			trainingCorpus = IConstants.TRAINING_DATA_FRENCH;
			modelFilename = IConstants.MODEL_NAME_FRENCH;
		}
		if (language.equalsIgnoreCase(IConstants.LANG_VIETNAMESE)) {
			trainingCorpus = IConstants.TRAINING_DATA_VIETNAMESE;
			modelFilename = IConstants.MODEL_NAME_VIETNAMESE;
		}
		try {
			System.err.println("Training the model on corpus: " + trainingCorpus);
			// train the model, using 100 iterations and cutoff = 5
			GISModel model = train(trainingCorpus, 100, 5);
			// persist the model
			File modelFile = new File(modelFilename);
			System.err.println("Saving the model as: " + modelFile);
			new SuffixSensitiveGISModelWriter(model, modelFile).persist();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// create Vietnamese SD model
		SDModelTrainer.createModel(IConstants.LANG_VIETNAMESE);
//		// create French SD model
//		SDModelTrainer.createModel(IConstants.LANG_FRENCH);
		System.out.println("Done.");
	}
}
