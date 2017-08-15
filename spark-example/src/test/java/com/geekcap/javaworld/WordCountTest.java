package com.geekcap.javaworld;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;

import com.holdenkarau.spark.testing.JavaRDDComparisons;
import com.holdenkarau.spark.testing.SharedJavaSparkContext;

import scala.Tuple2;
import scala.reflect.ClassTag;

public class WordCountTest extends SharedJavaSparkContext implements Serializable {

	private static final long serialVersionUID = 1L;

	@Test
	public void shouldCountInputFileContent() throws IOException {

		Path path = Paths.get("src/main/resources/input.txt");
		List<String> inputList = Files.readAllLines(path);
		Files.write(path, inputList, Charset.defaultCharset());

		JavaPairRDD<String, Integer> countRdd = WordCount.count(jsc().parallelize(inputList, 1));
		
		// [(9,,2), (10,,2), (1,1)]
		List<Tuple2<String, Integer>> expectedList = Arrays.asList(
												new Tuple2<String, Integer>("9", 2),
												new Tuple2<String, Integer>("10", 2), 
												new Tuple2<String, Integer>("1", 1) );

		JavaPairRDD<String, Integer> parallelizePairsExpected = jsc().parallelizePairs(expectedList);

		ClassTag<Tuple2<String, Integer>> tag = scala.reflect.ClassTag$.MODULE$.apply(Tuple2.class);

		JavaRDDComparisons.assertRDDEquals(JavaRDD.fromRDD(JavaPairRDD.toRDD(countRdd), tag),
				JavaRDD.fromRDD(JavaPairRDD.toRDD(parallelizePairsExpected), tag));

	}

}
