/*
 * @(#)Principal.java	1.23 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.security.cert.X509Certificate;

/**
 * This interface represents the abstract notion of a principal, which
 * can be used to represent any entity, such as an individual, a
 * corporation, and a login id.
 *
 * @author Li Gong
 * @version 1.23, 05/11/17
 * @see X509Certificate
 */
public interface Principal extends IsSerializable {

    /**
     * Compares this principal to the specified object.  Returns true
     * if the object passed in matches the principal represented by
     * the implementation of this interface.
     *
     * @param another principal to compare with.
     * @return true if the principal passed in is the same as that
     *         encapsulated by this principal, and false otherwise.
     */
    boolean equals(Object another);

    /**
     * Returns a string representation of this principal.
     *
     * @return a string representation of this principal.
     */
    String toString();

    /**
     * Returns a hashcode for this principal.
     *
     * @return a hashcode for this principal.
     */
    int hashCode();

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    String getName();
}
