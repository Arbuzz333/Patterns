package avakhidov.factories.entity.ingredient;

import avakhidov.factories.enums.MainIngredientEnum;
import avakhidov.factories.service.MainIngredient;

public class CottageCheese implements MainIngredient {

    @Override
    public MainIngredientEnum getMainIngredient() {
        return MainIngredientEnum.COTTAGE_CHEESE;
    }
}
