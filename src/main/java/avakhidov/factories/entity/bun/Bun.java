package avakhidov.factories.entity.bun;


import avakhidov.factories.entity.Product;
import avakhidov.factories.entity.dough.ParameterPrepareDough;
import avakhidov.factories.entity.flour.Flour;
import avakhidov.factories.enums.Finished;
import avakhidov.factories.event.EventListenerBun;
import avakhidov.factories.event.EventManager;

import java.util.UUID;

public abstract class Bun extends Product<ParameterPrepareDough> {

    private boolean recipeReady = false;

    private UUID uuid;

    private EventManager events;
    {
        this.uuid = UUID.randomUUID();
        this.events = new EventManager(EventManager.EventTypeEnum.FINISHED_TYPE);
    }

    protected Bun() {
    }

    public Bun(ParameterPrepareDough<? extends Flour> prepareDough, boolean recipeReady, double weight) {
        super(prepareDough, weight);
        this.recipeReady = recipeReady;
    }

    public abstract void setKindDough();

    @Override
    public Bun setFinished(Finished finished) {
        super.setFinished(finished);
        if (finished.equals(Finished.FINISHED)) {
            events.notify(EventManager.EventTypeEnum.FINISHED_TYPE, uuid);
        }
        return this;
    }

    public boolean getRecipeReady() {
        return recipeReady;
    }

    public void setRecipeReady(boolean recipeReady) {
        this.recipeReady = recipeReady;
    }

    public void setListenerSave(EventListenerBun listenerSave) {
        events.subscribe(EventManager.EventTypeEnum.FINISHED_TYPE, listenerSave);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bun)) return false;
        Bun bun = (Bun) o;
        return getRecipeReady() == bun.getRecipeReady()
                && getWeight() == bun.getWeight()
                && getFinished().equals(bun.getFinished())
                &&  getMainIngredient().equals(bun.getMainIngredient());
    }

}
