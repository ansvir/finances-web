package org.tohant.financesweb.util;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.Set;

public class FinancesDialect extends AbstractDialect implements IExpressionObjectDialect {

    public FinancesDialect() {
        super("Finances Dialect");
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IExpressionObjectFactory() {

            @Override
            public Set<String> getAllExpressionObjectNames() {
                return Collections.singleton("futils");
            }

            @Override
            public Object buildObject(IExpressionContext context,
                                      String expressionObjectName) {
                return new FinancesThymeleafUtil();
            }

            @Override
            public boolean isCacheable(String expressionObjectName) {
                return true;
            }
        };

    }
}