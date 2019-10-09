package blw.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by bulianwei on 2019/10/3.
  */
object Stream {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("Stream").setMaster("local")
    val streamingContext = new StreamingContext(conf, Seconds(2))
    val data: ReceiverInputDStream[String] = streamingContext.socketTextStream("localhost", 9999)

    data.print()
    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
