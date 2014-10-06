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

public class ClStrength {
    public ClStrength(String name, ClSymbolicWeight symbolicWeight) {
        _name = name;
        _symbolicWeight = symbolicWeight;
    }

    public ClStrength(String name, double w1, double w2, double w3) {
        _name = name;
        _symbolicWeight = new ClSymbolicWeight(w1, w2, w3);
    }

    public boolean isRequired() {
        return (this == required);
    }

    public String toString() {
        return name() + (!isRequired() ? (":" + symbolicWeight()) : "");
    }

    public ClSymbolicWeight symbolicWeight() {
        return _symbolicWeight;
    }

    public String name() {
        return _name;
    }

    public void set_name(String name) {
        _name = name;
    }

    public void set_symbolicWeight(ClSymbolicWeight symbolicWeight) {
        _symbolicWeight = symbolicWeight;
    }

    public static final ClStrength required = new ClStrength("<Required>",
            1000, 1000, 1000);

    public static final ClStrength strong = new ClStrength("strong", 1.0, 0.0,
            0.0);

    public static final ClStrength medium = new ClStrength("medium", 0.0, 1.0,
            0.0);

    public static final ClStrength weak = new ClStrength("weak", 0.0, 0.0, 1.0);

    private String _name;

    private ClSymbolicWeight _symbolicWeight;

}
