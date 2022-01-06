package swse.common;

public class DefenseBonus
{
    public static Attribute createDefenseBonus(Integer bonus, String defense)
    {
        if(bonus == null){
            return null;
        }
        return Attribute.create(defense+"DefenseBonus", bonus);
    }

    public static Attribute createDefenseBonus(Integer bonus, String defense, String modifier)
    {
        if(bonus == null){
            return null;
        }
        return Attribute.create(defense+"DefenseBonus", bonus).withModifier(modifier);
    }
}
