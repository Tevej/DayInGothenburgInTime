package main.se.tevej.game.view.gamerendering.entity;

import com.badlogic.ashley.core.Entity;

import main.se.tevej.game.model.components.PositionComponent;
import main.se.tevej.game.model.components.SizeComponent;
import main.se.tevej.game.view.gamerendering.base.GameRenderingFactory;
import main.se.tevej.game.view.gamerendering.base.TBatchRenderer;
import main.se.tevej.game.view.gamerendering.base.TTexture;

/**
 * Retrieves PositionComponent, SizeComponent and renders the given texture. This can
 * be used on multiple components.
 */
public class TextureEntityRenderable implements EntityRenderable {

    private TTexture texture;

    public TextureEntityRenderable(String path, GameRenderingFactory renderingFactory) {
        this.texture = renderingFactory.createTexture(path);
    }

    @Override
    public void render(float offsetX, float offsetY, TBatchRenderer batchRenderer,
                       Entity entity, float pixelPerTile) {
        PositionComponent pc = entity.getComponent(PositionComponent.class);
        SizeComponent sc = entity.getComponent(SizeComponent.class);

        batchRenderer.renderTexture(texture, (pc.getX() + offsetX) * pixelPerTile,
            (pc.getY() + offsetY) * pixelPerTile, sc.getWidth() * pixelPerTile,
            sc.getHeight() * pixelPerTile);
    }
}
