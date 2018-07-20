package garmin.com.academyshop;

import io.reactivex.Observable;

/**
 * Created by Octavian on 5/1/2017.
 */

public class LambdaAndRx {

    public static void main(String[] args) {
        Observable.just("Hello, world!")
                .map(s -> s.hashCode())
                .map(i -> Integer.toString(i))
                .subscribe(s -> System.out.println(s));
    }

}
