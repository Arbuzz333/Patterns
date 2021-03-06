package avakhidov.factories.cache;

import avakhidov.factories.entity.Product;
import avakhidov.factories.enums.MainIngredientEnum;
import avakhidov.factories.service.MainIngredient;
import avakhidov.factories.service.orders.ordersvisitor.OrdersMakerProduct;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;



public class ProductMakerCache {

    private final OrdersMakerProduct visitor;

    private List<Product> products = new ArrayList<>();

    public ProductMakerCache(
            OrdersMakerProduct visitor) {
        this.visitor = visitor;
    }

    public void initProductMaker(Class<?> clazz, int quantity) {
        visitor.initOrdersMakerProduct(quantity, clazz);
        try {
            products.clear();
            products.addAll(visitor.accept());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void initProductMakerDifferent(int quantity, Class<?>... clazzes) {

        products.clear();
        Arrays.stream(clazzes).forEach(c -> {
            visitor.initOrdersMakerProduct(quantity, c);
            try {
                products.addAll(visitor.accept());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @CacheEvict(value = "productList", allEntries = true)
    public List<Product<? extends MainIngredient>> makeProductListEvict(MainIngredientEnum flour) {

        return getBaseProductList(flour);
    }

    @CachePut(cacheNames = "product")
    public Product<? extends MainIngredient> getProductCachePut(MainIngredientEnum flour) {

        return getBaseProduct(flour);
    }

    @CacheEvict(cacheNames = "product")
    public Product<? extends MainIngredient> getProductEvict(MainIngredientEnum flour) {

        return getBaseProduct(flour);
    }

    @Cacheable(cacheNames="product", unless = "#result.finished.name().equals('FINISHED')")
    public Product<? extends MainIngredient> getProduct(MainIngredientEnum flour) {

        return getBaseProduct(flour);
    }

    @CachePut(cacheNames="productList", condition = "#flour.name()=='PREPARE_PANCAKE_DOUGH'")
    public List<Product<? extends MainIngredient>> getProductListCachePut(MainIngredientEnum flour) {

        return getBaseProductList(flour);
    }

    @Cacheable(cacheNames="productList")
    public List<Product<? extends MainIngredient>> getProductList(MainIngredientEnum flour) {

        return getBaseProductList(flour);
    }

    private Product<? extends MainIngredient> getBaseProduct(MainIngredientEnum flour) {

        Product<? extends MainIngredient> product = getBaseProductList(flour)
                .stream()
                .findFirst()
                .orElse(null);

        return product;
    }

    private List<Product<? extends MainIngredient>> getBaseProductList(MainIngredientEnum mainIngredientEnum) {
        List<Product<? extends MainIngredient>> productList = null;
        if (products != null) {
            productList = products
                    .stream()
                    .map(product1 -> (Product<? extends MainIngredient>)product1)
                    .filter(p -> p.getMainIngredient().getMainIngredient().equals(mainIngredientEnum))
                    .collect(Collectors.toList());
        }
        return productList;
    }

    @Cacheable(cacheNames="productList")
    public List<Product<? extends MainIngredient>> getProductListFromCache(MainIngredientEnum flour) {
        List<Product<? extends MainIngredient>> productList = null;
        return productList;
    }

    @Cacheable(cacheNames ="productDouble", key = "#mainIngredientKey")
    public ProductDouble getDoubleProduct(MainIngredientEnum mainIngredientKey, MainIngredientEnum mainIngredient) {
        ProductDouble productDouble = null;
        Product<? extends MainIngredient> productOne;
        Product<? extends MainIngredient> productTwo;
        Predicate<Product<? extends MainIngredient>> predicateOne = p -> p.getMainIngredient().getMainIngredient().equals(mainIngredientKey);
        Predicate<Product<? extends MainIngredient>> predicateTwo = p -> p.getMainIngredient().getMainIngredient().equals(mainIngredient);
        if (products != null) {
            productOne = products
                    .stream()
                    .map(product1 -> (Product<? extends MainIngredient>)product1)
                    .filter(predicateOne).findFirst().orElse(null);
            productTwo = products
                    .stream()
                    .map(product1 -> (Product<? extends MainIngredient>)product1)
                    .filter(predicateTwo).findFirst().orElse(null);
            productDouble = new ProductDouble(productOne, productTwo);
        }
        return productDouble;
    }

    @CachePut(cacheNames = "productDouble", key = "#mainIngredientKey")
    public ProductDouble getDoubleProductPut(MainIngredientEnum mainIngredientKey, Class clazzOne, Class clazzTwo) {
        ProductDouble productDouble;
        Product<? extends MainIngredient> productOne = getProductBtVisitor(clazzOne);
        Product<? extends MainIngredient> productTwo = getProductBtVisitor(clazzTwo);

        productDouble = new ProductDouble(productOne, productTwo);
        return productDouble;

    }

    private Product<? extends MainIngredient> getProductBtVisitor(Class clazz) {
        Product<? extends MainIngredient> product = null;

        visitor.initOrdersMakerProduct(1, clazz);
        try {
            product = (visitor.accept()).get(0);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return product;
    }

    /*key = "#mainIngredientKey" -- не обязательный параметр тут*/
    @CacheEvict(cacheNames = "productDouble", key = "#mainIngredientKey")
    public void productDoubleEvict(MainIngredientEnum mainIngredientKey, MainIngredientEnum mainIngredient) {
        Predicate<Product> predicateOne = p -> p.getMainIngredient().getMainIngredient().equals(mainIngredientKey);
        Predicate<Product> predicateTwo = p -> p.getMainIngredient().getMainIngredient().equals(mainIngredient);
        if (products != null) {
            products.removeIf(predicateOne);
            products.removeIf(predicateTwo);
        }
    }

    public List<Product> getProduct() {
        return products;
    }

    public OrdersMakerProduct getVisitor() {
        return visitor;
    }

}
