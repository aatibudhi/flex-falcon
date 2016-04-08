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

package org.apache.flex.compiler.internal.codegen.js.jx;

import org.apache.flex.compiler.codegen.IASGlobalFunctionConstants;
import org.apache.flex.compiler.codegen.ISubEmitter;
import org.apache.flex.compiler.codegen.js.IJSEmitter;
import org.apache.flex.compiler.definitions.IDefinition;
import org.apache.flex.compiler.internal.codegen.as.ASEmitterTokens;
import org.apache.flex.compiler.internal.codegen.js.JSSessionModel;
import org.apache.flex.compiler.internal.codegen.js.JSSubEmitter;
import org.apache.flex.compiler.internal.codegen.js.flexjs.JSFlexJSEmitter;
import org.apache.flex.compiler.internal.codegen.js.flexjs.JSFlexJSEmitterTokens;
import org.apache.flex.compiler.internal.codegen.js.utils.EmitterUtils;
import org.apache.flex.compiler.internal.definitions.AppliedVectorDefinition;
import org.apache.flex.compiler.internal.definitions.ClassDefinition;
import org.apache.flex.compiler.internal.definitions.InterfaceDefinition;
import org.apache.flex.compiler.internal.projects.FlexJSProject;
import org.apache.flex.compiler.internal.tree.as.ContainerNode;
import org.apache.flex.compiler.internal.tree.as.IdentifierNode;
import org.apache.flex.compiler.internal.tree.as.MemberAccessExpressionNode;
import org.apache.flex.compiler.internal.tree.as.NumericLiteralNode;
import org.apache.flex.compiler.internal.tree.as.VectorLiteralNode;
import org.apache.flex.compiler.projects.ICompilerProject;
import org.apache.flex.compiler.tree.ASTNodeID;
import org.apache.flex.compiler.tree.as.IASNode;
import org.apache.flex.compiler.tree.as.IContainerNode;
import org.apache.flex.compiler.tree.as.IExpressionNode;
import org.apache.flex.compiler.tree.as.IFunctionCallNode;
import org.apache.flex.compiler.utils.NativeUtils;

public class FunctionCallEmitter extends JSSubEmitter implements ISubEmitter<IFunctionCallNode>
{

    public FunctionCallEmitter(IJSEmitter emitter)
    {
        super(emitter);
    }

    @Override
    public void emit(IFunctionCallNode node)
    {
        // TODO (mschmalle) will remove this cast as more things get abstracted
        JSFlexJSEmitter fjs = (JSFlexJSEmitter) getEmitter();

        IASNode cnode = node.getChild(0);

        if (cnode.getNodeID() == ASTNodeID.MemberAccessExpressionID)
            cnode = cnode.getChild(0);

        ASTNodeID id = cnode.getNodeID();
        if (id != ASTNodeID.SuperID)
        {
            IDefinition def = null;

            boolean isClassCast = false;

            if (node.isNewExpression())
            {
                if (!(node.getChild(1) instanceof VectorLiteralNode))
                {
                    startMapping(node.getNewKeywordNode());
                    writeToken(ASEmitterTokens.NEW);
                    endMapping(node.getNewKeywordNode());
                }
                else
                {
                    VectorLiteralNode vectorLiteralNode = (VectorLiteralNode) node.getChild(1);
                    write("[");
                    ContainerNode contentsNode = vectorLiteralNode.getContentsNode();
                    int len = contentsNode.getChildCount();
                    for (int i = 0; i < len; i++)
                    {
                        getWalker().walk(contentsNode.getChild(i));
                        if (i < len - 1)
                        {
                            writeToken(ASEmitterTokens.COMMA);
                        }
                    }
                    write("]");
                    return;
                }
            }
            else
            {
                def = node.getNameNode().resolve(getProject());

                isClassCast = (def instanceof ClassDefinition || def instanceof InterfaceDefinition)
                        && !(NativeUtils.isJSNative(def.getBaseName()));
            }

            if (node.isNewExpression())
            {
                def = node.resolveCalledExpression(getProject());
                IExpressionNode nameNode = node.getNameNode();
                // all new calls to a class should be fully qualified names
                if (def instanceof ClassDefinition)
                {
                    startMapping(nameNode);
                    write(getEmitter().formatQualifiedName(def.getQualifiedName()));
                    endMapping(nameNode);
                }
                else
                {
                    // wrap "new someFunctionCall(args)" in parens so the
                    // function call gets parsed and evaluated before new
                    // otherwise it just looks like any other "new function"
                    // in JS.
                    if (nameNode.hasParenthesis())
                        write(ASEmitterTokens.PAREN_OPEN);                        
                    // I think we still need this for "new someVarOfTypeClass"
                    getEmitter().getWalker().walk(nameNode);
                    if (nameNode.hasParenthesis())
                        write(ASEmitterTokens.PAREN_CLOSE);                        
                }
                
                getEmitter().emitArguments(node.getArgumentsNode());
            }
            else if (!isClassCast)
            {
                if (def != null)
                {
                    boolean isInt = def.getBaseName().equals(IASGlobalFunctionConstants._int);
                    if (isInt || def.getBaseName().equals(IASGlobalFunctionConstants.trace)
                            || def.getBaseName().equals(IASGlobalFunctionConstants.uint))
                    {
                        ICompilerProject project = this.getProject();
                        if (project instanceof FlexJSProject)
                            ((FlexJSProject) project).needLanguage = true;
                        startMapping(node.getNameNode());
                        write(JSFlexJSEmitterTokens.LANGUAGE_QNAME);
                        write(ASEmitterTokens.MEMBER_ACCESS);
                        if (isInt)
                            write(JSFlexJSEmitterTokens.UNDERSCORE);
                        endMapping(node.getNameNode());
                    }
                    else if (def != null && def.getBaseName().equals("sortOn"))
                	{
                		if (def.getParent() != null &&
                    		def.getParent().getQualifiedName().equals("Array"))
                		{
                            ICompilerProject project = this.getProject();
                            if (project instanceof FlexJSProject)
                                ((FlexJSProject) project).needLanguage = true;
                            write(JSFlexJSEmitterTokens.LANGUAGE_QNAME);
                            write(ASEmitterTokens.MEMBER_ACCESS);
                            write("sortOn");
                            IContainerNode newArgs = EmitterUtils.insertArgumentsBefore(node.getArgumentsNode(), cnode);
                            fjs.emitArguments(newArgs);
                            return;
            			}
            		}

                    else if (def instanceof AppliedVectorDefinition)
                    {
                        IExpressionNode[] argumentNodes = node.getArgumentNodes();
                        int len = argumentNodes.length;
                        for (int i = 0; i < len; i++)
                        {
                            IExpressionNode argumentNode = argumentNodes[i];
                            getWalker().walk(argumentNode);
                            if(i < len - 1)
                            {
                                write(", ");
                            }
                        }
                        write(".slice()");
                        return;
                    }
                }
            	getWalker().walk(node.getNameNode());

                getEmitter().emitArguments(node.getArgumentsNode());
            }
            else //function-style cast
            {
                fjs.emitIsAs(node, node.getArgumentNodes()[0], node.getNameNode(), ASTNodeID.Op_AsID, true);
            }
        }
        else
        {
            fjs.emitSuperCall(node, JSSessionModel.SUPER_FUNCTION_CALL);
        }
    }

}
