package blw.flink

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
/**
  * Created by bulianwei on 2019/10/6.
  */
object Flink_Kafka {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    val data: DataStream[String] = env.addSource(new FlinkKafkaConsumer010[String]("test",new SimpleStringSchema(),properties))
    data.print("kafka")

    env.execute()
  }
}
