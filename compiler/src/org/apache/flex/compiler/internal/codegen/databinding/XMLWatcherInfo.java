/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.flex.compiler.internal.codegen.databinding;

import java.util.Collection;
import java.util.List;

import org.apache.flex.compiler.problems.ICompilerProblem;
import org.apache.flex.compiler.tree.as.IASNode;

/**
 * Holds the data needed to CG
 *  an XMLWatcher
 */
public class XMLWatcherInfo extends WatcherInfoBase
{
    public XMLWatcherInfo(Collection<ICompilerProblem> problems, IASNode sourceNode, List<String> eventNames)
    {
        super(problems, sourceNode, eventNames);
        this.type = WatcherType.XML;
    }
    
    private String propertyName;

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String name)
    {
       propertyName = name;
    }
}