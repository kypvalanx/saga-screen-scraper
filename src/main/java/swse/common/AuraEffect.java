package swse.common;

import org.apache.commons.collections.Bag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AuraEffect {
    private List<String> colors = new LinkedList<>();
    private String luminosity = "0.3";
    private String bright = "0.4";
    private String dim = "0.9";
    private String animationType = "torch";
    private String animationSpeed = "5";
    private String animationIntensity= "2";

    public AuraEffect withColor(String color) {
        colors.add(color);
        return this;
    }


    public AuraEffect withLuminosity(String luminosity) {
        this.luminosity = luminosity;
        return this;
    }


    public AuraEffect withBright(String bright) {
        this.bright = bright;
        return this;
    }



    public AuraEffect withDim(String dim) {
        this.dim = dim;
        return this;
    }



    public AuraEffect withAnimationType(String animationType) {
        this.animationType = animationType;
        return this;
    }

    public AuraEffect withAnimationSpeed(String animationSpeed) {
        this.animationSpeed = animationSpeed;
        return this;
    }

    public AuraEffect withAnimationIntensity(String animationIntensity) {
        this.animationIntensity = animationIntensity;
        return this;
    }

    //    AURA_COLOR("auraColor"),
    //    AURA_LUMINOSITY("auraLuminosity"),
    //    AURA_BRIGHT("auraBright"),
    //    AURA_DIM("auraDim"),
    //    AURA_ANIMATION_TYPE("auraAnimationType"),
    //    AURA_ANIMATION_SPEED("auraAnimationSpeed"),
    //    AURA_ANIMATION_INTENSITY("auraAnimationIntensity"),

    public List<Change> getChanges() {
        List<Change> changes = new ArrayList<>();


        changes.add(Change.create(ChangeKey.AURA_LUMINOSITY, luminosity));
        changes.add(Change.create(ChangeKey.AURA_BRIGHT, bright));
        changes.add(Change.create(ChangeKey.AURA_DIM, dim));
        if(animationType != null){
            changes.add(Change.create(ChangeKey.AURA_ANIMATION_TYPE, animationType));
        }

        if(animationSpeed != null) {
            changes.add(Change.create(ChangeKey.AURA_ANIMATION_SPEED, animationSpeed));
        }

        if(animationIntensity != null) {
            changes.add(Change.create(ChangeKey.AURA_ANIMATION_INTENSITY, animationIntensity));
        }

        if(colors.size() == 1 && !colors.get(0).equals("varies")){

            changes.add(Change.create(ChangeKey.AURA_COLOR, colors.get(0)));
        } else {
            changes.add(Change.create(ChangeKey.AURA_COLOR, "#lightsaberColor#"));
        }
        return changes;
    }

    public Choice getColorChoice() {
        Choice colorChoice = Choice.create("Select a lightsaber color.");
        if(colors.isEmpty()){
            colors.add("varies");
        }

        for (String color :
                colors) {
            if(color.equals("varies")){
                colorChoice.withOption(new Option().withDisplay("AVAILABLE_LIGHTSABER_COLORS").withPayload("AVAILABLE_LIGHTSABER_COLORS"));
            } else {
                colorChoice.withOption(new Option().withDisplay(color).withPayload("lightsaberColor", color));
            }
        }
        return colorChoice;
    }
}
