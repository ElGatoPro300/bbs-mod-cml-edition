package mchorse.bbs_mod.cubic.animation;

/**
 * Procedural Action Animator class
 *
 * This class is a specialized version of Animator that is intended to be used
 * for procedural models when the user opts-in to "Action Mode". This allows
 * procedural models to use the full suite of action-based animations (idle, walk, run, etc.)
 * instead of the hardcoded procedural math.
 */
public class ProceduralActionAnimator extends Animator
{
    // No specific overrides needed yet as Animator provides the full functionality,
    // but this class serves as a distinct type for the "new mode" requirement
    // and allows for future specialization.
}
