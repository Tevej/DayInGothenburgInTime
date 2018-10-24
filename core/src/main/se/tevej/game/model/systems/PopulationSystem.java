package main.se.tevej.game.model.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import main.se.tevej.game.model.components.InventoryComponent;
import main.se.tevej.game.model.components.buildings.HomeComponent;
import main.se.tevej.game.model.resources.NotEnoughResourcesException;
import main.se.tevej.game.model.resources.Resource;
import main.se.tevej.game.model.resources.ResourceType;

public class PopulationSystem extends TSystem {

    private Engine engine;
    private float gestationProgress;
    private int alivePop;
    @SuppressFBWarnings(
            value = "SS_SHOULD_BE_STATIC",
            justification = "No need to be static and checkbugs will complain if it is."
    )
    private final int popHunger = 10;
    @SuppressFBWarnings(
            value = "SS_SHOULD_BE_STATIC",
            justification = "No need to be static and checkbugs will complain if it is."
    )
    private final float gestationPeriod = 10f;

    public PopulationSystem() {
        super();
    }

    private void giveBirth() {
        ImmutableArray<Entity> homes = engine.getEntitiesFor(
                Family.all(HomeComponent.class).get());
        InventoryComponent inventoryC = engine.getEntitiesFor(
                Family.all(InventoryComponent.class).get())
                .first().getComponent(InventoryComponent.class);

        for (int i = 0; i < homes.size(); i++) {
            HomeComponent homeC = homes.get(i).getComponent(HomeComponent.class);
            if (homeC.getCurrentPopulation() < homeC.getMaxPopulation()) {
                homeC.updateCurrentPopulation((int) homeC.getCurrentPopulation() + 1);
                alivePop += 1;
                inventoryC.addResource(new Resource(1, ResourceType.CURRENTPOPULATION));
            }
        }
    }

    private void eatFood(float deltaTime) {
        InventoryComponent inventoryC = engine.getEntitiesFor(
                Family.all(InventoryComponent.class).get())
                .first().getComponent(InventoryComponent.class);
        float foodCost = popHunger * deltaTime;
        try {
            inventoryC.removeFromInventory(
                    new Resource(foodCost * alivePop, ResourceType.FOOD));
        } catch (NotEnoughResourcesException e) {
            killPopulation(foodCost, inventoryC);
        }
    }

    private void killPopulation(float foodCost, InventoryComponent inventoryC) {
        int survivors = (int) (inventoryC.getAmountOfResource(ResourceType.FOOD) / foodCost);
        int deaths = alivePop - survivors;
        try {
            inventoryC.removeFromInventory(
                    new Resource(deaths * foodCost, ResourceType.FOOD));
        } catch (NotEnoughResourcesException e) {
            System.out.println("This should never happen");
        }
        if (inventoryC.getAmountOfResource(ResourceType.CURRENTPOPULATION) > survivors) {
            try {
                inventoryC.removeFromInventory(
                        new Resource(survivors, ResourceType.CURRENTPOPULATION));
            } catch (NotEnoughResourcesException ex) {
                System.out.println("This should never happen!");
            }
        }
        alivePop -= deaths;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        giveBirth();
        giveBirth();
        giveBirth();
        giveBirth();
        giveBirth();
    }

    @Override
    public void update(float deltaTime) {
        gestationProgress += deltaTime;
        if (gestationProgress >= gestationPeriod) {
            giveBirth();
            gestationProgress = 0;
        }
        eatFood(deltaTime);
    }
}
