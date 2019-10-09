package blw.spark

import java.util
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

import scala.collection.JavaConversions._

/**
  * Created by bulianwei on 2019/10/7.
  */
object Spark_Kafka_Comm {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("Spark_Kafka_Comm").setMaster("local[*]").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sc = new StreamingContext(conf, Seconds(2))
    val map: Map[String, Object] = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "test",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer]
    )
    val dStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](sc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](List("test"), map))
    val value: DStream[String] = dStream.map(_.value())


    value.foreachRDD {
      rdd =>
        rdd.foreachPartition {
          partition => {
            val config = new JedisPoolConfig
            config.setMaxTotal(10)
            config.setMaxIdle(5)
            val pool = new JedisPool(config, "localhost", 6379, 10000)
            val resource: Jedis = pool.getResource
            partition.foreach(resource.hset("test", System.currentTimeMillis().toString, _))
          }
        }
    }
    value.print()
    sc.start()

    val jedis = new Jedis("localhost")
    while (true) {
      val keys: util.Set[String] = jedis.keys("*")
      keys.filter(_.nonEmpty).foreach {
        case k => {
          jedis.hgetAll(k).foreach {
            case (field, value) => println(s"key $k,  field:    $field,  value:  $value")
          }
        }

      }

      TimeUnit.SECONDS.sleep(3)
    }


    sc.awaitTermination()

  }


  def test() = {
    val jedis = new Jedis("localhost")
    val str: String = jedis.ping()

    println(str)
    //    jedis.hset("test","redis",System.currentTimeMillis().toString)
    //    jedis.hset("test","kafka",System.currentTimeMillis().toString)
    //    jedis.hset("test","flum",System.currentTimeMillis().toString)

    val keys: util.Set[String] = jedis.keys("*")
    println(keys)
    keys.filter(_.nonEmpty).foreach {

      case d => {
        jedis.hgetAll(d).foreach {
          case (field, value) => {

            println(s"key $d,  field:    $field,  value:  $value")
          }
        }
      }
    }

  }

}
