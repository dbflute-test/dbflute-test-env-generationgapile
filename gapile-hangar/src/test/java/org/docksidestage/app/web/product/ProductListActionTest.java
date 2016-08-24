package org.docksidestage.app.web.product;

import org.dbflute.optional.OptionalThing;
import org.dbflute.utflute.lastaflute.mock.TestingJsonData;
import org.docksidestage.app.web.base.paging.SearchPagingBean;
import org.docksidestage.unit.UnitHangarTestCase;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author jflute
 */
public class ProductListActionTest extends UnitHangarTestCase {

    public void test_index_searchByName() {
        // ## Arrange ##
        ProductListAction action = new ProductListAction();
        inject(action);
        ProductSearchBody body = new ProductSearchBody();
        body.productName = "P";

        // ## Act ##
        JsonResponse<SearchPagingBean<ProductRowBean>> response = action.index(OptionalThing.of(1), body);

        // ## Assert ##
        showJson(response);
        TestingJsonData<SearchPagingBean<ProductRowBean>> data = validateJsonData(response);
        data.getJsonBean().items.forEach(bean -> {
            log(bean);
            assertTrue(bean.productName.contains(body.productName));
        });
    }
}
