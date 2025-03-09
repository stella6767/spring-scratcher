import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JavaTestClass {

    @Test
    public void test(){
        List<String> classes =
                IntStream.range(0, 10).mapToObj(String::valueOf).collect(Collectors.toList());
        System.out.println(classes);
    }

}
