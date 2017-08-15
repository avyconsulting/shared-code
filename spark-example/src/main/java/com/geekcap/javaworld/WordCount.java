package com.geekcap.javaworld;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public final class WordCount {

	public static void main(String[] args) {

		doIt("src/main/resources/input.txt");
	}

	public static void doIt(String fileName) {
		SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Word count app");
		sparkConf.set("spark.logLineage", "true");
		sparkConf.set("spark.hadoop.validateOutputSpecs", "false");

		try (JavaSparkContext sc = new JavaSparkContext(sparkConf)) {
			write(sc.textFile(fileName));
		}
	}

	public static void write(JavaRDD<String> javaRDD) {
		count(javaRDD).saveAsTextFile("output.txt");
	}

	public static JavaPairRDD<String, Integer> count(JavaRDD<String> javaRDD) {
		return javaRDD.mapToPair(w -> new Tuple2<String, Integer>(w, 1)).reduceByKey((a, b) -> a + b);
	}
}
