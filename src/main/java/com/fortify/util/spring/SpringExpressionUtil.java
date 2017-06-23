/*******************************************************************************
 * (c) Copyright 2017 Hewlett Packard Enterprise Development LP
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.util.spring;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * This class provides several utility methods related to 
 * Spring Expression Language, for example for evaluating
 * (template) expressions on input objects.
 * 
 * @author Ruud Senden
 */
public class SpringExpressionUtil {
	private static final SpelExpressionParser SPEL_PARSER = new SpelExpressionParser();
	private static final StandardEvaluationContext SPEL_CONTEXT = new StandardEvaluationContext();
	
	protected SpringExpressionUtil() {}
	
	public static final StandardEvaluationContext getStandardEvaluationContext() {
		return SPEL_CONTEXT;
	}

	public static final Expression parseSimpleExpression(String exprStr) {
		return SPEL_PARSER.parseExpression(exprStr);
	}
	
	public static final Expression parseTemplateExpression(String exprStr) {
		return SPEL_PARSER.parseExpression(exprStr.replace("\\n", "\n"), new TemplateParserContext("${","}"));
	}
	
	public static final <T> T evaluateExpression(Object input, Expression expression, Class<T> returnType) {
		return evaluateExpression(null, input, expression, returnType);
	}

	public static final <T> T evaluateExpression(EvaluationContext context, Object input, Expression expression, Class<T> returnType) {
		if ( input==null || expression==null ) { return null; }
		context = context!=null ? context : getStandardEvaluationContext(); 
		return expression.getValue(context, input, returnType);
	}
	
	public static final <T> T evaluateExpression(Object input, String expression, Class<T> returnType) {
		return evaluateExpression(null, input, expression, returnType);
	}

	public static final <T> T evaluateExpression(EvaluationContext context, Object input, String expression, Class<T> returnType) {
		return evaluateExpression(context, input, parseSimpleExpression(expression), returnType);
	}
	
	public static final <T> T evaluateTemplateExpression(Object input, String expression, Class<T> returnType) {
		return evaluateTemplateExpression(null, input, expression, returnType);
	}

	public static final <T> T evaluateTemplateExpression(EvaluationContext context, Object input, String expression, Class<T> returnType) {
		return evaluateExpression(context, input, parseTemplateExpression(expression), returnType);
	}
}
