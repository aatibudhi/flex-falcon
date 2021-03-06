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

package org.apache.flex.compiler.internal.fxg.dom;

import static org.apache.flex.compiler.fxg.FXGConstants.*;

import java.util.Collection;

import org.apache.flex.compiler.problems.ICompilerProblem;

public class GradientEntryNode extends AbstractFXGNode
{
    private static final double RATIO_MIN_INCLUSIVE = 0.0;
    private static final double RATIO_MAX_INCLUSIVE = 1.0;

    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public int color = COLOR_BLACK;
    public double alpha = 1.0;
    public double ratio = Double.NaN;

    //--------------------------------------------------------------------------
    //
    // IFXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a GradientEntry node, without tag markup.
     */
    @Override
    public String getNodeName()
    {
        return FXG_GRADIENTENTRY_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value, Collection<ICompilerProblem> problems)
    {
        if (FXG_COLOR_ATTRIBUTE.equals(name))
            color = DOMParserHelper.parseRGB(this, value, name, color, problems);
        else if (FXG_ALPHA_ATTRIBUTE.equals(name))
            alpha = DOMParserHelper.parseDouble(this, value, name, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, alpha, problems);
        else if (FXG_RATIO_ATTRIBUTE.equals(name))
            ratio = DOMParserHelper.parseDouble(this, value, name, RATIO_MIN_INCLUSIVE, RATIO_MAX_INCLUSIVE, ratio, problems);
        else
            super.setAttribute(name, value, problems);
    }
}
