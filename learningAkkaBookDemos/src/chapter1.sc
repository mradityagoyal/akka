object chapter1 {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  import concurrent.Future
  import concurrent.ExecutionContext.Implicits.global
  var i, j = 0                                    //> i  : Int = 0
                                                  //| j  : Int = 0
  (1 to 100000).foreach(_ => Future { i = i + 1 })
  (1 to 100000).foreach(_ => j = j + 1)
  Thread.sleep(1000)
  println(s"${i} ${j}")                           //> 97387 100000
}