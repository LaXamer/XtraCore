/**
 * This file is part of XtraCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 XtraStudio <https://github.com/XtraStudio>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xtra.core.util.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xtra.core.command.runnable.CommandRunnable;
import com.xtra.core.command.runnable.RunAt;
import com.xtra.core.util.runnable.TripleRunnable;

public class MapSorter {

    /*public static Map<CommandRunnable, RunAt> sortRunAtPriority(Map<CommandRunnable, RunAt> map) {
        // Add the entries into a list to be ordered later.
        List<CommandRunnable> keys = new ArrayList<>();
        List<RunAt> values = new ArrayList<>();
        for (Map.Entry<CommandRunnable, RunAt> entry : map.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }

        // Create a mapping of the integer priority and the RunAt annotations
        Map<Integer, RunAt> runAtMaps = new TreeMap<>();
        for (RunAt value : values) {
            // Because the integer is specified as the key, it will be
            // automatically sorted for us
            runAtMaps.put(value.priority(), value);
        }

        // Bring the ordered RunAt values back together with their
        // CommandRunnables
        Map<CommandRunnable, RunAt> treeMap = new TreeMap<>();
        // You cannot 'get' from a set, so we need to track the index separately
        // from the for loop
        int index = 0;
        for (Map.Entry<Integer, RunAt> runAtMap : runAtMaps.entrySet()) {
            treeMap.put(keys.get(index), runAtMap.getValue());
            index++;
        }
        return treeMap;
    }*/

    public static Map<CommandRunnable, RunAt> sortRunAtPriority(Map<CommandRunnable, RunAt> map) {
        // Add the entries into a list to be ordered later.
        List<TripleRunnable<CommandRunnable, RunAt, Integer>> triples = new ArrayList<>();
        for (Map.Entry<CommandRunnable, RunAt> entry : map.entrySet()) {
            triples.add(new TripleRunnable<>(entry.getKey(), entry.getValue(), entry.getValue().priority()));
        }
        Collections.sort(triples);

        Map<CommandRunnable, RunAt> newMap = new LinkedHashMap<>();
        for (TripleRunnable<CommandRunnable, RunAt, Integer> triple : triples) {
            newMap.put(triple.getCommandRunnable(), triple.getRunAt());
        }
        return newMap;
    }
}
