package ZeroTypes.Exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: chhuening
 * Date: 18.06.12
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
public class TypeIsNotSerializableException extends RuntimeException {

    private Exception e;

    public TypeIsNotSerializableException(Exception e){
        this.e = e;
    }

    public Exception getInnterException(){
        return e;
    }
}
