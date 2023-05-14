package ma.enset;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int id = 7;
        System.out.println( "Hello World1!" + id);
        test(id);
        System.out.println( "Hello World3!" + id);
        System.out.println( "Hello World!" );
    }

    private static void  test(int id) {
        id = 8;
        System.out.println( "Hello World2!" + id);
    }
}
