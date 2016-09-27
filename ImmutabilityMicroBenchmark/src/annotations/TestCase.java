package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// this annotation is valid for types
@Target({ ElementType.TYPE })

// annotation only exists at the source level and is not included in compiled code
@Retention(RetentionPolicy.SOURCE)

public @interface TestCase {

	public String description();
	
}