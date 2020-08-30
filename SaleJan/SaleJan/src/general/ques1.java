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

public class ques1 {
	
	public static class MapForCountryCount extends Mapper<LongWritable,Text,Text,IntWritable>{
		
		public void map(LongWritable key,Text value, Context con)throws IOException, InterruptedException{
			String line = value.toString();
			String[] words = line.split(",");
			Text outputkey = new Text(words[7]);
			IntWritable outputvalue = new IntWritable(1);
			con.write(outputkey, outputvalue);
		}
		
	}
	public static class ReduceForCountryCount extends Reducer<Text,IntWritable,Text,IntWritable>{
		public void reduce(Text word, Iterable<IntWritable> values,Context con) throws IOException, InterruptedException{
			int sum=0;
			for(IntWritable value:values){
				sum=sum+value.get();
			}
			con.write(word, new IntWritable(sum));
		}
	}
	
	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		Configuration c = new Configuration();
		Job j = Job.getInstance(c, "Product count");
		j.setJarByClass(ques1.class);
		j.setMapperClass(MapForCountryCount.class);
		j.setReducerClass(ReduceForCountryCount.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(j, new Path(args[0]));
		FileOutputFormat.setOutputPath(j, new Path(args[1]));
		System.exit(j.waitForCompletion(true) ? 0 : 1);
	}
	

}
