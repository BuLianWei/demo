package blw.spark

import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by bulianwei on 2019/10/2.
  */
object LeiJia {
  def main(args: Array[String]): Unit = {
    @transient lazy val logger: Logger = Logger.getLogger("leijia")

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("leijia")
    val sp = new SparkContext(conf)
    val dataRDD: RDD[Int] = sp.makeRDD(List(1, 2, 3, 4, 5, 6), 4)

    println("**" * 20)
    val myAccumulator = new MyAccumulator
    sp.register(myAccumulator)

    //    val value: RDD[(Int, Int)] = dataRDD.mapPartitionsWithIndex((index, data) => data.map((_, index)))
    //    println(dataRDD.collect().toBuffer)
    val myA: RDD[Int] = dataRDD.map {
      case num => {
        myAccumulator.add(new Bean("zhangsan", num + 1))
        num
      }
    }

    println("=="*20+myA.collect().toBuffer)

    println("**"*20+myAccumulator.value)

  }
}

class MyAccumulator extends AccumulatorV2[Bean, ArrayBuffer[Bean]] {
  @transient lazy val logger: Logger = Logger.getLogger("jieb")
  val list = ArrayBuffer[Bean]()

  override def isZero: Boolean = list.isEmpty

  override def copy(): AccumulatorV2[Bean, ArrayBuffer[Bean]] = new MyAccumulator()

  override def reset(): Unit = list.clear()

  override def add(v: Bean): Unit = {
    //    list+=v
    //    if (list.exists(_.name==v.name)){
    if (list.contains(v)) {
      val index: Int = list.indexOf(v)
      val bean: Bean = list(index)
      list.remove(index)
      if (bean.num <= v.num)
        bean.num = v.num
      list += bean
    } else {
      list += v
    }
  }

  override def merge(other: AccumulatorV2[Bean, ArrayBuffer[Bean]]): Unit = {
    val that = other.value
    logger.error(s"list:$list")
    logger.error(s"other:$that")

    //    for (item <- that) {
    //      if (list.exists(_.name == item.name)) {
    //        //      if (list.contains(item)) {
    //        logger.error("true")
    //        val index: Int = list.indexOf(item)
    //        val bean: Bean = list(index)
    //        if (bean.num <= item.num) {
    //          bean.num = item.num
    //        }
    //
    //        list.remove(index)
    //        list += bean
    //      } else {
    //        list += item
    //      }
    //    }


    other match {
      case t: MyAccumulator =>
        t.list.foldLeft(this.list) {
          case (l: ArrayBuffer[Bean], b: Bean) => {
            if (l.contains(b)) {
              val i: Int = l.indexOf(b)
              if (l(i).num<b.num)
              l.update(i, b)
              l
            } else {
              l += b
            }

          }
        }
    }

  }

  override def value: ArrayBuffer[Bean] = list
}


class myAc extends AccumulatorV2[String, ArrayBuffer[String]] {
  val data = ArrayBuffer[String]()

  override def isZero: Boolean = data.isEmpty

  override def copy(): AccumulatorV2[String, ArrayBuffer[String]] = new myAc

  override def reset(): Unit = data.clear()

  override def add(v: String): Unit = {
    v match {
      case "b" =>
      case "e" =>
      case _ => data += v
    }
  }

  override def merge(other: AccumulatorV2[String, ArrayBuffer[String]]): Unit = data ++= other.value

  override def value: ArrayBuffer[String] = data
}

class Bean(var name: String, var num: Int) extends Serializable {
  override def equals(obj: scala.Any): Boolean = {
    val that: Bean = obj.asInstanceOf[Bean]
    this.name.equals(that.name)
  }

  override def hashCode(): Int = name.hashCode + num.hashCode()

  override def toString: String = s"Bean[$name,$num]"
}