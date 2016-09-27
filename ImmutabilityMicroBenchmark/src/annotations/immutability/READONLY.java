package annotations.immutability;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// annotation only exists at the source level and is not included in compiled code
@Retention(RetentionPolicy.SOURCE)

public @interface READONLY {

}