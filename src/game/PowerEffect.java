package game;

public class PowerEffect {
    public enum EffectType { EXPAND_PADDLE, SHRINK_PADDLE, SPEED_UP_BALL, SLOW_DOWN_BALL, FIRE_BALL, MULTI_BALL }
    public final EffectType type;
    public double remaining; // seconds

    public PowerEffect(EffectType type, double seconds) {
        this.type = type;
        this.remaining = seconds;
    }
}
