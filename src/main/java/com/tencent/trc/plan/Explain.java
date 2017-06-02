package com.tencent.trc.plan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Explain.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Explain {
	public String displayName() default "";

	public boolean normalExplain() default true;

	public boolean displayOnlyOnTrue() default false;

	public boolean skipHeader() default false;
}
