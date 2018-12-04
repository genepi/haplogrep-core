package phylotree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import core.AnnotationAAC;
import core.Polymorphism;
import exceptions.parse.sample.InvalidPolymorphismException;

public class Annotation {

	private HashMap<Polymorphism, AnnotationAAC> acidLookup;

	private static Annotation instance = null;

	String annotationPath = "aminoacidchange.txt";

	public void setAnnotationPath(String annotationPath) {
		this.annotationPath = annotationPath;
	}

	public static Annotation getInstance() {
		if (instance == null) {
			instance = new Annotation();
		}
		return instance;
	}

	private void loadLookup() {

		acidLookup = new HashMap<Polymorphism, AnnotationAAC>();
		InputStream annotationStream = this.getClass().getClassLoader().getResourceAsStream(annotationPath);
		
		BufferedReader annotationFileReader;

		try {
			annotationFileReader = new BufferedReader(new InputStreamReader(annotationStream));
			String line = annotationFileReader.readLine();
			line = annotationFileReader.readLine();
			// Read-in each line
			while (line != null) {
				StringTokenizer mainTokenizer = new StringTokenizer(line, "\t");

				String pos = mainTokenizer.nextToken();
				String gen = mainTokenizer.nextToken();
				short cod = Short.parseShort(mainTokenizer.nextToken());
				String aachange = mainTokenizer.nextToken();
				AnnotationAAC aac = new AnnotationAAC(pos, gen, cod, aachange);
				acidLookup.put(new Polymorphism(pos), aac);
				line = annotationFileReader.readLine();
			}

		} catch (InvalidPolymorphismException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HashMap<Polymorphism, AnnotationAAC> getAnnotation() {

		if (acidLookup == null) {
			loadLookup();
		}
		
		return acidLookup;
	}

}
