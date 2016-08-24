package org.docksidestage.app.web.product;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.lastaflute.web.validation.Required;

/**
 * @author jflute
 */
public class ProductDetailBean {

    @Required
    public Integer productId;
    @Required
    public String productName;
    @Required
    public String categoryName;
    @Required
    public Integer regularPrice;
    @Required
    public String productHandleCode;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
