object chapter1 {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(61); 
  println("Welcome to the Scala worksheet")

  import concurrent.Future
  import concurrent.ExecutionContext.Implicits.global;$skip(98); 
  var i, j = 0;System.out.println("""i  : Int = """ + $show(i ));System.out.println("""j  : Int = """ + $show(j ));$skip(51); 
  (1 to 100000).foreach(_ => Future { i = i + 1 });$skip(40); 
  (1 to 100000).foreach(_ => j = j + 1);$skip(21); 
  Thread.sleep(1000);$skip(24); 
  println(s"${i} ${j}")}
}
