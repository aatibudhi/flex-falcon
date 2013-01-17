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

package org.apache.flex.compiler.internal.js.codegen;

import java.io.FilterWriter;

import org.apache.flex.compiler.internal.as.codegen.ASEmitter;
import org.apache.flex.compiler.internal.tree.as.FunctionNode;
import org.apache.flex.compiler.internal.tree.as.FunctionObjectNode;
import org.apache.flex.compiler.js.codegen.IJSEmitter;
import org.apache.flex.compiler.tree.as.IExpressionNode;

/**
 * @author Michael Schmalle
 */
public class JSEmitter extends ASEmitter implements IJSEmitter
{

    public static final String CALL = "call";
    public static final String CONFIGURABLE = "configurable";
    public static final String CONSTRUCTOR = "constructor";
    public static final String DEFINE_PROPERTY = "defineProperty";
    public static final String PROTOTYPE = "prototype";
    public static final String SLICE = "slice";

    public JSEmitter(FilterWriter out)
    {
        super(out);
    }

    @Override
    public void emitFunctionObject(IExpressionNode node)
    {
        FunctionObjectNode f = (FunctionObjectNode) node;
        
        FunctionNode fnode = f.getFunctionNode();
        
        write(FUNCTION);
        
        emitParamters(fnode.getParameterNodes());
       
        emitFunctionScope(fnode.getScopedNode());
    }

}