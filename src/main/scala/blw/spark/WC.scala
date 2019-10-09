package blw.spark

import org.apache.flink.api.scala.{AggregateDataSet, DataSet, ExecutionEnvironment, GroupedDataSet, _}

/**
  * Created by bulianwei on 2019/9/29.
  */
object WC {

  def main(args: Array[String]): Unit = {
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    val txtData: DataSet[String] = env.readTextFile("data/data.txt")
//     val dataSet: GroupedDataSet[(String, Int)] = txtData.flatMap(_.split(" ")).map((_,1)).groupBy(0)
     val dataSet: GroupedDataSet[Bean1] = txtData.flatMap(_.split(" ")).map(Bean1(_,1)).groupBy(0)
//   val data: AggregateDataSet[(String,Int)] = dataSet.sum(1)
   val data: AggregateDataSet[Bean1] = dataSet.sum(1)
//    dataSet.first(1).print()
    data.setParallelism(1).print()
  }

}

case class Bean1( word:String, num:Int)