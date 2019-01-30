abstract class Key[V] {
  trait Aware

  def unapply[T](arg: TypesafeMap[T])(implicit ev: T <:< Aware): Option[V] = {
    Some(arg(this))
  }

  def apply(v: V): TypesafeMap[this.Aware] = {
    TypesafeMap().updated(this, v)
  }
}

case class TypesafeMap[+T] private (underlying: Map[Key[_], Any]) {
  def get[V](key: Key[V]): Option[V] = {
    underlying.get(key).map(_.asInstanceOf[V])
  }

  // Returns the value associated with this key. Will only compile
  // if the value exists.
  def apply[V](key: Key[V])(implicit has: T <:< key.Aware): V = {
    // Looks dangerous but will never throw an error!
    get[V](key).get
  }

  // Stores the key/value and adds key.Aware to our phantom type
  def updated[V](key: Key[V], value: V): TypesafeMap[T with key.Aware] = {
    TypesafeMap[T with key.Aware](underlying + (key -> value))
  }
}

object TypesafeMap {
  def apply(): TypesafeMap[Any] = TypesafeMap[Any](Map())
}
