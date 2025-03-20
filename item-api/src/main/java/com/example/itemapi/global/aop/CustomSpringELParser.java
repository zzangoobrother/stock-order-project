package com.example.itemapi.global.aop;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {
    private CustomSpringELParser() {}

    public static Object getDynamicValue(String[] parameterName, Object[] args, String key) {
        ExpressionParser expressionParser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterName.length; i++) {
            context.setVariable(parameterName[i], args[i]);
        }

        return expressionParser.parseExpression(key).getValue(context, Object.class);
    }
}
