package blw.spark

import com.redislabs.provider.redis._
import com.redislabs.provider.redis.streaming.RedisInputDStream
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * Created by bulianwei on 2019/10/6.
  */
object Spark_Read_Redis {


  def main(args: Array[String]): Unit = {

    createSession()

//    val sc = new SparkContext(new SparkConf()
//      .setMaster("local")
//      .setAppName("myApp")
//      // initial redis host - can be any node in cluster mode
//      .set("spark.redis.host", "localhost")
//      // initial redis port
//      .set("spark.redis.port", "6379")
//    )
//    val unit: RDD[(String, String)] = sc.fromRedisHash("test*")


    //    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark_Read_Redis").set("spark.reides.host","localhost").set("spark.redis.port","6379")
//    val ssc = new StreamingContext(sc,Seconds(3))
////
//    createStream(ssc)
////
////    ssc.start()
//    ssc.awaitTermination()

// val jedis = new Jedis("localhost")
//    val str: String = jedis.ping()
//
//    println(str)
////    jedis.hset("test","redis",System.currentTimeMillis().toString)
////    jedis.hset("test","kafka",System.currentTimeMillis().toString)
////    jedis.hset("test","flum",System.currentTimeMillis().toString)
//
//    val keys: util.Set[String] = jedis.keys("*")
//    println(keys)
//    for(k <- keys){
//      val map: util.Map[String, String] = jedis.hgetAll(k)
//      for (m <- map.keySet()){
//        val str1: String = jedis.hget(k, m)
//        println(s" key:$k,fields:$m,value:$str1")
//      }
//    }
  }

  def createSession()={
    val spark = SparkSession.builder.appName("Redis Stream Example")
      .master("local[*]")
      .config("spark.redis.host", "localhost")
      .config("spark.redis.port", "6379")
      .getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))
    val redisStream = ssc.createRedisStreamWithoutListname(Array("key"), storageLevel = StorageLevel.MEMORY_AND_DISK_2)
    redisStream.print()
//    ssc.start()
    ssc.awaitTermination()
  }

  def createStream(sc: StreamingContext) = {
    val redisStream = sc.createRedisStream(Array("key"), storageLevel = StorageLevel.MEMORY_AND_DISK_2)
    redisStream.print()
  }
   def createRedisStream(sc: StreamingContext) = {
    val dStream: RedisInputDStream[(String, String)] = sc.createRedisStream(Array("test"), storageLevel = StorageLevel.MEMORY_AND_DISK_2)
    dStream.print()
  }
}
