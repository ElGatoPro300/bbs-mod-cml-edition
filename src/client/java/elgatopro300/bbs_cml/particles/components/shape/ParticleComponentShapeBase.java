package elgatopro300.bbs_cml.particles.components.shape;

import elgatopro300.bbs_cml.data.types.BaseType;
import elgatopro300.bbs_cml.data.types.ListType;
import elgatopro300.bbs_cml.data.types.MapType;
import elgatopro300.bbs_cml.math.molang.MolangException;
import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.math.molang.expressions.MolangExpression;
import elgatopro300.bbs_cml.particles.ParticleUtils;
import elgatopro300.bbs_cml.particles.components.IComponentParticleInitialize;
import elgatopro300.bbs_cml.particles.components.ParticleComponentBase;
import elgatopro300.bbs_cml.particles.components.shape.directions.ShapeDirection;
import elgatopro300.bbs_cml.particles.components.shape.directions.ShapeDirectionInwards;
import elgatopro300.bbs_cml.particles.components.shape.directions.ShapeDirectionVector;

public abstract class ParticleComponentShapeBase extends ParticleComponentBase implements IComponentParticleInitialize
{
    public MolangExpression[] offset = {MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO};
    public ShapeDirection direction = ShapeDirectionInwards.OUTWARDS;
    public boolean surface = false;

    @Override
    protected void toData(MapType data)
    {
        data.put("offset", ParticleUtils.vectorToList(this.offset));

        if (this.direction != ShapeDirectionInwards.OUTWARDS)
        {
            data.put("direction", this.direction.toData());
        }

        if (this.surface)
        {
            data.putBool("surface_only", true);
        }
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has("offset"))
        {
            ParticleUtils.vectorFromList(map.getList("offset"), this.offset, parser);
        }

        if (map.has("direction"))
        {
            BaseType direction = map.get("direction");

            if (direction.isString())
            {
                this.direction = ShapeDirectionInwards.fromString(direction.asString());
            }
            else if (direction.isList())
            {
                ListType list = direction.asList();

                if (list.size() >= 3)
                {
                    this.direction = new ShapeDirectionVector(
                        parser.parseDataSilently(list.get(0)),
                        parser.parseDataSilently(list.get(1), MolangParser.ONE),
                        parser.parseDataSilently(list.get(2))
                    );
                }
            }
        }

        if (map.has("surface_only"))
        {
            this.surface = map.getBool("surface_only");
        }

        return super.fromData(map, parser);
    }
}