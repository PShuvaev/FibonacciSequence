/*
  Powered by Java 8.
  Lazy computation of the infinite list of Fibonacci numbers.
  Similar to the Haskellish "fibs = 1 : 1 : zipWith (+) fibs (tail fibs)"
*/
import java.util.function.*;

interface Func0<O>{
  O apply();
}
interface Func2<I1,I2,O>{
  O apply(I1 i1, I2 i2);
}

class Lazy<T>{
  private T val;
  private Func0<T> func;
  
  public Lazy(Func0<T> func){
    this.func = func;
  }
  
  public T value(){
    if(val != null) return val;
    return val = func.apply();
  }
  
  public static <A> Lazy<A> valueOf(A v){
    return new Lazy<A>(() ->  v);
  }
  
  public String toString(){
    return "Lazy(" + (val == null ? "λ" : val);
  }
}

class Cons<T>{
  private T _head;
  private Lazy<Cons<T>> _tail;
  
  public Cons(T head, Lazy<Cons<T>> tail){
    this._head = head;
    this._tail = tail;
  }
  
  public T head(){
    return _head;
  }
  
  public Cons<T> tail(){
    return _tail.value();
  }
  
  public String toString(){
    return "Cons(" + _head + ", ..)";
  }
}


class Fibonacci {
  static <T1,T2,T3> Lazy<Cons<T3>> zipWith(Func2<T1,T2,T3> f, Lazy<Cons<T1>> c1, Lazy<Cons<T2>> c2){
    return new Lazy<Cons<T3>>(() -> (c1.value() == null || c2.value() == null) ? null :
      new Cons<T3>(f.apply(c1.value().head(), c2.value().head()), new Lazy(() ->
        zipWith(f, Lazy.valueOf(c1.value().tail()), Lazy.valueOf(c2.value().tail())).value()
      ))
    );
  }
  
  static <T1> Lazy<Cons<T1>> tail(Lazy<Cons<T1>> c){
    return new Lazy(() -> c.value() == null ? null : c.value().tail());
  }
  
  static Lazy<Cons<Integer>> fibbs(){
    Lazy fbs = new Lazy(() -> fibbs().value());
    return Lazy.valueOf(new Cons(1, Lazy.valueOf(new Cons(1,
        zipWith((x,y) -> ((Number)x).intValue()+((Number)y).intValue(), 
            fbs,
            tail(fbs))
    ))));
  }
  
  public static void main(String[] args){
    Cons<Integer> t = fibbs().value();
    for(int i = 0; i < 500; i++)
      t = t.tail();
    
    System.out.println( t );
  }
}
