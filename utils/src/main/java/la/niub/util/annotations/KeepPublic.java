/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    NiubCoreLibrary
 *
 *    AddonSDKPublic
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  May 6, 2011
 *    @version: 1.0
 *
 ******************************************************************************/
package la.niub.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Keep a class with all its public members
 *
 * @author dhu
 */
@Target(ElementType.TYPE)
public @interface KeepPublic {

}
