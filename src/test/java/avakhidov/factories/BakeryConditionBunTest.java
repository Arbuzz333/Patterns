package avakhidov.factories;

import avakhidov.factories.bakery.BakeryCondition;
import avakhidov.factories.bakery.BakeryConditionBun;
import avakhidov.factories.bakery.StorageBakery;
import avakhidov.factories.bakery.StorageBakeryFlour;
import avakhidov.factories.entity.bun.Bun;
import avakhidov.factories.entity.flour.BuckwheatFlour;
import avakhidov.factories.entity.flour.Flour;
import avakhidov.factories.enums.GrindingFlour;
import avakhidov.factories.enums.bakery.BakeryConditionEnum;
import avakhidov.factories.market.BunShop;
import avakhidov.factories.market.Market;
import avakhidov.factories.service.Oven;
import avakhidov.factories.service.OvenWorks;
import avakhidov.factories.service.recipe.bun.BuckwheatBunRecipe;
import avakhidov.factories.service.serviceimpl.HoldOven;
import avakhidov.factories.service.serviceimpl.OvenWorksImpl;
import avakhidov.factories.service.serviceimpl.PreheatedOvenBunSingleton;
import avakhidov.factories.service.recipe.bun.WheatBunRecipe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BakeryConditionBunTest {

    private static final Logger logger = LoggerFactory.getLogger(BakeryConditionBun.class);

    @Autowired
    private BuckwheatBunRecipe buckwheatBunRecipe;

    @Autowired
    private WheatBunRecipe wheatBunRecipe;

    @Test
    public void getConditionTest() {

        Oven<Bun> oven = PreheatedOvenBunSingleton.getInstance();
        OvenWorks<Bun> ovenWorks = new OvenWorksImpl<>(oven,
                buckwheatBunRecipe.cooked(0.115));

        Market<Bun> market = new BunShop(30);

        StorageBakery<Flour> storageBakery = new StorageBakeryFlour(
                new  BigDecimal(225), new BuckwheatFlour(GrindingFlour.MEDIUM));

        BakeryCondition<Bun, Flour> bakeryCondition = new BakeryConditionBun<>(ovenWorks, market, storageBakery);

        BakeryConditionEnum conditionEnum = bakeryCondition.updateCondition();

        logger.info("conditionEnum {}", conditionEnum);
        assertEquals(conditionEnum, BakeryConditionEnum.DOWNTIME);

        bakeryCondition.updateMarket(market.setQuantity(15));
        logger.info("condition {}", bakeryCondition.getCondition());
        assertEquals(bakeryCondition.getCondition(), BakeryConditionEnum.DOWNTIME);

        bakeryCondition.updateStorageBakery(storageBakery.setWeight(new BigDecimal(325)));
        logger.info("condition {}", bakeryCondition.getCondition());
        assertEquals(bakeryCondition.getCondition(), BakeryConditionEnum.WORKS);

        Oven<Bun> ovenHold = new HoldOven<>();
        OvenWorks<Bun> ovenWorksHold = new OvenWorksImpl<>(ovenHold,
                wheatBunRecipe.cooked( 0.15));
        bakeryCondition.updateOven(ovenWorksHold);
        bakeryCondition.updateMarket(market);
        bakeryCondition.updateStorageBakery(storageBakery.setWeight(new BigDecimal(425)));
        logger.info("condition {}", bakeryCondition.getCondition());
        assertEquals(bakeryCondition.getCondition(), BakeryConditionEnum.PREPARATION_FOR_WORK);
    }
}
