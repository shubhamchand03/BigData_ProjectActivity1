package general;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ques4 {

	public static class MapForProductCount extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		public void map(LongWritable key, Text value, Context con)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] words = line.split(",");
			Text outputkey = new Text(words[1]);
			IntWritable outputvalue = new IntWritable(1);
			con.write(outputkey, outputvalue);
		}

	}

	public static class ReduceForProductCount extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text word, Iterable<IntWritable> values, Context con)
				throws IOException, InterruptedException {
			Text s1 = new Text("Product1");
			Text s2 = new Text("Product2");
			if (word.equals(s1) || word.equals(s2)) {
				int sum = 0;
				for (IntWritable value : values) {
					sum = sum + value.get();
				}
				con.write(word, new IntWritable(sum));
			}

		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		Configuration c = new Configuration();
		Job j = Job.getInstance(c, "Product Count");
		j.setJarByClass(ques4.class);
		j.setMapperClass(MapForProductCount.class);
		j.setReducerClass(ReduceForProductCount.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(j, new Path(args[0]));
		FileOutputFormat.setOutputPath(j, new Path(args[1]));
		System.exit(j.waitForCompletion(true) ? 0 : 1);
	}

}
