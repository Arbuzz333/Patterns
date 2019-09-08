package avakhidov.factories;

import avakhidov.factories.entity.Sesame;
import avakhidov.factories.entity.WheatFlour;
import avakhidov.factories.entity.cutlet.*;
import avakhidov.factories.entity.livestock.Chicken;
import avakhidov.factories.entity.livestock.Pig;
import avakhidov.factories.enums.FatMeat;
import avakhidov.factories.enums.GrindingFlour;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.log4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CutletTest {

    private static final Logger logger = Logger.getLogger(CutletTest.class);

    @Test
    public void cutletTest() {
        Cutlet porkCutlet = new PorkCutlet(
                new PorkMeat(FatMeat.MEDIUMFAT, new Pig())
                , true
                , 100.0);
        Cutlet chickenCutlet = new ChickenCutlet(
                new ChickenMeat(FatMeat.DIETARY, new Chicken())
                , true
                , 120.0);

        Cutlet.SesameBun sesame = porkCutlet.createSesameBun(new WheatFlour(GrindingFlour.FINE)
                , true
                , new Sesame());

        List<Cutlet> cutlets = new ArrayList<>();
        cutlets.add(porkCutlet);
        cutlets.add(chickenCutlet);
    }

}
