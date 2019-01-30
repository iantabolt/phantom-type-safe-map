import org.scalatest.FunSuite

class TypesafeMapTest extends FunSuite {
  case object Tokens extends Key[List[String]]
  case object PosTag extends Key[List[String]]
  case object Text extends Key[String]

  // def tokenize[T <: Text.Aware](
  //   text: TypesafeMap[T]
  // ): TypesafeMap[T with Tokens.Aware] = {
  //   text.updated(Tokens, text(Text).split("""\s*\b\s*""").toList)
  // }

  // def posTag[T <: Tokens.Aware](
  //   text: TypesafeMap[T]
  // ): TypesafeMap[T with PosTag.Aware] = {
  //   text.updated(PosTag, doPosTag(text(Tokens)))
  // }

  val text = TypesafeMap().updated(Text, "Hello, world!")

  test("A TypesafeMap should add values correctly") {
    val map = TypesafeMap()
    assert(map.underlying.isEmpty)
    assert(map.get(Text).isEmpty)
    val map1 = map.updated(Text, "Hello, world!")
    assert(map1.get(Text).contains("Hello, world!"))
    val map2 = map1.updated(Text, "Hello, Mars!")
    assert(map2.get(Text).contains("Hello, Mars!"))
    
    assert(map1(Text) == "Hello, world!")
  }
}
