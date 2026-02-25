package elgatopro300.bbs_cml.items;

import net.minecraft.util.StringIdentifiable;

public enum ItemDisplayMode implements StringIdentifiable
{
    NONE("none"),
    THIRD_PERSON_LEFT_HAND("thirdperson_lefthand"),
    THIRD_PERSON_RIGHT_HAND("thirdperson_righthand"),
    FIRST_PERSON_LEFT_HAND("firstperson_lefthand"),
    FIRST_PERSON_RIGHT_HAND("firstperson_righthand"),
    HEAD("head"),
    GUI("gui"),
    GROUND("ground"),
    FIXED("fixed");

    private final String name;

    private ItemDisplayMode(String name)
    {
        this.name = name;
    }

    @Override
    public String asString()
    {
        return this.name;
    }
}
