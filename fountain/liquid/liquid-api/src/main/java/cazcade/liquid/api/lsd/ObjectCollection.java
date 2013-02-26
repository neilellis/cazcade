/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class ObjectCollection<V> extends AbstractCollection<V> {

    private final Iterable<V> objects;


    public ObjectCollection(Iterable<V> objects) {
        super();
        this.objects = objects;
    }

    ObjectCollection() {
        super();
        objects = new ArrayList<V>();
    }

    @Nonnull @Override public Iterator<V> iterator() {
        return objects.iterator();
    }

    @Override public int size() {
        int count = 0;
        for (V object : objects) {
            count++;
        }
        return count;
    }


}
