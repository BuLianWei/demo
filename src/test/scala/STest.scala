import java.util

import scala.collection.mutable.ArrayBuffer

/**
  * Created by bulianwei on 2019/10/1.
  */
object STest {
  def main(args: Array[String]): Unit = {
val  a=ArrayBuffer(new Bean("zhangsan",1),new Bean("zhangsan",2),new Bean("zhangsan",3))
val  b=ArrayBuffer(new Bean("zhangsan",4),new Bean("zhangsan",3),new Bean("zhangsan",5))
    println(a.intersect(b))
    println(a.diff(b))
    println(a.union(b))



//    val list=List(new Bean("zhangsan",10),new Bean("zhangsan",11))
//    import scala.collection.mutable.ListBuffer
//    val list=ListBuffer[Bean]()
//    val list=new util.ArrayList[Bean]()
//  list+=new Bean("zhangsan",10)
//  list+=new Bean("zhangsan",11)
//
//    for (item <- list){
//      println(item.age)
//    }


  }
}

 class Bean(val name:String, age:Int){
   override def equals(obj: scala.Any): Boolean = {
     val that: Bean = obj.asInstanceOf[Bean]
     this.name.equals(that)
   }

   override def hashCode(): Int = name.hashCode+age.hashCode()
   override def toString: String = s"Bea[$name,$age]"
 }
