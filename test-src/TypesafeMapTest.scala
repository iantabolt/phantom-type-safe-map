import org.scalatest.FunSuite

class TypesafeMapTest extends FunSuite {
  case object Tokens extends Key[List[String]]
  case object Text extends Key[String]
  case object Counts extends Key[(Int, Int)] // (num chars, num words)

  // requires Text, adds Tokens
  def withTokens[T <: Text.Aware](
    m: TypesafeMap[T]
  ): TypesafeMap[T with Tokens.Aware] = {
    m.updated(Tokens, m(Text).split("""\s*\b\s*""").filter(_.nonEmpty).toList)
  }

  // requires Text and Tokens, adds Counts
  def withCounts[T <: Text.Aware with Tokens.Aware](
    m: TypesafeMap[T]
  ): TypesafeMap[T with Counts.Aware] = {
    m.updated(Counts, (m(Text).length, m(Tokens).size))
  }

  val empty = TypesafeMap()

  test("TypesafeMap.updated should only compile if the value matches the key") {
    assertTypeError("empty.updated(Text, (25, 3))")
    assertCompiles("empty.updated(Counts, (25, 3))") 
  }

  test("TypesafeMap.get should return the right type") {
    assertTypeError("val v: Option[String] = empty.get(Counts)")
    assertCompiles("val v: Option[String] = empty.get(Text)")
  }

  test("TypesafeMap.updated should insert the values") {
    val map1 = empty
      .updated(Text, "Hello, world!")
      .updated(Text, "Hello, Mars!")
      .updated(Counts, (12, 4))
    assert(map1.get(Text).contains("Hello, Mars!"))
    assert(map1.get(Counts).contains((12, 4)))
  }

  test("TypesafeMap.apply should only compile if key is present") {
    assertTypeError("empty(Text)")
    val map1 = empty.updated(Text, "Hello, big world!")

    // Check that return types are correct
    withClue("apply(Text) should return a String") {
      assertTypeError("val v: Int   = map1(Text)")
      assertCompiles("val v: String = map1(Text)")
    }

    // Check that methods don't compile without required keys
    withClue("methods should be able to enforce required keys") {
      assertTypeError("withTokens(empty)")
      assertTypeError("withCounts(map1)")    
    }

    val map2 = withTokens(map1)
    // Why not test our tokenizer ¯\_(ツ)_/¯
    withClue("withTokens should add the right tokens") {
      assert(List("Hello", ",", "big", "world", "!") == map2(Tokens))
    }

    val map3 = withCounts(map2)
    assert((17, 5) == map3(Counts))
    assert("Hello, big world!" == map3(Text))
  }
}
