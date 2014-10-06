/*
 * Copyright (C) 1988 1999 Greg J. Badros and Alan Borning
 * Copyright (C) 2014 Agens AS
 *
 * Original Smalltalk Implementation by Alan Borning
 * This Java Implementation by Greg J. Badros
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package EDU.Washington.grad.gjb.cassowary;

import java.util.*;

// Set
// Encapsulate a mathematical "Set" ADT using java's
// hash table.  Just a convenience wrapper of the java.util.Hashtable class.
class Set {
    public Set() {
        hash = new Hashtable<Object, Object>();
    }

    public Set(int i) {
        hash = new Hashtable<Object, Object>(i);
    }

    public Set(int i, float f) {
        hash = new Hashtable<Object, Object>(i, f);
    }

    public Set(Hashtable<Object, Object> h) {
        hash = h;
    }

    public boolean containsKey(Object o) {
        return hash.containsKey(o);
    }

    public boolean insert(Object o) {
        return hash.put(o, o) == null ? true : false;
    }

    public boolean remove(Object o) {
        return hash.remove(o) == null ? true : false;
    }

    public void clear() {
        hash.clear();
    }

    public int size() {
        return hash.size();
    }

    public boolean isEmpty() {
        return hash.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public Object clone() {
        return new Set((Hashtable<Object, Object>) hash.clone());
    }

    public Enumeration<Object> elements() {
        return hash.elements();
    }

    public String toString() {
        StringBuffer bstr = new StringBuffer("{ ");
        Enumeration<Object> e = hash.keys();
        if (e.hasMoreElements())
            bstr.append(e.nextElement().toString());
        while (e.hasMoreElements()) {
            bstr.append(", " + e.nextElement());
        }
        bstr.append(" }\n");
        return bstr.toString();
    }

    private Hashtable<Object, Object> hash;
}
