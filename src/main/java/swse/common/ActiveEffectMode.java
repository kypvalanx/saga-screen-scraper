package swse.common;

public enum ActiveEffectMode {
    CUSTOM(0),MULTIPLY(1),ADD(2),DOWNGRADE(3),UPGRADE(4),OVERRIDE(5),MULTIPLY_DIE_RESULT(6);

    private final int value;

    ActiveEffectMode(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
