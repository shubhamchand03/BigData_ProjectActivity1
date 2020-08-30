package general;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

public class ques3 {
	
	public static class MapForMaxPaymentCount extends Mapper<LongWritable,Text,Text,IntWritable>{
		
		public void map(LongWritable key,Text value, Context con)throws IOException, InterruptedException{
			String line = value.toString();
			String[] words = line.split(",");
			Text outputkey = new Text(words[3]);
			IntWritable outputvalue = new IntWritable(1);
			con.write(outputkey, outputvalue);
		}
		
	}
	public static class ReduceForMaxPaymentCount extends Reducer<Text,IntWritable,Text,IntWritable>{ 
		Map<Text,IntWritable> m1 = new HashMap<>();
		public void reduce(Text word, Iterable<IntWritable> values,Context con) throws IOException, InterruptedException{
			
			int sum=0;
			for(IntWritable value:values){
				sum=sum+value.get();
			}
			m1.put(word, new IntWritable(sum));
		}
		public void cleanup(Context con) throws IOException, InterruptedException{
			Map.Entry<Text, IntWritable> result = null;
			for(Map.Entry<Text, IntWritable> currentEntry:m1.entrySet()){
				if(result==null || currentEntry.getValue().compareTo(result.getValue())>0){
					result = currentEntry;
				}
			}
			con.write(result.getKey(), result.getValue());
		}
	}
	
	public static void main(String[] args)throws IOException, ClassNotFoundException, InterruptedException{
		Configuration c = new Configuration();
		Job j = Job.getInstance(c,"Max Payment Type count");
		j.setJarByClass(ques3.class);
		j.setMapperClass(MapForMaxPaymentCount.class);
		j.setReducerClass(ReduceForMaxPaymentCount.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(j,new Path(args[0]));
		FileOutputFormat.setOutputPath(j,new Path(args[1]));
		System.exit(j.waitForCompletion(true)?0:1);
	}
	

}
