/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    WeFamily
 *
 *    KeepAll
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  Sep 2, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package la.niub.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Class specified by this annotation will be keep with all members in this
 * class when obfuscating
 * @author dhu
 *
 */
@Target(ElementType.TYPE)
public @interface KeepAll {

}
