/*
  Powered by Haxe 3.
  Lazy computation of the infinite list of Fibonacci numbers.
  Similar to the Haskellish "fibs = 1 : 1 : zipWith (+) fibs (tail fibs)"
*/

class Lazy<T>{
  private var val : T;
  private var func : Void -> T;
  
  public function new (func : Void -> T){
    this.func = func;
  }
  
  public function value(){
    if(val != null) return val;
    return val = func();
  }
  
  public static function valueOf<A>(v : A) : Lazy<A>{
    return new Lazy<A>(function(){
      return v;
    });
  }
}

class Cons<T>{
  private var _head : T;
  private var _tail : Lazy<Cons<T>>;
  
  public function new (head : T, tail : Lazy<Cons<T>>){
    this._head = head;
    this._tail = tail;
  }
  
  public function head(){
    return _head;
  }
  
  public function tail(){
    return _tail.value();
  }
}


class Fibonacci {
  static function zipWith<T1,T2,T3>(f : T1 -> T2 -> T3, c1 : Lazy<Cons<T1>>, c2 : Lazy<Cons<T2>>) : Lazy<Cons<T3>>{
    return new Lazy(function(){
      if(c1.value() == null || c2.value() == null) return null;
    
      return new Cons(f(c1.value().head(), c2.value().head()), new Lazy(function(){
        return zipWith(f, Lazy.valueOf(c1.value().tail()), Lazy.valueOf(c2.value().tail())).value();
      }));
    });
  }
  
  static function tail<T1>(c : Lazy<Cons<T1>>) : Lazy<Cons<T1>>{
    return new Lazy(function(){
      if(c.value() == null) return null;
      return c.value().tail();
    });
  }
  
  static var x = 0;
  
  static function fibbs() : Lazy<Cons<Int>>{
    var fbs = new Lazy(function(){return fibbs().value();});
    return Lazy.valueOf(new Cons(1, Lazy.valueOf(new Cons(1,
        zipWith(function(x,y){return x+y;}, 
            fbs,
            tail(fbs))
    ))));
  }
  
  static function main():Void{
    var t = fibbs().value();
    for(i in 0...100)
      t = t.tail();
    trace(t);
    //t = fibbs().value().tail().tail();
  }
}
