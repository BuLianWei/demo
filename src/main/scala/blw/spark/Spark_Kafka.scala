package blw.spark

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by bulianwei on 2019/10/6.
  */
object Spark_Kafka {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("kafka").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val sc = new StreamingContext(conf, Seconds(2))
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "d",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer])

  val data: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String,String](sc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](List("test"), kafkaParams))
    data.map(_.value()).flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).print()
    sc.start()
    sc.awaitTermination()

  }
}
