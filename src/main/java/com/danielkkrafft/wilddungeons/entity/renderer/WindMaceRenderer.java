package com.danielkkrafft.wilddungeons.entity.renderer;


import com.danielkkrafft.wilddungeons.entity.model.WindMaceModel;
import com.danielkkrafft.wilddungeons.item.WindMace;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WindMaceRenderer extends GeoItemRenderer<WindMace>
{
    public WindMaceRenderer()
    {
        super(new WindMaceModel());
    }
}